package net.auoeke.versionexpander;

import groovy.lang.Closure;
import java.util.function.Predicate;

public class VersionExpansionExtension {
    public Predicate<String> predicate = version -> true;

    public void include(Predicate<String> predicate) {
        this.predicate = this.predicate.or(predicate);
    }

    public void include(Closure predicate) {
        this.include(version -> (boolean) predicate.call(version));
    }

    public void include(String substring) {
        this.include(version -> version.contains(substring));
    }

    public void exclude(Predicate<String> predicate) {
        this.predicate = this.predicate.and(predicate.negate());
    }

    public void exclude(Closure predicate) {
        this.exclude(version -> (boolean) predicate.call(version));
    }

    public void exclude(String substring) {
        this.exclude(version -> version.contains(substring));
    }
}
