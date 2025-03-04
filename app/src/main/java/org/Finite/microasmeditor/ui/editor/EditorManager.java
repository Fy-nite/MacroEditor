package org.Finite.microasmeditor.ui.editor;

import java.util.HashMap;
import java.util.Map;
import androidx.lifecycle.MutableLiveData;

public class EditorManager {
    private static EditorManager instance;
    private final Map<String, String> openFiles;
    private String currentFile;
    private final MutableLiveData<String> activeContent;

    private EditorManager() {
        openFiles = new HashMap<>();
        activeContent = new MutableLiveData<>("");
        currentFile = "untitled";
        openFiles.put(currentFile, "");
    }

    public static EditorManager getInstance() {
        if (instance == null) {
            instance = new EditorManager();
        }
        return instance;
    }

    public void setContent(String content) {
        openFiles.put(currentFile, content);
        activeContent.postValue(content);
    }

    public String getContent() {
        return openFiles.getOrDefault(currentFile, "");
    }

    public void createFile(String filename, String content) {
        openFiles.put(filename, content);
        currentFile = filename;
        activeContent.postValue(content);
    }

    public void switchFile(String filename) {
        if (openFiles.containsKey(filename)) {
            currentFile = filename;
            activeContent.postValue(openFiles.get(filename));
        }
    }

    public MutableLiveData<String> getActiveContent() {
        return activeContent;
    }

    public String getCurrentFileName() {
        return currentFile;
    }
}
