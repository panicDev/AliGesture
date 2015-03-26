package hwdroid.dialog;

import hwdroid.dialog.DialogInterface.OnClickListener;
import hwdroid.dialog.DialogInterface.OnMultiChoiceClickListener;
import hwdroid.widget.ItemAdapter;
import hwdroid.widget.item.Item;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hw.droid.R;

/**
 * hide
 */
public abstract class DialogType {

    protected Context mContext;
    protected DropDownDialog mPopupWindow;
    protected ItemAdapter mAdapter;
    protected LocalHandler mHandler;
    protected DialogInterface mDialogInterface;

    protected ArrayList<String> mMessageList;
    protected ArrayList<Item> mItemList;
    private RelativeLayout mTitleView;
    private TextView mTitleText;
    private FrameLayout mCustomView;
    protected LinearLayout mFooterView;

    public String mTitleString;
    public String mButton1Text;
    public String mButton2Text;
    public String mButton3Text;
    public OnClickListener mOnClickListener;
    public OnClickListener mPositiveOnClickListener;
    public OnClickListener mNegativeOnClickListener;
    public OnClickListener mDismissOnClickListener;
    public OnMultiChoiceClickListener mMultiChoiceClickListener;

    public static final int HANDLER_UPDATE_ADAPTER = 0x100;
    public static final int HANDLER_CLOSE_DIALOG = 0x101;

    private Button mPositiveButton;
    private Button mDismissButton;
    private Button mCancelButton;
    private View mContentFooterDivider;

    public void copyDialogType(DialogType type) {
        mTitleString = type.mTitleString;
        mButton1Text = type.mButton1Text;
        mButton2Text = type.mButton2Text;
        mButton3Text = type.mButton3Text;
        mOnClickListener = type.mOnClickListener;
        mPositiveOnClickListener = type.mPositiveOnClickListener;
        mNegativeOnClickListener = type.mNegativeOnClickListener;
        mNegativeOnClickListener = type.mNegativeOnClickListener;
        mDismissOnClickListener = type.mDismissOnClickListener;
        mMultiChoiceClickListener = type.mMultiChoiceClickListener;
    }

    public DialogType(Context context,
                      DropDownDialog popupWindow,
                      DialogInterface dialogInterface,
                      ItemAdapter adapter,
                      View menuView) {
        mContext = context;
        mDialogInterface = dialogInterface;
        mPopupWindow = popupWindow;
        mAdapter = adapter;
        mHandler = new LocalHandler(this);

        mTitleView = (RelativeLayout) menuView.findViewById(R.id.hw_dialog_title_view);
        mTitleText = (TextView) menuView.findViewById(R.id.hw_dialog_title_text);
        mCustomView = (FrameLayout) menuView.findViewById(R.id.hw_dialog_custom_view);
        mFooterView = (LinearLayout) menuView.findViewById(R.id.hw_dialog_footer_view);
        mContentFooterDivider = (View) menuView.findViewById(R.id.hw_dialog_contetnt_footer_divider);
        init();
    }

    public void init() {
        mMessageList = new ArrayList<String>();
        mItemList = new ArrayList<Item>();
    }

