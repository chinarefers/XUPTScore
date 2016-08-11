package com.xy.fy.view;

import com.xy.fy.main.R;
import com.xy.fy.util.ViewUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class PullDoorView extends RelativeLayout {

	private Context mContext;

	private Scroller mScroller;

	private int mScreenWidth = 0;

	private int mScreenHeigh = 0;

	private int mLastDownY = 0;

	private int mCurryY;

	private int mDelY;
	private long mLastTime = 0;
	private boolean mCloseFlag = false;

	private ImageView mImgView;

	public PullDoorView(Context context) {
		super(context);
		mContext = context;
		setupView();
	}

	public PullDoorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setupView();
	}

	@SuppressLint("NewApi")
	private void setupView() {

		// 这个Interpolator你可以设置别的 我这里选择的是有弹跳效果的Interpolator
		Interpolator polator = new BounceInterpolator();
		mScroller = new Scroller(mContext, polator);

		// 获取屏幕分辨率
		WindowManager wm = (WindowManager) (mContext
				.getSystemService(Context.WINDOW_SERVICE));
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		mScreenHeigh = dm.heightPixels;
		mScreenWidth = dm.widthPixels;

		// 这里你一定要设置成透明背景,不然会影响你看到底层布局
		this.setBackgroundColor(Color.argb(0, 0, 0, 0));
		mImgView = new ImageView(mContext);
		mImgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mImgView.setScaleType(ImageView.ScaleType.FIT_XY);// 填充整个屏幕
		mImgView.setImageResource(R.drawable.left1); // 默认背景
		addView(mImgView);
		startBounceAnim(mScreenHeigh, -mScreenHeigh, 2000);
	}

	// 设置推动门背景
	public void setBgImage(int id) {
		mImgView.setImageResource(id);
	}

	// 设置 填充类型 默认是填充整个屏幕
	public void setScaletype(ImageView.ScaleType scaleType) {
		mImgView.setScaleType(scaleType);// 填充整个屏幕
	}

	public void setBgBitmap(Bitmap bitmap) {
		mImgView.setImageBitmap(bitmap);
	}

	// 设置推动门背景
	public void setBgImage(Drawable drawable) {
		mImgView.setImageDrawable(drawable);
	}

	// 推动门的动画
	public void startBounceAnim(int startY, int dy, int duration) {
		mScroller.startScroll(0, startY, 0, dy, duration);
		invalidate();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.currentTimeMillis();
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastDownY = (int) event.getY();
			mLastTime = System.currentTimeMillis();
			return true;
		case MotionEvent.ACTION_MOVE:
			mCurryY = (int) event.getY();
			mDelY = mCurryY - mLastDownY;
			// 只准上滑有效
			if (mDelY < 0) {
				scrollTo(0, -mDelY);
			}

			break;
		case MotionEvent.ACTION_UP:
			mCurryY = (int) event.getY();
			mDelY = mCurryY - mLastDownY;
			if (mDelY < -2) {
				long curryTime = System.currentTimeMillis();
				long d = curryTime - mLastTime;
				if (Math.abs(mDelY) > mScreenHeigh / 2
						|| (d <= 300 && mDelY > -1000)) {

					// 向上滑动超过半个屏幕高的时候 开启向上消失动画
					startBounceAnim(this.getScrollY(), mScreenHeigh, 600);
					mCloseFlag = true;

				} else {
					// 向上滑动未超过半个屏幕高的时候 开启向下弹动动画
					startBounceAnim(this.getScrollY(), -this.getScrollY(), 1000);
				}
			}
			if (-2 < mDelY && mDelY <= 0) {
				startBounceAnim(80, -80, 1000);
			}

			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void computeScroll() {

		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			Log.i("scroller", "getCurrX()= " + mScroller.getCurrX()
					+ "     getCurrY()=" + mScroller.getCurrY()
					+ "  getFinalY() =  " + mScroller.getFinalY());
			// 不要忘记更新界面
			postInvalidate();
		} else {
			if (mCloseFlag) {
				this.setVisibility(View.GONE);
				ViewUtil.showToast(mContext, "温馨提示：推动门图片是会更新的奥！");
				/*com.mc.request.asyn.CheckVersionAsyntask checkVersionAsyntask = new com.mc.request.asyn.CheckVersionAsyntask(
						mContext);
				checkVersionAsyntask.execute();*/

			}
		}
	}

}
