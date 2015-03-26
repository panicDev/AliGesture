package hwdroid.dialog.itemview;

import hwdroid.dialog.item.DropdownDialogSingleChoiceItem;
import hwdroid.widget.item.Item;
import hwdroid.widget.itemview.ItemView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hw.droid.R;

public class DropdownDialogSingleChoiceItemView extends LinearLayout implements ItemView {
    protected TextView mTextView;
    private RadioButton mRadioButton;

    public DropdownDialogSingleChoiceItemView(Context context) {
        this(context, null);
    }

    public DropdownDialogSingleChoiceItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void prepareItemView() {
        mTextView = (TextView) findViewById(R.id.hw_text);
        mRadioButton = (RadioButton) findViewById(R.id.hw_radio);
    }

    @Override
    public void setObject(Item object) {
        DropdownDialogSingleChoiceItem item = (DropdownDialogSingleChoiceItem) object;
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
        return mRadioButton.isChecked();
    }

    @Override
    public void setSubTextSingleLine(boolean enabled) {
        // TODO Auto-generated method stub

    }
}
