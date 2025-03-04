package org.Finite.microasmeditor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import org.Finite.microasmeditor.databinding.ActivityMainBinding;
import org.Finite.microasmeditor.ui.home.HomeFragment;
import android.widget.Toast;
import java.io.*;
import org.finite.*;
import org.finite.Common.common;
import android.util.Log;
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private ActivityResultLauncher<String[]> openFileLauncher;
    private ActivityResultLauncher<String> saveFileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        
        // Setup file launchers
        setupFileLaunchers();

        // Setup button click listeners
        binding.appBarMain.btnSave.setOnClickListener(view -> saveFile());
        binding.appBarMain.btnRun.setOnClickListener(view -> {
            try {
                runCode();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        binding.appBarMain.btnInsertString.setOnClickListener(view -> insertString());
        binding.appBarMain.btnInsertLabel.setOnClickListener(view -> insertLabel());

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setupFileLaunchers() {
        openFileLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    readFile(uri);
                }
            }
        );

        saveFileLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("text/plain"),
            uri -> {
                if (uri != null) {
                    writeFile(uri);
                }
            }
        );
    }

    private void readFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            HomeFragment homeFragment = getCurrentHomeFragment();
            if (homeFragment != null) {
                homeFragment.setText(content.toString());
            }
            inputStream.close();
        } catch (IOException e) {
            Toast.makeText(this, "Error reading file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void writeFile(Uri uri) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            HomeFragment homeFragment = getCurrentHomeFragment();
            if (homeFragment != null && outputStream != null) {
                outputStream.write(homeFragment.getText().getBytes());
                outputStream.close();
                Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error saving file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private HomeFragment getCurrentHomeFragment() {
        // Get the NavHostFragment first
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        if (navHostFragment != null) {
            // Get the current fragment from the NavHostFragment
            Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
            if (currentFragment instanceof HomeFragment) {
                return (HomeFragment) currentFragment;
            }
        }
        Toast.makeText(this, "Error: Could not access editor", Toast.LENGTH_SHORT).show();
        return null;
    }

    private void saveFile() {
        saveFileLauncher.launch("document.txt");
    }

    private void runCode() throws IOException {
        HomeFragment homeFragment = getCurrentHomeFragment();
        if (homeFragment != null) {
            String code = homeFragment.getText();
            if (code != null && !code.isEmpty()) {

                Toast.makeText(this, "Running code: " + code.substring(0, Math.min(20, code.length())) + "...", 
                             Toast.LENGTH_SHORT).show();
                common.exitOnHLT = false;
                File stdout = common.WrapStdoutToFile(); // use this later

                // get the current open file
                String content = homeFragment.getText();
                File tempfile = File.createTempFile("temp", ".asm");
                FileWriter writer = new FileWriter(tempfile);
                writer.write(content);
                writer.close();
                String tempfilename = tempfile.getAbsolutePath();

                interp.runFile(tempfilename); // hmmm this thing sucks
                // spit out the contents of the stdout over the debugger
                // delete the temp file
                //tempfile.delete();

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(stdout));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Log.d("MASMOUTPUT", line);
                    }
                }
                catch (IOException e) {
                    Toast.makeText(this, "Error reading stdout: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                finally {
                    common.UnwrapStdout();
                }
            } else {
                Toast.makeText(this, "No code to run", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void insertString() {
        HomeFragment homeFragment = getCurrentHomeFragment();
        if (homeFragment != null) {
            EditText editor = homeFragment.getEditor();
            if (editor != null) {
                String template = "db $100 \"Your string here\\n\"";
                int currentPosition = editor.getSelectionStart();
                String currentText = homeFragment.getText();
                String newText = currentText.substring(0, currentPosition) + template + 
                               currentText.substring(currentPosition);
                homeFragment.setText(newText);
            }
        }
    }

    private void insertLabel() {
        HomeFragment homeFragment = getCurrentHomeFragment();
        if (homeFragment != null) {
            EditText editor = homeFragment.getEditor();
            if (editor != null) {
                String template = "lbl your_label";
                int currentPosition = editor.getSelectionStart();
                String currentText = homeFragment.getText();
                String newText = currentText.substring(0, currentPosition) + template + 
                               currentText.substring(currentPosition);
                homeFragment.setText(newText);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}