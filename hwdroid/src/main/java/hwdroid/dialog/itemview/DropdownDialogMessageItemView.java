package hwdroid.dialog.itemview;

import hwdroid.dialog.item.DropdownDialogMessageItem;
import hwdroid.widget.item.Item;
import hwdroid.widget.itemview.ItemView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hw.droid.R;

public class DropdownDialogMessageItemView extends LinearLayout implements ItemView {

    protected TextView mTextView;

    public DropdownDialogMessageItemView(Context context) {
        this(context, null);
    }

    public DropdownDialogMessageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void prepareItemView() {
        mTextView = (TextView) findViewById(R.id.hw_text);
    }

    @Override
    public void setObject(Item object) {
        DropdownDialogMessageItem item = (DropdownDialogMessageItem) object;
        setTextView(item.mText);
    }

    public void setTextView(String text) {
        if (mTextView != null) {
            mTextView.setText(text);
        }
    }

    @Override
    public void setSubTextSingleLine(boolean enabled) {
        // TODO Auto-generated method stub

    }
}
