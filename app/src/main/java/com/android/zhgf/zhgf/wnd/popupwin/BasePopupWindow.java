package com.android.zhgf.zhgf.wnd.popupwin;/**
 * Created by Administrator on 2016/6/19.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

//import io.dcloud.HBuilder.Hello.R;

import java.util.LinkedList;
import java.util.zip.Inflater;

/*******************************************************************************************************
 * Class Name:WndProject - BasePopupWindow
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title BasePopupWindow
 * @Package com.jiechu.wnd.popupwin
 * @date 2016/6/19 17:18
 * @Description
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class BasePopupWindow extends PopupWindow {
    private static final String TAG = BasePopupWindow.class.getSimpleName();

    private Context mCtx;
    private LinkedList<View> mViewList;
    private LayoutInflater mInflat;
    private View mPwroot;

    private int screenWidth,screenHeight;


    public BasePopupWindow(Activity pCxt, int pLayoutInflat, IDefineView plistener){
        mCtx = pCxt;
        DisplayMetrics mDMerics=new DisplayMetrics();
        pCxt.getWindowManager().getDefaultDisplay().getMetrics(mDMerics);
        screenWidth = mDMerics.widthPixels;
        screenHeight = mDMerics.heightPixels/4;
        mViewList = new LinkedList<View>();

        mInflat = (LayoutInflater) pCxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPwroot = mInflat.inflate(pLayoutInflat,null);
        this.setContentView(mPwroot);

        if (plistener!=null && plistener instanceof IDefineView){
            plistener.doDefine(mPwroot);
        }else{
            throw new RuntimeException(){
                @Override
                public String getMessage() {
                    return TAG+":"+TAG+"异常，View对象定义接口空,请在外部定义实现IDefineView接口并将此监听器传入";
                }
            };
        }

        this.setOutsideTouchable(true);
        this.setWidth(screenWidth);
        this.setHeight(screenHeight);
        ColorDrawable mColor = new ColorDrawable(0);
        this.setBackgroundDrawable(mColor);

    }

    void setSize(int w,int h){
        this.setWidth(w);
        this.setHeight(h);
    }





    public interface IDefineView{
        void doDefine(View v);
    }
}
