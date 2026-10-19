package com.lion.villagersplus.config;


/// Based on OmegaConfig by Draylar.
/// https://github.com/Draylar/omega-config
@SuppressWarnings({"unchecked"})
public interface Config {

    default void save() {
        OmegaConfig.writeConfig((Class<Config>) getClass(), this);
    }

    default void validate() {
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

