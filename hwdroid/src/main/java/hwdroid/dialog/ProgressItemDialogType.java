package hwdroid.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hw.droid.R;

import hwdroid.widget.ItemAdapter;
import hwdroid.widget.item.Item;

import java.lang.ref.WeakReference;

public class ProgressItemDialogType extends DialogType {

    public static final int HANDLER_UPDATE_PROGRESS = 0x200;

    private RelativeLayout mProgressView;
    private TextView mProgressMessage;
    private TextView mProgressCount;
    private ProgressBar mProgressBar;

    private int mValue;
    private int mMaxValue;

    private ProgressHandler mProgressHandler;

    public ProgressItemDialogType(Context context, DropDownDialog popupWindow,
                                  DialogInterface dialogInterface, ItemAdapter adapter,
                                  View menuView) {
        super(context, popupWindow, dialogInterface, adapter, menuView);
        mProgressHandler = new ProgressHandler(this);

        mProgressView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.hw_dropdown_dialog_progress_item_view, null, false);
        mProgressMessage = (TextView) mProgressView.findViewById(R.id.hw_dialog_progress_message_text);
        mProgressCount = (TextView) mProgressView.findViewById(R.id.hw_dialog_progress_count_text);
        mProgressBar = (ProgressBar) mProgressView.findViewById(android.R.id.progress);
    }

    @Override
    public Item createItemView(String text, boolean selected) {
        return null;
    }

    @Override
    public void showChild() {
        setView(mProgressView);
    }

    @Override
    public void updateItem(int value, int maxValue) {
        if (mMaxValue != maxValue) {
            mMaxValue = maxValue;
            mProgressBar.setMax(mMaxValue);
        }

        mValue = value;

        if (mProgressCount != null) {
            if (!mProgressHandler.hasMessages(HANDLER_UPDATE_PROGRESS)) {
                mProgressHandler.sendEmptyMessageDelayed(HANDLER_UPDATE_PROGRESS, 0);
            }
        }

        if (mProgressBar != null) {
            mProgressBar.setProgress(mValue);
        }
    }

    public void setMessage(int titleId) {
        mProgressMessage.setText(mContext.getString(titleId));
    }

    public void setMessage(CharSequence title) {
        if (title == null) return;
        mProgressMessage.setText(title.toString());
    }

    private static class ProgressHandler extends Handler {
        private final WeakReference<ProgressItemDialogType> mType;

        public ProgressHandler(ProgressItemDialogType type) {
            this.mType = new WeakReference<ProgressItemDialogType>(type);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_UPDATE_PROGRESS:
                    if (mType.get().mValue > mType.get().mMaxValue) {
                        mType.get().mProgressCount.setText("" + mType.get().mMaxValue + "/" + mType.get().mMaxValue);
                        //mProgressBar.setProgress(mMaxValue);
                        return;
                    }

                    mType.get().mProgressCount.setText("" + mType.get().mValue + "/" + mType.get().mMaxValue);
                    // mProgressBar.setProgress(mValue);
                    break;
                default:
                    break;
            }
        }
    }
}
