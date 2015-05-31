package lab.chabingba.eventorganizer.Visual;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;

/**
 * Created by Tsvetan on 2015-05-27.
 */
public class FlyInMenuContainer extends LinearLayout {
    private View menu;
    private View content;
    private int menuMargin = 80;
    private int magicPaddingRemove = 40;

    private int sdkVersion = Build.VERSION.SDK_INT;

    private int currentContentOffset = 0;
    private GlobalConstants.MenuState menuState = GlobalConstants.MenuState.CLOSED;

    private Scroller scroller = new Scroller(this.getContext(), new SmoothInterpolator());
    private Runnable runnable = new AnimationRunnable();
    private Handler handler = new Handler();

    private int menuAnimationDuration = 1000;
    private int menuAnimationInterval = 16;

    public FlyInMenuContainer(Context context) {
        super(context);
    }

    public FlyInMenuContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.setPadding(0, 0, 0, 0);

        //get the linear layout for the menu
        this.menu = this.getChildAt(0);

        //get the linear layout for the content
        this.content = this.getChildAt(1);

        this.menu.setVisibility(View.GONE);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (this.sdkVersion > 12) {
            magicPaddingRemove = 0;
        }

        if (changed) {
            this.calculateChildDimension();
        }

        this.menu.layout(l, t - magicPaddingRemove, r - this.menuMargin, b);

        this.content.layout(l + this.currentContentOffset, t - magicPaddingRemove, r + this.currentContentOffset, b);
    }

    private void calculateChildDimension() {
        this.content.getLayoutParams().height = this.getHeight();
        this.content.getLayoutParams().width = this.getWidth();

        this.menu.getLayoutParams().height = this.getHeight();
        this.menu.getLayoutParams().width = this.getWidth() - this.menuMargin;
    }

    public void toggleMenu() {
        switch (this.menuState) {
            case CLOSED:
                this.menuState = GlobalConstants.MenuState.OPENING;
                this.menu.setVisibility(View.VISIBLE);
                this.scroller.startScroll(0, 0, this.getMenuWidth(), 0, menuAnimationDuration);
                break;
            case OPEN:
                this.menuState = GlobalConstants.MenuState.CLOSING;
                this.scroller.startScroll(this.currentContentOffset, 0, -this.currentContentOffset, 0, menuAnimationDuration);
                break;
            default:
                return;
        }

        this.handler.postDelayed(this.runnable, menuAnimationInterval);
    }

    private int getMenuWidth() {
        return this.menu.getLayoutParams().width;
    }

    private void adjustContentPosition(boolean isAnimationOngoing) {
        int scrollerOffset = this.scroller.getCurrX();
        this.content.offsetLeftAndRight(scrollerOffset - this.currentContentOffset);
        this.currentContentOffset = scrollerOffset;

        this.invalidate();

        if (isAnimationOngoing) {
            this.handler.postDelayed(this.runnable, menuAnimationInterval);
        } else {
            this.onMenuTransitionComplete();
        }
    }

    private void onMenuTransitionComplete() {
        switch (this.menuState) {
            case OPENING:
                this.menuState = GlobalConstants.MenuState.OPEN;
                break;
            case CLOSING:
                this.menuState = GlobalConstants.MenuState.CLOSED;
                this.menu.setVisibility(View.GONE);
                break;
            default:
                return;
        }
    }

    public GlobalConstants.MenuState getMenuState() {
        return this.menuState;
    }

    private class SmoothInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            return (float) Math.pow(input - 1, 5) + 1;
        }
    }

    private class AnimationRunnable implements Runnable {

        @Override
        public void run() {
            boolean isAnimationOngoing = FlyInMenuContainer.this.scroller.computeScrollOffset();

            FlyInMenuContainer.this.adjustContentPosition(isAnimationOngoing);
        }
    }
}
