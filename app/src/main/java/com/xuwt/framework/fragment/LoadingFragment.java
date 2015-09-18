package com.xuwt.framework.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.xuwt.framework.widget.base.MRelativeLayout;


public abstract class LoadingFragment extends BaseFragment {

	public static final int NOTSET = 0;
	public static final int INITIALIZED = 1;
	public static final int LOADING = 2;
	public static final int SUCCESSFUL = 4;
	public static final int ERROR = 5;
	
	protected static final int WHAT_SHOWLOADING = 0;
	protected static final int DELAY_SHOWLOADING = 500;

	private AlphaAnimation hideAnim;
	
	protected int mStatus = NOTSET;

	protected View BlankView;
	protected View LoadingView;
	protected View ErrorView;
	protected View ContentView;
	
	protected Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case WHAT_SHOWLOADING:
				gotoLoading();
				break;
			}
		};
	};
	
	public int getStatus() {
		return mStatus;
	}
	
	protected boolean getAutoLoading() {
		return true;
	}
	
	protected boolean getRetryOnError() {
		return true;
	}

	public LoadingFragment() { }
	
	@Override
	View performCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mChildFragmentManager != null) {
            mChildFragmentManager.noteStateNotSaved();
        }
        return getContentView(onCreateView(inflater, container, savedInstanceState));
	}
	
	private View getContentView(View view) {
		MRelativeLayout root = new MRelativeLayout(getActivity());
		root.addView(ContentView = view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		int blankResId = getBlankResId();
		if(blankResId != 0) {
			BlankView = LayoutInflater.from(getActivity()).inflate(blankResId, null, false);
		}
		if(BlankView == null) {
			BlankView = getBlankView();
		}
		if(BlankView != null) {
			root.addView(BlankView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			BlankView.setVisibility(View.GONE);
		}
		int errResId = getErrorResId();
		if(errResId != 0) {
			ErrorView = LayoutInflater.from(getActivity()).inflate(errResId, null, false);
		}
		if(ErrorView == null) {
			ErrorView = getErrorView();
		}
		if(ErrorView != null) {
			root.addView(ErrorView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			ErrorView.setVisibility(View.GONE);
		}
		int loadingResId = getLoadingResId();
		if(loadingResId != 0) {
			LoadingView = LayoutInflater.from(getActivity()).inflate(loadingResId, null, false);
		}
		if(LoadingView == null) {
			LoadingView = getLoadingView();
		}
		if(LoadingView != null) {
			root.addView(LoadingView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			LoadingView.setVisibility(View.GONE);
		} else {
			throw new IllegalStateException("you should override getLoadingResId or getLoadingView method");
		}
		mStatus = INITIALIZED;
		return root;
	}
	
	@Override
	protected void onBindListener() {
		super.onBindListener();
		if(ErrorView != null) {
			ErrorView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onApplyLoadingData();
				}
			});
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(getAutoLoading() && (mStatus == INITIALIZED || 
				(mStatus == ERROR && getRetryOnError()))) {
			onApplyLoadingData();
		}
	}
	
	protected void hideViewInAnim(final View view, final AnimationListener l) {
		if(view != null) {
			if(hideAnim == null) {
				hideAnim = new AlphaAnimation(1.0f, 0.0f);
				hideAnim.setDuration(1000);
			}
			hideAnim.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					if(l != null) {
						l.onAnimationStart(animation);
					}
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					if(l != null) {
						l.onAnimationRepeat(animation);
					}
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					view.setVisibility(View.GONE);
					if(l != null) {
						l.onAnimationEnd(animation);
					}
				}
			});
			view.startAnimation(hideAnim);
		}
	}
	
	/**
	 * 是否使用动画隐藏/显示view
	 * @return
	 */
	protected boolean useAnimation() {
		return true;
	}
	
	/**
	 * 显示loading时是否隐藏contentview
	 * @return
	 */
	protected boolean hideContentOnAction() {
		return true;
	}
	
	/**
	 * 切换至加载空白状态
	 */
	public void gotoBlank() {
		mHandler.removeMessages(WHAT_SHOWLOADING);
		if(LoadingView != null)
			LoadingView.setVisibility(View.GONE);
		if(ContentView != null)
			ContentView.setVisibility(View.GONE);
		if(ErrorView != null)
			ErrorView.setVisibility(View.GONE);
		if(BlankView != null)
			BlankView.setVisibility(View.VISIBLE);
		mStatus = SUCCESSFUL;
	}
	
	/**
	 * 切换至加载中状态
	 */
	public void gotoLoading() {
		gotoLoading(false);
	}
	
	/**
	 * 切换至加载中状态
	 */
	public void gotoLoading(boolean delay) {
		if(delay) {
			mHandler.sendEmptyMessageDelayed(WHAT_SHOWLOADING, getLoadingDelay());
			return;
		}
		if(mStatus == LOADING)
			return;
		if(ContentView != null)
			ContentView.setVisibility(hideContentOnAction() ? View.GONE : View.VISIBLE);
		if(BlankView != null)
			BlankView.setVisibility(View.GONE);
		if(ErrorView != null)
			ErrorView.setVisibility(View.GONE);
		if(LoadingView != null)
			LoadingView.setVisibility(View.VISIBLE);
		mStatus = LOADING;
	}
	
	/**
	 * 获取延迟显示Loading的delay值
	 * @return
	 */
	protected int getLoadingDelay() {
		return DELAY_SHOWLOADING;
	}
	
	/**
	 * 切换至加载出错状态
	 */
	public void gotoError() {
		gotoError(null);
	}
	
	/**
	 * 切换至加载出错状态
	 */
	public void gotoError(AnimationListener l) {
		mHandler.removeMessages(WHAT_SHOWLOADING);
		if(mStatus == ERROR)
			return;
		if(useAnimation()) {
			hideViewInAnim(LoadingView, l);
		} else {
			if(LoadingView != null)
				LoadingView.setVisibility(View.GONE);
		}
		if(ContentView != null)
			ContentView.setVisibility(View.GONE);
		if(BlankView != null)
			BlankView.setVisibility(View.GONE);
		if(ErrorView != null)
			ErrorView.setVisibility(View.VISIBLE);
		mStatus = ERROR;
	}
	
	/**
	 * 切换至加载成功状态
	 */
	public void gotoSuccessful() {
		gotoSuccessful(null);
	}
	
	/**
	 * 切换至加载成功状态
	 */
	public void gotoSuccessful(AnimationListener l) {
		mHandler.removeMessages(WHAT_SHOWLOADING);
		if(useAnimation() && mStatus != SUCCESSFUL) {
			hideViewInAnim(LoadingView, l);
		} else {
			if(LoadingView != null)
				LoadingView.setVisibility(View.GONE);
		}
		if(BlankView != null)
			BlankView.setVisibility(View.GONE);
		if(ErrorView != null)
			ErrorView.setVisibility(View.GONE);
		if(ContentView != null)
			ContentView.setVisibility(View.VISIBLE);
		mStatus = SUCCESSFUL;
	}
	
	/**
	 * 加载数据
	 */
	public void onApplyLoadingData() {
		gotoLoading();
	}

	
	/**
	 * 获取加载中布局文件resid
	 * @return
	 */
	protected int getLoadingResId() {
		return 0;
	}
	/**
	 * 获取加载中布局文件resid
	 * @return
	 */
	protected View getLoadingView() {
		return null;
	}
	/**
	 * 获取加载出错布局文件resid
	 * @return
	 */
	protected int getErrorResId() {
		return 0;
	}
	/**
	 * 获取加载出错布局文件resid
	 * @return
	 */
	protected View getErrorView() {
		return null;
	}
	/**
	 * 获取加载无数据布局文件resid
	 * @return
	 */
	protected int getBlankResId() {
		return 0;
	}
	/**
	 * 获取加载无数据布局文件resid
	 * @return
	 */
	protected View getBlankView() {
		return null;
	}
	

}