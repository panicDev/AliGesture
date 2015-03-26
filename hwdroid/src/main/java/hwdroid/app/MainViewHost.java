package hwdroid.app;

import hwdroid.widget.FooterBar.FooterBar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hw.droid.R;

/**
 * hide
 */
public class MainViewHost extends LinearLayout {

    private FrameLayout mContentView;
    private FooterBar mFooterBar;

    /**
     * construct
     *
     * @param context
     */
    public MainViewHost(Context context) {
        this(context, null);
    }

    public MainViewHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mContentView = (FrameLayout) findViewById(R.id.hw_action_bar_content_view);
        if (mContentView == null || !(mContentView instanceof FrameLayout)) {
            throw new IllegalArgumentException("No FrameLayout with the id R.id.hw_action_bar_content_view found in the layout.");
        }

        mFooterBar = (FooterBar) findViewById(R.id.hw_footer_bar);
        if (mFooterBar == null || !(mFooterBar instanceof FooterBar)) {
            throw new IllegalArgumentException("No FooterBar with the id R.id.hw_footer_bar found in the layout.");
        }

    }

    /**
     * get content view
     *
     * @return contentview instance.
     */
    public FrameLayout getContentView() {
        return mContentView;
    }

    /**
     * get footerbar view.
     *
     * @return FooterBar instance.
     */
    public FooterBar getFooterBarImpl() {
        return mFooterBar;
    }
}
