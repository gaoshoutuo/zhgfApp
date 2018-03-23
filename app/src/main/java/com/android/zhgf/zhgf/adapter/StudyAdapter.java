package com.android.zhgf.zhgf.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.bean.StudyInfoEntity;

import java.util.ArrayList;

/**
 * Created by TZXX001 on 2017/9/27.
 */

public class StudyAdapter extends BaseAdapter implements OnClickListener{

    ArrayList<StudyInfoEntity> studyList;
    Activity activity;
    LayoutInflater inflater = null;
    private InnerItemOnclickListener mListener;

    @Override
    public void onClick(View view) {
        mListener.click(view);
    }

    public interface InnerItemOnclickListener {
         public void click(View v);
     }

    public StudyAdapter(Activity activity, ArrayList<StudyInfoEntity> studyList) {
        this.activity = activity;
        this.studyList = studyList;
        this.inflater = LayoutInflater.from(activity);

    }
    public void setOnInnerItemOnclickListener(InnerItemOnclickListener listener){
        this.mListener = listener;
    }
    @Override
    public int getCount() {
        return studyList == null ? 0 : studyList.size();
    }

    @Override
    public StudyInfoEntity getItem(int position) {
        if (studyList != null && studyList.size() != 0) {
            return studyList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        StudyAdapter.ViewHolder mHolder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.studylist_item, null);
            mHolder = new StudyAdapter.ViewHolder();
            mHolder.titleTv = (TextView)view.findViewById(R.id.title_tv);
            mHolder.studyIv = (ImageView)view.findViewById(R.id.study_iv);
            mHolder.studyBtn = (Button)view.findViewById(R.id.study_btn);
            mHolder.testBtn = (Button)view.findViewById(R.id.test_btn);
            mHolder.testScoresGoodBtn = (Button)view.findViewById(R.id.testScoresGood_btn);
            mHolder.testScoresGreatBtn = (Button)view.findViewById(R.id.testScoresGreat_btn);
            mHolder.testScoresBadBtn = (Button)view.findViewById(R.id.testScoresBad_btn);
            mHolder.lineV = (View)view.findViewById(R.id.lineV);
            view.setTag(mHolder);
        } else {
            mHolder = (StudyAdapter.ViewHolder) view.getTag();
        }
        //获取position对应的数据
        StudyInfoEntity study = getItem(position);
        mHolder.titleTv.setText(study.getTitleStr());
        mHolder.studyBtn.setOnClickListener(this);
        mHolder.studyBtn.setTag(position);
        switch(study.getButtonTypeInt()) {
            case 1:
                mHolder.studyBtn.setVisibility(View.VISIBLE);
                mHolder.testBtn.setVisibility(View.GONE);
                mHolder.testScoresGoodBtn.setVisibility(View.GONE);
                mHolder.testScoresGreatBtn.setVisibility(View.GONE);
                mHolder.testScoresBadBtn.setVisibility(View.GONE);
                break;
            case 2:
                String sorceStr = study.getTestSorceStr();
                int sorceInt = 0;
                /*if(sorceStr!=null && ("".equals(sorceStr.trim()) || sorceStr.matches("[0-9]+"))){
                    sorceInt = Integer.parseInt(sorceStr);
                }*/
                try {
                    sorceInt = Integer.parseInt(sorceStr);
                } catch (NumberFormatException e) {
                    sorceInt = 0;
                }
                if(sorceInt == 0){
                    mHolder.testBtn.setVisibility(View.VISIBLE);
                    mHolder.testBtn.setOnClickListener(this);
                    mHolder.testScoresGoodBtn.setVisibility(View.GONE);
                    mHolder.testScoresGreatBtn.setVisibility(View.GONE);
                    mHolder.testScoresBadBtn.setVisibility(View.GONE);
                }else if (sorceInt == 100){
                    mHolder.testScoresGreatBtn.setText(study.getTestSorceStr() + "分");
                    mHolder.testBtn.setVisibility(View.GONE);
                    mHolder.testScoresGoodBtn.setVisibility(View.GONE);
                    mHolder.testScoresGreatBtn.setVisibility(View.VISIBLE);
                    mHolder.testScoresGreatBtn.setOnClickListener(this);
                    mHolder.testScoresBadBtn.setVisibility(View.GONE);
                }else if (sorceInt >= 60 && sorceInt < 100){
                    mHolder.testScoresGoodBtn.setText(study.getTestSorceStr() + "分");
                    mHolder.testBtn.setVisibility(View.GONE);
                    mHolder.testScoresGoodBtn.setVisibility(View.VISIBLE);
                    mHolder.testScoresGoodBtn.setOnClickListener(this);
                    mHolder.testScoresGreatBtn.setVisibility(View.GONE);
                    mHolder.testScoresBadBtn.setVisibility(View.GONE);
                }else if (sorceInt < 60){
                    mHolder.testScoresBadBtn.setText(study.getTestSorceStr() + "分");
                    mHolder.testBtn.setVisibility(View.GONE);
                    mHolder.testScoresGoodBtn.setVisibility(View.GONE);
                    mHolder.testScoresGreatBtn.setVisibility(View.GONE);
                    mHolder.testScoresBadBtn.setVisibility(View.VISIBLE);
                    mHolder.testScoresBadBtn.setOnClickListener(this);
                }
                mHolder.studyBtn.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        /*if(position == 0){

            mHolder.lineV.setVisibility(View.GONE);

        }*/
        /*Drawable drawableLeft = activity.getResources().getDrawable(R.drawable.icons8_pen48);
        mHolder.studyBtn.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);*/

        //mHolder.studyBtn.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
        return view;
    }
    static class ViewHolder {
        LinearLayout studyLLayout;
        // title
        TextView titleTv;
        // 图片
        ImageView studyIv;
        //
        Button studyBtn;
        //
        Button testBtn;
        //
        Button testScoresGoodBtn;
        //
        Button testScoresGreatBtn;
        //
        Button testScoresBadBtn;

        View lineV;

    }
}
