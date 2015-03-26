package hwdroid.app;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.ListView;

public class HWPreferenceFragment extends PreferenceFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listview = getFragmentListView();
        if (listview != null) {
            listview.setPadding(0, 0, 0, 0);
        }
    }

    private ListView getFragmentListView() {
        ListView listView = null;
        View root = getView();

        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }

        View rawListView = root.findViewById(android.R.id.list);
        if (!(rawListView instanceof ListView)) {
            return null;
        }
        listView = (ListView) rawListView;

        return listView;
    }

    public void addPreferencesFromResourceImpl(int preferencesResId) {
        addPreferencesFromResource(preferencesResId);
    }

}
