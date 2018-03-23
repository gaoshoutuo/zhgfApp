package com.android.zhgf.zhgf.tool;

//import com.topnews.bean.CityEntity;
import com.android.zhgf.zhgf.bean.NewsEntity;
import com.android.zhgf.zhgf.bean.NewsInfoEntity;
import com.android.zhgf.zhgf.bean.StudyInfoEntity;

import java.util.ArrayList;
import java.util.List;

//import com.topnews.bean.NewsClassify;

public class Constants {
	/*
	 * 获取新闻列表
	 */
	public static ArrayList<NewsInfoEntity> getNewsList() {
		ArrayList<NewsInfoEntity> newsList = new ArrayList<NewsInfoEntity>();
		for(int i =0 ; i < 10 ; i++){
			NewsInfoEntity news = new NewsInfoEntity();
			//news.setId(i);
			//news.setNewsId(i);
			//news.setCommentNum(i + 10);

			//news.setSource_url("http://news.sina.com.cn/c/2014-05-05/134230063386.shtml");
			news.setTitle("关于组织民兵应急连第四次集中强化训练的通知");
			List<String> url_list = new ArrayList<String>();
			if(i%2 == 1){
				String url1 = "http://gyfpjh.com/uploads/image/201612/1480921212113664-lp.jpg";
				String url2 = "https://s9.rr.itc.cn/r/wapChange/20175_28_17/a0c54p8306958631745.jpg";
				String url3 = "http://img1.gtimg.com/news/pics/hv1/197/152/2205/143419082.jpg";
				news.setPicOne(url1);
				news.setPicTwo(url2);
				news.setPicThr(url3);
				//news.setSource_url("http://tech.sina.com.cn/zl/post/detail/i/2013-11-06/pid_8436571.htm?from=groupmessage&isappinstalled=0");
				url_list.add(url1);
				url_list.add(url2);
				url_list.add(url3);
			}else{
				news.setTitle("关于组织全市民兵组织整顿工作检查点验的通知");
				String url = "http://img1.gtimg.com/news/pics/hv1/175/137/2193/142634935.png";
				news.setPicOne(url);
				url_list.add(url);
			}
			news.setPicList(url_list);
			news.setPublishTime(Long.valueOf(i));
			news.setSource("中国军网");

			newsList.add(news);
		}
		return newsList;
	}
	public static ArrayList<StudyInfoEntity> getStudyList() {
		ArrayList<StudyInfoEntity> studyList = new ArrayList<StudyInfoEntity>();
		for(int i =1 ; i < 20 ; i++){
			StudyInfoEntity StudyInfo = new StudyInfoEntity();
			StudyInfo.setTitleStr("学习资料" + String.valueOf(i));
			StudyInfo.setButtonTypeInt(1);
			studyList.add(StudyInfo);
		}
		return studyList;
	}
	public static ArrayList<StudyInfoEntity> getTestList() {
		ArrayList<StudyInfoEntity> studyList = new ArrayList<StudyInfoEntity>();
		for(int i =1 ; i < 20 ; i++){

			if(i == 1){
				StudyInfoEntity StudyInfo = new StudyInfoEntity();
				StudyInfo.setTitleStr("试卷" + String.valueOf(i) + "政治测试");
				StudyInfo.setButtonTypeInt(2);
				studyList.add(StudyInfo);
			} else if(i == 2){
				StudyInfoEntity StudyInfo = new StudyInfoEntity();
				StudyInfo.setTitleStr("试卷" + String.valueOf(i) + "政治测试");
				StudyInfo.setButtonTypeInt(2);
				StudyInfo.setTestSorceStr("90");
				studyList.add(StudyInfo);
			} else if(i == 3){
				StudyInfoEntity StudyInfo = new StudyInfoEntity();
				StudyInfo.setTitleStr("试卷" + String.valueOf(i) + "政治测试");
				StudyInfo.setButtonTypeInt(2);
				StudyInfo.setTestSorceStr("59");
				studyList.add(StudyInfo);
			}else if(i == 4){
				StudyInfoEntity StudyInfo = new StudyInfoEntity();
				StudyInfo.setTitleStr("试卷" + String.valueOf(i) + "政治测试");
				StudyInfo.setButtonTypeInt(2);
				StudyInfo.setTestSorceStr("100");
				studyList.add(StudyInfo);
			}else {
				StudyInfoEntity StudyInfo = new StudyInfoEntity();
				StudyInfo.setTitleStr("试卷" + String.valueOf(i) + "政治测试");
				StudyInfo.setButtonTypeInt(2);
				studyList.add(StudyInfo);
			}

		}
		return studyList;
	}
	/** mark=0 ：推荐 */
	public final static int mark_recom = 0;
	/** mark=1 ：热门 */
	public final static int mark_hot = 1;
	/** mark=2 ：首发 */
	public final static int mark_frist = 2;
	/** mark=3 ：独家 */
	public final static int mark_exclusive = 3;
	/** mark=4 ：收藏 */
	public final static int mark_favor = 4;
	
	/*
	 * 获取城市列表
	 *//*
	public static ArrayList<CityEntity> getCityList(){
		ArrayList<CityEntity> cityList =new ArrayList<CityEntity>();
		CityEntity city1 = new CityEntity(1, "安吉", 'A');
		CityEntity city2 = new CityEntity(2, "北京", 'B');
		CityEntity city3 = new CityEntity(3, "长春", 'C');
		CityEntity city4 = new CityEntity(4, "长沙", 'C');
		CityEntity city5 = new CityEntity(5, "大连", 'D');
		CityEntity city6 = new CityEntity(6, "哈尔滨", 'H');
		CityEntity city7 = new CityEntity(7, "杭州", 'H');
		CityEntity city8 = new CityEntity(8, "金沙江", 'J');
		CityEntity city9 = new CityEntity(9, "江门", 'J');
		CityEntity city10 = new CityEntity(10, "山东", 'S');
		CityEntity city11 = new CityEntity(11, "三亚", 'S');
		CityEntity city12 = new CityEntity(12, "义乌", 'Y');
		CityEntity city13 = new CityEntity(13, "舟山", 'Z');
		cityList.add(city1);
		cityList.add(city2);
		cityList.add(city3);
		cityList.add(city4);
		cityList.add(city5);
		cityList.add(city6);
		cityList.add(city7);
		cityList.add(city8);
		cityList.add(city9);
		cityList.add(city10);
		cityList.add(city11);
		cityList.add(city12);
		cityList.add(city13);
		return cityList;
	}*/
	/* 频道中区域 如杭州 对应的栏目ID */
	public final static int CHANNEL_CITY = 3;
}
