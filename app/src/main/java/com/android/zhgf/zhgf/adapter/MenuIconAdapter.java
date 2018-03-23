package com.android.zhgf.zhgf.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.activity.MenuActivity;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.interfaceL.ViewHolderInterface;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * 用于GridView装载数据的适配器
 * @author xxs
 *
 */
public class MenuIconAdapter extends BaseAdapter {
	public ArrayList<HashMap<String,Object>> menuList;//定义一个list对象
	private Context mContext;//上下文
	public static final int APP_PAGE_SIZE = 9;//每一页装载数据的大小
	private Activity activity;
	private LayoutInflater inflater = null;
	private int height;
	private GridView mGv;
	private int intRowNum;
	/**
	 * 构造方法
	 * @param context 上下文
	 * @param menuList 集合
	 * @param page 当前页
	 */
	public MenuIconAdapter(Activity activity,  ArrayList<HashMap<String,Object>> menuList, GridView gv, int intRowNum,int page) {
		this.menuList = new ArrayList<HashMap<String,Object>>();
		//
		int i = page * APP_PAGE_SIZE;//当前页的其实位置
		int iEnd = i+APP_PAGE_SIZE;//所有数据的结束位置
		while ((i<menuList.size()) && (i<iEnd)) {
			this.menuList.add(menuList.get(i));
			i++;
		}
		this.activity = activity;
		this.mGv = gv;
		this.intRowNum = intRowNum;
		inflater = LayoutInflater.from(activity);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return this.menuList == null ? 0 : this.menuList.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (menuList != null && menuList.size() != 0) {
			return menuList.get(position);
		}
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {

		MenuAdapter.ViewHolder mHolder;
		View view = convertView;
		if (view == null) {
			if(intRowNum == 3){
				view = inflater.inflate(R.layout.menu_item, null);
			}else {
				view = inflater.inflate(R.layout.menubottom_item, null);
			}

			mHolder = new MenuAdapter.ViewHolder();
			mHolder.menuImageIv = (ImageView)view.findViewById(R.id.menuImageIv);
			mHolder.menuNameTv = (TextView)view.findViewById(R.id.menuNameTv);
			mHolder.badgeTv = (TextView)view.findViewById(R.id.icon_badge1);
			view.setTag(mHolder);
		}else{
			mHolder = (MenuAdapter.ViewHolder) view.getTag();
		}
		HashMap<String,Object> map = (HashMap)getItem(position);
		mHolder.menuImageIv.setImageResource((int)map.get("itemImage"));
		mHolder.menuNameTv.setText((String)map.get("itemText"));
		/*String name=transetCatId()
        if()*/
		switch (position){
			case 2:
				int govEdu=(int)map.get("badgenumber");
				Log.e("govEdu",govEdu+"");
				if(govEdu!=0){
					mHolder.badgeTv.setText(govEdu+"");
					mHolder.badgeTv.setVisibility(View.VISIBLE);
				}
				break;
			case 3:
				int onlineStudy=(int)map.get("badgenumber");
				if(onlineStudy!=0) {
					mHolder.badgeTv.setText(onlineStudy + "");
					mHolder.badgeTv.setVisibility(View.VISIBLE);
				}
				break;
			case 4:
				int workdynamic=(int)map.get("badgenumber");
				if(workdynamic!=0) {
					mHolder.badgeTv.setText(workdynamic + "");
					mHolder.badgeTv.setVisibility(View.VISIBLE);
				}
				break;
			case 5:
				int newsCount=(int)map.get("badgenumber");
				if(newsCount!=0) {
					mHolder.badgeTv.setText(newsCount + "");
					mHolder.badgeTv.setVisibility(View.VISIBLE);

				}
				break;
			default:break;
		}
		AbsListView.LayoutParams param = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				mGv.getHeight()/intRowNum);
		view.setLayoutParams(param);

		//ViewGroup.LayoutParams itemparams = view.getLayoutParams();
		//itemparams.height = height;
		//view.setLayoutParams(itemparams);
		return view;
	}
public Badge setbadgeview(ViewHolder vh ,int badgenumber){
	Badge badge=new QBadgeView(AppApplication.getApp()).bindTarget(vh.menuImageIv);
	badge.setBadgeGravity(Gravity.END|Gravity.TOP);
	badge.setBadgeNumber(badgenumber);
	return badge;
}


	static class ViewHolder {
		/**
		 * badgenumber 未读消息几条
		 * catId  消息种类
		 * 1 政治教育
		 2 在线学习
		 3 工作动态
		 4 新闻资讯
		 5 请销假
		 */
		ImageView menuImageIv;
		TextView menuNameTv,badgeTv;
		
	}
	public String transetCatId(int catId){
		String name=null;
      switch (catId){
		  case 1:
			  name="政治教育";
			  break;
		  case 2:
			  name="在线学习";
			  break;
		  case 3:
			  name="工作动态";
			  break;
		  case 4:
			  name="新闻资讯";
			  break;
		  case 5:
			  name="请销假";
			  break;
		  default:break;

	  }
		return name;
	}
	
}
