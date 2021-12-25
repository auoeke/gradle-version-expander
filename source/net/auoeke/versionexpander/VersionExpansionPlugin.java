package net.auoeke.versionexpander;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ExternalDependency;
import org.gradle.api.artifacts.ModuleIdentifier;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.plugins.internal.DefaultAdhocSoftwareComponent;
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven;
import org.gradle.util.internal.GUtil;

@SuppressWarnings("unused")
public class VersionExpansionPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        var extension = project.getExtensions().create("versionExpansion", VersionExpansionExtension.class);

        project.getTasks().withType(AbstractPublishToMaven.class).all(task -> project.getComponents().withType(SoftwareComponentInternal.class).all(component -> GUtil.uncheckedCall(() -> {
            var field = DefaultAdhocSoftwareComponent.class.getDeclaredField("variants");
            field.trySetAccessible();

            return (Map<Configuration, ?>) field.get(component);
        }).keySet().forEach(variant -> {
            var configurations = new HashMap<Configuration, Map<ModuleIdentifier, String>>();

            variant.getHierarchy().forEach(configuration -> project.getConfigurations().stream()
                .filter(c -> c.isCanBeResolved() && !c.getName().equals(Dependency.DEFAULT_CONFIGURATION) && c.getHierarchy().contains(configuration))
                .forEach(c -> configurations.computeIfAbsent(c, c1 -> c1.getResolvedConfiguration().getFirstLevelModuleDependencies().stream()
                    .map(dependency -> dependency.getModule().getId())
                    .collect(Collectors.toMap(ModuleVersionIdentifier::getModule, ModuleVersionIdentifier::getVersion))
                ))
            );

            configurations.forEach((configuration, versions) -> {
                variant.getAllDependencies().forEach(dependency -> {
                    if (dependency instanceof ExternalDependency external) {
                        var version = versions.get(external.getModule());

                        if (version != null && extension.test(configuration, external.getModule(), dependency.getVersion(), version)) {
                            external.version(constraint -> constraint.require(version));
                        }
                    }
                });

                variant.getAllDependencyConstraints().forEach(constraint -> {
                    var version = versions.get(constraint.getModule());

                    if (version != null && extension.test(configuration, constraint.getModule(), constraint.getVersion(), version)) {
                        constraint.version(versionConstraint -> versionConstraint.require(version));
                    }
                });
            });
        })));
    }
}
