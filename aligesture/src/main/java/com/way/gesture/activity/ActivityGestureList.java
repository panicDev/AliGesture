package com.way.gesture.activity;

import hwdroid.app.HWActivity;
import hwdroid.dialog.AlertDialog;
import hwdroid.dialog.DialogInterface;
import hwdroid.widget.ActionBar.ActionBarView.OnRightWidgetItemClick2;
import hwdroid.widget.FooterBar.FooterBarMenu;
import hwdroid.widget.FooterBar.FooterBarType.OnFooterItemClick;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.way.gesture.GestureCommond;
import com.way.gesture.GestureService;
import com.way.gesture.R;
import com.way.gesture.bean.GestureObject;
import com.way.gesture.util.GestureDataManager;
import com.way.gesture.util.GestureImageViewHandler;
import com.way.gesture.view.GestureImageView;
import com.way.gesture.view.HelpDialog;
import com.way.util.MyLog;
import com.way.widget.SlidingLeftViewGroup;

public class ActivityGestureList extends HWActivity implements
        CompoundButton.OnCheckedChangeListener {
    private static final int MENU_ADD = 100;
    private static final int MENU_HELP = 200;
    GestureImageViewHandler mHandler;
    ArrayList<GestureObject> mGestureObjectLists;
    private boolean mEnable = false;
    private HelpDialog mHelpDialog = null;
    GestureDataManager mGestureDataManager;
    private FooterBarMenu mFooterBarMenu;

    private void update(boolean enable) {
        if (enable) {
            findViewById(android.R.id.empty).setVisibility(View.INVISIBLE);
            getListView().setVisibility(View.VISIBLE);
            Settings.System.putInt(getContentResolver(), "way_gesture_switch",
                    1);
            return;
        }
        findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
        getListView().setVisibility(View.INVISIBLE);
        Settings.System.putInt(getContentResolver(), "way_gesture_switch", 0);
    }

    private ListView getListView() {
        return (ListView) findViewById(android.R.id.list);
    }

    private GestureObject mLastDeleteGestureObject;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != GestureCommond.GestureSucess)
            return;
        switch (requestCode) {
            case GestureCommond.GestureCreateNew:// 103
                updateList();
                Intent intent = new Intent();
                intent.putExtra("list_count", mGestureObjectLists.size());
                setResult(GestureCommond.GestureSucess, intent);
                break;
            case GestureCommond.GestureEdit:// 104
                updateList();
                if (mLastDeleteGestureObject != null) {
                    MyLog.i("liweiping", "mLastDeleteGestureObject.id = "
                            + mLastDeleteGestureObject.mRecorderID);
                    mGestureDataManager.delete(mLastDeleteGestureObject);
                    mLastDeleteGestureObject = null;
                    setResult(GestureCommond.GestureSucess);
                    return;
                }
                Intent intent1 = new Intent();
                intent1.putExtra("list_count", mGestureObjectLists.size());
                setResult(GestureCommond.GestureSucess, intent1);
                break;
            case GestureCommond.GestureEditJob:// 105
                setResult(GestureCommond.GestureSucess);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton,
                                 boolean isChecked) {
        mEnable = isChecked;
        update(isChecked);
        sendBroadcast(new Intent("com.way.gesture.SWITCH_CHANGE"));
        if (mFooterBarMenu != null)
            mFooterBarMenu.setItemEnable(MENU_ADD, mEnable);
        if (isChecked)
            startService(new Intent(ActivityGestureList.this,
                    GestureService.class));
        else
            stopService(new Intent(ActivityGestureList.this,
                    GestureService.class));
        MyLog.d("gesture", "onCheckedChanged :" + isChecked);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // AppUtil.transWindows(this,
        // getResources().getColor(R.color.indicator_selected_color));
        setActivityContentView(R.layout.activity_gesture_list);
        showBackKey(true);
        CheckBox actionBarSwitch = new CheckBox(this);
        actionBarSwitch.setOnCheckedChangeListener(this);
        mEnable = Settings.System.getInt(getContentResolver(),
                "way_gesture_switch", 1) == 1;
        actionBarSwitch.setChecked(mEnable);
        setRightWidgetView(actionBarSwitch);
        setRightWidgetClickListener2(new OnRightWidgetItemClick2() {

            @Override
            public void onRightWidgetItemClick(View arg0) {
                CheckBox checkBox = (CheckBox) arg0;
                checkBox.performClick();
            }
        });
        addFooterBarMenu();
        mGestureDataManager = GestureDataManager.defaultManager(this);
        mHandler = new GestureImageViewHandler();
    }

    private void addFooterBarMenu() {
        mFooterBarMenu = new FooterBarMenu(this);
        mFooterBarMenu.addItem(MENU_ADD,
                getResources().getString(R.string.getsure_option_add),
                getResources().getDrawable(R.drawable.add_menu));
        mFooterBarMenu.addItem(MENU_HELP,
                getResources().getString(R.string.getsure_help_menu),
                getResources().getDrawable(R.drawable.help_menu));
        mFooterBarMenu.setPrimaryItemCount(2);
        mFooterBarMenu.updateItems();
        getFooterBarImpl().addView(mFooterBarMenu);
        getFooterBarImpl().setVisibility(View.VISIBLE);

        mFooterBarMenu.setOnFooterItemClick(new OnFooterItemClick() {
            @Override
            public void onFooterItemClick(View view, int id) {
                switch (id) {
                    case MENU_ADD:
                        Intent intent = new Intent();
                        intent.setClass(ActivityGestureList.this,
                                ActivityAddOption.class);
                        startActivityForResult(intent,
                                GestureCommond.GestureCreateNew);
                        break;
                    case MENU_HELP:
                        if (mHelpDialog == null)
                            mHelpDialog = new HelpDialog();
                        mHelpDialog.show(ActivityGestureList.this, false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeAll();
        mHelpDialog = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if (mHelpDialog != null)
        // mHelpDialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.d("GestureAction", "onResume");
        updateList();
        if (mHelpDialog == null) {
            mHelpDialog = new HelpDialog();
            mHelpDialog.show(ActivityGestureList.this, true);
        }
    }

    public void updateList() {
        mGestureObjectLists = mGestureDataManager.getGestureArrayList();
        MyLog.w("activity list ", "count : " + mGestureObjectLists.size());
        ((ListView) findViewById(android.R.id.list))
                .setAdapter(new GestureListAdapter(this));
        update(mEnable);
    }

    private class GestureListAdapter extends BaseAdapter implements
            SlidingLeftViewGroup.OnSlideListener {
        private SlidingLeftViewGroup mSlidingItem;
        private final LayoutInflater mInflater;

        public GestureListAdapter(Context context) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mGestureObjectLists.size();
        }

        @Override
        public Object getItem(int position) {
            return mGestureObjectLists.get(position);
        }

        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.multiview, parent,
                        false);
                holder = new ViewHolder();

                holder.mListItem = (SlidingLeftViewGroup) convertView
                        .findViewById(R.id.mymultiViewGroup);
                holder.mLL = (LinearLayout) convertView
                        .findViewById(R.id.item_ll);
                holder.mName = (TextView) convertView
                        .findViewById(R.id.textview);
                holder.mGestureImageView = (GestureImageView) convertView
                        .findViewById(R.id.gestureImageView);
                holder.mDel = (Button) convertView
                        .findViewById(R.id.gestures_delete);
                holder.mEditName = (Button) convertView
                        .findViewById(R.id.gestures_edit_name);
                holder.mEditGesture = (Button) convertView
                        .findViewById(R.id.gestures_edit_gesture);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();

            setupHolder(holder, convertView, position);
            return convertView;
        }

        private void setupHolder(ViewHolder holder, View convertView,
                                 int position) {
            final GestureObject gestureObject = mGestureObjectLists
                    .get(position);
            int style = gestureObject.mGestureType;
            if (style == 0) {
                String prefxAppName = ActivityGestureList.this
                        .getString(R.string.startapp);
                holder.mName.setText(prefxAppName + gestureObject.mAppName);
            } else if (style == 1) {
                String prefixCallName = ActivityGestureList.this
                        .getString(R.string.gesturelistcallto);
                holder.mName.setText(prefixCallName + gestureObject.mUserName);
            } else {
                String prefixSmsName = ActivityGestureList.this
                        .getString(R.string.gesturelistsendsms);
                holder.mName.setText(prefixSmsName + gestureObject.mUserName);
            }
            holder.mGestureImageView.setGesture(gestureObject);

            holder.mPos = position;
            holder.mLL.setTag(holder);

            holder.mDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDeleteMenuItem(gestureObject);

                }
            });
            holder.mEditName.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            startEditName(gestureObject);
                        }
                    }, 300);

                    if (mSlidingItem != null) {
                        mSlidingItem.MoveBack(false);
                    }
                }
            });
            holder.mEditGesture.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            startEditGesture(gestureObject);
                        }
                    }, 300);
                    if (mSlidingItem != null) {
                        mSlidingItem.MoveBack(false);
                    }
                }
            });

            holder.mLL.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Intent intent = new Intent();
                            intent.setClass(ActivityGestureList.this,
                                    ActivityCreateGesture.class);
                            intent.putExtra("recorderID",
                                    gestureObject.mRecorderID);
                            intent.putExtra("mode", "Edit");
                            startActivityForResult(intent,
                                    GestureCommond.GestureEdit);
                        }
                    }, 300);

                    if (mSlidingItem != null) {
                        mSlidingItem.MoveBack(false);
                    }
                }
            });

            holder.mListItem.setSlidingListener(this);
        }

        private void startEditGesture(GestureObject gestureObject) {
            mLastDeleteGestureObject = gestureObject;
            Intent intent = new Intent();
            intent.setClass(ActivityGestureList.this,
                    ActivityCreateGesture.class);
            intent.putExtra("AppName", gestureObject.mAppName);
            intent.putExtra("mode", "");
            intent.putExtra("packageName", gestureObject.mPackageName);
            intent.putExtra("userName", gestureObject.mUserName);
            intent.putExtra("phoneNumber", gestureObject.mPhoneNumber);
            intent.putExtra("commond", gestureObject.mGestureType);
            intent.putExtra("activityName", gestureObject.mClassName);
            intent.putExtra("recorderID", gestureObject.mRecorderID);
            startActivityForResult(intent,
                    GestureCommond.GestureEdit);
        }

        private void startEditName(GestureObject gestureObject) {
            Intent intent1 = new Intent();
            intent1.setClass(ActivityGestureList.this, ActivityAddOption.class);
            intent1.putExtra("AppName", gestureObject.mAppName);
            intent1.putExtra("mode", "Edit");
            intent1.putExtra("packageName", gestureObject.mPackageName);
            intent1.putExtra("userName", gestureObject.mUserName);
            intent1.putExtra("phoneNumber", gestureObject.mPhoneNumber);
            intent1.putExtra("commond", gestureObject.mGestureType);
            intent1.putExtra("activityName", gestureObject.mClassName);
            intent1.putExtra("recorderID", gestureObject.mRecorderID);
            startActivityForResult(intent1,
                    GestureCommond.GestureEditJob);
        }

        private void onDeleteMenuItem(final GestureObject gestureObject) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    ActivityGestureList.this);
            builder.setTitle(R.string.gestureDelete);
            builder.setMessage(R.string.gestureDeleteMessage);
            builder.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface,
                                            int position) {
                            mGestureDataManager.delete(gestureObject);
                            dialogInterface.dismiss();
                            mGestureObjectLists.remove(gestureObject);
                            notifyDataSetChanged();
                            if (mSlidingItem != null) {
                                mSlidingItem.MoveBack(false);
                            }
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.create().show();
        }

        @Override
        public void onSlideToLeft(SlidingLeftViewGroup item) {
            mSlidingItem = item;
        }

        @Override
        public void onSlideBack() {
            mSlidingItem = null;
        }

        @Override
        public void onSlidingStart(SlidingLeftViewGroup item) {
            if (mSlidingItem != null && item != null && mSlidingItem != item) {
                mSlidingItem.MoveBack(false);
            }
        }

        private class ViewHolder {
            private int mPos;
            private SlidingLeftViewGroup mListItem;
            private LinearLayout mLL;
            private TextView mName;
            private GestureImageView mGestureImageView;
            private Button mDel;
            private Button mEditName;
            private Button mEditGesture;
        }
    }
}