package com.android.zhgf.zhgf.wnd.global;/**
 * Created by Administrator on 2016/4/21.
 */

import android.app.Service;
import android.util.Log;

import java.util.LinkedHashMap;

/*******************************************************************************************************
 * Class Name:WndProject - ServiceInstanceManager
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title ServiceInstanceManager
 * @Package com.jiechu.wnd
 * @date 2016/4/21 9:58
 * @Description Service服务管理类
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class ServiceInstanceManager {
    private static final String TAG = ServiceInstanceManager.class.getSimpleName();
    LinkedHashMap<String,Service> mServices=null;

    public ServiceInstanceManager(){
        mServices = new LinkedHashMap<String,Service>();
    }
    /**
     * @ClassName: ServiceInstanceManager
     * @Method: addService
     * @Params p_srv 一个Service对象
     * @Description: 添加一个Service对象
     * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
     * @date 2016/4/21 10:11
     * ${tags}
     */
    public void addService(Service p_srv){
        String srv_name = p_srv.getClass().getSimpleName();
        if (mServices.get(srv_name)!=null){
            Log.e(TAG, "addServiceInstance: 当前Service["+srv_name+"]已存在");
        }else{
            mServices.put(srv_name,p_srv);
            Log.e(TAG, "addServiceInstance: 当前Service[" + srv_name + "]添加成功");
        }
    }
    /**
     * @ClassName: ServiceInstanceManager
     * @Method: removeService
     * @Params key 索引
     * @Description: 删除一个Service对象
     * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
     * @date 2016/4/21 10:11
     * ${tags}
     */
    public void removeService(String key){
        if (mServices.get(key)!=null){
            mServices.remove(key);
            Log.e(TAG, "addServiceInstance: 当前Service[" + key + "]移除成功");
        }else{
            Log.e(TAG, "removeService: 当前Service[" + key + "]不存在");
        }
    }
    /**
     * @ClassName: ServiceInstanceManager
     * @Method: get
     * @Params key 索引
     * @Description: 
     * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
     * @date 2016/4/21 10:12
     * ${tags} 
     */
    public Service get(String key){
        return mServices.get(key);
    }
}
