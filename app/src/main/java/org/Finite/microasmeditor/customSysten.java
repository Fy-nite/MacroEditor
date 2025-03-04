package org.Finite.microasmeditor;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class customSysten implements customSystem {
    @Override
    public void println(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String formattedMessage = String.format("[%s] %s\n", timestamp, message);
        Log.d("MASM:", formattedMessage);
    }
}
