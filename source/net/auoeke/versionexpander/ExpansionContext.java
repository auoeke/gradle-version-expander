package net.auoeke.versionexpander;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleIdentifier;

public final class ExpansionContext {
    public final String version;
    public final String resolvedVersion;
    public final Configuration configuration;

    private final ModuleIdentifier module;

    public ExpansionContext(Configuration configuration, ModuleIdentifier module, String version, String resolvedVersion) {
        this.configuration = configuration;
        this.module = module;
        this.version = version;
        this.resolvedVersion = resolvedVersion;
    }

    public String getModule() {
        return this.module.toString();
    }

    public String getGroup() {
        return this.module.getGroup();
    }

    public String getName() {
        return this.module.getName();
    }
}
