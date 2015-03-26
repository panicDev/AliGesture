
package hwdroid.app;

import android.view.View;
import android.widget.FrameLayout;

import hwdroid.widget.ActionBar.ActionBarView;
import hwdroid.widget.ActionBar.ActionBarView.OnLeftWidgetItemClick;
import hwdroid.widget.ActionBar.ActionBarView.OnLeftWidgetItemClick2;
import hwdroid.widget.ActionBar.ActionBarView.OnOptionMenuClick;
import hwdroid.widget.ActionBar.ActionBarView.OnRightWidgetItemClick;
import hwdroid.widget.ActionBar.ActionBarView.OnRightWidgetItemClick2;
import hwdroid.widget.ActionBar.ActionBarView.OnSecondRightWidgetItemClick2;
import hwdroid.widget.ActionBar.ActionBarView.OnTitle2ItemClick;

/**
 * hide
 */
public interface HWActivityInterface {

    static final String HW_ACTION_BAR_TITLE = "HWActivityInterface.HW_ACTION_BAR_TITLE";

    /**
     * hide
     *
     * @return
     */
    FrameLayout getContentView();

    /**
     * hide
     *
     * @return
     */
    int createLayout();

    /**
     * hide
     */
    void onPreContentChanged();

    /**
     * hide.
     */
    void onPostContentChanged();

    /**
     * set option title.
     *
     * @param title
     */
    void setOptionTitle2(CharSequence title);

    /**
     * set option items, the items will be shown on the popup view.
     *
     * @param optionItem
     * @param click
     */
    void setOptionItems(CharSequence[] optionItem, OnOptionMenuClick click);

    /**
     * set actionbarview title.
     *
     * @param title
     */
    void setTitle2(CharSequence title);

    /**
     * set actionbarview title.
     * if you want listnenr the click event, pls set OnTitle2ItemClick.
     *
     * @param title
     * @param click
     */
    void setTitle2(CharSequence title, OnTitle2ItemClick click);

    /**
     * set actionbarview sub title.
     *
     * @param title
     */
    void setSubTitle2(CharSequence title);

    /**
     * set the back widget's shown.
     * if true, will set the back widget visible.
     * if false, will set the back widget gone.
     * if you call showBackKey() and setLeftWidgetView() together, will show the laster widget.
     *
     * @param show
     */
    void showBackKey(boolean show);

    /**
     * handle the backkey event when user pressed the actionbarview's backkey.
     * if you want to handle the backkey event. you can override the onBackKey().
     * if you don't override the function. will call finish() by default.
     */
    void onBackKey();

    /**
     * set custom view on the left side of {@link ActionBarView}.
     */
    void setLeftWidgetView(View v);

    /**
     * set custom view on the right side of {@link ActionBarView}.
     */
    void setRightWidgetView(View v);

    /**
     * set custom view on the right side of {@link ActionBarView}.
     */
    void setRightWidgetView(View v, boolean clickable);

    /**
     * set the second<count from right> custom view on the right side of {@link ActionBarView}.
     */
    void setSecondRightWidgetView(View v);

    /**
     * set the second<count from right> custom view on the right side of {@link ActionBarView}.
     */
    void setSecondRightWidgetView(View v, boolean clickable);

    /**
     * remove view on the left side of {@link ActionBarView}.
     */
    void removeLeftWidgetView();

    /**
     * remove view on the right side of {@link ActionBarView}.
     */
    void removeRightWidgetView();

    /**
     * remove the second<count from right> view on the right side of {@link ActionBarView}.
     */
    void removeSecondRightWidgetView();

    /**
     * set listener on the left side of {@link ActionBarView}
     *
     * @param click
     */
    void setLeftWidgetClickListener(OnLeftWidgetItemClick click);

    /**
     * set listener on the right side of {@link ActionBarView}
     *
     * @param click
     */
    void setRightWidgetClickListener(OnRightWidgetItemClick click);

    /**
     * set listener on the left side of {@link ActionBarView}
     *
     * @param click
     */
    void setLeftWidgetClickListener2(OnLeftWidgetItemClick2 click);

    /**
     * set listener on the right side of {@link ActionBarView}
     *
     * @param click
     */
    void setRightWidgetClickListener2(OnRightWidgetItemClick2 click);

    /**
     * set listener on the second<count from right> custom view at the right side of {@link ActionBarView}
     *
     * @param click
     */
    void setSecondRightWidgetClickListener2(OnSecondRightWidgetItemClick2 click);

    /**
     * setting enable status to the left side of {@link ActionBarView}
     *
     * @param enabled
     */
    void setLeftWidgetItemEnabled(boolean enabled);

    /**
     * setting enable status to the right side of {@link ActionBarView}
     *
     * @param enabled
     */
    void setRightWidgetItemEnabled(boolean enabled);

    /**
     * setting enable status of the second<count from right> custom view on the right side of {@link ActionBarView}
     *
     * @param enabled
     */
    void setSecondRightWidgetItemEnabled(boolean enabled);

    /**
     * get enable status of the left widget.
     *
     * @return true if enabled equal true.
     */
    boolean isLeftWidgetItemEnabled();

    /**
     * get enable status of the right widget.
     *
     * @return true if enabled equal true.
     */
    boolean isRightWidgetItemEnabled();

    /**
     * get enable status of the second<count from right> right widget.
     *
     * @return true if enabled equal true.
     */
    boolean isSecondRightWidgetItemEnabled();

}
