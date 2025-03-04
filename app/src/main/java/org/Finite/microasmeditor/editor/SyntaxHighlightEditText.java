package org.Finite.microasmeditor.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import org.Finite.microasmeditor.R;
import org.Finite.microasmeditor.settings.SettingsManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxHighlightEditText extends EditText {
    private final Paint lineNumberPaint;
    private final int lineNumberPadding;
    private final Rect lineNumberRect;

    // Syntax patterns
    private static final Pattern KEYWORD_PATTERN = Pattern.compile("\\b(mov|add|sub|mul|div|and|or|xor|jmp|je|jne|push|pop)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern REGISTER_PATTERN = Pattern.compile("\\b(ax|bx|cx|dx|si|di|sp|bp)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\b\\d+\\b|\\b0x[0-9a-fA-F]+\\b");
    private static final Pattern COMMENT_PATTERN = Pattern.compile(";.*$", Pattern.MULTILINE);
    private static final Pattern LABEL_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*:", Pattern.MULTILINE);

    public SyntaxHighlightEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        // Setup line number paint
        lineNumberPaint = new Paint();
        lineNumberPaint.setColor(ContextCompat.getColor(context, R.color.comment));
        lineNumberPaint.setTextSize(getTextSize());
        lineNumberPaint.setTextAlign(Paint.Align.RIGHT);
        
        lineNumberPadding = (int) (getTextSize() * 2);
        lineNumberRect = new Rect();
        
        // Set padding for line numbers
        setPadding(lineNumberPadding, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        
        // Add syntax highlighting
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                highlightSyntax(s);
            }
        });

        applySettings();
        
        // Listen for settings changes
        SettingsManager.getInstance(context).registerOnSharedPreferenceChangeListener(
            (prefs, key) -> applySettings()
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int lineCount = getLineCount();
        int lineHeight = getLineHeight();
        int paddingTop = getPaddingTop();
        
        // Draw line numbers
        for (int i = 0; i < lineCount; i++) {
            int baseline = lineHeight * (i + 1) + paddingTop;
            canvas.drawText(String.valueOf(i + 1), lineNumberPadding - 10, baseline, lineNumberPaint);
        }
        
        super.onDraw(canvas);
    }

    private void highlightSyntax(Editable editable) {
        // Clear existing spans
        ForegroundColorSpan[] spans = editable.getSpans(0, editable.length(), ForegroundColorSpan.class);
        for (ForegroundColorSpan span : spans) {
            editable.removeSpan(span);
        }

        // Set default text color
        editable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.text)), 
                         0, editable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply syntax highlighting
        highlightPattern(editable, KEYWORD_PATTERN, R.color.keyword);
        highlightPattern(editable, REGISTER_PATTERN, R.color.register);
        highlightPattern(editable, NUMBER_PATTERN, R.color.number);
        highlightPattern(editable, COMMENT_PATTERN, R.color.comment);
        highlightPattern(editable, LABEL_PATTERN, R.color.label);
    }

    private void highlightPattern(Editable editable, Pattern pattern, int colorResId) {
        Matcher matcher = pattern.matcher(editable);
        int color = ContextCompat.getColor(getContext(), colorResId);
        while (matcher.find()) {
            editable.setSpan(new ForegroundColorSpan(color), 
                            matcher.start(), matcher.end(), 
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void applySettings() {
        SettingsManager settings = SettingsManager.getInstance(getContext());
        setTextSize(settings.getFontSize());
        
        // Show/hide line numbers
        if (!settings.showLineNumbers()) {
            setPadding(getPaddingRight(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        } else {
            setPadding(lineNumberPadding, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }
        
        invalidate(); // Redraw the view
    }
}
