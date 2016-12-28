package com.teamacronymcoders.base.savesystem;

import com.google.gson.Gson;
import com.teamacronymcoders.base.util.ClassLoading;
import com.teamacronymcoders.base.util.files.BaseFileUtils;

import java.io.File;

public class SaveLoader {
    private static File configFolder;
    private static File saveFolder;
    private static Gson gson;

    public static <T> T getSavedObject(String name, Class<T> clazz) {
        createSaveFolder();
        File savedFile = new File(saveFolder, name);
        T savedObject = null;
        if(savedFile.exists()) {
            String json = BaseFileUtils.readFileToString(savedFile);
            if(json != null) {
                savedObject = gson.fromJson(json, clazz);
            }
        }
        if(savedObject == null) {
            savedObject = ClassLoading.createObjectInstance(clazz);
        }
        return savedObject;
    }

    public static <T> void saveObject(String name, T object) {
        createSaveFolder();
        File fileToSaveTo = new File(saveFolder, name);
        String json = gson.toJson(object);
        BaseFileUtils.writeStringToFile(json, fileToSaveTo);
    }

    public static void createSaveFolder() {
        saveFolder = new File(configFolder, "saved");
        gson = new Gson();

        BaseFileUtils.createFolder(saveFolder);
    }

    public static void setConfigFolder(File folder) {
        configFolder = folder;
    }
}