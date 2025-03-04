package org.Finite.microasmeditor.ui.console;

import androidx.lifecycle.MutableLiveData;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConsoleManager {
    private static ConsoleManager instance;
    private final MutableLiveData<String> consoleOutput;
    private final StringBuilder contentBuilder;

    private ConsoleManager() {
        consoleOutput = new MutableLiveData<>("");
        contentBuilder = new StringBuilder();
    }

    public static ConsoleManager getInstance() {
        if (instance == null) {
            instance = new ConsoleManager();
        }
        return instance;
    }

    public void println(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String formattedMessage = String.format("[%s] %s\n", timestamp, message);
        contentBuilder.append(formattedMessage);
        consoleOutput.postValue(contentBuilder.toString());
    }

    public void clear() {
        contentBuilder.setLength(0);
        consoleOutput.postValue("");
    }

    public MutableLiveData<String> getConsoleOutput() {
        return consoleOutput;
    }
}
