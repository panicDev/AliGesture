package hwdroid.widget.itemview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hw.droid.R;

import hwdroid.widget.item.Item;
import hwdroid.widget.item.WidgetText2CBItem;

/**
 * View representation of the {@link hwdroid.widget.item.WidgetText2CBItem}.
 */
public class WidgetText2CBItemView extends LinearLayout implements ItemView {

    private TextView mTextView;
    private TextView mSubTextView;
    private LinearLayout mLeftWidgetFrame;
    private LinearLayout mRightWidgetFrame;

    private CheckBox mCheckBox;
    private RadioButton mRadioButton;

    public WidgetText2CBItemView(Context context) {
        this(context, null);
    }

    public WidgetText2CBItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void prepareItemView() {
        mTextView = (TextView) findViewById(R.id.hw_text);
        mSubTextView = (TextView) findViewById(R.id.hw_subtext);
        mLeftWidgetFrame = (LinearLayout) findViewById(R.id.hw_left_widget_frame);
        mRightWidgetFrame = (LinearLayout) findViewById(R.id.hw_right_widget_frame);
    }

    public void setObject(Item object) {
        final WidgetText2CBItem item = (WidgetText2CBItem) object;

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

        mTextView.setEnabled(object.isEnabled());
        mSubTextView.setEnabled(object.isEnabled());

        setTextView(item.mText);
        setSubtextView(item.mSubText);

        mRightWidgetFrame.removeAllViews();
        switch (item.getTypeMode()) {
            case CHECK_MODE:
                if (mCheckBox == null) {
                    mCheckBox = new CheckBox(this.getContext());
                    mCheckBox.setClickable(false);
                    mCheckBox.setSelected(false);
                    mCheckBox.setFocusable(false);
                    mRightWidgetFrame.setVisibility(View.VISIBLE);
                }

                mRightWidgetFrame.addView(mCheckBox);
                setCheckBoxChecked(item.isChecked());
                break;
            case RADIO_MODE:
                if (mRadioButton == null) {
                    mRadioButton = new RadioButton(this.getContext());
                    mRadioButton.setClickable(false);
                    mRadioButton.setFocusable(false);
                    mRightWidgetFrame.setVisibility(View.VISIBLE);
                }

                mRightWidgetFrame.addView(mRadioButton);
                setRadioButtonChecked(item.isChecked());
                break;
            default:
                mRightWidgetFrame.setVisibility(View.GONE);
                break;
        }

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

    private void setCheckBoxChecked(boolean checked) {
        if (mCheckBox != null)
            mCheckBox.setChecked(checked);
    }

    private void setRadioButtonChecked(boolean checked) {
        if (mRadioButton != null)
            mRadioButton.setChecked(checked);
    }
}

