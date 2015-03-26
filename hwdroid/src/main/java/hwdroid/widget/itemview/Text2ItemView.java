
package hwdroid.widget.itemview;

import hwdroid.widget.item.Item;
import hwdroid.widget.item.Text2Item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hw.droid.R;

/**
 * View representation of the {@link hwdroid.widget.item.Text2Item}.
 */
public class Text2ItemView extends LinearLayout implements ItemView {

    private TextView mTextView;
    private TextView mSubTextView;
    private LinearLayout mRightWidgetFrame;

    private CheckBox mCheckBox;
    private RadioButton mRadioButton;

    public Text2ItemView(Context context) {
        this(context, null);
    }

    public Text2ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void prepareItemView() {
        mTextView = (TextView) findViewById(R.id.hw_text);
        mSubTextView = (TextView) findViewById(R.id.hw_subtext);
        mRightWidgetFrame = (LinearLayout) findViewById(R.id.hw_right_widget_frame);
    }

    public void setObject(Item object) {
        final Text2Item item = (Text2Item) object;

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

    private void setCheckBoxChecked(boolean checked) {
        if (mCheckBox != null)
            mCheckBox.setChecked(checked);
    }

    private void setRadioButtonChecked(boolean checked) {
        if (mRadioButton != null)
            mRadioButton.setChecked(checked);
    }

    @Override
    public void setSubTextSingleLine(boolean enabled) {
        if (mSubTextView != null) {
            mSubTextView.setSingleLine(enabled);
        }
    }
}
