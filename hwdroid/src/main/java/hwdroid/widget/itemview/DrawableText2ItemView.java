package hwdroid.widget.itemview;

import hwdroid.widget.item.DrawableText2Item;
import hwdroid.widget.item.Item;
import hwdroid.widget.item.SeparatorItem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hw.droid.R;

/**
 * View representation of the {@link hwdroid.widget.item.DrawableText2Item}.
 */
public class DrawableText2ItemView extends LinearLayout implements ItemView {

    private TextView mTextView;
    private TextView mSubTextView;
    private ImageView mImageView;

    private LinearLayout mRightWidgetFrame;
    private CheckBox mCheckBox;
    private RadioButton mRadioButton;
    private ImageView mRightImageView;

    public DrawableText2ItemView(Context context) {
        this(context, null);
    }

    public DrawableText2ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void prepareItemView() {
        mTextView = (TextView) findViewById(R.id.hw_text);
        mSubTextView = (TextView) findViewById(R.id.hw_subtext);
        mImageView = (ImageView) findViewById(R.id.hw_drawable);
        mRightWidgetFrame = (LinearLayout) findViewById(R.id.hw_right_widget_frame);
    }

    public void setObject(Item object) {
        final DrawableText2Item item = (DrawableText2Item) object;

        if (item.leftDrawable == null) {
            mImageView.setVisibility(View.GONE);
        } else {
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setScaleType(item.scaleType);
            mImageView.setImageDrawable(item.leftDrawable);
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
                    mCheckBox.setFocusable(false);

                }
                mRightWidgetFrame.setVisibility(View.VISIBLE);
                mCheckBox.setEnabled(object.isEnabled());
                mRightWidgetFrame.addView(mCheckBox);
                setCheckBoxChecked(item.isChecked());
                break;
            case RADIO_MODE:
                if (mRadioButton == null) {
                    mRadioButton = new RadioButton(this.getContext());
                    mRadioButton.setClickable(false);
                    mRadioButton.setFocusable(false);
                }
                mRightWidgetFrame.setVisibility(View.VISIBLE);
                mRadioButton.setEnabled(object.isEnabled());
                mRightWidgetFrame.addView(mRadioButton);
                setRadioButtonChecked(item.isChecked());
                break;
            case IMAGE_MODE:
            case NORMAL_MODE:
                if (item.rightDrawable != null) {
                    if (mRightImageView == null) {
                        mRightImageView = new ImageView(this.getContext());
                    }

                    mRightWidgetFrame.addView(mRightImageView);
                    mRightImageView.setImageDrawable(item.rightDrawable);
                    mRightWidgetFrame.setVisibility(View.VISIBLE);
                } else {
                    mRightWidgetFrame.setVisibility(View.GONE);
                }
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
