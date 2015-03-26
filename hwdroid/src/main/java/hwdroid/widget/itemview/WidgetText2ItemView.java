package hwdroid.widget.itemview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hw.droid.R;

import hwdroid.widget.item.Item;
import hwdroid.widget.item.WidgetText2Item;

/**
 * View representation of the {@link hwdroid.widget.item.WidgetText2Item}.
 */
public class WidgetText2ItemView extends LinearLayout implements ItemView {

    private TextView mTextView;
    private TextView mSubTextView;
    private LinearLayout mLeftWidgetFrame;
    private LinearLayout mRightWidgetFrame;

    public WidgetText2ItemView(Context context) {
        this(context, null);
    }

    public WidgetText2ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void prepareItemView() {
        mTextView = (TextView) findViewById(R.id.hw_text);
        mSubTextView = (TextView) findViewById(R.id.hw_subtext);
        mLeftWidgetFrame = (LinearLayout) findViewById(R.id.hw_left_widget_frame);
        mRightWidgetFrame = (LinearLayout) findViewById(R.id.hw_right_widget_frame);
    }

    public void setObject(Item object) {
        final WidgetText2Item item = (WidgetText2Item) object;

        if (item.leftviewgroup == null) {
            mLeftWidgetFrame.setVisibility(View.GONE);
        } else {
            mLeftWidgetFrame.setVisibility(View.VISIBLE);

            // ensure this view just could be added only once
            LinearLayout parent = (LinearLayout) item.leftviewgroup.getTag(item.mWidgetTagKey);
            if (parent != null) {
                parent.removeAllViews();
                parent.setTag(item.mWidgetTagKey, null);
                item.leftviewgroup.setTag(item.mWidgetTagKey, null);
            }

            // before add new view, we should confirm disconnect between the old child view
            // avoid the old child view get layout view by getTag() and then clear new child view
            LinearLayout child = (LinearLayout) mLeftWidgetFrame.getTag(item.mWidgetTagKey);
            if (child != null) {
                mLeftWidgetFrame.removeAllViews();
                mLeftWidgetFrame.setTag(item.mWidgetTagKey, null);
                child.setTag(item.mWidgetTagKey, null);
            }


            if (null == item.leftviewgroup.getTag(item.mWidgetTagKey))
                mLeftWidgetFrame.addView(item.leftviewgroup);

            // setup new connection between layout and child view
            item.leftviewgroup.setTag(item.mWidgetTagKey, mLeftWidgetFrame);
            mLeftWidgetFrame.setTag(item.mWidgetTagKey, item.leftviewgroup);
        }

        if (item.rightviewgroup == null) {
            mRightWidgetFrame.setVisibility(View.GONE);
        } else {
            mRightWidgetFrame.setVisibility(View.VISIBLE);

            // refer to the leftviewgroup
            LinearLayout parent = (LinearLayout) item.rightviewgroup.getTag(item.mWidgetTagKey);
            if (parent != null) {
                parent.removeAllViews();
                parent.setTag(item.mWidgetTagKey, null);
                item.rightviewgroup.setTag(item.mWidgetTagKey, null);
            }

            LinearLayout child = (LinearLayout) mRightWidgetFrame.getTag(item.mWidgetTagKey);
            if (child != null) {
                mRightWidgetFrame.removeAllViews();
                mRightWidgetFrame.setTag(item.mWidgetTagKey, null);
                child.setTag(item.mWidgetTagKey, null);
            }

            if (null == item.rightviewgroup.getTag(item.mWidgetTagKey))
                mRightWidgetFrame.addView(item.rightviewgroup);

            item.rightviewgroup.setTag(item.mWidgetTagKey, mRightWidgetFrame);
            mRightWidgetFrame.setTag(item.mWidgetTagKey, item.rightviewgroup);
        }

        mTextView.setEnabled(object.isEnabled());
        mSubTextView.setEnabled(object.isEnabled());

        setTextView(item.mText);
        setSubtextView(item.mSubText);

    }

    private void setTextView(String text) {
        mTextView.setText(text);
    }

    private void setSubtextView(String text) {
        Log.i("xugaoyang", "setSubTextView start()");
        if (text == null || text.isEmpty()) {
            mSubTextView.setText("");
            mSubTextView.setVisibility(View.GONE);
            mTextView.setGravity(Gravity.CENTER_VERTICAL);
        } else {
            mTextView.setGravity(Gravity.BOTTOM);
            mSubTextView.setVisibility(View.VISIBLE);
            mSubTextView.setText(text);
        }
    }

    @Override
    public void setSubTextSingleLine(boolean enabled) {
        if (mSubTextView != null) {
            mSubTextView.setSingleLine(enabled);
        }
    }
}
