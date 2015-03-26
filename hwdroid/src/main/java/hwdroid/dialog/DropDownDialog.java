package hwdroid.dialog;

import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.hw.droid.R;

import hwdroid.widget.ItemAdapter;

public class DropDownDialog implements DialogInterface {

    private Context mContext;
    private View mMenuView;
    private ListView mListView;
    private ItemAdapter mAdapter;
    private android.app.Dialog mDialog;

    private DialogType mItemDialogType;
    private DialogType mMessageItemDialogType;
    private DialogType mProgressItemDialogType;
    private DialogType mSingleChoiceItemDialogType;
    private DialogType mMultiChoiceItemDialogType;
    private DialogType mDialogType;

    public DialogInterface.OnCancelListener mOnCancelListener;
    public DialogInterface.OnDismissListener mOnDismissListener;

    private static DialogInterface.OnKeyListener mOnKeyListener;

    @SuppressWarnings("deprecation")
    public DropDownDialog(Context context) {

        mContext = context;
        mDialog = new android.app.Dialog(context, R.style.Theme_HWDroid_Dialog_PushUpIn);

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.hw_dropdown_dialog_view, null);

        mListView = (ListView) mMenuView.findViewById(R.id.hw_dialog_listview);

        mListView.setOnItemClickListener(new OnItemClickListener() {

                                             @Override
                                             public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                                                 mDialogType.onDropdownDialogItemClick(arg0, view, position, id);
                                             }
                                         }
        );


        mAdapter = new ItemAdapter(mContext);
        mAdapter.setNotifyOnChange(false);
        mListView.setAdapter(mAdapter);

        mItemDialogType = new ItemDialogType(context, this, this, mAdapter, mMenuView);
        mMessageItemDialogType = new MessageItemDialogType(context, this, this, mAdapter, mMenuView);
        mProgressItemDialogType = new ProgressItemDialogType(context, this, this, mAdapter, mMenuView);
        mSingleChoiceItemDialogType = new SingleChoiceItemDialogType(context, this, this, mAdapter, mMenuView);
        mMultiChoiceItemDialogType = new MultiChoiceItemDialogType(context, this, this, mAdapter, mMenuView);
        mDialogType = mMessageItemDialogType;

        mDialog.setContentView(mMenuView);
        mDialog.setCanceledOnTouchOutside(true);
    }

    public Window getWindow() {
        return mDialog.getWindow();
    }

    public void showDialog() {
        mDialogType.createDialogRootView();
        mDialog.show();
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mOnCancelListener = listener;
        mDialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(android.content.DialogInterface dialog) {
                if (mOnCancelListener != null) {
                    mOnCancelListener.onCancel(DropDownDialog.this);
                }
            }
        });
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
        mDialog.setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(android.content.DialogInterface dialog) {
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss(DropDownDialog.this);
                }
            }
        });
    }

    public void callDismiss() {
        mDialogType.callDismiss();
    }

    public void updateProgressItemMessage(int value, int maxValue) {
        mDialogType.updateItem(value, maxValue);
    }

    public void setTitle(int titleId) {
        mDialogType.setTitle(titleId);
    }

    public void setTitle(CharSequence title) {
        mDialogType.setTitle(title);
    }

    public void setMessage(int titleId) {
        mDialogType.setMessage(titleId);
    }

    public void setMessage(CharSequence title) {
        mDialogType.setMessage(title);
    }

    public void setView(View v) {
        mDialogType.setView(v);
    }

    public void setCancelable(boolean flag) {
        mDialog.setCancelable(flag);
    }

    public void setButton1(int titleId, final OnClickListener listener) {
        mDialogType.setButton1(mContext.getString(titleId), listener);
    }

    public void setButton1(CharSequence text, final OnClickListener listener) {
        if (text == null) return;
        mDialogType.setButton1(text.toString(), listener);
    }

    public void setButton2(int titleId, final OnClickListener listener) {
        mDialogType.setButton2(mContext.getString(titleId), listener);
    }

    public void setButton2(CharSequence text, final OnClickListener listener) {
        if (text == null) return;
        mDialogType.setButton2(text.toString(), listener);
    }

    public void setButton3(int titleId, final OnClickListener listener) {
        mDialogType.setButton3(mContext.getString(titleId), listener);
    }

    public void setButton3(CharSequence text, final OnClickListener listener) {
        if (text == null) return;
        mDialogType.setButton3(text.toString(), listener);
    }

    protected void setProgressItem(CharSequence message, DialogInterface.OnCancelListener cancelListener) {
        setCurrentDialogType(mProgressItemDialogType);
        if (message == null) {
            setMessage("");
        } else {
            setMessage(message.toString());
        }

        setOnCancelListener(cancelListener);
    }

    void addItem(CharSequence text) {
        if (text == null) return;
        addItem(text.toString(), false);
    }

    public void addItem(int stringId) {
        addItem(mContext.getString(stringId), false);
    }

    void addItem(CharSequence text, boolean selected) {
        if (text == null) return;
        setCurrentDialogType(mItemDialogType);
        mDialogType.addItem(text.toString(), selected);
    }

    public void addItem(int stringId, boolean selected) {
        setCurrentDialogType(mItemDialogType);
        mDialogType.addItem(mContext.getString(stringId), selected);
    }

    public void addItems(CharSequence[] items, final OnClickListener listener) {
        if (items == null) return;
        boolean[] selected = new boolean[items.length];
        addItems(items, selected, listener);
    }

    public void addItems(CharSequence[] items, boolean[] checkedItems, final OnClickListener listener) {
        if (items == null) return;
        setCurrentDialogType(mItemDialogType);
        mDialogType.addItems(items, listener, checkedItems);
    }


    public void addSingleChoiceItem(int stringId) {
    }

    public void addSingleChoiceItem(CharSequence text) {
    }

    public void setSingleChoiceItems(CharSequence[] items, int checkedItem, final OnClickListener listener) {
        if (items == null) return;

        setCurrentDialogType(mSingleChoiceItemDialogType);
        boolean[] selected = new boolean[items.length];

        for (int i = 0; i < selected.length; i++) {
            if (checkedItem == i) {
                selected[i] = true;
            }
        }

        addItems(items, selected, listener);
    }

    public void addMultiChoiceItem(CharSequence items, boolean checkedItems) {
        if (items == null) return;
        setCurrentDialogType(mMultiChoiceItemDialogType);
        mDialogType.addItem(items.toString(), checkedItems);
    }

    public void setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
                                    final DialogInterface.OnMultiChoiceClickListener listener) {
        if (items == null) return;
        setCurrentDialogType(mMultiChoiceItemDialogType);
        mDialogType.addItems(items, listener, checkedItems);
    }

    @Override
    public void cancel() {
        mDialog.dismiss();
    }


    private void setCurrentDialogType(DialogType type) {
        if (mDialogType == mMessageItemDialogType) {
            mDialogType = type;
            changeDialogType(mDialogType);
        }
    }

    private void changeDialogType(DialogType type) {
        type.copyDialogType(mMessageItemDialogType);
    }

    @Override
    public void dismiss() {
        mDialog.dismiss();
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
    }

    public Button getButton(int whichButton) {
        return mDialogType.getButton(whichButton);
    }

    public void setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        mOnKeyListener = onKeyListener;

        mDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode, KeyEvent event) {
                if (mOnKeyListener != null) {
                    return mOnKeyListener.onKey(DropDownDialog.this, keyCode, event);
                }

                return false;
            }
        });
    }

}

