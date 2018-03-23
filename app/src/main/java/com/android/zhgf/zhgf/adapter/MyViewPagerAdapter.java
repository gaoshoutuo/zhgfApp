package com.android.zhgf.zhgf.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;

import java.util.List;

/**
 * 实现ViewPager页卡切换的适配器
 * @author Administrator
 *
 */
public class MyViewPagerAdapter extends PagerAdapter {
	private List<GridView> array;
	/**
	 * 供外部调用（new）的方法
	 * @param context  上下文
	 * @param array    添加的序列对象
	 */
	public MyViewPagerAdapter(Context context, List<GridView> array) {
		this.array=array;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return array.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}
	@Override
	public Object instantiateItem(View arg0, int arg1)
	{
		((ViewPager) arg0).addView(array.get(arg1));
		return array.get(arg1);
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2)
	{
		((ViewPager) arg0).removeView((View) arg2);
	}

}
