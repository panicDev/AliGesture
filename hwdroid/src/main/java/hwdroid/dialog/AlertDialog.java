package hwdroid.dialog;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import hwdroid.dialog.DialogInterface.OnCancelListener;
import hwdroid.dialog.DialogInterface.OnDismissListener;
import hwdroid.dialog.DialogInterface.OnShowListener;

/**
 * A subclass of Dialog that can display one, two or three buttons.
 * use like android.app.AlertDialog.
 */
public class AlertDialog extends Dialog {
    /**
     * The identifier for the positive button.
     */
    public static final int BUTTON_POSITIVE = -1;

    /**
     * The identifier for the negative button.
     */
    public static final int BUTTON_NEGATIVE = -2;

    /**
     * The identifier for the neutral button.
     */
    public static final int BUTTON_NEUTRAL = -3;

    public DropDownDialog mXXDropDownDialog;

    public AlertDialog(Context context) {
        this(context, 0);
    }

    public AlertDialog(Context context, int attr) {
        super(context, attr);
        initXXDropDownDialog(context);
    }

    private void initXXDropDownDialog(Context context) {
        mXXDropDownDialog = new DropDownDialog(context);
    }

    @Override
    public void setCancelMessage(Message msg) {
        // TODO Auto-generated method stub
    }

    /**
     * Sets whether the dialog is cancelable or not.  Default is true.
     */
    @Override
    public void setCancelable(boolean flag) {
        mXXDropDownDialog.setCancelable(flag);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        mXXDropDownDialog.setCanceledOnTouchOutside(cancel);
    }

    @Override
    public void setDismissMessage(Message msg) {
        // TODO Auto-generated method stub
    }

    /**
     * Sets the callback that will be called if the dialog is canceled.
     */
    @Override
    public void setOnCancelListener(OnCancelListener listener) {
        mXXDropDownDialog.setOnCancelListener(listener);
    }

