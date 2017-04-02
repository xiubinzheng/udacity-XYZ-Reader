package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.util.AppBarStateChangeListener;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private Cursor mCursor;
    private int mPosition = 0;
    private TextView mTxtTitle, mTxtAuthor, mTxtDate;
    private ImageView mImageViewPhoto;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private FrameLayout mFrameLayoutCollapsingToolbar;
    private AppBarLayout mAppBarLayout;
    private FloatingActionButton mFabShareButton;
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private Toolbar mToolbar;
    private AppTextSwitcher mSimpleTextSwitcherToolbarTitle;
    private boolean mMenuVisible = false;
    public static final String ARG_ITEM_POSITION = "item_position";
    private static final String TAG = "ArticleDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_detail);

        setViews();
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mPosition = getIntent().getExtras().getInt(ARG_ITEM_POSITION, 0);
            }
        }

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(final int position) {
                if (mSimpleTextSwitcherToolbarTitle.getAlpha() == 0.0) {

                    mCollapsingToolbarLayout.setVisibility(View.VISIBLE);
                    AlphaAnimation alphaAnimationFadeOut = new AlphaAnimation(1.0f, 0f);
                    alphaAnimationFadeOut.setDuration(1000);
                    mCollapsingToolbarLayout.startAnimation(alphaAnimationFadeOut);
                    mCollapsingToolbarLayout.setVisibility(View.INVISIBLE);

                    new Runnable() {
                        @Override
                        public void run() {
                            setTitleInformation(position);
                        }
                    }.run();

                    mCollapsingToolbarLayout.setVisibility(View.INVISIBLE);
                    AlphaAnimation alphaAnimationFadeIn = new AlphaAnimation(0f, 1.0f);
                    alphaAnimationFadeIn.setDuration(1000);
                    mCollapsingToolbarLayout.startAnimation(alphaAnimationFadeIn);
                    mCollapsingToolbarLayout.setVisibility(View.VISIBLE);


                } else {
                    setTitleInformation(position);
                }
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                switch (state) {
                    case COLLAPSED:
                        DisplayToolbarContents();
                        break;
                    case EXPANDED:
                        hideToolbarContents();
                        break;
                }
            }
        });

        mTxtTitle.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                "OpenSans-Bold.ttf"));
        mTxtAuthor.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                "OpenSans-Regular.ttf"));
        mTxtDate.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                "OpenSans-Regular.ttf"));

        mFabShareButton.setOnClickListener(this);
        mFrameLayoutCollapsingToolbar.setOnClickListener(this);
    }

    private void setViews() {
        mTxtTitle = (TextView) findViewById(R.id.text_view_title);
        mTxtAuthor = (TextView) findViewById(R.id.text_view_author);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mFabShareButton = (FloatingActionButton) findViewById(R.id.fab_share);
        mTxtDate = (TextView) findViewById(R.id.text_view_date);
        mImageViewPhoto = (ImageView) findViewById(R.id.image_view_photo);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mFrameLayoutCollapsingToolbar = (FrameLayout) findViewById(R.id.frame_layout_collapsing_toolbar);
        mToolbar = (Toolbar) findViewById(R.id.up_container);
        mSimpleTextSwitcherToolbarTitle = (AppTextSwitcher) findViewById(R.id.simple_text_switcher_toolbar_title);
        mPager = (ViewPager) findViewById(R.id.pager);
    }


    private void DisplayToolbarContents() {
        mMenuVisible = true;
        invalidateOptionsMenu();

        mFabShareButton.hide();

        mSimpleTextSwitcherToolbarTitle.animate()
                .alpha(1.0f)
                .setDuration(300)
                .start();
    }

    private void hideToolbarContents() {
        mMenuVisible = false;
        invalidateOptionsMenu();

        mFabShareButton.show();

        mSimpleTextSwitcherToolbarTitle.animate()
                .alpha(0.0f)
                .setDuration(300)
                .start();
    }

    private void setTitleInformation(int position) {
        mCursor.moveToPosition(position);
        Picasso.with(this)
                .load(mCursor.getString(ArticleLoader.Query.PHOTO_URL))
                .into(mImageViewPhoto);

        mCollapsingToolbarLayout.setTitle(mCursor.getString(ArticleLoader.Query.TITLE));
        mSimpleTextSwitcherToolbarTitle.setSwitcherText(mCursor.getString(ArticleLoader.Query.TITLE));

        mTxtTitle.setText(mCursor.getString(ArticleLoader.Query.TITLE));
        mTxtAuthor.setText(mCursor.getString(ArticleLoader.Query.AUTHOR));
        mTxtDate.setText(DateUtils.getRelativeTimeSpanString(
                mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL).toString());
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        setTitleInformation(mPosition);
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), cursor);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));
        mPager.setCurrentItem(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        if (!mMenuVisible) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        mCursor.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.fab_share:
                shareContent();
                break;
            case R.id.frame_layout_collapsing_toolbar:
                mCursor.moveToPosition(mPager.getCurrentItem());
                FullScreenImageActivity.startActivity(this,
                        mCursor.getString(ArticleLoader.Query.TITLE),
                        mCursor.getString(ArticleLoader.Query.AUTHOR),
                        mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE),
                        mCursor.getString(ArticleLoader.Query.PHOTO_URL));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_previous:
                setToPreviousItem();
                break;
            case R.id.action_next:
                setToNextItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setToPreviousItem() {
        if (mPager.getCurrentItem() != 0) {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
        }
    }

    private void setToNextItem() {
        if (mPager.getCurrentItem() != mPager.getAdapter().getCount() - 1) {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
        }
    }

    private void shareContent() {
        mCursor.moveToPosition(mPager.getCurrentItem());
        String title = mCursor.getString(ArticleLoader.Query.TITLE);
        String text = mCursor.getString(ArticleLoader.Query.BODY);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TITLE, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_using)));
        } else {
            Log.e(TAG, "No Intent available to handle action");
            Toast.makeText(this, R.string.no_app_available_to_share_content, Toast.LENGTH_LONG).show();
        }
    }

    private static class MyPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        private WeakReference<Cursor> mCursorWeakReference;

        public MyPagerAdapter(android.support.v4.app.FragmentManager fm, Cursor cursor) {
            super(fm);
            mCursorWeakReference = new WeakReference<Cursor>(cursor);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            mCursorWeakReference.get().moveToPosition(position);
            return ArticleDetailFragment.newInstance(mCursorWeakReference.get().getString(ArticleLoader.Query.BODY));
        }

        @Override
        public int getCount() {
            return mCursorWeakReference.get().getCount();
        }
    }
}
