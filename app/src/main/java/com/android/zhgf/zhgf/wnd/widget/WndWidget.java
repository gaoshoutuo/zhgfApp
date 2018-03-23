package com.android.zhgf.zhgf.wnd.widget;/**
 * Created by Administrator on 2016/6/16.
 */

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/*******************************************************************************************************
 * Class Name:WndProject - WndWidget
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title WndWidget
 * @Package com.jiechu.wnd.widget
 * @date 2016/6/16 22:20
 * @Description
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class WndWidget extends AppWidgetProvider {


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}