    private void createFooterView() {
        boolean hasDivider = false;

        mFooterView.removeAllViews();
        if (mButton1Text == null && mButton2Text == null && mButton3Text == null) {
            mFooterView.setVisibility(View.GONE);
            mContentFooterDivider.setVisibility(View.GONE);
        }

        final LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (mButton2Text != null) {

            if (mCancelButton == null) {
                mCancelButton = (Button) layoutInflater.inflate(R.layout.hw_dropdown_dialog_cancel_button, mFooterView, false);
            }

            mCancelButton.setText(mButton2Text);
            mFooterView.addView(mCancelButton);

            mCancelButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    if (mNegativeOnClickListener != null) {
                        mNegativeOnClickListener.onClick(mDialogInterface, DialogInterface.BUTTON_NEGATIVE);
                    }

//					if(mOnCancelListener != null) {
//						mOnCancelListener.onCancel(mDialogInterface);
//					}

                    cancelDialog();
                }
            });

            hasDivider = false;
        }

        if (mButton3Text != null) {
            if (hasDivider == true) {
                addDivider();
            }

            if (mDismissButton == null) {
                mDismissButton = (Button) layoutInflater.inflate(R.layout.hw_dropdown_dialog_cancel_button, mFooterView, false);
            }

            mDismissButton.setText(mButton3Text);
            mFooterView.addView(mDismissButton);

            mDismissButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    if (mDismissOnClickListener != null) {
                        mDismissOnClickListener.onClick(mDialogInterface, DialogInterface.BUTTON_NEUTRAL);
                    }

//                    if(mOnDismissListener != null) {
//						mOnDismissListener.onDismiss(mDialogInterface);
//					}

                    cancelDialog();
                }
            });
        }

        if (mButton1Text != null) {
            if (hasDivider == true) {
                addDivider();
            }

            if (mPositiveButton == null) {
                mPositiveButton = (Button) layoutInflater.inflate(R.layout.hw_dropdown_dialog_ok_button, mFooterView, false);
            }

            mPositiveButton.setText(mButton1Text);
            mFooterView.addView(mPositiveButton);
            mPositiveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    if (mPositiveOnClickListener != null) {
                        mPositiveOnClickListener.onClick(mDialogInterface, DialogInterface.BUTTON_POSITIVE);
                    }

                    cancelDialog();
                }
            });

            hasDivider = false;
        }
    }

    private void addDivider() {
        ImageView dividerImg = new ImageView(mContext);
        final LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 0);
        dividerImg.setLayoutParams(lp);
        dividerImg.setBackgroundResource(R.drawable.hw_list_divider);
        mFooterView.addView(dividerImg);
    }

    public void onDropdownDialogItemClick(AdapterView<?> arg0, View arg1,
                                          int arg2, long arg3) {
        int which = 0;

        if (mMessageList != null) {
            which += mMessageList.size();
        }

        int pos = arg2 - which;

        updateChoicesItem(pos);

        callBack(pos);

        if (doDismiss()) {
            callDismiss();
        }
    }

    public abstract Item createItemView(String text, boolean selected);

    public abstract void showChild();

    public boolean doDismiss() {
        return false;
    }

    public void updateChoicesItem(int pos) {

    }

    public void updateItem(int value, int maxValue) {

    }

    public void setView(View v) {
        mCustomView.setVisibility(View.VISIBLE);
        mCustomView.removeAllViews();
        mCustomView.addView(v);
    }

    public void addItem(String text, boolean selected) {
        Item item = createItemView(text, selected);
        mItemList.add(item);
    }

    public void addItems(CharSequence[] items, final OnClickListener listener, boolean[] selected) {
        if (items == null) return;

        mOnClickListener = listener;
        for (int i = 0; i < items.length; i++) {
            addItem(items[i].toString(), selected[i]);
        }
    }

    public void addItems(CharSequence[] items, final DialogInterface.OnMultiChoiceClickListener listener, boolean[] selected) {
        if (items == null) return;

        boolean[] checked;

        if (selected == null) {
            checked = new boolean[items.length];
        } else {
            checked = selected;
        }

        mMultiChoiceClickListener = listener;
        for (int i = 0; i < items.length; i++) {
            addItem(items[i].toString(), checked[i]);
        }
    }

    public void callBack(int index) {
        if (mOnClickListener != null) mOnClickListener.onClick(mDialogInterface, index);
    }

    public void createDialogRootView() {
        mAdapter.clear();
        mFooterView.setVisibility(View.VISIBLE);


        if (mTitleString != null) {
            mTitleView.setVisibility(View.VISIBLE);
            mTitleText.setText(mTitleString);
        } else {
            mTitleView.setVisibility(View.GONE);
            mTitleText.setText("");
        }

        showChild();

        createFooterView();
    }

    public void setTitle(int titleId) {
        mTitleString = mContext.getString(titleId);
    }

    public void setTitle(CharSequence title) {
        if (title == null) return;
        mTitleString = title.toString();
    }

    public void setMessage(int titleId) {
        mMessageList.add(mContext.getString(titleId));
    }

    public void setMessage(CharSequence title) {
        mMessageList.clear();
        mMessageList.add(title.toString());
    }

    public void setButton1(String text, final OnClickListener listener) {
        mPositiveOnClickListener = listener;
        mButton1Text = text;
    }

    public void setButton2(String text, final OnClickListener listener) {
        mNegativeOnClickListener = listener;
        mButton2Text = text;
    }

    public void setButton3(String text, final OnClickListener listener) {
        mDismissOnClickListener = listener;
        mButton3Text = text;
    }

    public static class LocalHandler extends Handler {
        private final WeakReference<DialogType> mType;

        public LocalHandler(DialogType type) {
            this.mType = new WeakReference<DialogType>(type);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_UPDATE_ADAPTER:
                    try {
                        if (mType.get().mAdapter != null) {
                            mType.get().mAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {

                    }
                    break;
                case HANDLER_CLOSE_DIALOG:
                    try {
                        mType.get().mPopupWindow.cancel();
                    } catch (Exception e) {

                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void cancelDialog() {
        mHandler.sendEmptyMessage(HANDLER_CLOSE_DIALOG);
    }

    public void callDismiss() {
        cancelDialog();
    }

    public Button getButton(int whichButton) {
        if (whichButton == AlertDialog.BUTTON_POSITIVE) {
            return mPositiveButton;
        } else if (whichButton == AlertDialog.BUTTON_NEGATIVE) {
            return mCancelButton;
        } else if (whichButton == AlertDialog.BUTTON_NEUTRAL) {
            return mDismissButton;
        }

        return null;
    }

}
