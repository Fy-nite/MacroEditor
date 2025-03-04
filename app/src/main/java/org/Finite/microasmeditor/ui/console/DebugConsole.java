package org.Finite.microasmeditor.ui.console;

import android.util.Log;
import java.io.OutputStream;
import java.io.PrintStream;

public class DebugConsole extends PrintStream {
    private static final String TAG = "MASMDebug";
    private final ConsoleManager consoleManager;
    private StringBuilder lineBuffer = new StringBuilder();

    public DebugConsole() {
        super(new OutputStream() {
            @Override
            public void write(int b) {
                // Dummy output stream
            }
        });
        this.consoleManager = ConsoleManager.getInstance();
    }

    @Override
    public void print(String s) {
        lineBuffer.append(s);
        // Also log to LogCat for debugging
        Log.d(TAG, s);
    }

    @Override
    public void println(String s) {
        lineBuffer.append(s);
        String fullLine = lineBuffer.toString();
        // Send to both our console and LogCat
        consoleManager.println(fullLine);
        Log.d(TAG, fullLine);
        lineBuffer = new StringBuilder();
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        String s = new String(buf, off, len);
        print(s);
    }

    @Override
    public void write(int b) {
        write(new byte[]{(byte) b}, 0, 1);
    }
}
