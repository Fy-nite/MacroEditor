package org.Finite.microasmeditor.ui.console;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ConsoleViewModel extends ViewModel {
    private final ConsoleManager consoleManager;

    public ConsoleViewModel() {
        consoleManager = ConsoleManager.getInstance();
    }

    public LiveData<String> getConsoleOutput() {
        return consoleManager.getConsoleOutput();
    }

    public void clearConsole() {
        consoleManager.clear();
    }
}
