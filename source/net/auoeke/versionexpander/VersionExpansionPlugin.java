package net.auoeke.versionexpander;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ArtifactCollection;
import org.gradle.api.artifacts.ExternalDependency;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@SuppressWarnings("unused")
public class VersionExpansionPlugin implements Plugin<Project> {
    private static Stream<Node> children(Node node) {
        return Stream.iterate(node.getFirstChild(), Objects::nonNull, Node::getNextSibling).filter(Element.class::isInstance);
    }

    private static Optional<Node> maybeChild(Node node, String name) {
        return children(node).filter(child -> child.getNodeName().equals(name)).findFirst();
    }

    private static Node child(Node node, String name) {
        return maybeChild(node, name).get();
    }

    private static String text(Node node, String name) {
        return child(node, name).getTextContent();
    }

    @Override
    public void apply(Project project) {
        var extension = project.getExtensions().create("versionExpansion", VersionExpansionExtension.class);
        var resolvedDependencies = new HashMap<String, String>();

        project.getConfigurations().all(configuration -> configuration.getIncoming().afterResolve(dependencies -> {
            Stream.of(dependencies.getArtifacts())
                .mapMulti(ArtifactCollection::forEach)
                .map(result1 -> result1.getVariant().getOwner())
                .filter(ModuleComponentIdentifier.class::isInstance)
                .map(ModuleComponentIdentifier.class::cast)
                .forEach(component -> resolvedDependencies.put(component.getModuleIdentifier().toString(), component.getVersion()));

            dependencies.getDependencies().withType(ExternalDependency.class).forEach(dependency -> {
                if (dependency.getVersionConstraint() instanceof MutableVersionConstraint constraint && extension.predicate.test(dependency.getVersion())) {
                    constraint.require(resolvedDependencies.get(dependency.getModule().toString()));
                }
            });
        }));

        project.afterEvaluate(project1 -> project1.getExtensions().getByType(PublishingExtension.class).getPublications().withType(MavenPublication.class).all(publication -> {
            publication.getPom().withXml(pom -> children(child(pom.asElement(), "dependencies")).forEach(dependency -> {
                maybeChild(dependency, "version").filter(version -> extension.predicate.test(version.getTextContent())).ifPresent(versionNode -> {
                    var version = resolvedDependencies.get(text(dependency, "groupId") + ":" + text(dependency, "artifactId"));

                    if (version != null) {
                        if (versionNode == null) {
                            versionNode = ((Document) pom.asElement().getParentNode()).createElement("version");
                            dependency.appendChild(versionNode);
                        }

                        versionNode.setTextContent(version);
                    }
                });
            }));
        }));
    }
}
