package hwdroid.widget.FooterBar;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.hw.droid.R;

import hwdroid.widget.item.Item;
import hwdroid.widget.item.TextItem;
import hwdroid.widget.itemview.TextItemView;

import java.util.List;

public class FooterBarPopupListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Item> mItems;
    private ListView mListView;

    public FooterBarPopupListAdapter(ListView listView, Context context, List<Item> items) {
        this.mListView = listView;
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TextItem item = (TextItem) getItem(position);
        TextItemView cell = (TextItemView) convertView;

        if (cell == null) {
            cell = (TextItemView) item.newView(mContext, null);
            cell.setBackgroundResource(R.drawable.hw_popup_list_item_selector);
            cell.prepareItemView();
        }

        cell.setObject(item);

        if (item.isEnabled()) {

            cell.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    /*
                     *  as every row is still receiving their touches
                     *  we can use this to manually trigger onItemClick
                     *  since it doesn't firing in popupWindow.setFocusable(false)  
                     */
                    mListView.getOnItemClickListener().onItemClick(mListView, v, position, getItemId(position));

                }
            });
        } else {
            cell.setOnClickListener(null);
        }

        return (View) cell;
    }

}
