package hwdroid.dialog.itemview;

import hwdroid.dialog.item.DropdownDialogTextItem;
import hwdroid.widget.item.Item;
import hwdroid.widget.itemview.ItemView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hw.droid.R;

public class DropdownDialogTextItemView extends LinearLayout implements ItemView {

    protected TextView mTextView;

    public DropdownDialogTextItemView(Context context) {
        this(context, null);
    }

    public DropdownDialogTextItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void prepareItemView() {
        mTextView = (TextView) findViewById(R.id.hw_text);
    }

    @Override
    public void setObject(Item object) {
        DropdownDialogTextItem item = (DropdownDialogTextItem) object;
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
