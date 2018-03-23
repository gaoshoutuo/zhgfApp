package com.android.zhgf.zhgf.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.topnews.CityListActivity;
//import com.topnews.DetailsActivity;
import com.android.zhgf.zhgf.JavaBean.News.PageInfo;
import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.activity.NewsDetailsActivity;
import com.android.zhgf.zhgf.adapter.NewsInfoAdapter;
import com.android.zhgf.zhgf.adapter.StudyAdapter;
import com.android.zhgf.zhgf.bean.NewsInfoEntity;
import com.android.zhgf.zhgf.bean.StudyInfoEntity;
import com.android.zhgf.zhgf.view.HeadListView;
import com.android.zhgf.zhgf.view.SwipeRefreshView;
import com.android.zhgf.zhgf.wnd.iface.INetwork;
import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;

import java.util.ArrayList;

public class NewsFragment extends Fragment implements OnItemClickListener, StudyAdapter.InnerItemOnclickListener {
	private final static String TAG = "NewsFragment";
	private Activity activity;
	//
	private ArrayList<NewsInfoEntity> newsList = new ArrayList<NewsInfoEntity>();
    //
	private ArrayList<StudyInfoEntity> studyList = new ArrayList<StudyInfoEntity>();
	private HeadListView mListView;
	private ListView lvNews;
	private NewsInfoAdapter newsAdapter;
	private StudyAdapter studyAdapter;
	private String text;
	private String channel_id;
	private int typeInt;
    private int othertypeInt;
	private ImageView detail_loading;
	public final static int SET_NEWSLIST = 0;
    public final static int LOAD_NEWSLIST = 1;
    public final static int REFLASH_NEWSLIST = 2;
	//Toast提示框
	private RelativeLayout notify_view;
	private TextView notify_view_text;
	String httpURLStr;
	private SwipeRefreshView mSwipeRefreshView;
    private PageInfo pageinfo = new PageInfo();
    private boolean loadBln = false;
    private boolean reflashBln = false;
    private boolean firstLoadBln = true;

    private String jsonStr;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		/*Bundle args = getArguments();
		text = args != null ? args.getString("text") : "";
		channel_id = args != null ? args.getString("id") : "";
		typeInt = args != null ? args.getInt("type") : 0;
        Log.e(TAG,  TAG + "gettypeInt=" + typeInt +"_________________________________________________________________");*/
		//initData();

