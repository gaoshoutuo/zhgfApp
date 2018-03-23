package com.android.zhgf.zhgf.activity;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.zhgf.zhgf.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 我虽然不是一个活动，但接近于一个活动
 */

public class BottomMenu implements View.OnClickListener {
    private PopupWindow popupWindow;
    private ImageView video1,video2,camera1,camera2,sound1,sound2,recordStart,recordStop;
    private View mMenuView;
    private Activity mContext;
    private View.OnClickListener clickListener;
    private List<ImageView> lists;
    private boolean isReally=false;
    private TextView videoStart,videoStop,videoDelete;
    private TextView record_start,record_stop,record_delete;
  /*  public void setList(){
        lists.add(0,camera1);
        lists.add(1,camera2);
        lists.add(2,sound1);
        lists.add(3,sound2);
        lists.add(4,video1);
        lists.add(5,video2);
    }*/
    public List<ImageView> getLists(){
        return lists;
    }

    private void initView1(){//音视频
        sound1 = mMenuView.findViewById(R.id.menu4_pic1);
        sound2 = mMenuView.findViewById(R.id.menu4_pic2);
        video1= mMenuView.findViewById(R.id.menu4_pic3);
        video2= mMenuView.findViewById(R.id.menu4_pic4);
        sound1.setOnClickListener(this);
        sound2.setOnClickListener(this);
        video1.setOnClickListener(this);
        video2.setOnClickListener(this);
    }
    private void initView2(){//照片
        camera1 = mMenuView.findViewById(R.id.menu3_pic1);
        camera2 = mMenuView.findViewById(R.id.menu3_pic2);
        camera1.setOnClickListener(this);
        camera2.setOnClickListener(this);

    }

    private void initView3() {
        recordStart= mMenuView.findViewById(R.id.record_start);
        recordStop= mMenuView.findViewById(R.id.record_stop);
        recordStart.setOnClickListener(this);
        recordStop.setOnClickListener(this);
    }
    private void initView4(){
        record_delete=mMenuView.findViewById(R.id.record_delete_pop);
        record_stop=mMenuView.findViewById(R.id.record_stop_pop);
        record_start=mMenuView.findViewById(R.id.record_start_pop);
        record_delete.setOnClickListener(this);
        record_stop.setOnClickListener(this);
        record_start.setOnClickListener(this);
    }
    private void initView5(){
        videoDelete=mMenuView.findViewById(R.id.video_delete_pop);
        videoStop=mMenuView.findViewById(R.id.video_stop_pop);
        videoStart=mMenuView.findViewById(R.id.video_start_pop);
        videoDelete.setOnClickListener(this);
        videoStop.setOnClickListener(this);
        videoStart.setOnClickListener(this);
    }

    public BottomMenu(int rId,Activity context, View.OnClickListener clickListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
        mContext = context;
        if (rId == R.id.intelligence_video) {
            mMenuView = inflater.inflate(R.layout.bottom_menu_4, null);
            initView1();
        } else if (rId == R.id.intelligence_picture) {
            mMenuView = inflater.inflate(R.layout.bottom_menu_3, null);
            initView2();
        }else if(rId==7156){
            mMenuView = inflater.inflate(R.layout.bottom_menu_5, null);
            initView3();
        }else if(rId==R.id.intelligence_wav_file){
            mMenuView = inflater.inflate(R.layout.pop_layout_record, null);
            initView4();
        }else if(rId==R.id.intelligence_video_file){
            mMenuView = inflater.inflate(R.layout.pop_layout_video, null);
            initView5();
        }
            popupWindow = new PopupWindow(mMenuView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
         //popupWindow.setAnimationStyle(R.style.popwin_anim_style);

        if(rId==R.id.intelligence_wav_file||rId==R.id.intelligence_video_file){
           // ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.colorWhite));
            ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.teansetpa));
            popupWindow.setBackgroundDrawable(dw);
            isReally=true;
        }else {
            ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.colorHappy));
            popupWindow.setBackgroundDrawable(dw);
        }

       //setList();
        }

    /**
     * 显示菜单
     */
    public void show() {
        //得到当前activity的rootView
        View rootView = ((ViewGroup) mContext.findViewById(android.R.id.content)).getChildAt(0);
        if (isReally){
            popupWindow.showAtLocation(rootView, Gravity.CENTER_VERTICAL, 0, 0);
        }else {
            popupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }

        //popupWindow.showAtLocation(rootView, Gravity.CENTER_VERTICAL, 0, 0);
    }

    @Override
    public void onClick(View view) {
        popupWindow.dismiss();
        switch (view.getId()) {
            case R.id.bottom_menu_3:
                break;
            case R.id.bottom_menu_23:
                break;
            default:
                clickListener.onClick(view);
                break;
        }
    }

  /*  @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        int height = mMenuView.findViewById(R.id.pop_layout).getTop();
        int y=(int) event.getY();
        if(event.getAction()==MotionEvent.ACTION_UP){
            if(y<height){
                popupWindow. dismiss();
            }
        }
        return true;
    }*/

}

