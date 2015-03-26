
package hwdroid.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hw.droid.R;

import hwdroid.util.CompatibilityUtil;
import hwdroid.util.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>A widget that provides some action buttons with several different display mode
 * for the user to pick a result to launch into, and the user can put into the
 * custom view into action sheet custom domain container if prefer to.</p>
 * <p/>
 * <p>ActionSheet has been designed to compatibility with tablet, it will show specific
 * different GUI style on tablet.</p>
 * <p/>
 * <p>ActionSheet default preset a cancel button at the bottom on the phone. but without
 * on tablet. the cancel button's visibility and display text can be customized by user.</p>
 * <p/>
 * <p>ActionSheet has several display modes as below:</p>
 * <p>1. NORMAL_LIST, a listView contains some common buttons, support customize the items.</p>
 * <p>2. NORMAL_GRID, a gridView contains some common buttons.</p>
 * <p>3. SINGLE_CHOICE, similarity to NORMAL_LIST, but the items like a radio group.</p>
 * <p>4. MULTI_CHOICE, base on NORMAL_LIST, but the items can be checked, and has a confirm
 * button to click to fetch the final items checked status.</p>
 * <p>5. CUSTOM_VIEW, the content domain just a customized container for the users. put anything
 * into here, ActionSheet will auto limit the custom view height and width.</p>
 * <p>6. ACTION_BUTTON, a list of item that contain a pure text at item center with onClick callback.</p>
 *
 * @author caiying.cy
 */
public class ActionSheet implements OnClickListener {
    /**
     * <p>action sheet display mode.</p>
     */
    enum DisplayMode {
        NORMAL_LIST,
        NORMAL_GRID,
        SINGLE_CHOICE,
        MULTI_CHOICE,
        CUSTOM_VIEW,
        ACTION_BUTTON
    }

    /**
     * <p>action sheet display style.</p>
     */
    public enum DisplayStyle {
        NORMAL,
        PHONE,
        TABLET
    }

    public static enum DismissReason {
        APPLICATION_INVOKE,
        CANCEL_BUTTON_CLICK,
        OUTSIDE_TOUCH,
        BACK_KEY_PRESS,
        COMMON_BUTTON_INDEX,
        SINGLE_CHOICE_INDEX,
        ACTION_BUTTON_INDEX,
        NORMAL_GRID_INDEX,
        OTHER_REASON;
        private int mIndex;

        public void setIndex(int index) {
            this.mIndex = index;
        }

        public int getIndex() {
            return this.mIndex;
        }
    }

    /**
     * the interface expose to the third application for handling action sheet dismiss callback.
     */
    public static interface ActionSheetDismissListener {
        /**
         * <p>actionSheet dismiss call back.</p>
         *
         * @param actionSheet actionSheet reference
         */
        void onDismiss(ActionSheet actionSheet);
    }

    /**
     * the interface expose to the third application for handling action sheet callback
     * when use the common buttons display mode.
     * <p/>
     * See {@link #setCommonButtons(java.util.List<String>, java.util.List<Integer>, java.util.List<Boolean>, hwdroid.widget.ActionSheet.CommonButtonListener)},
     */
    public static interface CommonButtonListener extends ActionSheetDismissListener {

        /**
         * action sheet common button click event
         *
         * @param position the clicked button position
         */
        void onClick(int position);
    }

    /**
     * the interface expose to the third application for handling action sheet callback
     * when use the single choice display mode.
     * <p/>
     * See {@link #setSingleChoiceItems(CharSequence[], int, hwdroid.widget.ActionSheet.SingleChoiceListener)}
     */
    public static interface SingleChoiceListener extends ActionSheetDismissListener {

        /**
         * single choice button click event
         *
         * @param position the clicked item position
         */
        void onClick(int position);
    }

    /**
     * the interface expose to the third application for handling action sheet callback
     * when use the multi choice display mode.
     * <p/>
     * See {@link #setMultiChoiceItems(CharSequence[], boolean[], hwdroid.widget.ActionSheet.MultiChoiceListener, hwdroid.widget.ActionSheet.ActionButton)}
     */
    public static interface MultiChoiceListener extends ActionSheetDismissListener {

        /**
         * the item toggled
         *
         * @param position the toggled item position
         */
        void onItemToggle(int position);

        /**
         * confirm the selected items
         *
         * @param itemStatus the all items select status
         */
        void onConfirm(boolean[] itemStatus);
    }

    /**
     * action button designed similarity to the cancel button, they locates at between cancel button and custom content view,
     * action button accept custom the display text on button and the text color, and accept a click listener, the listener
     * will be invoked by action sheet when the user click the corresponding action button.
     */
    public static class ActionButton {
        boolean enable;
        String text;
        int textColor;
        ColorStateList textColorStateList;
        TextView mTextView;
        OnClickListener mOnClickListener;

        public ActionButton() {
        }

        public ActionButton(TextView textView, OnClickListener onClickListener) {
            this.mTextView = textView;
            this.mOnClickListener = onClickListener;
        }

        public ActionButton(String text, int textColor, OnClickListener mOnClickListener) {
            this.text = text;
            this.textColor = textColor;
            this.mOnClickListener = mOnClickListener;
            this.enable = true;
        }

        public ActionButton(String text, ColorStateList textColorStateList, OnClickListener mOnClickListener) {
            this(text, textColorStateList, mOnClickListener, true);
        }

        public ActionButton(String text, ColorStateList textColorStateList, OnClickListener mOnClickListener, boolean enable) {
            this.text = text;
            this.textColorStateList = textColorStateList;
            this.mOnClickListener = mOnClickListener;
            this.enable = enable;
        }

        public void setTextView(TextView textView) {
            this.mTextView = textView;
        }

        public TextView getTextView() {
            return this.mTextView;
        }

        public static ActionButton copyFrom(ActionButton actionButton) {
            ActionButton newActionButton = new ActionButton();
            newActionButton.enable = actionButton.enable;
            newActionButton.text = actionButton.text;
            newActionButton.textColor = actionButton.textColor;
            newActionButton.textColorStateList = actionButton.textColorStateList;
            newActionButton.mTextView = actionButton.mTextView;
            newActionButton.mOnClickListener = actionButton.mOnClickListener;
            return newActionButton;
        }
    }

    private class ItemAdapter extends BaseAdapter {
        private List<ActionListItem> mObjects = new ArrayList<ActionListItem>();
        private Context mContext;
        private ActionSheetCustomAdapter mListItemCustom;

        public ItemAdapter(Context context, List<ActionListItem> datas, ActionSheetCustomAdapter items) {
            this.mContext = context;
            this.mObjects = datas;
            this.mListItemCustom = items;
        }

        @Override
        public int getCount() {
            return mObjects.size();
        }

        @Override
        public ActionListItem getItem(int position) {
            return mObjects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView == null) {
                view = mListItemCustom.newView(this.mContext, position, parent);
            } else {
                view = convertView;
            }

            mListItemCustom.bindView(view, this.mContext, this.mObjects, position);
            return view;
        }

