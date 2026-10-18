package com.lion.villagersplus.config;

/// Adapted from omega-config by Draylar: https://github.com/Draylar/omega-config
@SuppressWarnings({"unchecked"})
public interface Config {

    default void save() {
        OmegaConfig.writeConfig((Class<Config>) getClass(), this);
    }

    String getName();

    default String getModid() {
        return null;
    }

    default String getExtension() {
        return "json";
    }

    default String getDirectory() {
        return "";
    }
}

