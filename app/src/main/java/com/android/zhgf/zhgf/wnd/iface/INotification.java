package com.android.zhgf.zhgf.wnd.iface;/**
 * Created by Administrator on 2016/4/20.
 */

/*******************************************************************************************************
 * Class Name:WndProject - INotification
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title INotification
 * @Package com.jiechu.wnd.iface
 * @date 2016/4/20 19:22
 * @Description 定义统一的消息更新接口
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public interface INotification {
    public void doGPSUpdate(Object Sender, long gps_lon, long gps_lat);//在需要更新GPS坐标的对象当中实现doGPSUpdate接口
}
