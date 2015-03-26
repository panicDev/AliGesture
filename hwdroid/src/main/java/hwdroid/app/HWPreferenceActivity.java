package hwdroid.app;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.hw.droid.R;

import hwdroid.widget.ActionBar.ActionBarView;
import hwdroid.widget.ActionBar.ActionBarView.OnBackKeyItemClick;
import hwdroid.widget.ActionBar.ActionBarView.OnLeftWidgetItemClick;
import hwdroid.widget.ActionBar.ActionBarView.OnLeftWidgetItemClick2;
import hwdroid.widget.ActionBar.ActionBarView.OnOptionMenuClick;
import hwdroid.widget.ActionBar.ActionBarView.OnRightWidgetItemClick;
import hwdroid.widget.ActionBar.ActionBarView.OnRightWidgetItemClick2;

/**
 * a custom preference activity.
 * built in custom actionbar style.
 * <p/>
 * <p>
 * Must take the initiative to call the method, to initialize the actionbar.
 * </p>
 * <p>
 * follow:
 * </p>
 * <p>
 * initActionBar();
 * </p>
 * <p>
 * setTitle2("xxxxx");
 * </p>
 * <p>
 * this.showBackKey(true);
 * </p>
 */
public class HWPreferenceActivity extends PreferenceActivity {

    private ActionBarView mActionBarView;

    /**
     * init actionbar.
     */
    public void initActionBar() {
        ActionBar actionBar = this.getActionBar();

        if (null != actionBar) {
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            mActionBarView = new ActionBarView(this);

            actionBar.setCustomView(mActionBarView,
                    new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//	        mActionBarView.setBackgroundResource(R.drawable.hw_actionbar_background);
        }
    }

    /**
     * set title text
     *
     * @param title
     */
    public void setTitle2(CharSequence title) {
        if (mActionBarView != null) {
            mActionBarView.setTitle(title);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setPadding(0, 0, 0, 0);
    }

    /**
     * add preferences xml
     *
     * @param preferencesResId
     */
    @SuppressWarnings("deprecation")
    public void addPreferencesFromResourceImpl(int preferencesResId) {
        addPreferencesFromResource(preferencesResId);
    }

    /**
     * don't use
     *
     * @param view
     */
    public void setActionBarCustomView(View view) {
//    	ActionBar ab = getActionBar();
//        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
//                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME |
//                ActionBar.DISPLAY_SHOW_TITLE);
//
//        ab.setCustomView(view);
        //TODO: don't call android actionbar custom view. must call AcionBarView.
    }

    /**
     * set actionbar key status.
     *
     * @param show true: show, false: hide
     */
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
     * feedback after press backkey.
     */
    public void onBackKey() {
        finish();
    }

    /**
     * set left widget view.
     *
     * @param v custom view
     */
    public void setLeftWidgetView(View v) {
        if (mActionBarView != null && v != null) {
            mActionBarView.addLeftItem(v);
        }
    }

    /**
     * set right widget view
     *
     * @param v custom view
     */
    public void setRightWidgetView(View v) {
        if (mActionBarView != null && v != null) {
            mActionBarView.addRightItem(v);
        }
    }

    public void removeLeftWidgetView() {
        if (mActionBarView == null) return;

        mActionBarView.removeLeftItem();
    }

    public void removeRightWidgetView() {
        if (mActionBarView == null) return;

        mActionBarView.removeRightItem();
    }

    /**
     * set option title text.
     *
     * @param title
     */
    public void setOptionTitle2(CharSequence title) {
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
    public void setOptionItems(CharSequence[] optionItem, OnOptionMenuClick click) {
        if (mActionBarView != null) {
            mActionBarView.setOptionItems(optionItem, click);
        }
    }

    /**
     * set the enable status to left widget.
     *
     * @param enabled
     */
    public void setLeftWidgetItemEnable(boolean enabled) {
        if (mActionBarView != null) {
            mActionBarView.setLeftWidgetItemEnable(enabled);
        }
    }

    /**
     * set the enable status to right widget.
     *
     * @param enabled
     */
    public void setRightWidgetItemEnable(boolean enabled) {
        if (mActionBarView != null) {
            mActionBarView.setRightWidgetItemEnable(enabled);
        }
    }

    /**
     * set custom view on the right side of {@link ActionBarView}.
     */
    public void setRightWidgetView(View v, boolean clickable) {
        if (mActionBarView != null && v != null) {
            mActionBarView.addRightItem(v, clickable);
        }
    }

    /**
     * get left widget click's status
     *
     * @return true: enable, false: disable
     */
    public boolean getLeftWidgetItemEnabled() {
        if (mActionBarView != null) {
            return mActionBarView.isLeftWidgetItemEnabled();
        }

        return false;
    }

    /**
     * get right widget click's status
     *
     * @return true: enable, false: disable
     */
    public boolean getRightWidgetItemEnable() {
        if (mActionBarView != null) {
            return mActionBarView.isRightWidgetItemEnabled();
        }

        return false;
    }

    /**
     * set listener on the left side of {@link ActionBarView}
     *
     * @param click
     */
    public void setLeftWidgetClickListener(OnLeftWidgetItemClick click) {
        if (mActionBarView == null) return;
        mActionBarView.setOnLeftWidgetItemClickListener(click);
    }

    /**
     * set listener on the right side of {@link ActionBarView}
     *
     * @param click
     */
    public void setRightWidgetClickListener(OnRightWidgetItemClick click) {
        if (mActionBarView == null) return;
        mActionBarView.setOnRightWidgetItemClickListener(click);
    }

    /**
     * set listener on the left side of {@link ActionBarView}
     *
     * @param click
     */
    public void setLeftWidgetClickListener2(OnLeftWidgetItemClick2 click) {
        if (mActionBarView == null) return;
        mActionBarView.setOnLeftWidgetItemClickListener2(click);
    }

    /**
     * set listener on the right side of {@link ActionBarView}
     *
     * @param click
     */
    public void setRightWidgetClickListener2(OnRightWidgetItemClick2 click) {
        if (mActionBarView == null) return;
        mActionBarView.setOnRightWidgetItemClickListener2(click);
    }
}
