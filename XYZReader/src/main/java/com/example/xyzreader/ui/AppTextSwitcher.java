package com.example.xyzreader.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.example.xyzreader.R;

public class AppTextSwitcher extends TextSwitcher {

    private String mText;
    private int mTextAppearance;

    public AppTextSwitcher(Context context) {
        super(context);
        initializeView();
    }

    public AppTextSwitcher(Context context, AttributeSet attributes) {
        super(context, attributes);
        initializeAttributes(attributes);
        initializeView();
    }

    public void setSwitcherText(String text) {
        mText = text;
        setText(text);
    }

    private ViewFactory mViewFactory = new ViewFactory() {
        @SuppressWarnings("deprecation")
        @Override
        public View makeView() {
            TextView textView = new TextView(getContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextAppearance(mTextAppearance);
            } else {
                textView.setTextAppearance(getContext(), mTextAppearance);
            }
            textView.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                    "OpenSans-Bold.ttf"));
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            return textView;
        }
    };

    private void initializeAttributes(AttributeSet attributeSet) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attributeSet, R.styleable.AppTextSwitcher, 0, 0);
        try {
            mText = typedArray.getString(R.styleable.AppTextSwitcher_switcherText);
            mTextAppearance = typedArray.getResourceId(R.styleable.AppTextSwitcher_switcherTextAppearance, android.R.style.TextAppearance_Medium);
        } finally {
            typedArray.recycle();
        }
    }

    private void initializeView() {
        setFactory(mViewFactory);

        Animation in = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.fade_out);
        setInAnimation(in);
        setOutAnimation(out);

        setCurrentText(mText);
    }
}