        @Override
        public boolean isEnabled(int position) {
            return mListItemCustom.isEnable(position);
        }
    }

    /**
     * the interface should be implements by the list item data model.
     */
    public interface ActionListItem {
    }

    ;

    /**
     * use for customizing the list common buttons layout.
     *
     * @see setCustomAdapterView( java.util.List< hwdroid.widget.ActionSheet.ActionListItem>, hwdroid.widget.ActionSheet.ActionSheetCustomAdapter)
     */
    public interface ActionSheetCustomAdapter {

        /**
         * create list view item view.
         */
        View newView(Context context, int position, ViewGroup parent);

        /**
         * bind item data to view.
         */
        void bindView(View view, Context context, List<ActionListItem> listItems, int position);

        /**
         * list item whether enable
         */
        boolean isEnable(int position);
    }

    /**
     * normal list mode item data model.
     */
    public static class CommonButtonItem implements ActionListItem {
        public int icon;
        public String text;
        public boolean enable;
        public boolean showBadge;
        public String badgeText;

        public CommonButtonItem(String text) {
            this(-1, text);
        }

        public CommonButtonItem(int icon, String text) {
            this(icon, text, true);
        }

        public CommonButtonItem(int icon, String text, boolean enable) {
            this.icon = icon;
            this.text = text;
            this.enable = enable;
        }
    }

    /**
     * single choice mode item data model.
     */
    public static class SingleChoiceItem implements ActionListItem {
        public String text;
        public boolean showBadge;
        public String badgeText;

        public SingleChoiceItem(String text) {
            this.text = text;
        }
    }

    /**
     * multi choice mode item data model.
     */
    public static class MultiChoiceItem implements ActionListItem {
        public String text;
        public boolean checked;
        public boolean showBadge;
        public String badgeText;

        public MultiChoiceItem(String text, boolean checked) {
            this.text = text;
            this.checked = checked;
        }
    }

    private class ViewHolder {
        RelativeLayout layout;
        ImageView imageView;
        TextView textView;
        FrameLayout badgeAnchor;
        BadgeView badgeView;
        FrameLayout rightWidget;
        CheckBox checkBox;
        RadioButton radioButton;
    }

    private class BadgeViewInfo {
        public boolean isShow = false;
        public String text = "";
    }

    private static class PostponeClick {
        OnClickListener mClickListener;
        CommonButtonListener mCommonButtonListener;
        SingleChoiceListener mSingleChoiceListener;
        MultiChoiceListener mMultiChoiceListener;
        boolean[] itemStatus;
        int index;
        View clickView;

        public PostponeClick(OnClickListener clickListener, View clickView) {
            this.mClickListener = clickListener;
            this.clickView = clickView;
        }

        public PostponeClick(SingleChoiceListener singleChoiceListener, int index) {
            this.mSingleChoiceListener = singleChoiceListener;
            this.index = index;
        }

        public PostponeClick(CommonButtonListener commonButtonListener, int index) {
            this.mCommonButtonListener = commonButtonListener;
            this.index = index;
        }

        public void performClick() {
            if (mClickListener != null && clickView != null) {
                this.mClickListener.onClick(this.clickView);
            }

            if (mCommonButtonListener != null) {
                this.mCommonButtonListener.onClick(index);
            }

            if (mSingleChoiceListener != null) {
                this.mSingleChoiceListener.onClick(index);
            }

            if (mMultiChoiceListener != null) {
                this.mMultiChoiceListener.onConfirm(itemStatus);
            }

        }
    }

    private static final int CANCEL_BUTTON_ID = 100;
    private static final int BG_VIEW_ID = 10;
    private static final int TRANSLATE_DURATION = 300;
    private static final int ALPHA_DURATION = 300;

    private boolean mOutsideTouchable = true;
    private boolean mCancelButtonVisible = true;
    private List<ActionButton> mActionButtons;
    private List<View> mItemDividers;
    private List<BadgeViewInfo> mBadgeViewInfos;
    private ActionSheetPopupWindow mPopupWindow;
    private ActionSheetDialog mDialog;
    private ActionSheetDismissListener mActionSheetDismissListener;
    private CommonButtonListener mCommonButtonListener;
    private OnClickListener mCancelButtonClickListener;
    private OnClickListener mOutsideClickListener;
    private Context mContext;
    private LinearLayout mPanel;
    private ImageView mUpArrow;
    private ImageView mDownArrow;
    private String mCancelButtonTitle;
    private Button mCancelButton;
    private View mBg;
    private FrameLayout mParent;
    private RelativeLayout mLayoutContainer;
    private View mAnchorView;
    private String mTitle;
    private TextView mTitleView;
    private LinearLayout mActionButtonsLayout;
    private ScrollView mScrollView;
    private FixedListView mListView;
    private FixedGridView mGridView;
    private ItemAdapter mCustomAdapter;
    private LinearLayout mLayoutDomain;
    private SingleChoiceListener mSingleChoiceListener;
    private MultiChoiceListener mMultiChoicelistener;
    private View mCustomView;
    private Drawable background;
    private int mSinleChoiceCheckedItem;
    private int mBgColor;
    private int paddingOffset;
    private float actionSheetTextSize = 16;
    private int mCommonButtonMinHeight = 50;
    private int mCommonButtonMaxWidth;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenPadding;
    private int mCustomViewHorizontalPadding = -1;
    private int mCustomViewVerticalPadding = -1;

    private WindowManager mWindowManager;
    private DisplayMode mDisplayMode = DisplayMode.NORMAL_LIST;
    private DisplayStyle mDisplayStyle = DisplayStyle.NORMAL;
    private DismissReason mDismissReason = DismissReason.OTHER_REASON;
    private PostponeClick mPostponeClick;
    private boolean isNeedBuild = false;
    private boolean isNewContentView = false;
    private boolean isAnimating = false;
    private boolean isShowByActionSheet = false;
    private boolean showWithDialog = false;
    private boolean forceLimitHeight = false;
    private boolean statusBarIsShowing = true;
    private boolean clickResponseAfterAnimation = false;

    /**
     * <p>generate or update the action sheet content views</p>
     *
     * @return action sheet content view
     */
    public View createContentView() {
        if (isNeedBuild) {
            constructActionSheet();

            isNewContentView = true;
            isNeedBuild = false;
        }
        return mParent;
    }

    /**
     * help to enforce limit action sheet content height
     */
    public void forceLimitContentHeight(boolean limit) {
        this.forceLimitHeight = limit;
        this.isNeedBuild = true;
    }

    /**
     * let action sheet to know the system UI status bar is visible.
     */
    public void setStatusBarVisiblility(boolean visible) {
        this.statusBarIsShowing = visible;
        this.isNeedBuild = true;
    }

    /**
     * show action sheet content view use dialog
     */
    public void showWithDialog() {
        showWithDialog2();
    }

    /**
     * show action sheet content view use dialog
     *
     * @return dialog instance
     */
    public Dialog showWithDialog2() {
        calcScreenSize();
        if (mDialog == null) {
            mDialog = new ActionSheetDialog(this, mContext, R.style.HWDroid_ActionSheet_dialog);
            WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
            lp.width = mScreenWidth;
            lp.height = mScreenHeight;
            lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;
            lp.windowAnimations = R.style.HWDroid_ActionSheetNoAnimation;
            mDialog.getWindow().setAttributes(lp);
            // handle the dialog on back key pressed event.
            mDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    invokeDismissCallbacks();
                }
            });
        }

        if (mDialog.isShowing()) return mDialog;

        isAnimating = true;
        isShowByActionSheet = true;
        showWithDialog = true;

        setDispalyStyle(DisplayStyle.PHONE);
        setOutsideTouchable(true);
        createContentView();
        if (isNewContentView) {
            if (mPopupWindow != null) mPopupWindow.setContentView(null);
            mDialog.setContentView(mParent);

            limitActionSheetHeight();
        }

        if (Config.HW_AUI_ANIMATION_ENABLE) {
            if (mBg != null) mBg.startAnimation(createAlphaInAnimation());
            mPanel.startAnimation(createTranslationInAnimation());
        }
        mDialog.show();
        mDialog.getWindow().setLayout(mScreenWidth, mScreenHeight);
        isAnimating = false;
        isNewContentView = false;
        return mDialog;
    }

    /**
     * <p>promote the action sheet views. actually the action sheet promote content view
     * supported by {@link android.widget.PopupWindow}, here will call the showAtLocation
     * method from the popup window.</p>
     *
     * @param parent a parent view to get the {@link android.view.View#getWindowToken()} token from
     */
    public void show(View parent) {
        show(parent, null);
    }

    /**
     * <p>promote the action sheet views. actually the action sheet promote content view
     * supported by {@link android.widget.PopupWindow}, here will call the showAtLocation
     * method from the popup window.</p>
     *
     * @param parent     a parent view to get the {@link android.view.View#getWindowToken()} token from
     * @param parentRect ActionSheet will fix the the content position on tablet by parentRect.
     */
    public void show(View parent, Rect parentRect) {
        if (null == mPopupWindow) {
            mPopupWindow = new ActionSheetPopupWindow(this);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setTouchable(true);
        }

        if (isAnimating || mPopupWindow.isShowing()) return;

        isAnimating = true;
        isShowByActionSheet = true;
        showWithDialog = false;

        mAnchorView = parent;

        calcScreenSize();
        if (!isShowPhoneStyle()) {
            createContentView();
            if (isNewContentView) {
                if (null != mDialog) mDialog.setContentView(new View(mContext));
                mPopupWindow.setContentView(mParent);
                layoutTabletViews(parentRect);
            }
        } else {
            createContentView();
            if (isNewContentView) {
                if (null != mDialog) mDialog.setContentView(new View(mContext));
                mPopupWindow.setContentView(mParent);
            }

            mPopupWindow.setHeight(mScreenHeight);
            mPopupWindow.setWidth(mScreenWidth);
            if (Config.HW_AUI_ANIMATION_ENABLE) {
                if (mBg != null) mBg.startAnimation(createAlphaInAnimation());
                mPanel.startAnimation(createTranslationInAnimation());
            }

            limitActionSheetHeight();

        }
        mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        isAnimating = false;
        isNewContentView = false;
    }

    private boolean appendScrollActionButtons() {
        int limitSize = 3;
        if (mActionButtons == null) return false;
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            limitSize = 2;
        }

        return mActionButtons.size() > limitSize;
    }

    private void limitActionSheetHeight() {
        float percent = 0.88f;
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            percent = 0.98f;
        }
        mPanel.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (forceLimitHeight || mPanel.getMeasuredHeight() > mScreenHeight * percent) {
            int limitHeight = (int) (mScreenHeight * percent);
            mPanel.getLayoutParams().height = limitHeight;
        }
    }

    private int limitActionSheetWidth() {
        int minWidth;
        if (!isShowPhoneStyle()) {
            if (mDisplayMode == DisplayMode.NORMAL_GRID)
                minWidth = 250;
            else
                minWidth = 250;
            minWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidth,
                    mContext.getResources().getDisplayMetrics());
        } else {
            int orientation = mContext.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                minWidth = (int) (mScreenWidth * 0.65);
            } else {
                minWidth = (int) (mScreenWidth * 0.95);
            }
        }
        return minWidth;
    }

    private int limitScrollCauseOffset(boolean withMargin) {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        if (withMargin)
            result += paddingOffset;
        return result;
    }

    private int limitPaddingOffset() {
        int offset = 0;
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            offset = (int) (mScreenHeight * 0.025f);
        } else {
            offset = (int) (mScreenWidth * 0.025f);
        }
        return offset;
    }

    /**
     * <p>dismiss with animation</p>
     */
    public void dismiss() {
        mDismissReason = DismissReason.APPLICATION_INVOKE;
        dismissInternal();
    }

    private void dismissInternal() {
        dismissInternal(true);
    }

    public void dismiss(boolean hasAnimation) {
        mDismissReason = DismissReason.APPLICATION_INVOKE;
        dismissInternal(hasAnimation);
    }

    /**
     * <p>Dispose of the popup window. This method can be invoked only after
     * {@link #show(android.view.View)} has been executed. Failing that, calling
     * this method will have no effect.</p>
     *
     * @param clickResponseAfterAnimation if whether has animation
     */
    private void dismissInternal(boolean clickResponseAfterAnimation) {
        if (isAnimating || (!showWithDialog && null != mPopupWindow && !mPopupWindow.isShowing())
                || (showWithDialog && mDialog != null && !mDialog.isShowing())
                || (mDialog == null && mPopupWindow == null)) return;

        isAnimating = true;

        if (clickResponseAfterAnimation && isShowPhoneStyle() && Config.HW_AUI_ANIMATION_ENABLE) {
            mPanel.startAnimation(createTranslationOutAnimation());
            if (mBg != null) mBg.startAnimation(createAlphaOutAnimation());
            mPanel.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mPostponeClick != null) {
                            mPostponeClick.performClick();
                            mPostponeClick = null;
                        }

                        invokeDismissCallbacks();

                        if (mDialog != null && showWithDialog) {
                            mDialog.dismissImmediate();
                        } else {
                            mPopupWindow.dismissImmediate();
                        }
                        isAnimating = false;
                        isShowByActionSheet = false;
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }, ALPHA_DURATION);
        } else {
            if (mDialog != null && showWithDialog) {
                mPanel.startAnimation(createNoAnimation());
                if (mBg != null) mBg.startAnimation(createNoAnimation());
                mPanel.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mPostponeClick != null) {
                            mPostponeClick.performClick();
                            mPostponeClick = null;
                        }

                        invokeDismissCallbacks();

                        mDialog.dismissImmediate();
                        isAnimating = false;
                        isShowByActionSheet = false;
                    }
                }, 1);
            } else {
                if (mPostponeClick != null) {
                    mPostponeClick.performClick();
                    mPostponeClick = null;
                }

                invokeDismissCallbacks();

                mPopupWindow.dismissImmediate();
                isAnimating = false;
                isShowByActionSheet = false;
            }
        }
    }

    private void invokeDismissCallbacks() {
        if (mActionSheetDismissListener != null) {
            mActionSheetDismissListener.onDismiss(this);
        }
        if (mCommonButtonListener != null) {
            mCommonButtonListener.onDismiss(this);
        }
        if (mSingleChoiceListener != null) {
            mSingleChoiceListener.onDismiss(this);
        }
        if (mMultiChoicelistener != null) {
            mMultiChoicelistener.onDismiss(this);
        }
    }

    /**
     * <p>construct a action sheet with basic instrument initialized.</P>
     */
    public ActionSheet(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        this.mScreenPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                8, mContext.getResources().getDisplayMetrics());
        calcScreenSize();
        this.mCommonButtonMinHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mCommonButtonMinHeight, mContext.getResources().getDisplayMetrics());

        this.background = new ColorDrawable(Color.TRANSPARENT);
        if (!isShowPhoneStyle()) {
            this.mBgColor = Color.argb(51, 0, 0, 0);
        } else {
            this.mBgColor = Color.argb(66, 0, 0, 0);
        }

        this.paddingOffset = limitPaddingOffset();
        this.mCancelButtonTitle = mContext.getResources().getString(R.string.hw_search_view_cancel);

        mItemDividers = new ArrayList<View>();
        mActionButtons = new ArrayList<ActionButton>();
    }

    private void calcScreenSize() {
        this.mScreenWidth = mWindowManager.getDefaultDisplay().getWidth();
        this.mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();
        if (statusBarIsShowing)
            this.mScreenHeight -= limitScrollCauseOffset(false);
    }

    private void layoutTabletViews(Rect parentRect) {
        if (mAnchorView == null) return;

        Rect anchorRect = parentRect;
        if (anchorRect == null) {
            int[] location = new int[2];

            mAnchorView.getLocationOnScreen(location);

            anchorRect = new Rect(location[0], location[1], location[0]
                    + mAnchorView.getWidth(), location[1] + mAnchorView.getHeight());
        }

        mPanel.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int rootWidth = mPanel.getMeasuredWidth();

        mCommonButtonMaxWidth = limitActionSheetWidth();

        // calc content width
        if (mLayoutDomain != null) {
            mLayoutDomain.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if (mCommonButtonMaxWidth < mLayoutDomain.getMeasuredWidth()) {
                mCommonButtonMaxWidth = mLayoutDomain.getMeasuredWidth();
            }
        }

        // calc title width
        if (!TextUtils.isEmpty(mTitle) && mTitleView != null) {
            mTitleView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if (mCommonButtonMaxWidth < mTitleView.getMeasuredWidth()) {
                mCommonButtonMaxWidth = mTitleView.getMeasuredWidth();
            }
        }

        // calc custom view width
//        if(mCustomView != null) {
//            mCustomView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//            if(mCommonButtonMaxWidth < mCustomView.getMeasuredWidth()) {
//                mCommonButtonMaxWidth = mCustomView.getMeasuredWidth();
//            }
//        }

        // calc action button width
        if (!mActionButtons.isEmpty()) {
            for (ActionButton actionButton : mActionButtons) {
                actionButton.getTextView().measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                if (mCommonButtonMaxWidth < actionButton.getTextView().getMeasuredWidth()) {
                    mCommonButtonMaxWidth = actionButton.getTextView().getMeasuredWidth();
                }
            }
        }

        // update content width
        if (mLayoutDomain != null) {
            mLayoutDomain.getLayoutParams().width = mCommonButtonMaxWidth;
        }

        // update title width
        if (!TextUtils.isEmpty(mTitle) && mTitleView != null) {
            mTitleView.getLayoutParams().width = mCommonButtonMaxWidth;
        }

        // update custom view width
        if (mCustomView != null) {
            mCustomView.getLayoutParams().width = mCommonButtonMaxWidth;
        }

        // update action button width
        if (!mActionButtons.isEmpty()) {
            for (ActionButton actionButton : mActionButtons) {
                actionButton.getTextView().getLayoutParams().width = mCommonButtonMaxWidth;
            }
        }

        // update divider width
        for (View divider : mItemDividers) {
            divider.getLayoutParams().width = mCommonButtonMaxWidth;
        }

        rootWidth = mCommonButtonMaxWidth;

        mPopupWindow.setWidth(mScreenWidth);
        mPopupWindow.setHeight(mScreenHeight);

        int yPos = 0;
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mPanel.getLayoutParams();
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) mDownArrow.getLayoutParams();
        if (anchorRect.top < mScreenHeight / 2) {
            yPos = anchorRect.bottom - 3 * paddingOffset;
            mUpArrow.setVisibility(View.VISIBLE);
            mDownArrow.setVisibility(View.GONE);
            param = (ViewGroup.MarginLayoutParams) mUpArrow.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        } else {
            mUpArrow.setVisibility(View.GONE);
            mDownArrow.setVisibility(View.VISIBLE);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            mLayoutContainer.getLayoutParams().height = anchorRect.top - 3 * paddingOffset;
        }
        int xPos = 0;
        int requestedX = anchorRect.centerX();
        int arrowWidth = mUpArrow.getMeasuredWidth();

        if (anchorRect.left + rootWidth > mScreenWidth) {
            xPos = (mScreenWidth - rootWidth - mScreenPadding);
        } else if (anchorRect.left - (rootWidth / 2) < 0) {
            xPos = anchorRect.left + mScreenPadding;
        } else {
            xPos = (anchorRect.centerX() - (rootWidth / 2));
        }

        param.leftMargin = (requestedX - xPos) - (arrowWidth / 2);
        FrameLayout.LayoutParams ps = (FrameLayout.LayoutParams) mLayoutContainer.getLayoutParams();
        ps.leftMargin = xPos;
        ps.topMargin = yPos;
    }

    /**
     * <p>consider the screen rotate, action sheet layout should be reset follow the rotate.</p>
     */
    public void updateLayout() {
        isNeedBuild = true;
        calcScreenSize();
        dismissInternal(false);
    }

    private Animation createTranslationInAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
                1, type, 0);
        an.setDuration(TRANSLATE_DURATION);
        return an;
    }

    private Animation createAlphaInAnimation() {
        AlphaAnimation an = new AlphaAnimation(0, 1);
        an.setDuration(ALPHA_DURATION);
        return an;
    }

    private Animation createTranslationOutAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
                0, type, 1);
        an.setDuration(TRANSLATE_DURATION);
        an.setFillAfter(true);
        return an;
    }

    private Animation createAlphaOutAnimation() {
        AlphaAnimation an = new AlphaAnimation(1, 0);
        an.setDuration(ALPHA_DURATION);
        an.setFillAfter(true);
        return an;
    }

    private Animation createNoAnimation() {
        AlphaAnimation an = new AlphaAnimation(1, 0);
        an.setDuration(1);
        an.setFillAfter(true);
        return an;
    }

    private void constructActionSheet() {
        mItemDividers.clear();

        mParent = new FrameLayout(mContext);
        if (!isShowPhoneStyle()) {
            mParent.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
        } else {
            mParent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        }

        constructBackground();

        constructContentPanel();

        mPanel.removeAllViews();

        if (!isShowPhoneStyle()) constructTopArrow();

        mLayoutDomain = new LinearLayout(mContext);
        mLayoutDomain.setBackground(mContext.getResources().getDrawable(R.drawable.hw_actionsheet_background));
        mLayoutDomain.setOrientation(LinearLayout.VERTICAL);

        constructTitle(mLayoutDomain);

        mActionButtonsLayout = constructActionButtonLayout();

        switch (mDisplayMode) {
            case ACTION_BUTTON:
                mCustomView = mActionButtonsLayout;
                constructCustomViewLayout(mLayoutDomain);
                break;
            case CUSTOM_VIEW:
                constructCustomViewLayout(mLayoutDomain);
                break;
            case NORMAL_GRID:
            case SINGLE_CHOICE:
            case MULTI_CHOICE:
            case NORMAL_LIST:
                constructListOrGridContentView(mLayoutDomain);
                break;
            default:
                break;
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
        if (!isShowPhoneStyle())
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1);
        params.gravity = Gravity.CENTER;
        mPanel.addView(mLayoutDomain, params);

        if (isShowPhoneStyle()) constructCancelButton();

        if (!isShowPhoneStyle()) constructBottomArrow();

        mLayoutContainer = new RelativeLayout(mContext);
        mLayoutContainer.addView(mPanel);
        mParent.addView(mLayoutContainer);
    }

    private void constructBackground() {
        mBg = new View(mContext);
        if (!isShowPhoneStyle()) {
            mBg.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
        } else {
            mBg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        }
        mBg.setBackgroundColor(mBgColor);
        mBg.setId(BG_VIEW_ID);
        mBg.setOnClickListener(this);
        mParent.addView(mBg);
    }

    private void constructContentPanel() {
        mPanel = new LinearLayout(mContext);
        if (!isShowPhoneStyle()) {
            mPanel.setMinimumWidth(limitActionSheetWidth());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            mPanel.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    limitActionSheetWidth(), RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            mPanel.setLayoutParams(params);
        }
        mPanel.setOrientation(LinearLayout.VERTICAL);
        mPanel.setBackgroundDrawable(background);
        mPanel.setPadding(0, paddingOffset, 0, paddingOffset);
    }

    private void constructTopArrow() {
        LinearLayout.LayoutParams params0 = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mUpArrow = new ImageView(mContext);
        mUpArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.hw_actionsheet_arrow_up));
        mUpArrow.setLayoutParams(params0);
        mPanel.addView(mUpArrow);
    }

    private void constructBottomArrow() {
        LinearLayout.LayoutParams params0 = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mDownArrow = new ImageView(mContext);
        mDownArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.hw_actionsheet_arrow_down));
        mDownArrow.setLayoutParams(params0);
        mPanel.addView(mDownArrow);
    }

    private void constructTitle(LinearLayout layout) {
        if (mTitleView == null && mTitle == null) return;

        mTitleView = new TextView(mContext);
        mTitleView.setGravity(Gravity.CENTER);
        mTitleView.setText(mTitle);
        if (mDisplayMode == DisplayMode.ACTION_BUTTON)
            mTitleView.setTextColor(mContext.getResources().getColor(R.color.hw_actionsheet_actionbutton_title));
        else
            mTitleView.setTextColor(mContext.getResources().getColor(R.color.hw_actionsheet_text_color_normal));
        mTitleView.setTextSize(actionSheetTextSize);
        mTitleView.setBackground(getMiddleNormalBackground());
        mTitleView.setClickable(true);
        layout.addView(mTitleView, createButtonLayoutParams());

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 1);
        View divider = new View(mContext);
        divider.setBackgroundResource(R.drawable.hw_list_divider);
        layout.addView(divider, params1);
        mItemDividers.add(divider);
    }

    private void constructCustomViewLayout(LinearLayout layout) {
        LinearLayout contentLayout = new LinearLayout(mContext);
        contentLayout.setOrientation(LinearLayout.VERTICAL);

        if (mDisplayMode != DisplayMode.ACTION_BUTTON) {
            if (mCustomViewHorizontalPadding < 0 && mCustomViewVerticalPadding < 0) {
                mCustomView.setBackground(getMiddleNormalBackground());
            } else {
                mCustomView.setBackground(getMiddleNormalNoHorizontalPaddingBackground());
                mCustomViewHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        mCustomViewHorizontalPadding, mContext.getResources().getDisplayMetrics());
                mCustomViewVerticalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        mCustomViewVerticalPadding, mContext.getResources().getDisplayMetrics());
                mCustomView.setPadding(mCustomViewHorizontalPadding, mCustomViewVerticalPadding,
                        mCustomViewHorizontalPadding, mCustomViewVerticalPadding);
            }
        }

        mCustomView.setClickable(true);
        contentLayout.addView(mCustomView);

        if (mDisplayMode != DisplayMode.ACTION_BUTTON && appendScrollActionButtons()) {
            contentLayout.addView(mActionButtonsLayout);
        }

        mScrollView = new ScrollView(mContext);
        mScrollView.addView(contentLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
        if (!isShowPhoneStyle())
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1);
        params.gravity = Gravity.CENTER;
        layout.addView(mScrollView, params);

        if (mDisplayMode != DisplayMode.ACTION_BUTTON && !appendScrollActionButtons()) {
            layout.addView(mActionButtonsLayout);
        }
    }

    private void constructListOrGridContentView(LinearLayout layout) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
        if (!isShowPhoneStyle()) {
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1);
        }
        params.gravity = Gravity.CENTER;
        switch (mDisplayMode) {
            case NORMAL_LIST:
            case SINGLE_CHOICE:
            case MULTI_CHOICE:
                mListView = new FixedListView(mContext);
                mListView.setFixed(appendScrollActionButtons());
                if (appendScrollActionButtons()) {
                    ScrollView scrollView = new ScrollView(mContext);
                    LinearLayout scrollWrapper = new LinearLayout(mContext);
                    scrollWrapper.setOrientation(LinearLayout.VERTICAL);
                    scrollWrapper.addView(mListView);
                    scrollWrapper.addView(mActionButtonsLayout);
                    scrollView.addView(scrollWrapper);
                    layout.addView(scrollView, params);
                } else {
                    layout.addView(mListView, params);
                    layout.addView(mActionButtonsLayout);
                }
                this.mListView.setAdapter(mCustomAdapter);
                this.mListView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (mDisplayMode) {
                            case NORMAL_LIST:
                                if (mCommonButtonListener != null) {
                                    if (isShowByActionSheet) {
                                        mPostponeClick = new PostponeClick(mCommonButtonListener, position);
                                    } else {
                                        mCommonButtonListener.onClick(position);
                                    }
                                }
                                mDismissReason = DismissReason.COMMON_BUTTON_INDEX;
                                mDismissReason.setIndex(position);
                                dismissInternal(clickResponseAfterAnimation);
                                break;
                            case SINGLE_CHOICE:
                                RadioButton radioButton = (RadioButton) view.findViewById(R.id.hw_item_radiobutton);
                                radioButton.setChecked(true);
                                if (isShowByActionSheet) {
                                    mPostponeClick = new PostponeClick(mSingleChoiceListener, position);
                                } else {
                                    mSingleChoiceListener.onClick(position);
                                }
                                mSinleChoiceCheckedItem = position;
                                mCustomAdapter.notifyDataSetChanged();
                                mDismissReason = DismissReason.SINGLE_CHOICE_INDEX;
                                mDismissReason.setIndex(position);
                                dismissInternal(clickResponseAfterAnimation);
                                break;
                            case MULTI_CHOICE:
                                CheckBox checkBox = (CheckBox) view.findViewById(R.id.hw_item_checkbox);
                                MultiChoiceItem multiItem = (MultiChoiceItem) parent.getItemAtPosition(position);
                                checkBox.setChecked(!checkBox.isChecked());
                                multiItem.checked = checkBox.isChecked();
                                mMultiChoicelistener.onItemToggle(position);
                                break;
                            default:
                                break;
                        }
                    }
                });
                break;
            case NORMAL_GRID:
                mGridView = new FixedGridView(mContext);
                mGridView.setFixed(appendScrollActionButtons());
                int offsetSpacing = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16, mContext.getResources().getDisplayMetrics());
                this.mGridView.setPadding(offsetSpacing, offsetSpacing,
                        offsetSpacing, offsetSpacing);
                this.mGridView.setHorizontalSpacing(offsetSpacing);
                this.mGridView.setVerticalSpacing(offsetSpacing);
                this.mGridView.setVerticalFadingEdgeEnabled(false);
                this.mGridView.setVerticalScrollBarEnabled(false);
                this.mGridView.setClipChildren(false);
                this.mGridView.setClipToPadding(false);
                this.mGridView.setNumColumns(3);
                this.mGridView.setFastScrollEnabled(false);
                this.mGridView.setSmoothScrollbarEnabled(true);
                this.mGridView.setSelector(R.drawable.hw_transparent);
                if (appendScrollActionButtons()) {
                    ScrollView scrollView = new ScrollView(mContext);
                    LinearLayout scrollWrapper = new LinearLayout(mContext);
                    scrollWrapper.setOrientation(LinearLayout.VERTICAL);
                    scrollWrapper.addView(mGridView);
                    scrollWrapper.addView(mActionButtonsLayout);
                    scrollView.addView(scrollWrapper);
                    layout.addView(scrollView, params);
                } else {
                    layout.addView(mGridView, params);
                    layout.addView(mActionButtonsLayout);
                }
                this.mGridView.setAdapter(mCustomAdapter);
                this.mGridView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (mCommonButtonListener != null) {
                            if (isShowByActionSheet) {
                                mPostponeClick = new PostponeClick(mCommonButtonListener, position);
                            } else {
                                mCommonButtonListener.onClick(position);
                            }
                        }
                        mDismissReason = DismissReason.NORMAL_GRID_INDEX;
                        mDismissReason.setIndex(position);
                        dismissInternal(clickResponseAfterAnimation);
                    }
                });
                break;

            default:
                break;
        }
    }

    private LinearLayout constructActionButtonLayout() {
        LinearLayout actionButtonsLayout = new LinearLayout(mContext);
        actionButtonsLayout.setOrientation(LinearLayout.VERTICAL);

        if (mActionButtons.size() == 0) return actionButtonsLayout;

        int actionButtonSize = mActionButtons.size();
        if (actionButtonSize > 0 && mDisplayMode != DisplayMode.ACTION_BUTTON) {
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, 1);
            View divider = new View(mContext);
            divider.setBackgroundResource(R.drawable.hw_list_divider);
            actionButtonsLayout.addView(divider, params1);
            mItemDividers.add(divider);
        }
        for (int i = 0; i < actionButtonSize; i++) {
            final ActionButton actionButton = mActionButtons.get(i);
            TextView actionButtonItem = null;
            if (actionButton.getTextView() == null) {
                actionButtonItem = new TextView(mContext);
                actionButtonItem.setGravity(Gravity.CENTER);
                actionButtonItem.setText(actionButton.text);
                if (actionButton.textColorStateList == null) {
                    actionButtonItem.setTextColor(actionButton.textColor);
                } else {
                    actionButtonItem.setTextColor(actionButton.textColorStateList);
                }
                actionButtonItem.setTextSize(actionSheetTextSize);
                actionButtonItem.setBackground(getMiddleBackground());
                actionButtonsLayout.addView(actionButtonItem, createButtonLayoutParams());
                actionButtonItem.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isShowByActionSheet) {
                            mPostponeClick = new PostponeClick(actionButton.mOnClickListener, v);
                        } else {
                            actionButton.mOnClickListener.onClick(v);
                        }

                        mDismissReason = DismissReason.ACTION_BUTTON_INDEX;
                        mDismissReason.setIndex(mActionButtons.indexOf(actionButton));
                        dismissInternal(clickResponseAfterAnimation);
                    }
                });
                actionButtonItem.setEnabled(actionButton.enable);
                actionButton.setTextView(actionButtonItem);
            } else {
                actionButtonItem = actionButton.getTextView();
            }

            if (i != mActionButtons.size() - 1) {
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, 1);
                View divider = new View(mContext);
                divider.setBackgroundResource(R.drawable.hw_list_divider);
                actionButtonsLayout.addView(divider, params1);
                mItemDividers.add(divider);
            }
        }
        return actionButtonsLayout;
    }

    private void constructCancelButton() {
        mCancelButton = new Button(mContext);
        mCancelButton.setTextSize(actionSheetTextSize);
        mCancelButton.setId(CANCEL_BUTTON_ID);
        mCancelButton.setBackgroundDrawable(getSingleBackground());
        mCancelButton.setText(getCancelButtonTitle());
        mCancelButton.setTextColor(mContext.getResources().getColor(R.color.hw_actionsheet_text_color));
        mCancelButton.setOnClickListener(this);
        LinearLayout.LayoutParams params = createButtonLayoutParams();
        params.topMargin = paddingOffset;
        mPanel.addView(mCancelButton, params);
        mCancelButton.setVisibility(mCancelButtonVisible ? View.VISIBLE : View.GONE);
    }

    private LinearLayout.LayoutParams createButtonLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        if (!isShowPhoneStyle())
            params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        return params;
    }

    private Drawable getSingleBackground() {
        return mContext.getResources().getDrawable(R.drawable.hw_actionsheet_single);
    }

    private Drawable getMiddleBackground() {
        return mContext.getResources().getDrawable(R.drawable.hw_actionsheet_middle);
    }

    private Drawable getMiddleNormalBackground() {
        return mContext.getResources().getDrawable(R.drawable.hw_actionsheet_middle_normal);
    }

    private Drawable getMiddleNormalNoHorizontalPaddingBackground() {
        return mContext.getResources().getDrawable(R.drawable.hw_actionsheet_middle_normal_no_padding);
    }

    /**
     * force limit action sheet display style
     *
     * @param style
     */
    public void setDispalyStyle(DisplayStyle style) {
        this.mDisplayStyle = style;
        isNewContentView = true;
    }

    /**
     * get action sheet display style
     *
     * @return display style
     */
    public DisplayStyle getDisplayStyle() {
        return this.mDisplayStyle;
    }

    public void setClickResponseAfaterAnimation(boolean whether) {
        this.clickResponseAfterAnimation = whether;
    }

    public boolean isClickResponseAfaterAnimation() {
        return this.clickResponseAfterAnimation;
    }

    public DismissReason getDismissReason() {
        return mDismissReason;
    }

    public void setDismissReason(DismissReason dismissReason) {
        this.mDismissReason = dismissReason;
    }

    /**
     * @return Whether the ActionSheet is currently showing.
     */
    public boolean isShowing() {
        return (mDialog != null && mDialog.isShowing()) ||
                (mPopupWindow != null && mPopupWindow.isShowing());
    }

    private boolean isShowPhoneStyle() {
        if (showWithDialog) return true;
        switch (mDisplayStyle) {
            case TABLET:
                return false;
            case PHONE:
                return true;
            case NORMAL:
                return !CompatibilityUtil.isTablet(mContext);
        }
        return false;
    }

    /**
     * Set the title text for this action sheet.
     *
     * @param title The new text to display in the title.
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * set a custom textView as the action sheet title.
     */
    public void setCustomTitle(TextView titleView) {
        this.mTitleView = titleView;
    }

    /**
     * a text view formated for custom view with some action buttons.
     *
     * @param message       the alert message on text view
     * @param actionButtons action button below custom view
     */
    public void setMessage(CharSequence message, ActionButton... actionButtons) {
        if (TextUtils.isEmpty(message)) return;

        int vPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, mContext.getResources().getDisplayMetrics());
        int hPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, mContext.getResources().getDisplayMetrics());

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        if (!isShowPhoneStyle()) {
            lp = new FrameLayout.LayoutParams(limitActionSheetWidth(), LayoutParams.WRAP_CONTENT);
        }
        FrameLayout wrapper = new FrameLayout(mContext);
        TextView tv = new TextView(mContext);
        tv.setLineSpacing(0.0f, 1.2f);
        tv.setTextColor(mContext.getResources().getColor(R.color.hw_actionsheet_text_color));
        tv.setTextSize(actionSheetTextSize);
        tv.setPadding(hPadding, vPadding, hPadding, vPadding);
        tv.setText(message);
        wrapper.addView(tv, lp);

        setCustomView(wrapper, actionButtons);
    }

    /**
     * Sets the background color for this action sheet.
     *
     * @param color the color of the background
     */
    public void setBackgroundColor(int color) {
        this.mBgColor = color;
    }

    private void setNormalGrid(List<ActionListItem> items, CommonButtonListener listener, ActionButton... actionButtons) {
        for (ActionListItem item : items) {
            if (!(item instanceof CommonButtonItem))
                throw new IllegalArgumentException("the arguments must be a list of CommonButtonItem.");
        }

        this.mDisplayMode = DisplayMode.NORMAL_GRID;
        if (listener != null)
            this.mCommonButtonListener = listener;
        setCustomAdapterView(items, new ActionSheetCustomAdapter() {
            @Override
            public View newView(Context context, int position, ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hw_actionsheet_item_grid, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.hw_item_text);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.hw_item_icon);
                view.setTag(viewHolder);
                return view;
            }

            @Override
            public void bindView(View view, Context context, List<ActionListItem> listItems,
                                 int position) {
                final ViewHolder viewHolder = (ViewHolder) view.getTag();
                final CommonButtonItem item = (CommonButtonItem) listItems.get(position);
                viewHolder.textView.setText(item.text);
                if (item.icon != -1) {
                    viewHolder.imageView.setVisibility(View.VISIBLE);
                    viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(item.icon));
                } else {
                    viewHolder.imageView.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean isEnable(int position) {
                return true;
            }
        });

        if (actionButtons != null)
            this.mActionButtons = Arrays.asList(actionButtons);
    }

    /**
     * custom normal list items.
     */
    public void setCustomAdapterView(List<ActionListItem> items, ActionSheetCustomAdapter actionSheetCustomAdapter) {
        this.mBadgeViewInfos = new ArrayList<BadgeViewInfo>();
        for (int i = 0; i < items.size(); i++) {
            this.mBadgeViewInfos.add(new BadgeViewInfo());
        }
        this.isNeedBuild = true;
        this.mCustomAdapter = new ItemAdapter(mContext, items, actionSheetCustomAdapter);
    }

    private void setNormalList(final List<ActionListItem> items, CommonButtonListener listener, ActionButton... actionButtons) {
        for (ActionListItem item : items) {
            if (!(item instanceof CommonButtonItem))
                throw new IllegalArgumentException("the arguments must be a list of CommonButtonItem.");
        }

        this.mDisplayMode = DisplayMode.NORMAL_LIST;
        if (listener != null)
            this.mCommonButtonListener = listener;
        setCustomAdapterView(items, new ActionSheetCustomAdapter() {
            @Override
            public View newView(Context context, int position, ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hw_actionsheet_item_list, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.layout = (RelativeLayout) view.findViewById(R.id.hw_item_layout);
                viewHolder.textView = (TextView) view.findViewById(R.id.hw_item_text);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.hw_item_icon);
                viewHolder.badgeAnchor = (FrameLayout) view.findViewById(R.id.hw_item_badgeview);
                viewHolder.badgeView = new BadgeView(mContext, viewHolder.badgeAnchor);
                viewHolder.rightWidget = (FrameLayout) view.findViewById(R.id.hw_item_right_widget);
                viewHolder.checkBox = (CheckBox) view.findViewById(R.id.hw_item_checkbox);
                viewHolder.radioButton = (RadioButton) view.findViewById(R.id.hw_item_radiobutton);
                view.setTag(viewHolder);
                return view;
            }

            @Override
            public void bindView(View view, Context context, List<ActionListItem> listItems,
                                 int position) {
                final ViewHolder viewHolder = (ViewHolder) view.getTag();
                final CommonButtonItem item = (CommonButtonItem) listItems.get(position);
                viewHolder.textView.setText(item.text);
                viewHolder.textView.setEnabled(item.enable);
                if (item.icon != -1) {
                    viewHolder.imageView.setVisibility(View.VISIBLE);
                    viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(item.icon));
                } else {
                    viewHolder.imageView.setVisibility(View.GONE);
                }
                if (mBadgeViewInfos.get(position).isShow) {
                    int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                            mContext.getResources().getDisplayMetrics());
                    viewHolder.badgeView.setBadgePosition(BadgeView.POSITION_CENTER);
                    viewHolder.badgeView.setBadgeMargin(offset);
                    viewHolder.badgeView.setText(mBadgeViewInfos.get(position).text);
                    viewHolder.badgeView.show();
                } else {
                    viewHolder.badgeView.hide();
                }
            }

            @Override
            public boolean isEnable(int position) {
                return ((CommonButtonItem) items.get(position)).enable;
            }
        });

        if (actionButtons != null)
            this.mActionButtons = Arrays.asList(actionButtons);
    }

    private void setSingleChoice(List<ActionListItem> items, SingleChoiceListener listener, ActionButton... actionButtons) {
        for (ActionListItem item : items) {
            if (!(item instanceof SingleChoiceItem))
                throw new IllegalArgumentException("the arguments must be a list of SingleChoiceItem.");
        }

        this.mDisplayMode = DisplayMode.SINGLE_CHOICE;
        if (listener != null)
            this.mSingleChoiceListener = listener;
        setCustomAdapterView(items, new ActionSheetCustomAdapter() {
            @Override
            public View newView(Context context, int position, ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hw_actionsheet_item_list, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.layout = (RelativeLayout) view.findViewById(R.id.hw_item_layout);
                viewHolder.textView = (TextView) view.findViewById(R.id.hw_item_text);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.hw_item_icon);
                viewHolder.badgeAnchor = (FrameLayout) view.findViewById(R.id.hw_item_badgeview);
                viewHolder.badgeView = new BadgeView(mContext, viewHolder.badgeAnchor);
                viewHolder.rightWidget = (FrameLayout) view.findViewById(R.id.hw_item_right_widget);
                viewHolder.checkBox = (CheckBox) view.findViewById(R.id.hw_item_checkbox);
                viewHolder.radioButton = (RadioButton) view.findViewById(R.id.hw_item_radiobutton);
                view.setTag(viewHolder);
                return view;
            }

            @Override
            public void bindView(View view, Context context, List<ActionListItem> listItems,
                                 int position) {
                final ViewHolder viewHolder = (ViewHolder) view.getTag();
                final SingleChoiceItem item = (SingleChoiceItem) listItems.get(position);
                viewHolder.textView.setText(item.text);
                viewHolder.rightWidget.setVisibility(View.VISIBLE);
                viewHolder.radioButton.setVisibility(View.VISIBLE);
                viewHolder.radioButton.setClickable(false);
                viewHolder.radioButton.setChecked(position == mSinleChoiceCheckedItem);
                if (mBadgeViewInfos.get(position).isShow) {
                    int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                            mContext.getResources().getDisplayMetrics());
                    viewHolder.badgeView.setBadgePosition(BadgeView.POSITION_CENTER);
                    viewHolder.badgeView.setBadgeMargin(offset);
                    viewHolder.badgeView.setText(mBadgeViewInfos.get(position).text);
                    viewHolder.badgeView.show();
                } else {
                    viewHolder.badgeView.hide();
                }
            }

            @Override
            public boolean isEnable(int position) {
                return true;
            }
        });

        if (actionButtons != null)
            this.mActionButtons = Arrays.asList(actionButtons);
    }

    private void setMultiChoice(List<ActionListItem> items, MultiChoiceListener listener, ActionButton... actionButtons) {
        for (ActionListItem item : items) {
            if (!(item instanceof MultiChoiceItem))
                throw new IllegalArgumentException("the arguments must be a list of MultiChoiceItem.");
        }

        this.mDisplayMode = DisplayMode.MULTI_CHOICE;
        if (listener != null)
            this.mMultiChoicelistener = listener;
        setCustomAdapterView(items, new ActionSheetCustomAdapter() {
            @Override
            public View newView(Context context, int position, ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hw_actionsheet_item_list, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.hw_item_text);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.hw_item_icon);
                viewHolder.badgeAnchor = (FrameLayout) view.findViewById(R.id.hw_item_badgeview);
                viewHolder.rightWidget = (FrameLayout) view.findViewById(R.id.hw_item_right_widget);
                viewHolder.checkBox = (CheckBox) view.findViewById(R.id.hw_item_checkbox);
                viewHolder.radioButton = (RadioButton) view.findViewById(R.id.hw_item_radiobutton);
                view.setTag(viewHolder);
                return view;
            }

            @Override
            public void bindView(View view, Context context, List<ActionListItem> listItems,
                                 int position) {
                final ViewHolder viewHolder = (ViewHolder) view.getTag();
                final MultiChoiceItem item = (MultiChoiceItem) listItems.get(position);
                viewHolder.textView.setText(item.text);
                viewHolder.textView.setText(item.text);
                viewHolder.rightWidget.setVisibility(View.VISIBLE);
                viewHolder.checkBox.setVisibility(View.VISIBLE);
                viewHolder.checkBox.setClickable(false);
                viewHolder.checkBox.setChecked(item.checked);
            }

            @Override
            public boolean isEnable(int position) {
                return true;
            }
        });

        if (actionButtons != null)
            this.mActionButtons = Arrays.asList(actionButtons);
    }

    /**
     * <p>consideration for the pure text common buttons</p>
     *
     * @param titles the common buttons title list
     */
    public void setCommonButtons(List<String> titles) {
        setCommonButtons(titles, null);
    }

    /**
     * <p>custom common buttons, here will receive two list, the common button titles and icons.
     * the two list must be one-to-one correspondence</p>
     *
     * @param titles the common buttons title list
     * @param icons  the common buttons icon list
     */
    public void setCommonButtons(List<String> titles, List<Integer> icons) {
        setCommonButtons(titles, icons, null);
    }

    /**
     * <p>custom common buttons, here will receive three list, the common button texts and icons, status.
     * the three list must be one-to-one correspondence</p>
     *
     * @param texts   button text align parent left.
     * @param icons   if exists, to left of text
     * @param enables if true the item enable, false otherwise.
     */
    public void setCommonButtons(List<String> texts, List<Integer> icons, List<Boolean> enables) {
        setCommonButtons(texts, icons, enables, null);
    }

    /**
     * <p>custom common buttons, here will receive three list, the common button texts and icons, status.
     * the three list must be one-to-one correspondence</p>
     *
     * @param texts    button text align parent left.
     * @param icons    if exists, to left of text
     * @param enables  if true the item enable, false otherwise.
     * @param listener the callback will be invoke in normal list mode.
     */
    public void setCommonButtons(List<String> texts, List<Integer> icons, List<Boolean> enables, CommonButtonListener listener) {
        if (texts == null || texts.size() == 0) return;

        if (icons != null && icons.size() != 0 && texts.size() != icons.size()) {
            throw new IllegalArgumentException("the icons list must be equal to texts list");
        }

        if (enables != null && enables.size() != 0 && texts.size() != enables.size()) {
            throw new IllegalArgumentException("the enables list must be equal to texts list");
        }

        List<ActionListItem> items = new ArrayList<ActionListItem>();
        int icon = -1;
        boolean enable = true;
        for (int i = 0; i < texts.size(); i++) {
            if (icons != null) icon = icons.get(i);
            if (enables != null) enable = enables.get(i);
            items.add(new CommonButtonItem(icon, texts.get(i), enable));
        }
        this.isNeedBuild = true;
        setNormalList(items, listener);
    }

    /**
     * take action sheet construct a grid view witch common button listener.
     */
    public void setCommonGridButtons(List<String> texts, List<Integer> icons, CommonButtonListener listener, ActionButton... actionButtons) {
        if (texts == null || texts.size() == 0) return;

        if (icons != null && icons.size() != 0 && texts.size() != icons.size()) {
            throw new IllegalArgumentException("the icons list must be equal to texts list");
        }
        List<ActionListItem> items = new ArrayList<ActionListItem>();
        int icon = -1;
        for (int i = 0; i < texts.size(); i++) {
            if (icons != null) icon = icons.get(i);
            items.add(new CommonButtonItem(icon, texts.get(i)));
        }
        setNormalGrid(items, listener, actionButtons);
    }

    /**
     * Set a list of items to be displayed in the action sheet as the content, you will be notified of
     * the selected item via the supplied listener. The list will have a check mark displayed to
     * the right of the text for the checked item. Clicking on an item in the list will dismiss the action sheet.
     *
     * @param items       the items to be displayed.
     * @param checkedItem specifies which item is checked. If -1 no items are checked.
     * @param listener    notified when an item on the list is clicked. The action sheet will be
     *                    dismissed when an item is clicked.
     */
    public void setSingleChoiceItems(CharSequence[] items, int checkedItem, final SingleChoiceListener listener) {
        List<ActionListItem> singleItems = new ArrayList<ActionListItem>();
        for (int i = 0; i < items.length; i++) {
            singleItems.add(new SingleChoiceItem(items[i].toString()));
        }
        this.mSinleChoiceCheckedItem = checkedItem;
        setSingleChoice(singleItems, listener);
    }

    /**
     * Set a list of items to be displayed in the action sheet as the content,
     * you will be notified of the selected item via the supplied listener.
     * The list will have a check mark displayed to the right of the text
     * for each checked item. Clicking on an item in the list will not
     * dismiss the action sheet. Clicking on a button will dismiss the action sheet.
     *
     * @param items        the text of the items to be displayed in the list.
     * @param checkedItems specifies which items are checked. It should be null in which case no
     *                     items are checked. If non null it must be exactly the same length as the array of
     *                     items.
     * @param listener     notified when an item on the list is clicked. The action sheet will not be
     *                     dismissed when an item is clicked. It will only be dismissed if clicked on a
     *                     button.
     * @param actionButton multi choice need a action button handle the result callback
     */
    public void setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
                                    final MultiChoiceListener listener, final ActionButton... actionButtons) {

        List<ActionListItem> multiItems = new ArrayList<ActionListItem>();
        for (int i = 0; i < items.length; i++) {
            multiItems.add(new MultiChoiceItem(items[i].toString(), checkedItems[i]));
        }
        if (actionButtons.length > 0) {
            final OnClickListener mOriginalConfirmClickListener = actionButtons[0].mOnClickListener;
            OnClickListener newActionListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean[] status = new boolean[mCustomAdapter.getCount()];
                    for (int i = 0; i < mCustomAdapter.getCount(); i++) {
                        status[i] = ((MultiChoiceItem) mListView.getItemAtPosition(i)).checked;
                    }

                    // no need use postpone click, here postpone by action button
                    mMultiChoicelistener.onConfirm(status);

                    mOriginalConfirmClickListener.onClick(v);
                }
            };
            ActionButton newActionButton = ActionButton.copyFrom(actionButtons[0]);
            newActionButton.mOnClickListener = newActionListener;
            actionButtons[0] = newActionButton;
            setMultiChoice(multiItems, listener, actionButtons);
        } else {
            setMultiChoice(multiItems, listener);
        }
    }

    /**
     * action sheet custom mode will show custom view with some action button.
     *
     * @param contentView   the view will be show at the custom view domain.
     * @param actionButtons the action buttons will be showed below custom view.
     */
    public void setCustomView(View contentView, ActionButton... actionButtons) {
        this.mDisplayMode = DisplayMode.CUSTOM_VIEW;
        this.mCustomView = contentView;
        this.isNeedBuild = true;

        if (actionButtons != null)
            this.mActionButtons = Arrays.asList(actionButtons);
    }

    /**
     * customize custom content view padding.
     *
     * @param hPadding horizontal padding, unit is dip.
     * @param vPadding vertical padding, unit is dip.
     */
    public void setCustomViewPadding(int hPadding, int vPadding) {
        this.mCustomViewHorizontalPadding = hPadding;
        this.mCustomViewVerticalPadding = vPadding;
    }

    /**
     * make action sheet show a list of action button, optional show the cancel button.
     *
     * @param actionButtons
     */
    public void setActionButtons(ActionButton... actionButtons) {
        this.mDisplayMode = DisplayMode.ACTION_BUTTON;
        this.mActionButtons = Arrays.asList(actionButtons);
        this.isNeedBuild = true;
    }

    /**
     * <p>determine the action button whether display enable status</p>
     *
     * @param index  the action button position
     * @param enable the action button enable status
     */
    public void setActionButtonEnable(int index, boolean enable) {
        if (mActionButtons == null || mActionButtons.size() < index) return;

        mActionButtons.get(index).enable = enable;

        if (!isNeedBuild)
            mActionButtons.get(index).getTextView().setEnabled(enable);
    }

    /**
     * <p>determine the common button whether display enable status</p>
     *
     * @param index  the common button position
     * @param enable the common button enable status
     */
    public void setCommonButtonEnable(int index, boolean enable) {
        if (mCustomAdapter == null) return;

        ActionListItem item = mCustomAdapter.getItem(index);

        if (!(item instanceof CommonButtonItem)) return;

        ((CommonButtonItem) item).enable = enable;
        mCustomAdapter.notifyDataSetChanged();
    }

    /**
     * Register a callback to be invoked when ActionSheet dismiss.
     *
     * @param listener The callback that will run
     */
    public void setActionSheetDismissListener(ActionSheetDismissListener listener) {
        this.mActionSheetDismissListener = listener;
    }

    /**
     * Register a callback to be invoked when this cancel button is clicked.
     *
     * @param listener The callback that will run
     */
    public void setCancelButtonClickListener(OnClickListener listener) {
        this.mCancelButtonClickListener = listener;
    }

    /**
     * <p>control cancel button visibility</p>
     *
     * @param visible
     */
    public void setCancalButtonVisiblity(boolean visible) {
        mCancelButtonVisible = visible;
        if (mCancelButton != null)
            mCancelButton.setVisibility(mCancelButtonVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * <p>get the cancel button visibility.</p>
     *
     * @return true if the cancel button is visible, false otherwise.
     */
    public boolean getCancelButtonVisiblity() {
        if (mCancelButton == null)
            return false;

        return mCancelButton.getVisibility() == View.VISIBLE;
    }

    /**
     * <p>custom cancel button text content.</p>
     *
     * @param title the text display on cancel button
     */
    public void setCancelButtonTitle(String title) {
        this.mCancelButtonTitle = title;
    }

    /**
     * <p>get the cancel text content.</p>
     *
     * @return the cancel button text
     */
    public String getCancelButtonTitle() {
        return mCancelButtonTitle;
    }

    /**
     * <p>show button badge view by position</p>
     *
     * @param position the button index
     * @param num      the number on badge view
     */
    public void showButtonBadgeView(int position, int num) {
        String text = String.valueOf(num);
        if (num <= 0) {
            text = "";
        }

        if (num > 99) {
            text = "99+";
        }
        showButtonBadgeView(position, text);
    }

    /**
     * <p>show button badge view by position</p>
     *
     * @param position the button index
     * @param text     the text on badge view, limit 3 characters. eg: 99+
     */
    public void showButtonBadgeView(int position, String text) {
        showBageView(position, text);
    }

    private void showBageView(int position, String text) {
        mBadgeViewInfos.get(position).isShow = true;
        mBadgeViewInfos.get(position).text = text;
        mCustomAdapter.notifyDataSetChanged();
    }

    /**
     * <p>hide button badge view by position</p>
     *
     * @param position the button index
     */
    public void hideButtonBadgeView(int position) {
        if (mBadgeViewInfos == null) return;
        mBadgeViewInfos.get(position).isShow = false;
        mCustomAdapter.notifyDataSetChanged();
    }

    /**
     * <p>Indicates whether anyone common button in all the buttons show badge view.</p>
     *
     * @return true if exists button item show badge view, false otherwise.
     */
    public boolean hasButtonShowBadgeView() {
        if (mBadgeViewInfos == null) return false;
        for (BadgeViewInfo badgeViewInfo : mBadgeViewInfos) {
            if (badgeViewInfo.isShow) return true;
        }
        return false;
    }

    /**
     * Register a callback to be invoked when the outside view is clicked.
     *
     * @param listener The callback that will run
     */
    public void setOutsideClickListener(OnClickListener listener) {
        this.mOutsideClickListener = listener;
    }

    /**
     * <p>Controls whether the action sheet will be informed of touch events outside
     * of its window, and dismiss itself. The default is true.</p>
     *
     * @param touchable true if the action sheet should dismiss when receive outside
     *                  touch events, false otherwise
     * @see #isOutsideTouchable()
     */
    public void setOutsideTouchable(boolean touchable) {
        this.mOutsideTouchable = touchable;
    }

    /**
     * <p>Indicates whether the action sheet will be informed of touch events
     * outside of its window.</p>
     *
     * @return true if the action sheet is outside touchable, false otherwise
     * @see #setOutsideTouchable(boolean)
     */
    public boolean isOutsideTouchable() {
        return mOutsideTouchable;
    }

    /**
     * <p>Setup action sheet listener.</p>
     *
     * @param listener see {@link hwdroid.widget.ActionSheet$ActionSheetListener}
     * @deprecated alternative to use the method
     * public void setCommonButtons(List<String> texts, List<Integer> icons, List<Boolean> enables, CommonButtonListener listener)
     */
    public void setCommonButtonListener(CommonButtonListener listener) {
        if (listener != null)
            this.mCommonButtonListener = listener;
    }

    /**
     * <p>all views onClick event handle at here.</p>
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == BG_VIEW_ID && !isOutsideTouchable()) {
            return;
        }

        if (v.getId() == CANCEL_BUTTON_ID) {
            if (mCancelButtonClickListener != null) {
                mCancelButtonClickListener.onClick(v);
            }
            mDismissReason = DismissReason.CANCEL_BUTTON_CLICK;
        }

        if (v.getId() == BG_VIEW_ID) {
            if (mOutsideClickListener != null) {
                mOutsideClickListener.onClick(v);
            }
            mDismissReason = DismissReason.OUTSIDE_TOUCH;
        }

        dismissInternal();
    }

    /**
     * custom popup windows, make it to worked fine with action sheet.
     * action sheet worked at tablet, the default dismiss action need immediately,
     * but worked at phone, the default dismiss action need with animation.
     */
    private static class ActionSheetPopupWindow extends PopupWindow {
        private ActionSheet mActionSheet;

        public ActionSheetPopupWindow(ActionSheet actionSheet) {
            this.mActionSheet = actionSheet;
        }

        @Override
        public void dismiss() {
            mActionSheet.setDismissReason(DismissReason.BACK_KEY_PRESS);
            if (!mActionSheet.isShowPhoneStyle()) {
                mActionSheet.dismissInternal(false);
            } else {
                mActionSheet.dismissInternal();
            }
        }

        public void dismissImmediate() {
            super.dismiss();
        }
    }

    private static class ActionSheetDialog extends Dialog {
        private ActionSheet mActionSheet;

        public ActionSheetDialog(ActionSheet actionSheet, Context context, int theme) {
            super(context, theme);
            this.mActionSheet = actionSheet;
        }

        @Override
        public void dismiss() {
            mActionSheet.setDismissReason(DismissReason.BACK_KEY_PRESS);
            if (!mActionSheet.isShowPhoneStyle()) {
                mActionSheet.dismissInternal(false);
            } else {
                mActionSheet.dismissInternal();
            }
        }

        public void dismissImmediate() {
            super.dismiss();
        }
    }

    private static class FixedGridView extends GridView {
        private boolean freezeHeight = false;

        public void setFixed(boolean enable) {
            this.freezeHeight = enable;
        }

        public FixedGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FixedGridView(Context context) {
            super(context);
        }

        public FixedGridView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int expandSpec = heightMeasureSpec;
            if (freezeHeight) {
                expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                        MeasureSpec.AT_MOST);
            }
            super.onMeasure(widthMeasureSpec, expandSpec);
        }
    }

    private static class FixedListView extends ListView {
        private boolean freezeHeight = false;

        public void setFixed(boolean enable) {
            this.freezeHeight = enable;
        }

        public FixedListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FixedListView(Context context) {
            super(context);
        }

        public FixedListView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int expandSpec = heightMeasureSpec;
            if (freezeHeight) {
                expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                        MeasureSpec.AT_MOST);
            }
            super.onMeasure(widthMeasureSpec, expandSpec);
        }
    }

}
