
package hwdroid.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.util.AttributeSet;

import hwdroid.widget.ActionSheet;
import hwdroid.widget.ActionSheet.SingleChoiceListener;

public class ActionListPreference extends ListPreference {
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private String mValue;
    private int mClickedDialogEntryIndex;
    private ActionSheet mActionSheet;
    private Context mContext;
    /**
     * Which button was clicked.
     */
    private int mWhichButtonClicked;

    public ActionListPreference(Context context) {
        super(context);
        mContext = context;
        mWhichButtonClicked = DialogInterface.BUTTON_NEGATIVE;
    }

    public ActionListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mWhichButtonClicked = DialogInterface.BUTTON_NEGATIVE;
    }

    @Override
    protected void showDialog(Bundle state) {
        mEntries = getEntries();
        mEntryValues = getEntryValues();
        mValue = getValue();
        mClickedDialogEntryIndex = findIndexOfValue(mValue);

        if (mEntries == null || mEntryValues == null) {
            throw new IllegalStateException(
                    "ActionListPreference requires an entries array and an entryValues array.");
        }

        String[] str = new String[mEntries.length];
        int length = mEntries.length;
        for (int i = 0; i < length; i++) {
            str[i] = mEntries[i].toString();
        }

        mActionSheet = new ActionSheet(mContext);
        mActionSheet.setSingleChoiceItems(str, mClickedDialogEntryIndex,
                new SingleChoiceListener() {

                    @Override
                    public void onDismiss(ActionSheet actionSheet) {
                        mActionSheet = null;
                    }

                    @Override
                    public void onClick(int which) {
                        mClickedDialogEntryIndex = which;
                        mWhichButtonClicked = which;

                        onDialogClosed(mWhichButtonClicked != DialogInterface.BUTTON_NEGATIVE);
                    }

                });

        mActionSheet.showWithDialog();
    }

    @Override
    protected void onClick() {
        if (mActionSheet != null)
            return;

        showDialog(null);
    }

    /**
     * Gets the actionSheet that is shown by this preference.
     *
     * @return The actionSheet, or null if a actionSheet is not being shown.
     */
    public ActionSheet getActionSheet() {
        return mActionSheet;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult && mClickedDialogEntryIndex >= 0 && mEntryValues != null) {
            String value = mEntryValues[mClickedDialogEntryIndex].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }

    public void onActivityDestroy() {
        if (mActionSheet == null) {
            return;
        }

        mActionSheet.dismiss();
        super.onActivityDestroy();
    }
}
