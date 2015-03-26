
package hwdroid.widget.FooterBar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hw.droid.R;

/**
 * a FooterBarButton View class.
 * the view is independent¡£
 * <p/>
 * create it and add to any view group.
 * <p>
 * sample
 * </p>
 * <p>
 * LinearLayout layout = new LinearLayout(context);
 * FooterBarMenu menu = new FooterBarMenu(context);
 * layout.addView(menu);
 * </p>
 */
public class FooterBarButton extends FooterBarView {

    private final static int MAXITEMSIZE = 2;
//    private int padding_left;
//    private int padding_right;
//
//    private int margin_left;
//    private int margin_right;

    public FooterBarButton(Context context) {
        this(context, null);
    }

    public FooterBarButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
//        Context context = this.getContext();
//        padding_left = context.getResources().getDimensionPixelSize(R.dimen.hw_footerbar_button_padding_left);
//        padding_right = context.getResources().getDimensionPixelSize(R.dimen.hw_footerbar_button_padding_right);
//
//        margin_left = context.getResources().getDimensionPixelSize(R.dimen.hw_footerbar_button_margin_left);
//        margin_right = context.getResources().getDimensionPixelSize(R.dimen.hw_footerbar_button_margin_right);

        setItemType(HORIZONTAL_ITEM_TYPE);
        setMaxItemSize(MAXITEMSIZE);
        //this.setPadding(padding_left, 0, padding_right, 0);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);
    }

    protected void addItemView() {
        mContentView.removeAllViews();

        LayoutParams layoutParams = new LayoutParams(0,
                LayoutParams.MATCH_PARENT, 1);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        FooterBarItem item;

        boolean addDividerFlag = false;

        for (int i = 0; i < mItemList.size(); i++) {

            if (addDividerFlag) {
                addDivider();
            }

            item = mItemList.get(i);
            item.setBackgroundResource(R.drawable.hw_btn_dialog);
            //layoutParams.setMargins(margin_left, 0, margin_right, 0);

            mContentView.addView(item, layoutParams);

            addDividerFlag = true;
        }
    }

    private void addDivider() {
        ImageView dividerImg = new ImageView(this.getContext());
        final LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 0);
        dividerImg.setLayoutParams(lp);
        dividerImg.setBackgroundResource(R.drawable.hw_list_divider);
        mContentView.addView(dividerImg);
    }
}
