package com.example.xyzreader.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class FullScreenImageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String ARG_TITLE = "title";
    private static final String ARG_AUTHOR = "author";
    private static final String ARG_DATE = "date";
    private static final String ARG_IMAGE_URL = "image_url";
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private RelativeLayout mRelativeLayoutControls;
    private TextView mTxtTitle, mTxtAuthor, mTxtDate;
    private boolean mVisible;
    private ImageView mImgViewPhoto;
    private ProgressBar mProgressBarLoading;
    private TextView mTxtError;
    private ImageButton mImgBtnClose;
    private FrameLayout mFrameLayoutFullScreen;

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private final Runnable mShowDelayedRunnable = new Runnable() {
        @Override
        public void run() {
            mRelativeLayoutControls.setVisibility(View.VISIBLE);
        }
    };

    private final Runnable mRemoveDelayedRunnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mFrameLayoutFullScreen.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public static void startActivity(Context context,
                                     String title,
                                     String author,
                                     String date,
                                     String imgUrl) {
        Intent intent = new Intent(context, FullScreenImageActivity.class);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_AUTHOR, author);
        intent.putExtra(ARG_DATE, date);
        intent.putExtra(ARG_IMAGE_URL, imgUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        bindViews();
        init();
        show();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.image_button_close:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void bindViews() {
        mRelativeLayoutControls = (RelativeLayout) findViewById(R.id.relative_layout_controls);
        mTxtTitle = (TextView) findViewById(R.id.text_view_title);
        mTxtAuthor = (TextView) findViewById(R.id.text_view_author);
        mImgViewPhoto = (ImageView) findViewById(R.id.image_view_photo);
        mProgressBarLoading = (ProgressBar) findViewById(R.id.progress_bar_image_loading);
        mTxtError = (TextView) findViewById(R.id.text_view_error);
        mImgBtnClose = (ImageButton) findViewById(R.id.image_button_close);
        mFrameLayoutFullScreen = (FrameLayout) findViewById(R.id.frame_layout_full_screen);
        mTxtDate = (TextView) findViewById(R.id.text_view_date);
    }

    private void init() {
        mImgBtnClose.setOnClickListener(this);
        mFrameLayoutFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        mImgBtnClose.setOnTouchListener(mDelayHideTouchListener);

        Picasso.with(this)
                .load(getIntent().getExtras().getString(ARG_IMAGE_URL))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mImgViewPhoto.setImageBitmap(bitmap);
                        mProgressBarLoading.setVisibility(View.GONE);
                        mTxtError.setVisibility(View.GONE);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        mProgressBarLoading.setVisibility(View.GONE);
                        mTxtError.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        // set other data
        mTxtTitle.setText(getIntent().getExtras().getString(ARG_TITLE));
        mTxtAuthor.setText(getIntent().getExtras().getString(ARG_AUTHOR));

        mTxtDate.setText(DateUtils.getRelativeTimeSpanString(
                Long.parseLong(getIntent().getExtras().getString(ARG_DATE)),
                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL).toString());

        // set fonts
        mTxtTitle.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                "OpenSans-Bold.ttf"));
        mTxtAuthor.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                "OpenSans-Regular.ttf"));
        mTxtDate.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                "OpenSans-Regular.ttf"));
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mFrameLayoutFullScreen.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        mHideHandler.removeCallbacks(mRemoveDelayedRunnable);
        mHideHandler.postDelayed(mShowDelayedRunnable, UI_ANIMATION_DELAY);
    }

    private void hide() {
        mRelativeLayoutControls.setVisibility(View.GONE);
        mVisible = false;

        mHideHandler.removeCallbacks(mShowDelayedRunnable);
        mHideHandler.postDelayed(mRemoveDelayedRunnable, UI_ANIMATION_DELAY);
    }


    private void delayedHide(int delayMilliSeconds) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMilliSeconds);
    }

}
