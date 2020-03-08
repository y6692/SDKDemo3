package com.qiwu.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.qiwu.ui.R;


/**
 *
 */
public abstract class LoadingLayout extends FrameLayout {
	/**
	 //Fragment共性-->页面共性-->视图的展示

	 任何应用其实就只有4种页面类型
	 ① 加载页面
	 ② 错误页面
	 ③ 空页面
	 ④ 成功页面

	 ①②③三种页面一个应用基本是固定的
	 每一个fragment/activity对应的页面④就不一样
	 进入应用的时候显示①,②③④需要加载数据之后才知道显示哪个
	 */
//	public static final int	STATE_NONE		= -1;			// 默认情况
//	public static final int	STATE_LOADING	= 0;			// 正在请求网络
//	public static final int	STATE_EMPTY		= 1;			// 空状态
//	public static final int	STATE_ERROR		= 2;			// 错误状态
//	public static final int	STATE_SUCCESS	= 3;			// 成功状态

	public LoadedResult				mCurState = LoadedResult.NORMAL;
	private View mLoadingView;
	private View mErrorView;
	private View mEmptyView;
	private View mSuccessView;
	private boolean isAutoLoadData = true;
	private boolean mDisplayFlag;
	public boolean mListenerFlag;

	private Context mContext;

	public LoadingLayout(Context context) {
		this(context,null);
	}

	public LoadingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		loadData();
	}




	/**
	 * @des 根据当前状态显示不同的view
	 * @call 1. LoadingPager初始化的时候
	 * @call 2.	显示正在加载数据
	 * @call 3. 正在加载数据执行完成的时候
	 */
	public void refreshUI(LoadedResult loadedResult) {

		mCurState = loadedResult;

		// 控制loading视图显示隐藏
		if (mLoadingView == null && loadedResult == LoadedResult.LOADING){
			// 1 加载页面
			mLoadingView = View.inflate(mContext, R.layout.loading_pager_loading, null);
			this.addView(mLoadingView);
		}
		if (mLoadingView != null){
			// 控制loading视图显示隐藏
			mLoadingView.setVisibility((loadedResult == LoadedResult.LOADING)?VISIBLE:GONE);
		}

		// 控制emptyView视图显示隐藏
		if (mEmptyView == null && loadedResult == LoadedResult.EMPTY){
			// 2 空页面
			mEmptyView = View.inflate(mContext, R.layout.loading_pager_empty, null);
			this.addView(mEmptyView);
		}
		if (mEmptyView != null){
			// 控制emptyView视图显示隐藏
			mEmptyView.setVisibility((loadedResult == LoadedResult.EMPTY)?VISIBLE:GONE);
		}

		// 控制errorView视图显示隐藏
		if (mErrorView == null && loadedResult == LoadedResult.ERROR){
			// 3 错误页面
			mErrorView = View.inflate(mContext, R.layout.loading_pager_error, null);
			mErrorView.findViewById(R.id.error_btn_retry).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					refreshUI(LoadedResult.LOADING);
					initData();
				}
			});
			this.addView(mErrorView);
		}
		if (mErrorView != null){
			// 控制errorView视图显示隐藏
			mErrorView.setVisibility((loadedResult == LoadedResult.ERROR)?VISIBLE:GONE);
		}

		if (mSuccessView == null && loadedResult == LoadedResult.SUCCESS) {
			// 创建成功视图
			try {
				mSuccessView = initSuccessView();
				this.addView(mSuccessView);
				initViewElement();
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		if (mSuccessView != null) {
			// 控制success视图显示隐藏
			mSuccessView.setVisibility((loadedResult == LoadedResult.SUCCESS) ? VISIBLE:GONE);
		}
		
		if (loadedResult == LoadedResult.DISPLAY){
			if (mSuccessView == null){
				mSuccessView = initSuccessView();
				this.addView(mSuccessView);
				initViewElement();
			}
			displayView();
			if (!mListenerFlag){
				try {
					initListener(mSuccessView);
					mListenerFlag = true;
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			if (mSuccessView.getVisibility() == GONE){
				mSuccessView.setVisibility(VISIBLE);
			}
		}
	}

	// 数据加载的流程
	/**
	 ① 触发加载  	进入页面开始加载/点击某一个按钮的时候加载
	 ② 异步加载数据  -->显示加载视图
	 ③ 处理加载结果
	 ① 成功-->显示成功视图
	 ② 失败
	 ① 数据为空-->显示空视图
	 ② 数据加载失败-->显示加载失败的视图
	 */
	/**
	 * @des 触发加载数据
	 * @call 暴露给外界调用,其实就是外界  触发加载数据
	 */
	public void loadData() {
		// 如果加载成功,那就无需再次加载
		if (mCurState != LoadedResult.SUCCESS && mCurState != LoadedResult.LOADING && mCurState != LoadedResult.ERROR) {
			// 保证每次执行的时候一定是加载中视图,而不是上次的mCurState
			mCurState = LoadedResult.LOADING;
			refreshUI(LoadedResult.SUCCESS);// 提前初始化成功页面
			refreshUI(LoadedResult.LOADING);// 2. 显示正在加载数据
			if (isAutoLoadData){
				initData();
			}
		}
	}

	class LoadDataTask implements Runnable {
		@Override
		public void run() {
			// ② 异步加载数据
			// 处理加载结果

//			LogUtils.sf("###结束加载数据:"+mCurState);
//			UIUtils.postTaskSafely(new Runnable() {
//				@Override
//				public void run() {
//					// 刷新ui
//					mCurState = initData().getState();
//					refreshUI();// 3. 正在加载数据执行完成的时候
//				}
//			});
		}
	}


	/**
	 * @des 正在加载数据,必须实现,但是不知道具体实现,定义成为抽象方法,让子类具体实现
	 *  loadData()方法被调用的时候
	 */
	public abstract void initData();

	/**
	 * @des 返回成功视图
	 *  正在加载数据完成之后,并且数据加载成功,我们必须告知具体的成功视图
	 */
	public abstract View initSuccessView();

	/**
	 * @des 初始化view元素
	 */
	public abstract void initViewElement();

	/**
	 * @des 显示成功视图
	 */
	public abstract void displayView();



	/**
	 * @des 初始化监听相关
	 */
	public void initListener(View successView){

	}

	public void success(){
		refreshUI(LoadedResult.DISPLAY);
	}

	public void error(){
		refreshUI(LoadedResult.ERROR);
	}

	public void empty(){
		refreshUI(LoadedResult.EMPTY);
	}

	public ImageView getEmptyView(){
		if (mEmptyView == null){
			mEmptyView = View.inflate(mContext, R.layout.loading_pager_empty, null);
		}
		return (ImageView) mEmptyView.findViewById(R.id.ivEmpty);
	}


	public ImageView getErrorView(){
		if (mErrorView == null){
			mErrorView = View.inflate(mContext, R.layout.loading_pager_error, null);
		}
		return (ImageView) mErrorView.findViewById(R.id.ivError);
	}

	public boolean isDisplayFlag() {
		return mDisplayFlag;
	}

	public void setDisplayFlag(boolean displayFlag) {
		mDisplayFlag = displayFlag;
	}

	public enum LoadedResult {
		NORMAL(-1),LOADING(0), ERROR(1), EMPTY(2),SUCCESS(3),DISPLAY(4);
		int	state;
		public int getState() {
			return state;
		}
		LoadedResult(int state) {
			this.state = state;
		}
	}


}
