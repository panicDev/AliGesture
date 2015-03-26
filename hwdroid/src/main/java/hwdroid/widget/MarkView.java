
package hwdroid.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabWidget;
import android.widget.TextView;

import com.hw.droid.R;

/**
 * A simple text label view that can be applied as a "mark" to any given
 * {@link android.view.View}. This class is intended to be instantiated at
 * runtime rather than included in XML layouts.
 */
public class MarkView extends TextView {

    public enum Position {
        POSITION_TOP_LEFT, POSITION_TOP_RIGHT,
        POSITION_BOTTOM_LEFT, POSITION_BOTTOM_RIGHT,
        POSITION_CENTER_LEFT, POSITION_CENTER_RIGHT,
        POSITION_CENTER
    }

    private static final int DEFAULT_MARGIN_DIP = 3;
    private static final int DEFAULT_WRAPPER_MARGIN_DIP = 10;
    private static final int DEFAULT_LR_PADDING_DIP = 5;
    private static final int DEFAULT_CORNER_RADIUS_DIP = 8;
    private static final int DEFAULT_MARK_COLOR = Color.parseColor("#52b800"); // Color.Green
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final Position DEFAULT_POSITION = Position.POSITION_TOP_RIGHT;

    private static Animation fadeIn;
    private static Animation fadeOut;

    private Context context;
    private View target;

    private Position markPosition;
    private int wrapperMargin;
    private int markMarginH;
    private int markMarginV;
    private int markColor;

    private boolean isShown;

    private ShapeDrawable markBg;

    private int targetTabIndex;
    private ImageView mNoneTipsView;

    public MarkView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MarkView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MarkView(Context context, View target) {
        this(context, target, DEFAULT_POSITION);
    }

    /**
     * Constructor - create a new MarkView instance attached to a target
     * {@link android.view.View}.
     *
     * @param context context for this view.
     * @param target  the View to attach the mark to.
     */
    public MarkView(Context context, View target, Position position) {
        this(context, null, android.R.attr.textViewStyle, target, 0, position);
    }

    /**
     * Constructor - create a new MarkView instance attached to a target
     * {@link android.widget.TabWidget} tab at a given index.
     *
     * @param context context for this view.
     * @param target  the TabWidget to attach the mark to.
     * @param index   the position of the tab within the target.
     */
    public MarkView(Context context, TabWidget target, int index, Position position) {
        this(context, null, android.R.attr.textViewStyle, target, index, position);
    }

