
package hwdroid.widget;

import hwdroid.util.Config;
import hwdroid.widget.item.Item;
import hwdroid.widget.item.Item.Type;
import hwdroid.widget.itemview.ItemView;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public abstract class ItemCursorAdapter extends CursorAdapter {
    private Type mType;
    private ArrayList<SelectedValue> mSelectedItems;
    private int mSelectedCount;

    public class SelectedValue {
        public boolean isSelected = false;

        public SelectedValue(boolean value) {
            isSelected = value;
        }
    }

    @SuppressWarnings("deprecation")
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c);
        initSelectedArray(c);
    }

    public ItemCursorAdapter(Context context, Cursor c, Type type) {
        this(context, c);
        mType = type;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if (Config.HW_INFO_LOGS_ENABLED)
            Log.i("hwdroid", "new View start()");

        Item item = setupItem(context, cursor);
        ItemView view = item.newView(context, parent);
        view.prepareItemView();

        if (Config.HW_INFO_LOGS_ENABLED)
            Log.i("hwdroid", "new View end()");

        return (View) view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (Config.HW_INFO_LOGS_ENABLED)
            Log.i("hwdroid", "bindView start()");

        Item item = setupItem(context, cursor);

        if (item == null) {
            item = setupItem(context, cursor);
        }

        if (mType != Type.NORMAL_MODE) {
            item.setTypeMode(mType);
        }

        item.setChecked(getSelectedStatus(cursor.getPosition()));

        ((ItemView) view).setObject(item);
        view.setTag(item);

        if (Config.HW_INFO_LOGS_ENABLED)
            Log.i("hwdroid", "bindView end()");
    }

    public abstract Item setupItem(Context context, Cursor cursor);

    public void setTypeMode(Type type) {
        mType = type;
    }

    public Type getTypeMode() {
        return mType;
    }

    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        initSelectedArray(cursor);
    }

    private void initSelectedArray(Cursor cursor) {
        mSelectedItems = new ArrayList<SelectedValue>();

        for (int i = 0; i < cursor.getCount() && cursor != null; i++) {
            mSelectedItems.add(new SelectedValue(false));
        }
    }

    public void setSelectedItem(int pos) {
        if (mSelectedItems == null) return;

        mSelectedItems.get(pos).isSelected = !mSelectedItems.get(pos).isSelected;

        if (mSelectedItems.get(pos).isSelected == true) {
            mSelectedCount++;
        } else {
            mSelectedCount--;
        }
    }

    public void doAllSelected(boolean select) {
        if (mSelectedItems == null) return;

        for (int i = 0; i < mSelectedItems.size(); i++) {
            mSelectedItems.get(i).isSelected = select;
        }

        if (select == true) {
            mSelectedCount = mSelectedItems.size();
        } else {
            mSelectedCount = 0;
        }
    }

    public boolean[] getSelectedList() {
        if (mSelectedItems == null && mSelectedItems.size() < 1) return null;

        boolean[] selected = new boolean[mSelectedItems.size()];
        for (int i = 0; i < mSelectedItems.size(); i++) {
            selected[i] = mSelectedItems.get(i).isSelected;
        }

        return selected;
    }

    public boolean getSelectedStatus(int pos) {
        if (mSelectedItems != null && mSelectedItems.size() > pos && pos >= 0) {
            return mSelectedItems.get(pos).isSelected;
        } else {
            return false;
        }
    }

    public int getSelectedCounts() {
        return mSelectedCount;
    }
}
