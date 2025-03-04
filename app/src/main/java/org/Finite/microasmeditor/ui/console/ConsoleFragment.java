package org.Finite.microasmeditor.ui.console;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import org.Finite.microasmeditor.databinding.FragmentConsoleBinding;

public class ConsoleFragment extends Fragment {
    private FragmentConsoleBinding binding;
    private ConsoleViewModel consoleViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
        consoleViewModel = new ViewModelProvider(this).get(ConsoleViewModel.class);

        binding = FragmentConsoleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textConsole;
        consoleViewModel.getConsoleOutput().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
