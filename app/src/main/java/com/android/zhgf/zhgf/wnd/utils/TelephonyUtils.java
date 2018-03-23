package com.android.zhgf.zhgf.wnd.utils;

/*******************************************************************************************************
 * Class Name:WndProject - TelephoneUtil
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title TelephoneUtil
 * @Package com.jiechu.wnd.utils
 * @date 2016/4/20 0:34
 * @Description Android电话设备信息的相关操作
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.android.zhgf.zhgf.activity.LessonListActivity;
import com.android.zhgf.zhgf.activity.StudyLessonActivity;
import com.android.zhgf.zhgf.adapter.GridLessonAdapter;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.app.Constant;
import com.android.zhgf.zhgf.bean.Lesson;
import com.android.zhgf.zhgf.utils.MyOkhttp;
import com.android.zhgf.zhgf.wnd.global.Configure;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TelephonyUtils {
    TelephonyManager mTelephony;
    ConnectivityManager cm;
    Context context;
    public TelephonyUtils(Context pContext){
        this.context = pContext;
        mTelephony = (TelephonyManager)pContext.getSystemService(Context.TELEPHONY_SERVICE);
        cm = (ConnectivityManager)pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    //获取本机电话号码
    public String getPhoneNumber(){
        if(mTelephony!=null){
            return mTelephony.getLine1Number();
        }
        return null;
    }

    //获取智能设备唯一编号
    public String getDeviceID(){
        if(mTelephony!=null){
            return mTelephony.getDeviceId();
        }
        return null;
    }

    //获取SIM卡号
    public String getSIM(){
        if(mTelephony!=null){
            return mTelephony.getSimSerialNumber();
        }
        return null;
    }
    //得到用户ID
    public String getSubscriberID(){
        if(mTelephony!=null){
            return mTelephony.getSubscriberId();
        }
        return null;
    }
    public void getProviders() {
        Configure tcfg = ((AppApplication)context).getConfigures();
        String net = getNetWork();
        List<String> infos = getNetWorkList();
        if (net == null || net.equals("WIFI")) {
            if (infos.size() > 1) {
                infos.remove("WIFI");
                net = infos.get(0);
                if (net.equals("3gwap") || net.equals("uniwap")
                        || net.equals("3gnet") || net.equals("uninet")) {
                    tcfg.setOperatorName(Constant.OPERATOR_CMCC);
                } else if (net.equals("cmnet") || net.equals("cmwap")) {
                    tcfg.setOperatorName(Constant.OPERATOR_UNICOM);
                } else if (net.equals("ctnet") || net.equals("ctwap")) {
                    tcfg.setOperatorName(Constant.OPERATOR_TELICOM);
                }
            } else {
                tcfg.setOperatorName(getProvidersName());
            }
        } else {
            if (net.equals("3gwap") || net.equals("uniwap")
                    || net.equals("3gnet") || net.equals("uninet")) {
                tcfg.setOperatorName(Constant.OPERATOR_CMCC);
            } else if (net.equals("cmnet") || net.equals("cmwap")) {
                tcfg.setOperatorName(Constant.OPERATOR_UNICOM);
            } else if (net.equals("ctnet") || net.equals("ctwap")) {
                tcfg.setOperatorName(Constant.OPERATOR_TELICOM);
            }
        }
    }
    public List<String> getNetWorkList() {

        NetworkInfo[] infos = cm.getAllNetworkInfo();
        List<String> list = new ArrayList<String>();
        if (infos != null) {
            for (int i = 0; i < infos.length; i++) {
                NetworkInfo info = infos[i];
                String name = null;
                if (info.getTypeName().equals("WIFI")) {
                    name = info.getTypeName();
                } else {
                    name = info.getExtraInfo();
                }
                if (name != null && list.contains(name) == false) {
                    list.add(name);
                    // System.out.println(name);
                }
            }
        }
        return list;
    }
    public String getNetWork() {
        String NOWNET = null;
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getTypeName().equals("WIFI")) {
                NOWNET = info.getTypeName();
            } else {
                NOWNET = info.getExtraInfo();// cmwap/cmnet/wifi/uniwap/uninet
            }
        }
        return NOWNET;
    }

    //获取运营商
    public String getProvidersName() {
        String ProvidersName = "";
        Configure tcfg = ((AppApplication)context).getConfigures();
        try {
            String operator = mTelephony.getSimOperator();
            if(operator!=null){

                if(operator.equals("46000") || operator.equals("46002")|| operator.equals("46007")){
                    //中国移动
                    ProvidersName = Constant.OPERATOR_CMCC;
                    tcfg.setOperatorName(Constant.OPERATOR_CMCC);
                }else if(operator.equals("46001")){
                    //中国联通
                    ProvidersName = Constant.OPERATOR_UNICOM;
                    tcfg.setOperatorName(Constant.OPERATOR_UNICOM);
                }else if(operator.equals("46003")){
                    //中国电信
                    ProvidersName = Constant.OPERATOR_TELICOM;
                    tcfg.setOperatorName(Constant.OPERATOR_TELICOM);
                }
            }else{
                operator = mTelephony.getSubscriberId();
                if (operator == null || operator.equals("")) {
                    //("未检测到sim卡信息!")
                    return ProvidersName;
                }
                if (operator != null) {
                    if (operator.startsWith("46000")
                            || operator.startsWith("46002")) {
                        //中国移动
                        ProvidersName = Constant.OPERATOR_CMCC;
                        tcfg.setOperatorName(Constant.OPERATOR_CMCC);
                    } else if (operator.startsWith("46001")) {
                        //中国联通
                        ProvidersName = Constant.OPERATOR_UNICOM;
                        tcfg.setOperatorName(Constant.OPERATOR_UNICOM);
                    } else if (operator.startsWith("46003")) {
                        //中国电信
                        ProvidersName = Constant.OPERATOR_TELICOM;
                        tcfg.setOperatorName(Constant.OPERATOR_TELICOM);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ProvidersName;
    }
    //WiFi是否连接
    public boolean isWifi() {

        NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
        if (activeNetInfo != null){
            if(activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI){
                return true;
            }
        }
        return false;
    }
    //WiFi是否可用
    public boolean isWifiAvailable()
    {

        NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }

        return false ;
    }
    //网络是否连接
    public  boolean isNetworkAvailable() {
        if (cm == null) {
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void GetIP(){
        //new GetData().execute("http://ip.cn/?hgjcfy=pfgb1");
        new GetData().execute("http://whois.pconline.com.cn/ipJson.jsp");
    }

    private class GetData extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            return MyOkhttp.get(params[0]);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(!TextUtils.isEmpty(result)){

                JSONObject jsonObject;
                Gson gson=new Gson();
                String jsonData=null;

                try {
                    String ip = result.split(":")[1].replaceAll("\"","").split(",")[0];
                    String serverName = result.split(":")[8].replaceAll("\"","").split(" ")[1].split(",")[0];
                    /*jsonObject = new JSONObject(temp);
                    jsonData = jsonObject.getString("IPCallBack");
                } catch (JSONException e) {
                    e.printStackTrace();*/
                } catch (Exception e){

                }

            }
        }
    }
}
