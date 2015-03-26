package hwdroid.dialog.itemview;

import hwdroid.dialog.item.DropdownDialogCustomItem;
import hwdroid.widget.item.Item;
import hwdroid.widget.itemview.ItemView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class DropdownDialogCustomItemView extends LinearLayout implements ItemView {

    View mMainView;
    Context mContext;

    public DropdownDialogCustomItemView(Context context) {
        this(context, null);
    }

    public DropdownDialogCustomItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public void prepareItemView() {
    }

    @Override
    public void setObject(Item object) {
        DropdownDialogCustomItem item = (DropdownDialogCustomItem) object;
        setCustomView(item.mCustomView);
    }

    public void setCustomView(int viewId) {
        mMainView = LayoutInflater.from(mContext).inflate(viewId, null, false);
        addView(mMainView);
    }

    public View getCustomView() {
        return mMainView;
    }

    @Override
    public void setSubTextSingleLine(boolean enabled) {
        // TODO Auto-generated method stub

    }
}
