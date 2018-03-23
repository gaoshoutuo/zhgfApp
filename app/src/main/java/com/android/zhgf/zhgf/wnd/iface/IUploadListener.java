package com.android.zhgf.zhgf.wnd.iface;/**
 * Created by Administrator on 2016/5/3.
 */

/*******************************************************************************************************
 * Class Name:WndProject - IUploadListener
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title IUploadListener
 * @Package com.jiechu.wnd.iface
 * @date 2016/5/3 10:35
 * @Description 所有上类的接口
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public interface IUploadListener {
    public void onRemote(boolean isSuccess, String pRemotePath, int intType);
}
