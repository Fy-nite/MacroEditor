package org.Finite.microasmeditor.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.Finite.microasmeditor.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private EditText editor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        editor = binding.editor;



        // Save text changes to ViewModel
        editor.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                homeViewModel.setText(s.toString());
            }
        });

        editor.setText("""
                #include "stdio.io"
                lbl main
                    ;; move for file discriptor
                    mov RAX 1
                    ;; set the location in memory for printf
                    mov RBX 100
                    db $100 "Hello, World!\\n"
                    ;; call printf
                    call #printf
                    hlt
                """);
//        editor.setSelection(editor.getText().length());
//
//        homeViewModel.getText().observe(getViewLifecycleOwner(), text -> {
//            editor.setText(text);
//        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public String getText() {
        return editor != null ? editor.getText().toString() : "";
    }

    public void setText(String text) {
        if (editor != null) {
            editor.setText(text);
        }
    }

    public EditText getEditor() {
        return editor;
    }
}