package hwdroid.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.hw.droid.R;

import hwdroid.widget.ActionBar.ActionBarView;
import hwdroid.widget.ActionBar.ActionBarView.OnBackKeyItemClick;
import hwdroid.widget.ActionBar.ActionBarView.OnLeftWidgetItemClick;
import hwdroid.widget.ActionBar.ActionBarView.OnLeftWidgetItemClick2;
import hwdroid.widget.ActionBar.ActionBarView.OnOptionMenuClick;
import hwdroid.widget.ActionBar.ActionBarView.OnRightWidgetItemClick;
import hwdroid.widget.ActionBar.ActionBarView.OnRightWidgetItemClick2;
import hwdroid.widget.ActionBar.ActionBarView.OnSecondRightWidgetItemClick2;
import hwdroid.widget.ActionBar.ActionBarView.OnTitle2ItemClick;
import hwdroid.widget.FooterBar.FooterBar;

/**
 * <p>
 * an abstract class extend from activity.
 * </p>
 * help the custom activity create headerbar and footerbar .
 * <p/>
 */
public abstract class HWActivity extends Activity implements HWActivityInterface {

    private ActionBarView mActionBarView;
    private MainViewHost mMainHost;

    public HWActivity() {
        super();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        ensureLayout();
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = this.getActionBar();

        if (null != actionBar) {
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            mActionBarView = new ActionBarView(this);
            setActionBarView(mActionBarView);
//	        mActionBarView.setBackgroundResource(R.drawable.hw_actionbar_background);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ensureLayout();
    }

    /**
     * built in layout.
     * <p>
     * Noto: custom activity don't rewrite this funcation.
     * </p>
     */
    public int createLayout() {
        return R.layout.hw_content_normal;
    }

    /**
     * Call this method to ensure a layout has already been inflated and
     * attached to the top-level View of this Activity.
     */
    public void ensureLayout() {
        if (!verifyLayout()) {
            setContentView(createLayout());
        }
    }

    /**
     * Verify the given layout contains everything needed by this Activity. A
     * HWActivity, for instance, manages an {@link ActionBarHost}. As a result
     * this method will return true of the current layout contains such a
     * widget.
     *
     * @return true if the current layout fits to the current Activity widgets
     * requirements
     */
    protected boolean verifyLayout() {
        return mMainHost != null;
    }


    @Override
    public void onContentChanged() {
        super.onContentChanged();

        onPreContentChanged();
        onPostContentChanged();
    }

    public void onPreContentChanged() {
        mMainHost = (MainViewHost) findViewById(R.id.hw_action_bar_host);
        if (mMainHost == null) {
            throw new RuntimeException("<HWDroid> hasn't R.id.hw_action_bar_host");
        }
    }

    public void onPostContentChanged() {

        boolean titleSet = false;

        final Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra(HWActivityInterface.HW_ACTION_BAR_TITLE);
            if (title != null) {
                titleSet = true;
                setTitle2(title);
            }
        }

        if (!titleSet) {
            // No title has been set via the Intent. Let's look in the
            // ActivityInfo
            try {
                final ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), 0);
                if (activityInfo.labelRes != 0) {
                    setTitle2(this.getResources().getText(activityInfo.labelRes));
                }
            } catch (NameNotFoundException e) {
                // Do nothing
            }
        }

        //getWindow().getDecorView().setBackgroundResource(R.drawable.hw_content_view_background);
    }

    /**
     * set actionbarview title.
     *
     * @param title
     */
    @Override
    public void setTitle2(CharSequence title) {
        ensureLayout();
        if (mActionBarView != null) {
            mActionBarView.setTitle(title);
        }
    }

    /**
     * set actionbarview title.
     * if you want listnenr the click event, pls set OnTitle2ItemClick.
     *
     * @param title
     */
    @Override
    public void setTitle2(CharSequence title, OnTitle2ItemClick click) {
        ensureLayout();
        if (mActionBarView != null) {
            mActionBarView.setTitle(title);
            mActionBarView.setOnTitle2ItemClickListener(click);
        }
    }

    /**
     * set actionbarview sub title.
     *
     * @param title
     */
    @Override
    public void setSubTitle2(CharSequence title) {
        ensureLayout();
        if (mActionBarView != null) {
            mActionBarView.setSubTitle(title);
        }
    }

    /**
     * set actionbar custom view.
     *
     * @param view
     * @param params
     */
    public void setActionBarView(ActionBarView view, ActionBar.LayoutParams params) {
        if (getActionBar() != null) {
            getActionBar().setCustomView(view, params);
        } else {
            mActionBarView = view;
        }
    }

    /**
     * set action bar custom view by the default params.
     *
     * @param view
     */
    public void setActionBarView(ActionBarView view) {
        setActionBarView(view, new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /**
     * get activity view's contentview.
     *
     * @return return the top-level view instance.
     */
    public FrameLayout getContentView() {
        ensureLayout();
        return mMainHost.getContentView();
    }

    /**
     * get ActionBarView view. user the view instance, you can set any custom view.
     */
    public ActionBarView getActionBarView() {
        return mActionBarView;
    }

    /**
     * get footerbar view. user the view instance, you can set any custom view.
     *
     * @return return {@line FooterBar} view.
     */
    public FooterBar getFooterBarImpl() {
        return mMainHost.getFooterBarImpl();
    }

    /**
     * <p>
     * Set the activity content from a layout resource. The resource will be
     * inflated, adding all top-level views to the activity.
     * </p>
     * <p>
     * This method is an equivalent to setContentView(int) that automatically
     * wraps the given layout in an {@link ActionBarHost} if needed..
     * </p>
     *
     * @param resID Resource ID to be inflated.
     * @see #setActionBarContentView(android.view.View)
     * @see #setActionBarContentView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    public void setActivityContentView(int resID) {
        final FrameLayout contentView = getContentView();
        contentView.removeAllViews();
        LayoutInflater.from(this).inflate(resID, contentView);
    }

    /**
     * <p>
     * Set the activity content to an explicit view. This view is placed
     * directly into the activity's view hierarchy. It can itself be a complex
     * view hierarchy.
     * </p>
     * <p>
     * This method is an equivalent to setContentView(View, LayoutParams) that
     * automatically wraps the given layout in an {@link ActionBarHost} if
     * needed.
     * </p>
     *
     * @param view   The desired content to display.
     * @param params Layout parameters for the view.
     * @see #setActionBarContentView(android.view.View)
     * @see #setActionBarContentView(int)
     */
    public void setActivityContentView(View view, LayoutParams params) {
        final FrameLayout contentView = getContentView();
        contentView.removeAllViews();
        contentView.addView(view, params);
    }

    /**
     * <p>
     * Set the activity content to an explicit view. This view is placed
     * directly into the activity's view hierarchy. It can itself be a complex
     * view hierarchy.
     * </p>
     * <p>
     * This method is an equivalent to setContentView(View) that automatically
     * wraps the given layout in an {@link ActionBarHost} if needed.
     * </p>
     *
     * @param view The desired content to display.
     * @see #setActionBarContentView(int)
     * @see #setActionBarContentView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    public void setActivityContentView(View view) {
        final FrameLayout contentView = getContentView();
        contentView.removeAllViews();
        contentView.addView(view);
    }

    /**
     * set actionbar backgroud.
     * if you don't set backgroud. the backgroud will be default.
     *
     * @param resId Resource Id to be Inflated.
     */
    @SuppressWarnings("deprecation")
    public void setActionBarBackgroudResource(int resId) {
        Drawable d = this.getResources().getDrawable(resId);
        mActionBarView.setBackgroundDrawable(d);
        //getWindow().getDecorView().setBackgroundDrawable(d);
    }

    /**
     * set the back widget's shown.
     * if true, will set the back widget visible.
     * if false, will set the back widget gone.
     * if you call showBackKey() and setLeftWidgetView() together, will show the laster widget.
     *
     * @param show
     */
    @Override
    public void showBackKey(boolean show) {
        if (mActionBarView == null) return;

        mActionBarView.showBackKey(show, new OnBackKeyItemClick() {

            @Override
            public void onBackKeyItemClick() {
                onBackKey();
            }
        });
    }

    /**
     * handle the backkey event when user pressed the actionbarview's backkey.
     * if you want to handle the backkey event. you can override the onBackKey().
     * if you don't override the function. will call finish() by default.
     */
    @Override
    public void onBackKey() {
        finish();
    }

    /**
     * set custom view on the left side of {@link ActionBarView}.
     */
    @Override
    public void setLeftWidgetView(View v) {
        if (mActionBarView != null && v != null) {
            mActionBarView.addLeftItem(v);
        }
    }

    /**
     * set custom view on the right side of {@link ActionBarView}.
     */
    @Override
    public void setRightWidgetView(View v) {
        if (mActionBarView != null && v != null) {
            mActionBarView.addRightItem(v);
        }
    }

    /**
     * set the second<count from right> custom view on the right side of {@link ActionBarView}.
     */
    @Override
    public void setSecondRightWidgetView(View v) {
        if (mActionBarView != null && v != null) {
            mActionBarView.addSecondRightItem(v);
        }
    }

    /**
     * set custom view on the right side of {@link ActionBarView}.
     */
    @Override
    public void setRightWidgetView(View v, boolean clickable) {
        if (mActionBarView != null && v != null) {
            mActionBarView.addRightItem(v, clickable);
        }
    }

    /**
     * set the second<count from right> custom view on the right side of {@link ActionBarView}.
     */
    @Override
    public void setSecondRightWidgetView(View v, boolean clickable) {
        if (mActionBarView != null && v != null) {
            mActionBarView.addSecondRightItem(v, clickable);
        }
    }

    /**
     * remove view on the left side of {@link ActionBarView}.
     */
    @Override
    public void removeLeftWidgetView() {
        if (mActionBarView == null) return;
        mActionBarView.removeLeftItem();
    }

    /**
     * remove view on the right side of {@link ActionBarView}.
     */
    @Override
    public void removeRightWidgetView() {
        if (mActionBarView == null) return;
        mActionBarView.removeRightItem();
    }

    /**
     * remove the second<count from right> custom view on the right side of {@link ActionBarView}.
     */
    @Override
    public void removeSecondRightWidgetView() {
        if (mActionBarView == null) return;
        mActionBarView.removeSecondRightItem();
    }

    /**
     * set listener on the left side of {@link ActionBarView}
     *
     * @param click
     */
    @Override
    public void setLeftWidgetClickListener(OnLeftWidgetItemClick click) {
        if (mActionBarView == null) return;
        mActionBarView.setOnLeftWidgetItemClickListener(click);
    }

    /**
     * set listener on the right side of {@link ActionBarView}
     *
     * @param click
     */
    @Override
    public void setRightWidgetClickListener(OnRightWidgetItemClick click) {
        if (mActionBarView == null) return;
        mActionBarView.setOnRightWidgetItemClickListener(click);
    }

    /**
     * set listener on the left side of {@link ActionBarView}
     *
     * @param click
     */
    @Override
    public void setLeftWidgetClickListener2(OnLeftWidgetItemClick2 click) {
        if (mActionBarView == null) return;
        mActionBarView.setOnLeftWidgetItemClickListener2(click);
    }

    /**
     * set listener on the right side of {@link ActionBarView}
     *
     * @param click
     */
    @Override
    public void setRightWidgetClickListener2(OnRightWidgetItemClick2 click) {
        if (mActionBarView == null) return;
        mActionBarView.setOnRightWidgetItemClickListener2(click);
    }

    /**
     * set listener on the second<count from right> custom view on right side of {@link ActionBarView}
     *
     * @param click
     */
    @Override
    public void setSecondRightWidgetClickListener2(OnSecondRightWidgetItemClick2 click) {
        if (mActionBarView == null) return;
        mActionBarView.setOnSecondRightWidgetItemClickListener2(click);
    }

    /**
     * set option title.
     *
     * @param title
     */
    @Override
    public void setOptionTitle2(CharSequence title) {
        ensureLayout();
        if (mActionBarView != null) {
            mActionBarView.setOptionTitle(title);
        }
    }

    /**
     * set option items, the items will be shown on the popup view.
     *
     * @param optionItem
     * @param click
     */
    @Override
    public void setOptionItems(CharSequence[] optionItem, OnOptionMenuClick click) {
        ensureLayout();
        if (mActionBarView != null) {
            mActionBarView.setOptionItems(optionItem, click);
        }
    }

    /**
     * setting enable status to the left side of {@link ActionBarView}
     *
     * @param enabled
     */
    @Override
    public void setLeftWidgetItemEnabled(boolean enabled) {
        if (mActionBarView != null) {
            mActionBarView.setLeftWidgetItemEnable(enabled);
        }
    }

    /**
     * setting enable status to the right side of {@link ActionBarView}
     *
     * @param enabled
     */
    @Override
    public void setRightWidgetItemEnabled(boolean enabled) {
        if (mActionBarView != null) {
            mActionBarView.setRightWidgetItemEnable(enabled);
        }
    }

    /**
     * setting enable status of the second<count from right> custom view on right side of {@link ActionBarView}
     *
     * @param enabled
     */
    @Override
    public void setSecondRightWidgetItemEnabled(boolean enabled) {
        if (mActionBarView != null) {
            mActionBarView.setSecondRightWidgetItemEnable(enabled);
        }
    }

    /**
     * get enable status of the left widgt.
     *
     * @return true if enabled equal true.
     */
    @Override
    public boolean isLeftWidgetItemEnabled() {
        if (mActionBarView != null) {
            return mActionBarView.isLeftWidgetItemEnabled();
        }

        return false;
    }

    /**
     * get enable status of the right widget.
     *
     * @return true if enabled equal true.
     */
    @Override
    public boolean isRightWidgetItemEnabled() {
        if (mActionBarView != null) {
            return mActionBarView.isRightWidgetItemEnabled();
        }

        return false;
    }

    /**
     * get enable status of the second<count from right> right widget.
     *
     * @return true if enabled equal true.
     */
    @Override
    public boolean isSecondRightWidgetItemEnabled() {
        if (mActionBarView != null) {
            return mActionBarView.isSecondRightWidgetItemEnabled();
        }

        return false;
    }

}
