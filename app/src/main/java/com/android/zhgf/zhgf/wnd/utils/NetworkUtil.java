package com.android.zhgf.zhgf.wnd.utils;/**
 * Created by Administrator on 2016/4/19.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.util.StringUtil;
import com.android.zhgf.zhgf.wnd.iface.INetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.dcloud.common.util.ReflectUtils.getApplicationContext;

/*******************************************************************************************************
 * Class Name:WndProject - NetworkUtis2
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title NetworkUtis
 * @Package com.jiechu.wnd.utils
 * @date 2016/4/19 18:11
 * @Description ...
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class NetworkUtil implements INetwork {
    private static final String TAG = NetworkUtil.class.getSimpleName();


    public NetworkUtil(){}

    /**
     * @ClassName: NetworkUtil
     * @Method: getLanIP
     * @Params Context pContext
     * @Description: 获取当前ip地址
     * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
     * @date 2016/4/20 0:33
     * ${tags} 
     */ 
    @Override
    public String getLanIP(Context pContext) {
        WifiManager wifiManager = (WifiManager) pContext
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        return int2ip(i);
    }

    @Override
    public String getInternetIP() {
        return null;
    }

    @Override
    public String getVPNIP() {
        return null;
    }
    /**
     * @ClassName: NetworkUtil
     * @Method: int2ip
     * @Params ipInt 整型的IP地址
     * @Description: 将ip的整数形式转换成ip形式
     * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
     * @date 2016/4/20 0:28
     * ${tags} 
     */ 
    public String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }




    /**
     * @ClassName: NetworkUtil
     * @Method: checkEnable
     * @Params pContext 上下文对象，可以为Application的上下文，也可以是Activity或是Service等对象的Context
     * @Description: 检查网络是否可用
     * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
     * @date 2016/4/19 23:54
     * @return -
     */
    @Override
    public boolean checkEnable(Context pContext) {
        boolean i = false;
        NetworkInfo localNetworkInfo = ((ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (localNetworkInfo != null) && (localNetworkInfo.isAvailable());
    }
/********************************************************************************************************************************************
 *  从网络获取数据函数集合
 ********************************************************************************************************************************************/
    /**
     * @ClassName: NetworkUtil
     * @Method: HttpSendGetData
     * @Params:
     *      String pURL                                 提交的HTTP URL地址
     *      String pParams                              提交的GET参数，参数格式为name1=val1&name2=val2&....namen=valn
     *      OnNetworkStatusListener listener            用户回调对象
     * @Description: 以GET的方式向Http服务器提交数据
     * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
     * @date 2016/4/19 22:45
     * ${tags} 
     */ 
    @Override
    public void HttpSendGetData(String pURL, String pParams, OnNetworkStatusListener listener){
        if (pURL.indexOf("http://")>-1||pURL.indexOf("https://")>-1){
            HttpURLConnection mConn = null;
            BufferedReader mReader = null;
            try {
                String strRealURL = pURL;
                if (null!=pParams){
                    strRealURL+=("?"+pParams);
                }
                URL mUrl = new URL(strRealURL);
                Log.e(TAG, "run: "+strRealURL);
                mConn = (HttpURLConnection) mUrl.openConnection();
                String GPS_URL = ((AppApplication)getApplicationContext()).getConfigures().GetGPSURL("GPS_DATA");

                String Login = ((AppApplication)getApplicationContext()).getConfigures().GetLOGINURL("Login");
                if(!(pURL.contains("AppDataIn")) && !(pURL.contains("Login"))){
                    mConn.setRequestProperty("accept", "*/*");
                    mConn.setRequestProperty("connection", "Keep-Alive");
                    mConn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
                }


                mConn.setRequestMethod("GET");
                /*Log.e(TAG, "run2: "+strRealURL);*/

                if(pURL.contains("AppLogin")){
                    mConn.setConnectTimeout(5000);
                    mConn.setReadTimeout(5000);
                }else{
                    mConn.setConnectTimeout(30000);
                    mConn.setReadTimeout(30000);
                }


                mConn.connect();//TODO 连接远程服务器



                Map<String,List<String>> mHeader = mConn.getHeaderFields();//TODO 获取HTTP文件头信息列表
                //TODO 使用FOR循环来获取每一项头信息，并加入HttpStatus对象实例
                HttpStatus mStatus = new HttpStatus();
                for (String key:mHeader.keySet()) {
                    if (null==key&&mHeader.get(key)!=null){
                        //TODO 如果KEY为空值，并且mHeader.get(KEY)不为空，说明是第一项协议信息，故单独处理
                        String tpHead = mHeader.get(key).get(0);
                        //TODO 使用正则表达式来匹配协议头信息
                        Pattern mPattern=Pattern.compile("(^HTTP)/(\\d\\.\\d) (\\d{0,3}) (.*)[\\r\\n|\\n]?", Pattern.DOTALL);
                        Matcher mchar=mPattern.matcher(tpHead);
                        if (mchar.find()){
                            try{
                                mStatus.setProtocal(mchar.group(1));//TODO 协议名称
                                mStatus.setVersion(mchar.group(2));//TODO 协议版本
                                mStatus.setStatusCode(mchar.group(3));//TODO 通信状态
                                mStatus.setMessage(mchar.group(4));//TODO 通信消息
                            }catch (Exception e){
                                if(listener!=null)listener.onError(0x0001,e.getMessage());
                            }
                        }else{
                            if(listener!=null)listener.onError(0x0002,"解析协议头部失败");
                        }
                    }else{
                        //TODO 如果是普通的头信息，则添加至HttpStatus的HTTP头部信息列表
                        mStatus.addHeader(key,mHeader.get(key));
                    }
                }
                //TODO 如果服务器返回的状态代码是200表示一切正常，可以从服务器获取对应的数据
                if (mStatus.getStatusCode()==200) {
                    //TODO 定义一个字符串的Builder对象，用于暂存从服务器端传入的数据

                    mReader = new BufferedReader(new InputStreamReader(mConn.getInputStream()));

                    StringBuilder sbHtml = new StringBuilder();
                    String strLine = "";
                    while ((strLine = mReader.readLine()) != null) {
                        sbHtml.append(strLine);
                    }

                    mReader.close();
                    mReader = null;
                    mConn.disconnect();
                    mConn = null;

                    /*
                    //TODO 验证传入数据的完整性
                    for (int i = 0; i <sbHtml.length(); i++) {
                        Log.e(TAG, "HttpSendGetData: "+sbHtml.substring(i,i+1) );
                    }*/
                    //TODO 将获取到的数据存入HttpStatus对象
                    //mStatus.setData(sbHtml.toString());
                    mStatus.setData(StringUtil.unicodeToString(sbHtml.toString()));
                    //TODO 监听器对象是否存在，如果存在则调用监听回调，发送成功信息给用户处理
                    if (listener != null) listener.onGetResult(true, mStatus);
                }else{
                    if(listener!=null)listener.onError(mStatus.getStatusCode(),"连接失败");
                }

            } catch (MalformedURLException e) {
                /*e.printStackTrace();*/
                if(listener!=null)listener.onError(0x0010,e.getMessage());
            } catch (IOException e) {
                /*e.printStackTrace();*/
                if(listener!=null)listener.onError(0x0011,e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
            }finally {
                //TODO 无论是否有异常抛出，都关闭输入流和断开链接
                if (mReader!=null) try {
                    mReader.close();
                } catch (IOException e) {
                    /*e.printStackTrace();*/
                    //TODO 监听器对象是否存在，如果存在则调用监听回调，发送错误代码以供用户处理
                    if(listener!=null)listener.onError(0x0012,e.getMessage());
                }
                if (mConn!=null) {
                    mConn.disconnect();
                }
            }
        }
    }
    /**
     * @ClassName: NetworkUtil
     * @Method: HttpSendGetData
     * @Params:
     *      String pURL                                 提交的HTTP URL地址
     *      Map<String, Object> pParams                 提交的POST参数，参数格式为name1=val1&name2=val2&....namen=valn
     *      OnNetworkStatusListener listener            用户回调对象
     * @Description: 以GET的方式向Http服务器提交数据
     * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
     * @date 2016/4/19 22:45
     * ${tags}
     */
    @Override
    public void HttpSendPostData(String pURL, Map<String, Object> pParams, OnNetworkStatusListener listener){
        if (pURL.indexOf("http://")>-1||pURL.indexOf("https://")>-1){
            HttpURLConnection mConn = null;
            BufferedReader mReader = null;
            PrintWriter mWrite = null;


            URL mUrl = null;
            try {
                mUrl = new URL(pURL);
                mConn = (HttpURLConnection)mUrl.openConnection();
                mConn.setRequestProperty("accept", "*/*");
                mConn.setRequestProperty("connection", "Keep-Alive");
                mConn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
                mConn.setUseCaches(false);
                mConn.setDoInput(true);
                mConn.setDoOutput(true);
                mConn.setRequestMethod("POST");

                if(pURL.contains("AppLogin")){
                    mConn.setConnectTimeout(5000);
                    mConn.setReadTimeout(5000);
                }
                mConn.connect();

                //TODO 如果传入的参数有值，则以POST的方式向服务器提交参数，参数格式为name1=val1&name2=val2&....namen=valn
                if (pParams!=null && pParams.size()>0){
                    String strParams = ParamsArrayToString(pParams);
                    Log.e(TAG, "HttpSendPostData: 要提交的POST的值为："+strParams);
                    mWrite = new PrintWriter(new OutputStreamWriter(mConn.getOutputStream()));
                    mWrite.write(strParams);
                    mWrite.flush();
                    mWrite.close();
                }



                Map<String,List<String>> mHeader = mConn.getHeaderFields();//TODO 获取HTTP文件头信息列表
                //TODO 使用FOR循环来获取每一项头信息，并加入HttpStatus对象实例
                HttpStatus mStatus = new HttpStatus();
                for (String key:mHeader.keySet()) {
                    if (null==key&&mHeader.get(key)!=null){
                        //TODO 如果KEY为空值，并且mHeader.get(KEY)不为空，说明是第一项协议信息，故单独处理
                        String tpHead = mHeader.get(key).get(0);
                        /*Log.e(TAG, "HttpSendPostData: "+tpHead);*/
                        //TODO 使用正则表达式来匹配协议头信息
                        Pattern mPattern=Pattern.compile("(^HTTP)/(\\d\\.\\d) (\\d{0,3}) (.*)[\\r\\n|\\n]?", Pattern.DOTALL);
                        Matcher mchar=mPattern.matcher(tpHead);
                        if (mchar.find()){
                            try{
                                /*Log.e(TAG, "HttpSendPostData: "+ mchar.group(1));
                                Log.e(TAG, "HttpSendPostData: "+ mchar.group(2));
                                Log.e(TAG, "HttpSendPostData: "+ mchar.group(3));
                                Log.e(TAG, "HttpSendPostData: "+ mchar.group(4));*/
                                mStatus.setProtocal(mchar.group(1));//TODO 协议名称
                                mStatus.setVersion(mchar.group(2));//TODO 协议版本
                                mStatus.setStatusCode(mchar.group(3));//TODO 通信状态
                                mStatus.setMessage(mchar.group(4));//TODO 通信消息
                            }catch (Exception e){
                                if(listener!=null)listener.onError(0x0001,e.getMessage());
                            }
                        }else{
                            if(listener!=null)listener.onError(0x0002,"解析协议头部失败");
                        }
                    }else{
                        //TODO 如果是普通的头信息，则添加至HttpStatus的HTTP头部信息列表
                        mStatus.addHeader(key,mHeader.get(key));
                    }
                }
                //TODO 如果服务器返回的状态代码是200表示一切正常，可以从服务器获取对应的数据
                if (mStatus.getStatusCode()==200){
                    //TODO 定义一个字符串的Builder对象，用于暂存从服务器端传入的数据,否则就不取

                    mReader = new BufferedReader(new InputStreamReader(mConn.getInputStream()));

                    StringBuilder sbHtml = new StringBuilder();
                    String strLine="";
                    while ((strLine=mReader.readLine())!=null){
                        sbHtml.append(strLine);
                    }

                    mReader.close();mReader=null;
                    mConn.disconnect();mConn=null;

                    /*
                    //TODO 验证传入数据的完整性
                    for (int i = 0; i <sbHtml.length(); i++) {
                        Log.e(TAG, "HttpSendGetData: "+sbHtml.substring(i,i+1) );
                    }*/
                    //TODO 将获取到的数据存入HttpStatus对象
                    mStatus.setData(sbHtml.toString());
                    //TODO 监听器对象是否存在，如果存在则调用监听回调，发送成功信息给用户处理
                    if(listener!=null)listener.onPostResult(true, mStatus);
                }


            } catch (MalformedURLException e) {
                /*e.printStackTrace();*/
                if(listener!=null)listener.onError(0x0010,e.getMessage());
            } catch (IOException e) {
                /*e.printStackTrace();*/
                if(listener!=null)listener.onError(0x0011,e.getMessage());
            }finally {
                //TODO 无论是否有异常抛出，都关闭输入流和断开链接
                if (mReader!=null) try {
                    mReader.close();
                } catch (IOException e) {
                    /*e.printStackTrace();*/
                    //TODO 监听器对象是否存在，如果存在则调用监听回调，发送错误代码以供用户处理
                    if(listener!=null)listener.onError(0x0012,e.getMessage());
                }
                if (mConn!=null) {
                    mConn.disconnect();
                }
            }

        }
    }
    /**
     * @ClassName: NetworkUtil
     * @Method: HttpSendPostDataByFormEncode
     * @Params
     * @Description: 以multipart/form-data方式向服务器提交数据
     * @author hnyashiro 5303363@qq.com
     * @date 2016/4/19 22:43
     * ${tags}
     */
    @Override
    public void HttpSendPostDataByFormEncode(String pURL, Map<String, Object> pParams, OnNetworkStatusListener listener) {
        /*setRequestProperty(" Content-Type ",
                " application/x-www-form-urlencoded ");  */
    }

    public Bitmap HttpGetRemoteImage(final String img_src,final OnNetworkStatusListener listener){
        if ((img_src!="")&&(img_src.indexOf("http://")>=0)) {
            Log.e(TAG, "HttpGetRemoteImage: 开始从网络上获取图片，只支持HTTP协议的图像信息");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL mUrl = new URL(img_src);
                        HttpURLConnection mConn = (HttpURLConnection)mUrl.openConnection();
                        mConn.setDoInput(true);
                        mConn.connect();
                        InputStream Ins=mConn.getInputStream();
                        /*int ln=0,total_ln=0;
                        byte[] mBytes=new byte[1024];
                        while ((ln=Ins.read(mBytes))>0){
                            
                        }*/
                        Bitmap img = BitmapFactory.decodeStream(Ins);
                        if (listener!=null) {
                            listener.onGetResult(true, img);
                        }else{
                            listener.onError(1000020,"远程图片【"+img_src+"】获取失败");
                        }
                        Ins.close();
                        //img.recycle();
                        Thread.currentThread().interrupt();
                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        return null;
    }


    /**
     * @ClassName: NetworkUtil
     * @Method: ParamsArrayToString
     * @Description: 将Map数组形式的数据转化为字符串形式的内容
     * @author hnyashiro 5303363@qq.com
     * @date 2016/4/18 19:44
     * @return -
     */
    String ParamsArrayToString(Map<String,Object> pParams){
        String strParams="";
        for (String key:pParams.keySet()){
            if (key!=""){
                try {
                    Object tpValue = pParams.get(key);
                    if(tpValue==null){
                        continue;
                    }
                    if (tpValue==null && tpValue==""){
                        return "";
                    }
                    if (tpValue instanceof Float||tpValue instanceof Integer||tpValue instanceof Long || tpValue instanceof Double){
                        strParams+= (key+"="+tpValue+"&");
                    }else {
                        strParams+= (key+"="+ URLEncoder.encode((String)tpValue, "UTF-8")+"&");
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return strParams;
    }

    byte[] ParseArrayToFormEncode(Map<String,Object> pParams){
        return null;
    }


    /**
     * @ClassName: NetworkUtis2.HttpStatus
     * @Params -
     * @Description: Http Response 对象
     * @author hnyashiro 5303363@qq.com
     * @date 2016/4/19 18:17
     */ 
    public class HttpStatus{
        /*TODO 与Message的what变量同样效果，用于多个网络操作调用同一监听器时区分不同操作的标识*/
        /*int what;*//*在当前版本中不适用*/
        String strMethod;
        Map<String,Object> mHeader;
        String strHttpProtocal = "";
        int intStatusCode = 0;
        String strMessage="";
        String strVersion="";

        String strData = "";

        Object objObject=null;

        public HttpStatus(){
            mHeader = new HashMap<String,Object>();
        }


        void setMethod(String pMethod){
            strMethod = pMethod;
        }
        public String getMthod(){
            return strMethod;
        }
        void addHeader(String key,Object data){
            mHeader.put(key,data);
        }
        public Object getHeader(String key){
            return mHeader.get(key);
        }

        void setProtocal(String strProtoal){
            strHttpProtocal = strProtoal;
        }
        void setStatusCode(String statusCode){
            intStatusCode = Integer.parseInt(statusCode);
        }

        void setMessage(String msg){
            strMessage = msg;
        }

        public String getProtocal(){
            return strHttpProtocal;
        }
        public int getStatusCode(){
            return intStatusCode;
        }
        public String getMessage(){
            return strMessage;
        }

        void setVersion(String ver){
            strVersion = ver;
        }
        public String getStrVersion(){
            return strVersion;
        }

        void setData(String pData){
            strData = pData;
        }
        public Object getData(){
            return strData;
        }

        void setObject(Object pObj){
            objObject = pObj;
        }
        public Object getObject(){
            return objObject;
        }
    }

    /**
    * @ClassName: NetworkUtil
    * @Method: DonwloadImage
     * @Params
    * @Description: 从网络上下载一张图片并保存在指定的目录下
    * @author hnyashiro 5303363@qq.com
    * @date 2016/4/30 22:28
    * @return
    * ${tags}
    */
    public void HttpGetImageToSave(String image_url,String save_file_path) {

        URL url = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            //构建图片的url地址
            /*url = new URL("http://avatar.csdn.net/C/6/8/1_bz419927089.jpg");*/
            url = new URL(image_url);
            //开启连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时的时间，5000毫秒即5秒
            conn.setConnectTimeout(5000);
            //设置获取图片的方式为GET
            conn.setRequestMethod("GET");
            //响应码为200，则访问成功
            if (conn.getResponseCode() == 200) {
                //获取连接的输入流，这个输入流就是图片的输入流
                is = conn.getInputStream();
                //构建一个file对象用于存储图片
                File file = new File(save_file_path);
                fos = new FileOutputStream(file);
                int len = 0,totalsize=0;
                byte[] buffer = new byte[1024];
                //将输入流写入到我们定义好的文件中
                while ((len = is.read(buffer)) != -1) {

                    fos.write(buffer, 0, len);
                    totalsize+=len;
                    Log.e(TAG, "HttpGetImageToSave: 下载了"+len+"/"+totalsize);
                }
                //将缓冲刷入文件
                fos.flush();
                //告诉handler，图片已经下载成功
                /*handler.sendEmptyMessage(LOAD_SUCCESS);*/
            }
        } catch (Exception e) {
            //告诉handler，图片已经下载失败
            /*handler.sendEmptyMessage(LOAD_ERROR);*/
            e.printStackTrace();
        } finally {
            //在最后，将各种流关闭
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                /*handler.sendEmptyMessage(LOAD_ERROR);*/
                e.printStackTrace();
            }
        }
    }
    /**
 * @ClassName: NetworkUtil
 * @Method: finalize
 * @Params 
 * @Description: 类析构函数,释放清理部分内容
 * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
 * @date 2016/4/20 0:41
 * ${tags} 
 */ 
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
