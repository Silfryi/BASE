package com.acronym.base.reference;

import java.io.File;

public class Reference {
 
    public final static String MODID = "base";
    public final static String NAME = "BASE";
    public static final String MINECRAFT_VERSION = "1.10";

    static final String VERSION_MAJOR = "0";
    static final String VERSION_MINOR = "0";
    static final String VERSION_PATCH = "0";

    public static final String BUILD_VERSION = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_PATCH + "-" + MINECRAFT_VERSION;

    public static TabBase tab;

    public static File CONFIG_DIR;

} 