		super.onCreate(savedInstanceState);
	}
	private void initEvent() {

		// 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
		mSwipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				initData();
			}
		});


		// 设置下拉加载更多
		mSwipeRefreshView.setOnLoadMoreListener(new SwipeRefreshView.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
                if(pageinfo == null){
                    // 加载完数据设置为不加载状态，将加载进度收起来
                    mSwipeRefreshView.setLoading(false);
                    return;
                }
                /*if(pageinfo.getTotalPage() > 1){
                    if(firstLoadBln){
                        pageinfo.setCurrPage(3);
                        firstLoadBln = false;
                    }else{
                        Log.e(TAG, "pageinfo.getCurrPage()_____________________________________________________________________(" + pageinfo.getCurrPage() + ")pageinfo.getTotalPage(" + pageinfo.getTotalPage());
                        if(pageinfo.getCurrPage() + 1 > pageinfo.getTotalPage()){
                            // 加载完数据设置为不加载状态，将加载进度收起来
                            mSwipeRefreshView.setLoading(false);
                            return;
                        }else{
                            pageinfo.setCurrPage(pageinfo.getCurrPage() + 1);
                        }
                    }
                    Log.e(TAG, "loadMoreData pageinfo.getCurrPage()_____________________________________________________________________(" + pageinfo.getCurrPage() );
                    loadMoreData();
                }else{
                    // 加载完数据设置为不加载状态，将加载进度收起来
                    mSwipeRefreshView.setLoading(false);
                    return;
                }*/
                if(pageinfo.getCurrPage() + 1 > pageinfo.getTotalPage()){
                    // 加载完数据设置为不加载状态，将加载进度收起来
                    mSwipeRefreshView.setLoading(false);
                    return;
                }
                loadMoreData();

			}
		});
	}
	public void setHttpURL(String httpURLStr){
		this.httpURLStr = httpURLStr;
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		this.activity = activity;
		super.onAttach(activity);
	}
	/** 此方法意思为fragment是否可见 ,可见时候加载数据 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
        Bundle args = getArguments();
        text = args != null ? args.getString("text") : "";
        channel_id = args != null ? args.getString("id") : "";
        typeInt = args != null ? args.getInt("type") : 0;
        othertypeInt = args != null ? args.getInt("othertype") : 0;

		if (isVisibleToUser) {
            Log.e(TAG, "fragment可见时加载数据_____________________________________________________________________");
			//fragment可见时加载数据
			//if(newsList !=null && newsList.size() !=0){
				//handler.obtainMessage(SET_NEWSLIST).sendToTarget();
			//}else{
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							// 加载数据
							getNewsData();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						handler.obtainMessage(SET_NEWSLIST).sendToTarget();
						Log.e(TAG, "handler.obtainMessage_____________________________________________________________________");
					}
				}).start();
			//}
		}else{
			//fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}
    // 加载数据
	private void getNewsData(){
		String HttpURL = "";
		String params = "";
		HttpURL =httpURLStr+ "/index.php";
		if(typeInt == 1){
			params = "m=app&c=index&a=contents&catid=" + channel_id;
		}else if (typeInt == 2){
			params = "m=app&c=index&a=contents&catid=" + channel_id;
		}
		if(loadBln){
            params+= "&&size=10&page=" + pageinfo.getCurrPage();
        }
		Log.e(TAG, "run: HttpURL="+HttpURL+"?"+ params + "typeInt="+typeInt + "_____________________________________________________________________");

		//String ResultString = new NetWorkUtils().HttpSendGetData(HttpURL,params);
		(new NetworkUtil()).HttpSendGetData(HttpURL, params, onNetWorkStatusListener);
	}

	private INetwork.OnNetworkStatusListener onNetWorkStatusListener =  new INetwork.OnNetworkStatusListener(){
		@Override
		public void onConnected(boolean isSuccess) {

		}

		@Override
		public void onDisconnected(boolean isSuccess) {

		}

		@Override
		public void onGetResult(boolean isSuccess, Object pData) {
			NetworkUtil.HttpStatus mHttp = (NetworkUtil.HttpStatus) pData;
			if (mHttp.getStatusCode() == 200) {
				Log.w(TAG, "onGetResult: "+mHttp.getData() +"_____________________________________________________________________");
				String strByJson = (String) mHttp.getData();
                jsonStr = (String) mHttp.getData();

			} else {
				Log.e(TAG,  TAG + "->onGetResult: 获取新闻标题失败_____________________________________________________________________");
			}
		}

		@Override
		public void onPostResult(boolean isSuccess, Object pData) {

		}

		@Override
		public void onError(int errCode, String errMessage) {
			Log.w(TAG, "onError: 是不是出错了呀");
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment, null);
		mSwipeRefreshView = (SwipeRefreshView)view.findViewById(R.id.srl);
		lvNews = (ListView) view.findViewById(R.id.mListView);
		TextView item_textview = (TextView)view.findViewById(R.id.item_textview);
		detail_loading = (ImageView)view.findViewById(R.id.detail_loading);
		//Toast提示框
		notify_view = (RelativeLayout)view.findViewById(R.id.notify_view);
		notify_view_text = (TextView)view.findViewById(R.id.notify_view_text);
		item_textview.setText(text);
		// 设置颜色属性的时候一定要注意是引用了资源文件还是直接设置16进制的颜色，因为都是int值容易搞混
		// 设置下拉进度的背景颜色，默认就是白色的
		mSwipeRefreshView.setProgressBackgroundColorSchemeResource(android.R.color.white);
		// 设置下拉进度的主题颜色
		mSwipeRefreshView.setColorSchemeResources(R.color.colorAccent,
				android.R.color.holo_blue_bright, R.color.colorPrimaryDark,
				android.R.color.holo_orange_dark, android.R.color.holo_red_dark, android.R.color.holo_purple);
		// 手动调用,通知系统去测量
		mSwipeRefreshView.measure(0, 0);
		mSwipeRefreshView.setRefreshing(false);
		initEvent();
		return view;
	}
    private void updateData(){
        NewsInfoEntity newsInfo = new NewsInfoEntity();
        ArrayList<StudyInfoEntity> studyListTemp = new ArrayList<StudyInfoEntity>();
        int numStartInt = 0;
        if(reflashBln){
            newsList = newsInfo.getNewsList(jsonStr);
        }else{
            numStartInt = newsList.size();
            newsList.addAll(newsInfo.getNewsList(jsonStr));

        }
        pageinfo = newsInfo.getPageInfo(jsonStr);
        if(typeInt == 2){
            ArrayList<NewsInfoEntity> newsListTemp = newsInfo.getNewsList(jsonStr);
            if(newsListTemp == null){ return;}
            for(int i = 0;i < newsListTemp.size();i++){
                StudyInfoEntity studyInfo = new StudyInfoEntity();
                studyInfo.setTitleStr("学习资料" + (numStartInt + i + 1));
                studyInfo.setButtonTypeInt(1);
                studyInfo.setIdStr(newsListTemp.get(i).getNewsId());
                studyListTemp.add(studyInfo);
            }
            if(reflashBln){
                studyList = studyListTemp;
            }else{
                studyList.addAll(studyListTemp);
            }

        }
    }
	private void initData() {
		/*if(typeInt == 1){
			//newsList = Constants.getNewsList();
		}else if (typeInt == 2){
			studyList = Constants.getStudyList();
		}*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            firstLoadBln = true;
                            reflashBln = true;
                            // 加载数据
                            getNewsData();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Log.d(TAG, "刷新成功哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈");;


                        handler.obtainMessage(REFLASH_NEWSLIST).sendToTarget();
                        reflashBln = false;
                        Log.e(TAG, "handler.obtainMessage_____________________________________________________________________");
                    }
                }).start();

                // 加载完数据设置为不刷新状态，将下拉进度收起来
                if (mSwipeRefreshView.isRefreshing()) {
                    mSwipeRefreshView.setRefreshing(false);
                }
            }
        }, 2000);
		/*new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
                    newsList.clear();
                    studyList.clear();
					// 加载数据
					getNewsData();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d(TAG, "刷新成功哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈");;


				handler.obtainMessage(SET_NEWSLIST).sendToTarget();
				Log.e(TAG, "handler.obtainMessage_____________________________________________________________________");
			}
		}).start();
        // 加载完数据设置为不刷新状态，将下拉进度收起来
        if (mSwipeRefreshView.isRefreshing()) {
            mSwipeRefreshView.setRefreshing(false);
        }*/


	}
	private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadBln = true;
                        // TODO Auto-generated method stub
                        try {
                            if(pageinfo == null){
                                // 加载完数据设置为不加载状态，将加载进度收起来
                                mSwipeRefreshView.setLoading(false);
                                return;
                            }
                            if(pageinfo.getTotalPage() > 1){
                                if(firstLoadBln){
                                    pageinfo.setCurrPage(3);
                                    firstLoadBln = false;
                                }else{

                                    if(pageinfo.getCurrPage() + 1 > pageinfo.getTotalPage()){
                                        // 加载完数据设置为不加载状态，将加载进度收起来
                                        mSwipeRefreshView.setLoading(false);
                                        return;
                                    }else{
                                        pageinfo.setCurrPage(pageinfo.getCurrPage() + 1);
                                    }
                                }
                                // 加载数据
                                getNewsData();
                            }else {
                                // 加载完数据设置为不加载状态，将加载进度收起来
                                mSwipeRefreshView.setLoading(false);
                                return;
                            }
                            // 加载数据
                            //getNewsData();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        handler.obtainMessage(LOAD_NEWSLIST).sendToTarget();
                        loadBln = false;
                        Log.d(TAG, "加载成功哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈");;

                    }
                }).start();

                // 加载完数据设置为不加载状态，将加载进度收起来
                mSwipeRefreshView.setLoading(false);
            }
        }, 2000);

	}
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
            NewsInfoEntity newsInfo = new NewsInfoEntity();
			switch (msg.what) {
				case SET_NEWSLIST:
					detail_loading.setVisibility(View.GONE);
					switch (typeInt){
						case 1:

							if(newsAdapter == null){
                                newsList = newsInfo.getNewsList(jsonStr);
                                pageinfo = newsInfo.getPageInfo(jsonStr);
								newsAdapter = new NewsInfoAdapter(activity,newsList);
							}
							lvNews.setAdapter(newsAdapter);

							lvNews.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view,
														int position, long id) {
                                    NewsInfoEntity newsInfoEntity = newsAdapter.getItem(position);
                                    if(newsInfoEntity == null){
                                        return;
                                    }
									Intent intent = new Intent(activity, NewsDetailsActivity.class);
									intent.putExtra("newsId", newsInfoEntity.getNewsId());
                                    intent.putExtra("channelId", channel_id);
                                    intent.putExtra("type", typeInt);
                                    intent.putExtra("othertype",othertypeInt);
									startActivity(intent);

								}
							});
                            break;
						case 2:
							if(studyAdapter == null){
                                newsList.clear();
                                studyList.clear();
                                newsList = newsInfo.getNewsList(jsonStr);
                                pageinfo = newsInfo.getPageInfo(jsonStr);
                                if(newsList == null){ return;}
                                for(int i = 0;i < newsList.size();i++){
                                    StudyInfoEntity studyInfo = new StudyInfoEntity();
                                    studyInfo.setTitleStr("学习资料" + ( i + 1));
                                    studyInfo.setButtonTypeInt(1);
                                    studyInfo.setIdStr(newsList.get(i).getNewsId());
                                    studyList.add(studyInfo);
                                }
                                studyAdapter = new StudyAdapter(activity,studyList);
							}
                            lvNews.setAdapter(studyAdapter);
                            studyAdapter.setOnInnerItemOnclickListener(new StudyAdapter.InnerItemOnclickListener() {
                                @Override
                                public void click(View v) {
                                    int position;
                                    position = (Integer) v.getTag();
                                    StudyInfoEntity studyInfoEntity = studyAdapter.getItem(position);
                                    if (studyInfoEntity == null){
                                        return;
                                    }
                                    Intent intent = new Intent(activity, NewsDetailsActivity.class);
                                    intent.putExtra("newsId", studyInfoEntity.getIdStr());
                                    intent.putExtra("channelId", channel_id);
                                    intent.putExtra("type", typeInt);
                                    intent.putExtra("othertype",othertypeInt);
                                    startActivity(intent);

                                }
                            });


							/*lvNews.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view,
														int position, long id) {
									Intent intent = new Intent(activity, NewsDetailsActivity.class);
									intent.putExtra("study", studyAdapter.getItem(position));
									startActivity(intent);

								}
							});*/
                            break;
                        default:
                            break;
					}
					break;
                case LOAD_NEWSLIST:
                    switch (typeInt){
                        case 1:
                            newsList.addAll(newsInfo.getNewsList(jsonStr));
                            pageinfo = newsInfo.getPageInfo(jsonStr);
                            if(newsAdapter == null){return;}
                            newsAdapter.notifyDataSetChanged();
                            break;
                        case 2:
                            ArrayList<NewsInfoEntity> newsListTemp = newsInfo.getNewsList(jsonStr);
                            if(newsListTemp == null){ return;}
                            if(studyList == null){ return;}
                            int numStartInt = studyList.size();
                            for(int i = 0;i < newsListTemp.size();i++){
                                StudyInfoEntity studyInfo = new StudyInfoEntity();
                                studyInfo.setTitleStr("学习资料" + (numStartInt + i + 1));
                                studyInfo.setButtonTypeInt(1);
                                studyInfo.setIdStr(newsListTemp.get(i).getNewsId());
                                studyList.add(studyInfo);
                            }
                            pageinfo = newsInfo.getPageInfo(jsonStr);
                            if(studyAdapter == null){return;}
                            studyAdapter.notifyDataSetChanged();
                            break;
                        default:
                            break;}
                    break;
                case REFLASH_NEWSLIST:
                    switch (typeInt){
                        case 1:
                            newsList = newsInfo.getNewsList(jsonStr);
                            pageinfo = newsInfo.getPageInfo(jsonStr);
                            newsAdapter = new NewsInfoAdapter(activity,newsList);
                            lvNews.setAdapter(newsAdapter);
                            lvNews.setOnItemClickListener(new OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    NewsInfoEntity newsInfoEntity = newsAdapter.getItem(position);
                                    if(newsInfoEntity == null){
                                        return;
                                    }
                                    Intent intent = new Intent(activity, NewsDetailsActivity.class);
                                    intent.putExtra("newsId", newsInfoEntity.getNewsId());
                                    intent.putExtra("channelId", channel_id);
                                    intent.putExtra("type", typeInt);
                                    intent.putExtra("othertype",othertypeInt);
                                    startActivity(intent);

                                }
                            });
                            //newsAdapter.notifyDataSetChanged();
                            break;
                        case 2:
                            newsList = newsInfo.getNewsList(jsonStr);
                            pageinfo = newsInfo.getPageInfo(jsonStr);
                            if(newsList == null){ return;}
                            studyList.clear();
                            for(int i = 0;i < newsList.size();i++){
                                StudyInfoEntity studyInfo = new StudyInfoEntity();
                                studyInfo.setTitleStr("学习资料" + ( i + 1));
                                studyInfo.setButtonTypeInt(1);
                                studyInfo.setIdStr(newsList.get(i).getNewsId());
                                studyList.add(studyInfo);
                            }
                            studyAdapter = new StudyAdapter(activity,studyList);
                            studyAdapter.setOnInnerItemOnclickListener(new StudyAdapter.InnerItemOnclickListener() {
                                @Override
                                public void click(View v) {
                                    int position;
                                    position = (Integer) v.getTag();
                                    StudyInfoEntity studyInfoEntity = studyAdapter.getItem(position);
                                    if (studyInfoEntity == null){
                                        return;
                                    }
                                    Intent intent = new Intent(activity, NewsDetailsActivity.class);
                                    intent.putExtra("newsId", studyInfoEntity.getIdStr());
                                    intent.putExtra("channelId", channel_id);
                                    intent.putExtra("type", typeInt);
                                    intent.putExtra("othertype",othertypeInt);
                                    startActivity(intent);
                                }
                            });
                            lvNews.setAdapter(studyAdapter);
                            break;
                        default:
                            break;
                    }
                    break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	/* 初始化选择城市的header*//*
	public void initCityChannel() {
		View headview = LayoutInflater.from(activity).inflate(R.layout.city_category_list_tip, null);
		TextView chose_city_tip = (TextView) headview.findViewById(R.id.chose_city_tip);
		chose_city_tip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(activity, CityListActivity.class);
				startActivity(intent);
			}
		});
		mListView.addHeaderView(headview);
	}*/
	
	/* 初始化通知栏目*//*
	private void initNotify() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				notify_view_text.setText(String.format(getString(R.string.ss_pattern_update), 10));
				notify_view.setVisibility(View.VISIBLE);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						notify_view.setVisibility(View.GONE);
					}
				}, 2000);
			}
		}, 1000);
	}*/
	/* 摧毁视图 */
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.d("onDestroyView", "channel_id = " + channel_id);
        newsAdapter = null;
        studyAdapter = null;
	}
	/* 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "channel_id = " + channel_id);
	}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void click(View v) {

    }
}
