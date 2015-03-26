
package hwdroid.preference;

import android.content.Context;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.hw.droid.R;

import hwdroid.widget.ActionSheet;
import hwdroid.widget.ActionSheet.ActionButton;
import hwdroid.widget.ActionSheet.MultiChoiceListener;

import java.util.HashSet;
import java.util.Set;

public class ActionMultiSelectListPreference extends MultiSelectListPreference {
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private Set<String> mValues = new HashSet<String>();
    private Set<String> mNewValues = new HashSet<String>();
    private boolean mPreferenceChanged;
    private Context mContext;
    private ActionSheet mActionSheet;

    public ActionMultiSelectListPreference(Context context) {
        super(context);
        mContext = context;
    }

    public ActionMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void showDialog(Bundle state) {
        mEntries = getEntries();
        mEntryValues = getEntryValues();
        mValues = getValues();

        if (mEntries == null || mEntryValues == null) {
            throw new IllegalStateException(
                    "ActionMultiSelectListPreference requires an entries array and "
                            + "an entryValues array.");
        }

        boolean[] checkedItems = getSelectedItems();
        String[] str = new String[mEntries.length];
        int length = mEntries.length;
        for (int i = 0; i < length; i++) {
            str[i] = mEntries[i].toString();
        }

        mActionSheet = new ActionSheet(mContext);
        mActionSheet.setMultiChoiceItems(str, checkedItems, new MultiChoiceListener() {

            @Override
            public void onDismiss(ActionSheet actionSheet) {
                mActionSheet = null;
            }

            @Override
            public void onItemToggle(int which) {

            }

            @Override
            public void onConfirm(boolean[] itemStatus) {
                for (int i = 0; i < itemStatus.length; i++) {
                    if (itemStatus[i]) {
                        mPreferenceChanged |= mNewValues.add(mEntryValues[i].toString());
                    } else {
                        mPreferenceChanged |= mNewValues.remove(mEntryValues[i].toString());
                    }
                }
                onDialogClosed(true);
            }
        }, new ActionButton(mContext.getString(android.R.string.ok), mContext.getResources()
                .getColor(R.color.hw_actionsheet_text_color), new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));

        mNewValues.clear();
        mNewValues.addAll(mValues);
        mActionSheet.showWithDialog();
    }

    private boolean[] getSelectedItems() {
        final CharSequence[] entries = getEntryValues();
        final int entryCount = entries.length;
        final Set<String> values = getValues();
        boolean[] result = new boolean[entryCount];

        for (int i = 0; i < entryCount; i++) {
            result[i] = values.contains(entries[i].toString());
        }

        return result;
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

        if (positiveResult && mPreferenceChanged) {
            final Set<String> values = mNewValues;
            if (callChangeListener(values)) {
                setValues(values);
            }
        }
        mPreferenceChanged = false;
    }

    public void onActivityDestroy() {
        if (mActionSheet == null) {
            return;
        }

        mActionSheet.dismiss();
        super.onActivityDestroy();
    }
}
