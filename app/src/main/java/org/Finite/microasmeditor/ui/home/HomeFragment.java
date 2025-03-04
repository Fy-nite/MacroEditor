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
import org.Finite.microasmeditor.ui.editor.EditorManager;
import android.text.Editable;
import android.text.TextWatcher;

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

        // Sync with EditorManager
        EditorManager.getInstance().getActiveContent().observe(getViewLifecycleOwner(), content -> {
            if (!editor.getText().toString().equals(content)) {
                editor.setText(content);
            }
        });

        // Update EditorManager when text changes
        editor.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                EditorManager.getInstance().setContent(s.toString());
            }
        });

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