
package hwdroid.widget.FooterBar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.hw.droid.R;

public class FooterBarItem extends LinearLayout {

    protected class ItemViewHolder {
        TextView text;
        ImageView icon;
        boolean mark;
        int markCount;
    }

    protected ItemViewHolder mViewHolder;
    private int mItemId;
    private Object mTag;


    public FooterBarItem(Context context) {
        this(context, null);
    }

    public FooterBarItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void init(Context context) {
        mViewHolder = new ItemViewHolder();
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(lp);
        mViewHolder.icon = new ImageView(context);

        addView(createIconBackground(mViewHolder.icon), lp);

        LayoutParams lp_tv = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mViewHolder.text = new TextView(context);
        mViewHolder.text.setTextAppearance(context, R.style.HWDroid_FooterBarButtonTextAppearance);
        mViewHolder.text.setGravity(Gravity.CENTER);
        mViewHolder.text.setSingleLine();
        mViewHolder.text.setEllipsize(TruncateAt.END);
        setTextViewLayoutParams(context, mViewHolder.text, lp_tv);
        addView(mViewHolder.text, lp_tv);

    }

    protected View createIconBackground(View icon) {
        return icon;
    }

    protected void setTextViewLayoutParams(Context context, TextView text, LayoutParams lp_tv) {
    }

    public void setItemId(int itemId) {
        mItemId = itemId;
    }

    public int getItemId() {
        return mItemId;
    }

    public void setItemTitle(CharSequence title) {
        mViewHolder.text.setText(title);

    }

    public CharSequence getItemTitle() {
        return mViewHolder.text.getText();
    }

    public void setItemTitle(int title) {
        mViewHolder.text.setText(title);

    }

    public void setItemDrawableIcon(Drawable icon) {
        mViewHolder.icon.setImageDrawable(icon);
    }

    public void setItemResourceIcon(int iconRes) {
        setItemDrawableIcon(this.getResources().getDrawable(iconRes));
    }

    public void setItemEnabled(boolean enabled) {
        mViewHolder.text.setEnabled(enabled);
        mViewHolder.icon.setEnabled(enabled);
        setClickable(enabled);
    }

    public boolean isItemEnabled() {
        return mViewHolder.text.isEnabled() && mViewHolder.icon.isEnabled();
    }

    public void setItemSelected(boolean select) {
        this.setSelected(select);
    }

    public void setItemTextSize(float size) {
        size = size / getResources().getConfiguration().fontScale;
        mViewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

    }

    public void setItemTextViewVisibility(int visibility) {
        mViewHolder.text.setVisibility(visibility);
    }

    public void setItemImageViewVisibility(int visibility) {
        mViewHolder.icon.setVisibility(visibility);
    }

    public void setItemTextColor(int colorId) {
        mViewHolder.text.setTextColor(colorId);
    }

    public void setItemTextColor(ColorStateList colors) {
        mViewHolder.text.setTextColor(colors);
    }

    public void setItemTag(Object tag) {
        mTag = tag;
    }

    public Object getItemTag() {
        return mTag;
    }

    public void setElementPadding(int left, int top, int right, int bottom) {
        mViewHolder.text.setPadding(left, top, right, bottom);
    }
}