    /**
     * Sets the callback that will be called when the dialog is dismissed for any reason.
     */
    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        mXXDropDownDialog.setOnDismissListener(listener);
    }

    /**
     * Sets the callback that will be called if a key is dispatched to the dialog.
     */
    @Override
    public void setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        mXXDropDownDialog.setOnKeyListener(onKeyListener);
    }

    /**
     * show dialog
     */
    @Override
    public void show() {
        mXXDropDownDialog.showDialog();
    }

    /**
     * set the title resources id displayed in the dialog.
     */
    public void setTitle(int titleId) {
        mXXDropDownDialog.setTitle(titleId);
    }

    /**
     * set the title string displayed in the dialog.
     */
    public void setTitle(CharSequence title) {
        mXXDropDownDialog.setTitle(title);
    }

    /**
     * set custom view displayed in the dialog.
     */
    public void setView(View v) {
        mXXDropDownDialog.setView(v);
    }

    /**
     * get the action button.
     *
     * @param whichButton
     * @return button instance.
     */
    public Button getButton(int whichButton) {
        return mXXDropDownDialog.getButton(whichButton);
    }

    /**
     * dismiss the dialog.
     */
    @Override
    public void dismiss() {
        mXXDropDownDialog.callDismiss();
    }

    public void setButton(int whichButton, CharSequence text, DialogInterface.OnClickListener listener) {
        if (whichButton == BUTTON_POSITIVE) {
            mXXDropDownDialog.setButton1(text, listener);
        } else if (whichButton == BUTTON_NEGATIVE) {
            mXXDropDownDialog.setButton2(text, listener);
        }
    }

    public void setButton(CharSequence text, DialogInterface.OnClickListener listener) {
        mXXDropDownDialog.setButton1(text, listener);
    }

    public void setButton2(CharSequence text, DialogInterface.OnClickListener listener) {
        mXXDropDownDialog.setButton2(text, listener);
    }

    public void setButton3(CharSequence text, DialogInterface.OnClickListener listener) {
        mXXDropDownDialog.setButton3(text, listener);
    }

    public View getCustomView() {
        return null;
    }

    @Override
    public Window getWindow() {
        return mXXDropDownDialog.getWindow();
    }

    @Override
    public boolean isShowing() {
        return mXXDropDownDialog.isShowing();
    }

    public void setOnShowListener(OnShowListener onShowListener) {

    }

    public void cancel() {
        mXXDropDownDialog.cancel();
    }

    public void setMessage(CharSequence message) {
        mXXDropDownDialog.setMessage(message);
    }

    public static class Builder {
        Context mContext;
        AlertDialog mDialog;

        public Builder(Context context) {
            mContext = context;
            mDialog = new AlertDialog(mContext);
        }

        public Context getContext() {
            return mContext;
        }

        /**
         * Set the title displayed in the  Dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(int titleId) {
            mDialog.mXXDropDownDialog.setTitle(titleId);
            return this;
        }

        /**
         * Set the title displayed in the  Dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(CharSequence title) {
            mDialog.mXXDropDownDialog.setTitle(title);
            return this;
        }

        /**
         * Set the title using the custom view .
         *
         * @param customTitleView The custom view to use as the title.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCustomTitle(View customTitleView) {
            //TODO: this case is worning. don't set customTitleView to customview!!!!!
            mDialog.mXXDropDownDialog.setView(customTitleView);
            return this;
        }

        /**
         * Set the message to display using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(int messageId) {
            mDialog.mXXDropDownDialog.setMessage(messageId);
            return this;
        }

        /**
         * Set the message to display.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(CharSequence message) {
            mDialog.mXXDropDownDialog.setMessage(message);
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param listener The  DialogInterface.OnClickListener to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(final DialogInterface.OnClickListener listener) {
            mDialog.mXXDropDownDialog.setButton1(android.R.string.ok, listener);
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param textId   The resource id of the text to display in the positive button
         * @param listener The DialogInterface.OnClickListener to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(int textId, final DialogInterface.OnClickListener listener) {
            mDialog.mXXDropDownDialog.setButton1(textId, listener);
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param text     The text to display in the positive button
         * @param listener The  DialogInterface.OnClickListener to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(CharSequence text, final DialogInterface.OnClickListener listener) {
            mDialog.mXXDropDownDialog.setButton1(text, listener);
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         *
         * @param listener The DialogInterface.OnClickListener to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(final DialogInterface.OnClickListener listener) {
            mDialog.mXXDropDownDialog.setButton2(android.R.string.cancel, listener);
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         *
         * @param textId   The resource id of the text to display in the negative button
         * @param listener The DialogInterface.OnClickListener to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(int textId, final DialogInterface.OnClickListener listener) {
            mDialog.mXXDropDownDialog.setButton2(textId, listener);
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         *
         * @param text     The text to display in the negative button
         * @param listener The DialogInterface.OnClickListener to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(CharSequence text, final DialogInterface.OnClickListener listener) {
            mDialog.mXXDropDownDialog.setButton2(text, listener);
            return this;
        }

        /**
         * Set a listener to be invoked when the neutral button of the dialog is pressed.
         *
         * @param listener The  DialogInterface.OnClickListener to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(final DialogInterface.OnClickListener listener) {
            mDialog.mXXDropDownDialog.setButton3(android.R.string.cancel, listener);
            return this;
        }

        /**
         * Set a listener to be invoked when the neutral button of the dialog is pressed.
         *
         * @param textId   The resource id of the text to display in the neutral button
         * @param listener The  DialogInterface.OnClickListener to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(int textId, final DialogInterface.OnClickListener listener) {
            mDialog.mXXDropDownDialog.setButton3(textId, listener);
            return this;
        }

        /**
         * Set a listener to be invoked when the neutral button of the dialog is pressed.
         *
         * @param text     The text to display in the neutral button
         * @param listener The  DialogInterface.OnClickListener to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(CharSequence text, final DialogInterface.OnClickListener listener) {
            mDialog.mXXDropDownDialog.setButton3(text, listener);
            return this;
        }

        /**
         * Sets whether the dialog is cancelable or not.  Default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCancelable(boolean cancelable) {
            mDialog.mXXDropDownDialog.setCancelable(cancelable);
            return this;
        }

        /**
         * Sets the callback that will be called if the dialog is canceled.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */

        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
            mDialog.setOnCancelListener(onCancelListener);
            return this;
        }

        /**
         * Sets the callback that will be called if a key is dispatched to the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
            mDialog.setOnKeyListener(onKeyListener);
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of the
         * selected item via the supplied listener.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setItems(CharSequence[] items, final DialogInterface.OnClickListener listener) {
            mDialog.mXXDropDownDialog.addItems(items, listener);
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content,
         * you will be notified of the selected item via the supplied listener.
         * The list will have a check mark displayed to the right of the text
         * for each checked item. Clicking on an item in the list will not
         * dismiss the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param items        the text of the items to be displayed in the list.
         * @param checkedItems specifies which items are checked. It should be null in which case no
         *                     items are checked. If non null it must be exactly the same length as the array of
         *                     items.
         * @param listener     notified when an item on the list is clicked. The dialog will not be
         *                     dismissed when an item is clicked. It will only be dismissed if clicked on a
         *                     button, if no buttons are supplied it's up to the user to dismiss the dialog.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
                                           final DialogInterface.OnMultiChoiceClickListener listener) {
            mDialog.mXXDropDownDialog.setMultiChoiceItems(items, checkedItems, listener);
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of
         * the selected item via the supplied listener. The list will have a check mark displayed to
         * the right of the text for the checked item. Clicking on an item in the list will not
         * dismiss the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param items       the items to be displayed.
         * @param checkedItem specifies which item is checked. If -1 no items are checked.
         * @param listener    notified when an item on the list is clicked. The dialog will not be
         *                    dismissed when an item is clicked. It will only be dismissed if clicked on a
         *                    button, if no buttons are supplied it's up to the user to dismiss the dialog.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, final DialogInterface.OnClickListener listener) {
            mDialog.mXXDropDownDialog.setSingleChoiceItems(items, checkedItem, listener);
            return this;
        }

        /**
         * Set a custom view to be the contents of the Dialog,
         */
        public Builder setView(View v) {
            mDialog.setView(v);
            return this;
        }

        /**
         * Creates a AlertDialog with the arguments supplied to this builder.
         */
        public AlertDialog create() {
            return mDialog;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of the
         * selected item via the supplied listener. This should be an array type i.e. R.array.foo
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setItems(int itemsId,
                                hwdroid.dialog.DialogInterface.OnClickListener listener) {
            // TODO Auto-generated method stub
            CharSequence[] items = this.mContext.getResources().getTextArray(itemsId);
            mDialog.mXXDropDownDialog.addItems(items, listener);
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of the
         * selected item via the supplied listener.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSingleChoiceItems(int itemsId, int checkedItem,
                                            hwdroid.dialog.DialogInterface.OnClickListener listener) {
            // TODO Auto-generated method stub
            CharSequence[] items = this.mContext.getResources().getTextArray(itemsId);
            mDialog.mXXDropDownDialog.setSingleChoiceItems(items, checkedItem, listener);
            return null;
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnDismissListener(OnDismissListener listener) {
            mDialog.mXXDropDownDialog.setOnDismissListener(listener);
            return this;
        }

    }
}
