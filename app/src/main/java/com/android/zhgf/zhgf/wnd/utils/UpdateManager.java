/*******************************************************************************************************
 * Class Name:WndProject - UploadManager
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title UploadManager
 * @Package com.jiechu.wnd.utils
 * @date 2016/4/22 21:17
 * @Description 更新管理器
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
package com.android.zhgf.zhgf.wnd.utils;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import com.android.zhgf.zhgf.app.AppApplication;
        import com.android.zhgf.zhgf.util.DateUtil;
        import com.android.zhgf.zhgf.util.SharedPreferencesUtils;
        import com.android.zhgf.zhgf.wnd.utils.NetHelper;
        import com.google.gson.Gson;

        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageInfo;
        import android.content.pm.PackageManager.NameNotFoundException;
        import android.net.Uri;
        import android.os.Environment;
        import android.os.Handler;
        import android.os.Message;
        import android.util.Log;

        import static io.dcloud.common.util.ReflectUtils.getApplicationContext;

public class UpdateManager {

    private String curVersion;
    private String newVersion;
    private int curVersionCode;
    private int newVersionCode;
    private String updateUrl;
    private String creatTime;
    private String updateInfo;
    private UpdateCallback callback;
    private Context ctx;

    private int progress;
    private Boolean hasNewVersion;
    private Boolean canceled;
    private String pattern = "yyyy-MM-dd";
    //存放更新APK文件的路径
    public static final String UPDATE_DOWNURL = "http://192.168.168.106/wnd_upload/wnd_upload.apk";
    //存放更新APK文件相应的版本说明路径
    public static final String UPDATE_CHECKURL = "/auto_update.txt";
    public static final String UPDATE_APKNAME = "wnd_update.apk";
    //public static final String UPDATE_VERJSON = "ver.txt";
    public static final String UPDATE_SAVENAME = "wnd.apk";
    private static final int UPDATE_CHECKCOMPLETED = 1;
    private static final int UPDATE_DOWNLOADING = 2;
    private static final int UPDATE_DOWNLOAD_ERROR = 3;
    private static final int UPDATE_DOWNLOAD_COMPLETED = 4;
    private static final int UPDATE_DOWNLOAD_CANCELED = 5;

    //从服务器上下载apk存放文件夹
    private String savefolder = Environment.getExternalStorageDirectory()+"/Wnd_Download/";

    //private String savefolder = "/sdcard/";
    //public static final String SAVE_FOLDER =Storage. // "/mnt/innerDisk";
    public UpdateManager(Context context, UpdateCallback updateCallback) {
        ctx = context;
        callback = updateCallback;
        //savefolder = context.getFilesDir();
        File ff=new File(savefolder);
        if (!ff.exists()) {
            ff.mkdirs();
        }
        canceled = false;

        getCurVersion();
    }

    public String getNewVersionName()
    {
        return newVersion;
    }

    public String getUpdateInfo()
    {
        return updateInfo;
    }

    private void getCurVersion() {
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0);
            curVersion = pInfo.versionName;
            curVersionCode = pInfo.versionCode;
        } catch (NameNotFoundException e) {
            Log.e("update", e.getMessage());
            curVersion = "1.1.1000";
            curVersionCode = 111000;
        }

    }

    public void checkUpdate() {
        hasNewVersion = false;
        new Thread(){
            // ***************************************************************
            /**
             * @by wainiwann add
             *
             */
            @Override
            public void run() {
                Log.i("@@@@@", ">>>>>>>>>>>>>>>>>>>>>>>>>>>getServerVerCode() ");
                try {

                    //String verjson = NetHelper.httpStringGet("http://192.168.168.105/wnd_upload/wnd_upload_version.txt");

                    String verjson = NetHelper.httpStringGet(((AppApplication)getApplicationContext()).getConfigures().getHostServer()+ UPDATE_CHECKURL);
                    //String verjson = "{\"creatTime\":\"2017-11-24\",\"updateUrl\":\"http://20.150.180.188/update/update.json\"}";

                    Gson gson = new Gson();
                    Update update = new Update();
                    update = gson.fromJson(verjson,Update.class);
                    //JSONArray array = new JSONArray(verjson);
                    creatTime = update.getCreatTime();
                    updateUrl = update.getUpdateUrl();
                    SharedPreferencesUtils helper = new SharedPreferencesUtils(ctx, "setting");
                    String currentcreatTime = helper.getString("creatTime");
                    hasNewVersion = DateUtil.compareDate(creatTime,currentcreatTime,pattern);
                    /*if (array.length() > 0) {
                        JSONObject obj = array.getJSONObject(0);
                        try {
                            updateTime = obj.getString("creatTime");
                            updateUrl = obj.getString("updateUrl");

                            if (newVersionCode > curVersionCode) {
                                hasNewVersion = true;
                            }
                        } catch (Exception e) {
                            newVersionCode = -1;
                            newVersion = "";
                            updateInfo = "";

                        }
                    }*/
                } catch (Exception e) {
                    Log.e("update", e.getMessage());
                }
                //hasNewVersion = true;newVersion = "1.1";
                updateHandler.sendEmptyMessage(UPDATE_CHECKCOMPLETED);
            };
            // ***************************************************************
        }.start();

    }

    public void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(
                Uri.fromFile(new File(savefolder, UPDATE_SAVENAME)),
                "application/vnd.android.package-archive");
        ctx.startActivity(intent);
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void downloadPackage()
    {


        new Thread() {
            @Override
            public void run() {
                try {
                    //URL url = new URL(UPDATE_DOWNURL);
                    //URL url = new URL("http://192.168.168.106/wnd_upload/wnd_upload.apk");
                    URL url = new URL(updateUrl);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();


                    File ApkFile = new File(savefolder,UPDATE_SAVENAME);


                    if(ApkFile.exists())
                    {

                        ApkFile.delete();
                    }


                    FileOutputStream fos = new FileOutputStream(ApkFile);

                    int count = 0;
                    byte buf[] = new byte[512];

                    do{

                        int numread = is.read(buf);
                        count += numread;
                        progress =(int)(((float)count / length) * 100);

                        updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOADING));
                        if(numread <= 0){

                            updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_COMPLETED);
                            break;
                        }
                        fos.write(buf,0,numread);
                    }while(!canceled);
                    if(canceled)
                    {
                        updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_CANCELED);
                    }
                    fos.close();
                    is.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();

                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,e.getMessage()));
                } catch(IOException e){
                    e.printStackTrace();

                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,e.getMessage()));
                } catch(Exception e){
                    e.printStackTrace();
                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,e.getMessage()));
                }

            }
        }.start();
    }

    public void cancelDownload()
    {
        canceled = true;
    }

    Handler updateHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE_CHECKCOMPLETED:

                    callback.checkUpdateCompleted(hasNewVersion, newVersion);
                    break;
                case UPDATE_DOWNLOADING:

                    callback.downloadProgressChanged(progress);
                    break;
                case UPDATE_DOWNLOAD_ERROR:

                    callback.downloadCompleted(false, msg.obj.toString());
                    break;
                case UPDATE_DOWNLOAD_COMPLETED:

                    callback.downloadCompleted(true, "");
                    break;
                case UPDATE_DOWNLOAD_CANCELED:

                    callback.downloadCanceled();
                default:
                    break;
            }
        }
    };

    public interface UpdateCallback {
        public void checkUpdateCompleted(Boolean hasUpdate,
                                         CharSequence updateInfo);

        public void downloadProgressChanged(int progress);
        public void downloadCanceled();
        public void downloadCompleted(Boolean sucess, CharSequence errorMsg);
    }
    private class Update{
        private String createTime;
        private String updateUrl;

        public String getCreatTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateUrl() {
            return updateUrl;
        }

        public void setUpdateUrl(String updateUrl) {
            this.updateUrl = updateUrl;
        }
    }

}
