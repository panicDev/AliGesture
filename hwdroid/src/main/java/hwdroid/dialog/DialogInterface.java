package hwdroid.dialog;

import android.view.KeyEvent;

/**
 * hide
 */
public interface DialogInterface {
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

    /**
     * @deprecated Use {@link #BUTTON_POSITIVE}
     */
    @Deprecated
    public static final int BUTTON1 = BUTTON_POSITIVE;

    /**
     * @deprecated Use {@link #BUTTON_NEGATIVE}
     */
    @Deprecated
    public static final int BUTTON2 = BUTTON_NEGATIVE;

    /**
     * @deprecated Use {@link #BUTTON_NEUTRAL}
     */
    @Deprecated
    public static final int BUTTON3 = BUTTON_NEUTRAL;

    public void cancel();

    public void dismiss();

    /**
     * Interface used to allow the creator of a dialog to run some code when the
     * dialog is canceled.
     * <p/>
     * This will only be called when the dialog is canceled, if the creator
     * needs to know when it is dismissed in general, use
     * {@link hwdroid.dialog.DialogInterface.OnDismissListener}.
     */
    public interface OnCancelListener {
        /**
         * This method will be invoked when the dialog is canceled.
         *
         * @param dialog The dialog that was canceled will be passed into the
         *               method.
         */
        public void onCancel(DialogInterface dialog);
    }

    /**
     * Interface used to allow the creator of a dialog to run some code when the
     * dialog is dismissed.
     */
    public interface OnDismissListener {
        /**
         * This method will be invoked when the dialog is dismissed.
         *
         * @param dialog The dialog that was dismissed will be passed into the
         *               method.
         */
        public void onDismiss(DialogInterface dialog);
    }

    /**
     * Interface used to allow the creator of a dialog to run some code when the
     * dialog is shown.
     */
    public interface OnShowListener {
        /**
         * This method will be invoked when the dialog is shown.
         *
         * @param dialog The dialog that was shown will be passed into the
         *               method.
         */
        public void onShow(DialogInterface dialog);
    }

    /**
     * Interface used to allow the creator of a dialog to run some code when an
     * item on the dialog is clicked..
     */
    public interface OnClickListener {
        /**
         * This method will be invoked when a button in the dialog is clicked.
         *
         * @param dialog The dialog that received the click.
         * @param which  The button that was clicked (e.g.
         *               {@link hwdroid.dialog.DialogInterface#BUTTON1}) or the position
         *               of the item clicked.
         */
        /* TODO: Change to use BUTTON_POSITIVE after API council */
        public void onClick(DialogInterface dialog, int which);
    }

    /**
     * Interface used to allow the creator of a dialog to run some code when an
     * item in a multi-choice dialog is clicked.
     */
    public interface OnMultiChoiceClickListener {
        /**
         * This method will be invoked when an item in the dialog is clicked.
         *
         * @param dialog    The dialog where the selection was made.
         * @param which     The position of the item in the list that was clicked.
         * @param isChecked True if the click checked the item, else false.
         */
        public void onClick(DialogInterface dialog, int which, boolean isChecked);
    }

    /**
     * Interface definition for a callback to be invoked when a key event is
     * dispatched to this dialog. The callback will be invoked before the key
     * event is given to the dialog.
     */
    public interface OnKeyListener {
        /**
         * Called when a key is dispatched to a dialog. This allows listeners to
         * get a chance to respond before the dialog.
         *
         * @param dialog  The dialog the key has been dispatched to.
         * @param keyCode The code for the physical key that was pressed
         * @param event   The KeyEvent object containing full information about
         *                the event.
         * @return True if the listener has consumed the event, false otherwise.
         */
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event);
    }
}

