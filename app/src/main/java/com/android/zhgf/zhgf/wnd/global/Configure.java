package com.android.zhgf.zhgf.wnd.global;/**
 * Created by Administrator on 2016/5/10.
 */

import android.content.Context;

import com.android.zhgf.zhgf.app.Constant;

import java.util.HashMap;
import java.util.Map;

/*******************************************************************************************************
 * Class Name:WndProject - Configure
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title Configure
 * @Package com.jiechu.wnd.global
 * @date 2016/5/10 13:37
 * @Description 配置类, 存放了APP使用过程当中的一些默认配置
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class Configure {
    Context m_Ctx;


    // 政工网地址
    String m_sHttpServer = "http://20.150.180.188";
    // RTMP视频查看服务器地址
    String m_sLiveServer = "rtmp://20.150.180.187:110/live/";



    // 业务系统接口地址,此接口地址与GPS、FTP上传为同一个
    String m_sLoginGPSServer = "http://20.150.180.188:81";
    // 视频会议系统地址
    String m_webRtc = "https://20.150.180.183:8443/";
    // FTP配置信息
    String i_sFTPServer =  "20.150.180.188";
    String i_sFTPServicesUser = "";
    String i_sFTPServicesPass = "";

    public String getQbls() {
        return m_sLoginGPSServer+m_Prms.get("QBLS");
    }

    public void setQbls(String qbls) {
        this.qbls = qbls;
    }
    public String getM_sLoginGPSServer() {
        return m_sLoginGPSServer;
    }
    String qbls;
    //String m_sLoginGPSServer = "http://112.11.127.22:8203";
    /*String m_sLiveServer = "rtmp://192.168.168.101/live/";*/
    //String m_sGPSServer = "http://wnd.jx96871.cn:7752";
    /*String m_sHttpServer = "http://wnd.jx96871.cn";*/
    //String m_sHttpServer = "http://112.11.127.22:88";
    //String m_sGPSServer = "http://192.168.10.188:8090";

    public String getFTPServicesUser() {
        return i_sFTPServicesUser;
    }

    public void setFTPServicesUser(String i_sFTPServicesUser) {
        this.i_sFTPServicesUser = i_sFTPServicesUser;
    }

    public String getFTPServicesPass() {
        return i_sFTPServicesPass;
    }

    public void setFTPServicesPass(String i_sFTPServicesPass) {
        this.i_sFTPServicesPass = i_sFTPServicesPass;
    }

    String OPERATOR_NAME = Constant.OPERATOR_UNICOM;
    //TODO APP&SERVER交互地址列表
    Map<String,String> m_Prms;
    public Configure(/*Context p_Ctx*/){
        //m_Ctx = p_Ctx;

        m_Prms = new HashMap<String,String>();
        m_Prms.put("TZGG"                   ,"http://wnd.jx96871.cn");//通知公告
        m_Prms.put("QBLS"                   ,"/appmobile/GetInformationList");//情报报知历史
        m_Prms.put("MRYW"                   ,"http://wnd.jx96871.cn");//每日要闻
        m_Prms.put("WebDefault"             ,"http://wnd.jx96871.cn");//WebActivity的默认页
        m_Prms.put("Login"                  ,"/AppMobile/AppLogin");
        //m_Prms.put("Login"                  ,"/Login/Index");
        m_Prms.put("Logout"                 ,"/AppMobile/AppLogOut");
        m_Prms.put("RequestOpenVideo"       ,"/AppMobile/RequestOpenVideo");//轮循是否打开视频
        m_Prms.put("AppVideoOpen"           ,"/AppMobile/AppVideoOpen");//当打开视频后，通知服务端已经打开

        m_Prms.put("FeedbackSubmit"         ,"/AppMobile/InformationManger");//情报报告提交
        m_Prms.put("ContactList"            ,"/AppData/GetData");//获取联系人
        m_Prms.put("CheckNo"                ,"/MobileApp/GetCheckNo");//验证码
        m_Prms.put("UpdatePwd"              ,"/MobileApp/ModifyPassword");//密码更新
        m_Prms.put("AdImage"                ,"/MobileApp/AdImage");//首页顶部图片轮播
        m_Prms.put("AutoUpdate"             ,"/MobileApp/AutoUpdate");//自动更新
        m_Prms.put("GPS_DATA"               ,"/AppMobile/AppDataIn");
        m_Prms.put("GetCurrentJGMB"         ,"/AppMobile/GetCurrentJGMBSystemList");
        m_Prms.put("GetProvinceCityTree"    ,"/AppMobile/GetProvinceCityTree");
        m_Prms.put("GetProvinceCityTree"    ,"/AppMobile/GetJGMBList");


    }

    public String getZhgfServicesAddress(){
        String zhgfServicesAddress = "";
        String[] tmp = m_sLoginGPSServer.split("://");
        if(tmp.length > 1){
            zhgfServicesAddress =  tmp[1];
        }
        return zhgfServicesAddress;
    }

    public void setOperatorName(String OperatorName){
        OPERATOR_NAME = OperatorName;
    }
    public String GetOperatorName(){
        return OPERATOR_NAME;
    }
    public void setHostServer(String val){
        if (val.indexOf("://")>0){
            m_sHttpServer = val;
        }else {
            m_sHttpServer = "http://" + val;
        }
    }
    public String getHostServer(){
        return m_sHttpServer;
    }
    public void setRtmpServer(String val){
        m_sLiveServer = val;
    }

    public void setFTPServer(String host){
        if (host.indexOf(":")>0){
            i_sFTPServer = host.substring(0,host.lastIndexOf(":"));
        }else {
            i_sFTPServer = host;
        }
    }
    public void setLoginGPSServer(String val){
        if (val.indexOf("://")>0){
            m_sLoginGPSServer = val;
        }else {
            m_sLoginGPSServer = "http://" + val;
        }
    }
    public void setLiveServer(String val){
        m_sLiveServer = "rtmp://" + val + "/live/";
    }
    public void setWebRtc(String val){
        m_webRtc ="https://" + val + "/";
    }
    public String getFTPServer(){
        return i_sFTPServer;
    }


    String GetHttpServer(){
        return m_sHttpServer;
    }

    public String GetAlternatelyURL(String key){
        return m_sHttpServer+m_Prms.get(key);
    }


    public String GetGPSURL(String key){
        return m_sLoginGPSServer+m_Prms.get(key);
    }

    public String GetLOGINURL(String key){
        return m_sLoginGPSServer+m_Prms.get(key);
    }

    public String GetWebURL(String key){
        return m_Prms.get(key);
    }

    public String GetLiveURL(){
        return m_sLiveServer;
    }

    public String GetWebRtc(){
        return m_webRtc;
    }
}
