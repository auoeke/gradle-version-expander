package net.auoeke.versionexpander;

import groovy.lang.Closure;
import java.util.function.Predicate;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleIdentifier;

public class VersionExpansionExtension {
    public Predicate<ExpansionContext> predicate = version -> true;

    public void include(Predicate<ExpansionContext> predicate) {
        this.predicate = this.predicate.or(predicate);
    }

    public void include(Closure<Boolean> predicate) {
        this.include(predicate::call);
    }

    public void exclude(Predicate<ExpansionContext> predicate) {
        this.predicate = this.predicate.and(predicate.negate());
    }

    public void exclude(Closure<Boolean> predicate) {
        this.exclude(predicate::call);
    }

    public boolean test(Configuration configuration, ModuleIdentifier module, String version, String resolvedVersion) {
        return this.predicate.test(new ExpansionContext(configuration, module, version, resolvedVersion));
    }
}
