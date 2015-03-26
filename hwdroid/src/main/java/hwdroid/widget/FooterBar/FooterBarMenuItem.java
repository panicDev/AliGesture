
package hwdroid.widget.FooterBar;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hw.droid.R;

import hwdroid.widget.MarkView;

/**
 * @hide a footerbarmenu item class.
 * create the FooterBarMenuItem instance, and add the instance to FooterbarMenu.
 */
public class FooterBarMenuItem extends FooterBarItem {

    private ImageView mMarkRootView;
    private MarkView mMarkView;

    public FooterBarMenuItem(Context context) {
        super(context);
    }

    public FooterBarMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void init(Context context) {
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.hw_footer_bar_menu_item_view, this);
        LinearLayout iconLayout = (LinearLayout) layout.findViewById(R.id.hw_footer_menu_item_icon);
        mMarkRootView = (ImageView) layout.findViewById(R.id.hw_mark_root_view);

        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mViewHolder = new ItemViewHolder();
        mViewHolder.icon = new ImageView(context);
        iconLayout.addView(createIconBackground(mViewHolder.icon));

        mViewHolder.text = new TextView(context);
        mViewHolder.text.setTextAppearance(context, R.style.HWDroid_FooterBarButtonTextAppearance);
        mViewHolder.text.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        mViewHolder.text.setSingleLine();
        mViewHolder.text.setEllipsize(TruncateAt.END);
        setTextViewLayoutParams(context, mViewHolder.text, lp);
        iconLayout.addView(mViewHolder.text, lp);
    }

    protected View createIconBackground(View icon) {
        return icon;
    }

    protected void setTextViewLayoutParams(Context context, TextView tv, LayoutParams lp_tv) {
        lp_tv.width = LayoutParams.MATCH_PARENT;
        lp_tv.height = 0;
        lp_tv.weight = 1;
        tv.setGravity(Gravity.CENTER | Gravity.BOTTOM);
    }

    public void setMarkViewEnable(boolean enable) {
        if (enable) {
            if (mMarkView == null) {
                mMarkView = new MarkView(getContext(), mMarkRootView);
            }

            mMarkView.show();
        } else {
            if (mMarkView != null) {
                mMarkView.hide();
            }
        }
    }

    public void setMarkViewEnable(boolean enable, int count) {
        setMarkViewEnable(enable);

        if (enable) {
            mMarkView.setText(String.valueOf(count));
            mMarkView.show();
        }
    }


}
