package hwdroid.dialog.itemview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hw.droid.R;

import hwdroid.dialog.item.DropdownDialogMultiChoiceItem;
import hwdroid.widget.item.Item;
import hwdroid.widget.itemview.ItemView;

public class DropdownDialogMultiChoiceItemView extends LinearLayout implements ItemView {
    protected TextView mTextView;
    private CheckBox mRadioButton;

    public DropdownDialogMultiChoiceItemView(Context context) {
        this(context, null);
    }

    public DropdownDialogMultiChoiceItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void prepareItemView() {
        mTextView = (TextView) findViewById(R.id.hw_text);
        mRadioButton = (CheckBox) findViewById(R.id.hw_radio);
    }

    @Override
    public void setObject(Item object) {
        DropdownDialogMultiChoiceItem item = (DropdownDialogMultiChoiceItem) object;
        setTextView(item.mText);
        setChecked(item.isChecked());
    }

    public void setTextView(String text) {
        if (mTextView != null) {
            mTextView.setText(text);
        }
    }


    public void setChecked(boolean checked) {
        mRadioButton.setChecked(checked);
    }

    public boolean isChecked() {
        // TODO Auto-generated method stub
        return mRadioButton.isChecked();
    }

    @Override
    public void setSubTextSingleLine(boolean enabled) {
        // TODO Auto-generated method stub

    }
}
