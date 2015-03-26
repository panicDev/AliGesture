package hwdroid.widget.FooterBar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.hw.droid.R;

public class FooterBar extends LinearLayout {
    private Animation mInAnimation;
    private Animation mOutAnimation;
    private Context mContext;
    private boolean isAnimationEnable = false;

    public FooterBar() {
        super(null);
        throw new RuntimeException("<FooterBar> don't use default construct!!");
    }

    public FooterBar(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public FooterBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        mInAnimation = (Animation) AnimationUtils.loadAnimation(mContext, R.anim.hw_footerbar_in);
        mOutAnimation = (Animation) AnimationUtils.loadAnimation(mContext, R.anim.hw_footerbar_out);
    }

    public void showFooterBar(int visibility) {
        this.setVisibility(visibility);
    }

    public void addFooterBarView(View v) {
        removeFooterBarView();
        addView(v);
    }

    public void removeFooterBarView() {
        this.removeAllViews();
    }

    public void setAnimationEnable(boolean isEnable) {
        this.isAnimationEnable = isEnable;
    }

    @Override
    public void setVisibility(int visibility) {

        if (isAnimationEnable)
            if (View.VISIBLE == visibility)
                this.startAnimation(mInAnimation);
            else if (View.GONE == visibility)
                this.startAnimation(mOutAnimation);

        super.setVisibility(visibility);
    }

}
