package hwdroid.app;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.hw.droid.R;

/**
 * <p>
 * an abstract class extend from activity.
 * </p>
 * help the custom activity create headerbar and footerbar .
 * <p/>
 */
public abstract class HWListActivity extends HWActivity {
    private ListAdapter mAdapter;
    private ListView mListView;
    private View mEmptyView;

    private Handler mHandler = new Handler();
    private boolean mFinishedStart = false;

    private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mListView.focusableViewAvailable(mListView);
        }
    };

    public HWListActivity() {
        super();
    }

    /**
     * This method will be called when an item in the list is selected.
     * Subclasses should override. Subclasses can call
     * getListView().getItemAtPosition(position) if they need to access the data
     * associated with the selected item.
     *
     * @param l        The ListView where the click happened
     * @param v        The view that was clicked within the ListView
     * @param position The position of the view in the list
     * @param id       The row id of the item that was clicked
     */
    protected void onListItemClick(ListView l, View v, int position, long id) {
    }

    /**
     * Set the currently selected list item to the specified position with the
     * adapter's data
     *
     * @param position The position to select in the managed ListView
     */
    public void setSelection(int position) {
        mListView.setSelection(position);
    }

    /**
     * Get the position of the currently selected list item.
     *
     * @return The position of the currently selected ListView item.
     */
    public int getSelectedItemPosition() {
        return mListView.getSelectedItemPosition();
    }

    /**
     * Get the ListAdapter row ID of the currently selected list item.
     *
     * @return The identifier of the selected ListView item.
     */
    public long getSelectedItemId() {
        return mListView.getSelectedItemId();
    }

    /**
     * Get the activity's ListView widget.
     *
     * @return The ListView managed by the current {@link hwdroid.app.HWListActivity}
     */
    public ListView getListView() {
        ensureLayout();
        return mListView;
    }

    /**
     * Get the ListAdapter associated with this activity's ListView.
     *
     * @return The ListAdapter currently associated to the underlying ListView
     */
    public ListAdapter getListAdapter() {
        return mAdapter;
    }

    /**
     * Provides the Adapter for the ListView handled by this
     * {@link hwdroid.app.HWListActivity}
     *
     * @param adapter The ListAdapter to set.
     */
    public void setListAdapter(ListAdapter adapter) {
        synchronized (this) {
            ensureLayout();
            mAdapter = adapter;
            mListView.setAdapter(adapter);
        }
    }

    @Override
    public int createLayout() {
        return R.layout.hw_list_content_normal;
    }

    @Override
    protected boolean verifyLayout() {
        return super.verifyLayout() && mListView != null;
    }

    @Override
    public void onPreContentChanged() {
        super.onPreContentChanged();

        mEmptyView = findViewById(android.R.id.empty);
        mListView = (ListView) findViewById(android.R.id.list);
        if (mListView == null) {
            throw new RuntimeException("Your content must have a ListView whose id attribute is " + "'android.R.id.list'");
        }
    }

    @Override
    public void onPostContentChanged() {
        super.onPostContentChanged();

        if (mEmptyView != null) {
            mListView.setEmptyView(mEmptyView);
        }
        mListView.setOnItemClickListener(mOnItemClickListener);
        if (mFinishedStart) {
            setListAdapter(mAdapter);
        }
        mHandler.post(mRequestFocus);
        mFinishedStart = true;
    }

    @Override
    public void setActivityContentView(int resID) {
        throwSetActionBarContentViewException();
    }

    @Override
    public void setActivityContentView(View view, LayoutParams params) {
        throwSetActionBarContentViewException();
    }

    @Override
    public void setActivityContentView(View view) {
        throwSetActionBarContentViewException();
    }

    private void throwSetActionBarContentViewException() {
        throw new UnsupportedOperationException(
                "The setActionBarContentView method is not supported for HWListActivity. In order to get a custom layout you must return a layout identifier in createLayout");
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            onListItemClick((ListView) parent, v, position, id);
        }
    };

}
