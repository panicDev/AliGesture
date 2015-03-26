package hwdroid.widget;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

/**
 * <p>
 * The base implementation of an Adapter to use with a {@link PagedView}.
 * Clients may create classes that extends from this base implementation. The
 * work consists on overriding the {@link hwdroid.widget.PagedAdapter#getCount()} and
 * {@link hwdroid.widget.PagedAdapter#getView(int, android.view.View, android.view.ViewGroup)} methods.
 * </p>
 */
public abstract class PagedAdapter implements Adapter {

    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public abstract int getCount();

    public abstract Object getItem(int position);

    public abstract long getItemId(int position);

    public boolean hasStableIds() {
        throw new UnsupportedOperationException("hasStableIds(int) is not supported in the context of a SwipeAdapter");
    }

    public abstract View getView(int position, View convertView, ViewGroup parent);

    public final int getItemViewType(int position) {
        throw new UnsupportedOperationException("getItemViewType(int) is not supported in the context of a SwipeAdapter");
    }

    public final int getViewTypeCount() {
        throw new UnsupportedOperationException("getViewTypeCount() is not supported in the context of a SwipeAdapter");
    }

    public final boolean isEmpty() {
        throw new UnsupportedOperationException("isEmpty() is not supported in the context of a SwipeAdapter");
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

}
