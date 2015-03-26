package com.way.gesture.activity;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.way.gesture.GestureCommond;
import com.way.gesture.R;
import com.way.ui.swipeback.SwipeBackActivity;
import com.way.util.AppUtil;
import com.way.util.ApplicationInfo;
import com.way.util.MyLog;

public class ActivitySelectApp extends SwipeBackActivity implements
        OnItemClickListener {
    private ListView mAppListView;
    private ProgressBar mProgressBar;
    private List<ApplicationInfo> mAllApps;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
//		AppUtil.transWindows(this,
//				getResources().getColor(R.color.indicator_selected_color));
        setActivityContentView(R.layout.select_app);
        showBackKey(true);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAppListView = ((ListView) findViewById(android.R.id.list));
        mAppListView.setOnItemClickListener(this);
        new GetAppTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAllApps.clear();
        mAllApps = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        ApplicationInfo info = mAllApps.get(position);
        ComponentName componentName = info.componentName;
        Intent intent = new Intent();
        intent.setClass(this, ActivityCreateGesture.class);
        intent.putExtra("AppName", info.title);
        intent.putExtra("packageName", componentName.getPackageName());
        intent.putExtra("activityName", componentName.getClassName());
        MyLog.d("packageInfo:", componentName.getPackageName() + "  : "
                + componentName.getClassName());
        intent.putExtra("commond", 0);
        setResult(GestureCommond.GestureSucess, intent);
        finish();
    }

    private class AppListAdapter extends BaseAdapter {
        private Context mContext;

        public AppListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mAllApps.size();
        }

        @Override
        public ApplicationInfo getItem(int position) {
            return mAllApps.get(position);
        }

        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.app_list_item, parent, false);
                holder = new ViewHolder();
                holder.appName = (TextView) convertView
                        .findViewById(R.id.textview1);
                holder.appIcon = (ImageView) convertView
                        .findViewById(R.id.imageview1);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            ApplicationInfo info = getItem(position);
            holder.appName.setText(info.title);
            holder.appIcon.setImageBitmap(info.iconBitmap);
            return convertView;
        }
    }

    private class ViewHolder {
        TextView appName;
        ImageView appIcon;
    }

    private class GetAppTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAppListView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mAllApps = AppUtil.getAllApps(ActivitySelectApp.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
            mAppListView.setVisibility(View.VISIBLE);
            mAppListView.setAdapter(new AppListAdapter(ActivitySelectApp.this));
        }

    }

}