    public MarkView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null, 0, DEFAULT_POSITION);
    }

    public MarkView(Context context, AttributeSet attrs, int defStyle, View target, int tabIndex, Position position) {
        super(context, attrs, defStyle);
        init(context, target, tabIndex, position);
    }

    private void init(Context context, View target, int tabIndex, Position position) {
        setTextSize(12f / getResources().getConfiguration().fontScale);
        this.context = context;
        this.target = target;
        this.targetTabIndex = tabIndex;

        // apply defaults
        wrapperMargin = dipToPixels(DEFAULT_WRAPPER_MARGIN_DIP);
        markPosition = position;
        markMarginH = dipToPixels(DEFAULT_MARGIN_DIP);
        markMarginV = markMarginH;
        markColor = DEFAULT_MARK_COLOR;

        setTypeface(Typeface.DEFAULT_BOLD);
        int paddingPixels = dipToPixels(DEFAULT_LR_PADDING_DIP);
        setPadding(paddingPixels, 0, paddingPixels, 0);
        setTextColor(DEFAULT_TEXT_COLOR);

        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(200);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(200);

        isShown = false;

        if (this.target != null) {
            applyTo(this.target);
        } else {
            show();
        }

    }

    private void applyTo(View target) {
        if (target instanceof TabWidget) {
            RelativeLayout container = new RelativeLayout(context);
            // set target to the relevant tab child container
            target = ((TabWidget) target).getChildTabViewAt(targetTabIndex);
            this.target = target;

            ((ViewGroup) target).addView(container, new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));

            this.setVisibility(View.INVISIBLE);
            container.addView(this);
        } else {
            LayoutParams lp = target.getLayoutParams();
            ViewParent parent = target.getParent();
            RelativeLayout container = new RelativeLayout(context);
            // TODO verify that parent is indeed a ViewGroup
            ViewGroup group = (ViewGroup) parent;
            int index = group.indexOfChild(target);

            group.removeView(target);
            group.addView(container, index, lp);

            RelativeLayout.LayoutParams wlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            wlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            FrameLayout wrapper = new FrameLayout(getContext());
            container.addView(wrapper, wlp);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(wrapperMargin, wrapperMargin, wrapperMargin, wrapperMargin);
            wrapper.addView(target, params);

            FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            applyLayoutParams(tlp, false);
            wrapper.addView(this, tlp);

            FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(wrapperMargin, wrapperMargin);
            applyLayoutParams(lp1, true);
            mNoneTipsView = new ImageView(getContext());
            mNoneTipsView.setImageDrawable(getResources().getDrawable(R.drawable.hw_mark_none_tips));
            wrapper.addView(mNoneTipsView, lp1);

            this.setVisibility(View.INVISIBLE);
            mNoneTipsView.setVisibility(View.INVISIBLE);
            group.invalidate();
        }
    }

    /**
     * Make the mark visible in the UI.
     */
    public void show() {
        show(false, null);
    }

    /**
     * Make the mark visible in the UI.
     *
     * @param animate flag to apply the default fade-in animation.
     */
    public void show(boolean animate) {
        show(animate, fadeIn);
    }

    /**
     * Make the mark visible in the UI.
     *
     * @param anim Animation to apply to the view when made visible.
     */
    public void show(Animation anim) {
        show(true, anim);
    }

    /**
     * Make the mark non-visible in the UI.
     */
    public void hide() {
        hide(false, null);
    }

    /**
     * Make the mark non-visible in the UI.
     *
     * @param animate flag to apply the default fade-out animation.
     */
    public void hide(boolean animate) {
        hide(animate, fadeOut);
    }

    /**
     * Make the mark non-visible in the UI.
     *
     * @param anim Animation to apply to the view when made non-visible.
     */
    public void hide(Animation anim) {
        hide(true, anim);
    }

    /**
     * Toggle the mark visibility in the UI.
     */
    public void toggle() {
        toggle(false, null, null);
    }

    /**
     * Toggle the mark visibility in the UI.
     *
     * @param animate flag to apply the default fade-in/out animation.
     */
    public void toggle(boolean animate) {
        toggle(animate, fadeIn, fadeOut);
    }

    /**
     * Toggle the mark visibility in the UI.
     *
     * @param animIn  Animation to apply to the view when made visible.
     * @param animOut Animation to apply to the view when made non-visible.
     */
    public void toggle(Animation animIn, Animation animOut) {
        toggle(true, animIn, animOut);
    }

    private void show(boolean animate, Animation anim) {
        if (getBackground() == null) {
            if (markBg == null) {
                markBg = getDefaultBackground();
            }
            setBackgroundDrawable(markBg);
        }

        if (getText().length() > 0) {
            this.setVisibility(View.VISIBLE);
            this.mNoneTipsView.setVisibility(View.INVISIBLE);
            if (animate) {
                this.startAnimation(anim);
            }
        } else {
            this.setVisibility(View.INVISIBLE);
            this.mNoneTipsView.setVisibility(View.VISIBLE);
            if (animate) {
                mNoneTipsView.startAnimation(anim);
            }
        }
        isShown = true;
    }

    private void hide(boolean animate, Animation anim) {
        if (this.getVisibility() == View.VISIBLE) {
            this.setVisibility(View.INVISIBLE);
            if (animate) {
                this.startAnimation(anim);
            }
        } else {
            mNoneTipsView.setVisibility(View.INVISIBLE);
            if (animate) {
                mNoneTipsView.startAnimation(anim);
            }
        }
        isShown = false;
    }

    private void toggle(boolean animate, Animation animIn, Animation animOut) {
        if (isShown) {
            hide(animate && (animOut != null), animOut);
        } else {
            show(animate && (animIn != null), animIn);
        }
    }

    /**
     * Increment the numeric mark label. If the current mark label cannot be
     * converted to an integer value, its label will be set to "0".
     *
     * @param offset the increment offset.
     */
    public int increment(int offset) {
        CharSequence txt = getText();
        int i;
        if (txt != null) {
            try {
                i = Integer.parseInt(txt.toString());
            } catch (NumberFormatException e) {
                i = 0;
            }
        } else {
            i = 0;
        }
        i = i + offset;
        setText(String.valueOf(i));
        return i;
    }

    /**
     * Decrement the numeric mark label. If the current mark label cannot be
     * converted to an integer value, its label will be set to "0".
     *
     * @param offset the decrement offset.
     */
    public int decrement(int offset) {
        return increment(-offset);
    }

    private ShapeDrawable getDefaultBackground() {

        int r = dipToPixels(DEFAULT_CORNER_RADIUS_DIP);
        float[] outerR = new float[]{
                r, r, r, r, r, r, r, r
        };

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(markColor);

        return drawable;

    }

    private void applyLayoutParams(FrameLayout.LayoutParams params, boolean margin) {
        switch (markPosition) {
            case POSITION_TOP_LEFT:
                params.gravity = Gravity.LEFT | Gravity.TOP;
                if (margin)
                    params.setMargins(markMarginH, markMarginV, 0, 0);
                break;
            case POSITION_TOP_RIGHT:
                params.gravity = Gravity.RIGHT | Gravity.TOP;
                if (margin)
                    params.setMargins(0, markMarginV, markMarginH, 0);
                break;
            case POSITION_BOTTOM_LEFT:
                params.gravity = Gravity.LEFT | Gravity.BOTTOM;
                if (margin)
                    params.setMargins(markMarginH, 0, 0, markMarginV);
                break;
            case POSITION_BOTTOM_RIGHT:
                params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                if (margin)
                    params.setMargins(0, 0, markMarginH, markMarginV);
                break;
            case POSITION_CENTER:
                params.gravity = Gravity.CENTER;
                if (margin)
                    params.setMargins(0, 0, 0, 0);
                break;
            case POSITION_CENTER_LEFT:
                params.gravity = Gravity.CENTER | Gravity.LEFT;
                if (margin)
                    params.setMargins(markMarginH, 0, 0, 0);
                break;
            case POSITION_CENTER_RIGHT:
                params.gravity = Gravity.CENTER | Gravity.RIGHT;
                if (margin)
                    params.setMargins(0, 0, markMarginH, 0);
                break;
            default:
                break;
        }
    }

    /**
     * Returns the target View this mark has been attached to.
     */
    public View getTarget() {
        return target;
    }

    /**
     * Is this mark currently visible in the UI?
     */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /**
     * Returns the horizontal margin from the target View that is applied to
     * this mark.
     */
    public int getHorizontalMarkMargin() {
        return markMarginH;
    }

    /**
     * Returns the vertical margin from the target View that is applied to this
     * mark.
     */
    public int getVerticalMarkMargin() {
        return markMarginV;
    }

    /**
     * Set the horizontal/vertical margin from the target View that is applied
     * to this mark.
     *
     * @param markMargin the margin in pixels.
     */
    public void setMarkMargin(int markMargin) {
        this.markMarginH = markMargin;
        this.markMarginV = markMargin;
    }

    /**
     * Set the horizontal/vertical margin from the target View that is applied
     * to this mark
     *
     * @param horizontal margin in pixels.
     * @param vertical   margin in pixels.
     */
    public void setMarkMargin(int horizontal, int vertical) {
        this.markMarginH = horizontal;
        this.markMarginV = vertical;
    }

    /**
     * Returns the color value of the mark background.
     */
    public int getMarkBackgroundColor() {
        return markColor;
    }

    /**
     * Set the color value of the mark background.
     *
     * @param markColor the mark background color.
     */
    public void setMarkBackgroundColor(int markColor) {
        this.markColor = markColor;
        markBg = getDefaultBackground();
    }

    private int dipToPixels(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                r.getDisplayMetrics());
        return (int) px;
    }

}
