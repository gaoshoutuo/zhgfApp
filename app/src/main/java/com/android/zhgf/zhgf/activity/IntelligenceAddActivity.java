package com.android.zhgf.zhgf.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zhgf.zhgf.JavaBean.IntelligenceInfo;
import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.bean.IntelligenceHistoryBean;
import com.android.zhgf.zhgf.database.bean.NotificationBean;
import com.android.zhgf.zhgf.util.MediaPlayUtil;
import com.android.zhgf.zhgf.util.StringUtil;
import com.android.zhgf.zhgf.util.UriUtils;
import com.android.zhgf.zhgf.wnd.global.Configure;
import com.android.zhgf.zhgf.wnd.iface.INetwork;
import com.android.zhgf.zhgf.wnd.iface.impl.User;
import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;
import com.android.zhgf.zhgf.wnd.utils.RandomFile;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IntelligenceAddActivity extends BaseActivity implements View.OnClickListener {
    private List<IntelligenceInfo> list = new ArrayList<>();
    private ImageView intelligencePicture;
    private ImageView wavFile,videoFile;
    private ImageView intelligenceVideo;
    private BottomMenu bottomMenu;

    private List<ImageView> imageViews;
    private boolean isvideo = false, isaudio = false;

    private EditText et;
    private Button sendButton;
    private TextView location;
   private  String mSoundData = "/sdcard/tuo/sound/" + new RandomFile().GetDirByDate()+"_"+System.currentTimeMillis()+".amr";//声音文件
   private File  sound = new File(mSoundData);
   private String ftpWorking="";
    String userPhone;
private Random random1=new Random();
    private Random random2=new Random();

    public View getMyView(int layoutId, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return view;
    }

    private void setImageViews() {
        imageViews = new ArrayList<>();
        imageViews.add(0, (ImageView) findViewById(R.id.intelligence_picture1));
        imageViews.add(1, (ImageView) findViewById(R.id.intelligence_picture2));
        imageViews.add(2, (ImageView) findViewById(R.id.intelligence_picture3));
        imageViews.add(3, (ImageView) findViewById(R.id.intelligence_picture4));
        imageViews.add(4, (ImageView) findViewById(R.id.intelligence_picture5));
        imageViews.add(5, (ImageView) findViewById(R.id.intelligence_picture6));
        imageViews.add(6, (ImageView) findViewById(R.id.intelligence_picture7));
        imageViews.add(7, (ImageView) findViewById(R.id.intelligence_picture8));
        imageViews.add(8, (ImageView) findViewById(R.id.intelligence_picture9));
    }

    public List<ImageView> getImageViews() {
        return imageViews;
    }

    private void initView() {
        intelligencePicture = findViewById(R.id.intelligence_picture);
        intelligencePicture.setOnClickListener(this);
        intelligenceVideo = findViewById(R.id.intelligence_video);
        intelligenceVideo.setOnClickListener(this);
        videoFile=findViewById(R.id.intelligence_video_file);
        wavFile=findViewById(R.id.intelligence_wav_file);
        videoFile.setOnClickListener(this);
        wavFile.setOnClickListener(this);
        setIvRight(R.drawable.icons8_playlist48);
        ivRight.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        setImageViews();
        et = findViewById(R.id.intelligence_text);
        sendButton = findViewById(R.id.send_intelligence);
        sendButton.setOnClickListener(this);
        location = findViewById(R.id.intell_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //继承util里的
               /* GPSService g= new GPSService((GlobalHandler) getApplication());
                location.setText("纬度"+g.getLatitude()+"\n"+"经度"+g.getLongitude());*/
                new AppApplication().getHandler();
            }
        });
        for(ImageView imageView:imageViews){
          imageView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

                  ((ImageView)view).setBackgroundResource(R.drawable.small_image_holder_listpage);
                  new AlertDialog.Builder(IntelligenceAddActivity.this).setTitle("确认删除本张照片吗？")
                          .setIcon(android.R.drawable.ic_dialog_info)
                          .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  // 点击“确认”后的操作
                                  //((ImageView)view).setBackgroundResource(R.drawable.small_image_holder_listpage);

                                  ((ImageView)view).setImageDrawable(null);
                                  switch (view.getId()){
                                      case R.id.intelligence_picture1:
                                          new File("/sdcard/tuo/camera/1.jpg").delete();
                                          break;
                                      case R.id.intelligence_picture2:
                                          new File("/sdcard/tuo/camera/2.jpg").delete();
                                          break;
                                      case R.id.intelligence_picture3:
                                          new File("/sdcard/tuo/camera/3.jpg").delete();
                                          break;
                                      case R.id.intelligence_picture4:
                                          new File("/sdcard/tuo/camera/4.jpg").delete();
                                          break;
                                      case R.id.intelligence_picture5:
                                          new File("/sdcard/tuo/camera/5.jpg").delete();
                                          break;
                                      case R.id.intelligence_picture6:
                                          new File("/sdcard/tuo/camera/6.jpg").delete();
                                          break;
                                      case R.id.intelligence_picture7:
                                          new File("/sdcard/tuo/camera/7.jpg").delete();
                                          break;
                                      case R.id.intelligence_picture8:
                                          new File("/sdcard/tuo/camera/8.jpg").delete();
                                          break;
                                      case R.id.intelligence_picture9:
                                          new File("/sdcard/tuo/camera/9.jpg").delete();
                                          break;
                                      default:break;
                                  }
                              }
                          })
                          .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  // 点击“返回”后的操作,这里不设置没有任何操作
                              }
                          }).show();
              }
          });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intelligence_add);
        setTitle("情报报知");
        // setToolbarBarBackground(R.color.intelligence);
        initView();
        LinearLayoutManager lm = new LinearLayoutManager(this);
     /*   rView.setLayoutManager(lm);
        IntelligenceAdapter lAdapter=new IntelligenceAdapter(list);
        rView.setAdapter(lAdapter);*/
        fileDelete("/sdcard/tuo");
        String LatitudeStr=  Double.toString(((AppApplication)getApplication()).getLatitude());
        String LongitudeStr =  Double.toString(((AppApplication)getApplication()).getLongitude());
        location.setText("经度:" + LongitudeStr + " 纬度:" + LatitudeStr);
        new Thread(new Runnable() {
            @Override
            public void run() {
              //  ftpInit("192.168.10.103","21","kexue","123456");

                String ftpIp=((AppApplication) getApplication()).getConfigures().getFTPServer();
                String ftpuser=((AppApplication) getApplication()).getConfigures().getFTPServicesUser();
                String ftipass=((AppApplication) getApplication()).getConfigures().getFTPServicesPass();
                Log.e("ftpIp",ftpuser+ftipass);
                ftpInit(ftpIp,"21",ftpuser,ftipass);
               // ftpInit("112.11.127.22","5165","txzg_qbbz","jX96871@qq.com");
                userPhone = (String)mUser.getCache(User.CACHE_KEY_USER_PHONE);
                try{
                    ftpClient.sendCommand("XMKD " + userPhone);
                    ftpClient.changeWorkingDirectory(userPhone);
                  ftpWorking= ftpChangeWorkPath();
                    Log.e("ftpwork",ftpWorking);
                    //  ftpClient.changeWorkingDirectory(userPhone);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 服务器读取json
     */
    private void sendRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("xxx")
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONObeject(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * jsonObject 解析
     * json
     *
     * @param jsonData
     */
    private void parseJSONObeject(String jsonData) {
       /* try{
            JSONArray ja=new JSONArray();
            for(int i=0;i<ja.length();i++){
                JSONObject jb=ja.getJSONObject(i);
                IntelligenceInfo info=new IntelligenceInfo(jb.getInt("id"),jb.getString("name"));
                list.add(info);09
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @TargetApi(26)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu3_pic1:
                    Toast.makeText(IntelligenceAddActivity.this, "拍摄照片", Toast.LENGTH_SHORT).show();
                    try {
                        takePhoto(v.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.menu3_pic2:
                    Toast.makeText(IntelligenceAddActivity.this, "本地照片", Toast.LENGTH_SHORT).show();
                    try {
                        takePhoto(v.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.menu4_pic1:
                    try {
                        openAudio();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(IntelligenceAddActivity.this, "录制音频", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.menu4_pic2:
                    openAudioFile();
                    Toast.makeText(IntelligenceAddActivity.this, "本地音频", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.menu4_pic3:
                    try {
                        openVideo();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(IntelligenceAddActivity.this, "拍摄视频", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.menu4_pic4:
                    openVideoFile();
                    Toast.makeText(IntelligenceAddActivity.this, "本地视频", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.record_start:

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            record();
                        }
                    }).start();

                    Toast.makeText(IntelligenceAddActivity.this, "开始录制，请汇报情况", Toast.LENGTH_SHORT).show();
                    bottomMenu.show();
                    break;
                case R.id.record_stop:
                   /* am.stopRecord();*/
                    stop();
                   // play();

                    Toast.makeText(IntelligenceAddActivity.this, "已经停止", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.record_delete_pop:
                    sound.delete();
                    wavFile.setVisibility(View.GONE);
                    break;
                case R.id.record_start_pop:
                    File file=new File("/sdcard/tuo/sound");
                    File[] filelist= file.listFiles();
                    File file1=filelist[0];
                    Log.e("file1",file1.getPath());
                        play(file1.getPath());
                    //不管是amr 还是MP3都可以播放 了却一个心头大患
                    break;
                case R.id.record_stop_pop:
                    stopPlay();
                    break;

                case R.id.video_delete_pop:
                 video.delete();
                    videoFile.setVisibility(View.GONE );
                    break;
                case R.id.video_start_pop:
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(videoUri, "video/mp4");
                    startActivity(intent);
                    break;
                case R.id.video_stop_pop:

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.intelligence_picture:
                bottomMenu = new BottomMenu(R.id.intelligence_picture, IntelligenceAddActivity.this, clickListener);
                bottomMenu.show();
                break;
            case R.id.intelligence_video:
                bottomMenu = new BottomMenu(R.id.intelligence_video, IntelligenceAddActivity.this, clickListener);
                bottomMenu.show();
                break;
            case R.id.ivRight:
               String clientmb= new User(AppApplication.getApp()).getCache(User.CACHE_KEY_USER_PHONE)+"";
                String httpServer=((AppApplication) getApplication()).getConfigures().getQbls();
                        Map<String,Object> map=new HashMap<>();
                map.put("clientmb",clientmb);
                sendInterface(httpServer,map);
                Log.e("httpServer",httpServer+map);//http://20.150.180.188:81/appmobile/GetInformationList{clientmb=18857391232}
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intentHis= new Intent(IntelligenceAddActivity.this, Intelligence.class);
                intentHis.putExtra("jsonData", (Serializable) jsonList);
                Log.e("jsonobj",jsonList.get(0).getYpwj());
                startActivity(intentHis);
                //可能改成startAFResult   而且得加一句  或者加个进度条先滚然后再跳入 不管怎么样 先试试
                break;
            case R.id.ivBack:

                fileDelete("/sdcard/tuo");
                /*destory();*/
                finish();
                break;
            case R.id.intelligence_wav_file:
                bottomMenu = new BottomMenu(R.id.intelligence_wav_file, IntelligenceAddActivity.this, clickListener);
                bottomMenu.show();
            break;
            case R.id.intelligence_video_file:
                bottomMenu = new BottomMenu(R.id.intelligence_video_file, IntelligenceAddActivity.this, clickListener);
                bottomMenu.show();
                break;
            case R.id.send_intelligence:

                try {
                    sendButton.setEnabled(false);
                    sendButton.setBackgroundColor(Color.parseColor("#969696"));
               /* String status= ftpClient.getStatus();
                    Log.d("吃饭",status);
                    if(status!="200"){
                    Toast.makeText(IntelligenceAddActivity.this, "异常退出，请检查VPN网络状态", Toast.LENGTH_LONG).show();
                    finish();
                }*/
                        //提交  把文字图片音频 视频 上传服务器 并且生成Json
                        getEditText();
                    // sendToServer();
                    ProgressDialog pd=new ProgressDialog(IntelligenceAddActivity.this);
                    pd.setTitle("进度条");
                    pd.setMessage("请等待");
                    pd.setCancelable(true);
                    pd.show();
                    fileUp("/sdcard/tuo");
                    //检测faildMap里面是否有
                        makeJsonObject(mapJson);
                        saveJson("/sdcard/tuo/file.json",json);
                       // sendButton.setEnabled(false);
                     Thread tr=  new Thread(new Runnable() {
                           @Override
                           public void run() {
                               try {
                                 /*  DateFormat df3 = DateFormat.getTimeInstance();//只显示出时分秒
                                   Log.e("client",df3.format(new Date()));*/

                                   //ftpClient.sendCommand("XCMD"+"user");
                                //   ftpClient.changeWorkingDirectory("/user/date");//"/"+"用户手机号"+"用户上传时间/"
                                   ftpClient.storeFile("file.json", new FileInputStream(new File("/sdcard/tuo/file.json")));
                                   ftpClient.printWorkingDirectory();//目录就是根目录
                                   Log.e("client",ftpClient.printWorkingDirectory().toString());
                                   /*ftpclient.sendServer("XMKD /test/bb\r\n"); //执行服务器上的FTP命令
                                   ftpclient.readServerResponse一定要在sendServer后调用*/
                                   /*
                                    10-26 10:02:24.871 17461-17783/com.android.zhgf.zhgf E/client: 10:02:24
                                    10-26 10:02:24.936 17461-17783/com.android.zhgf.zhgf E/client: /user/date
                                    */
                               }
                               catch (IOException e){
                                   e.printStackTrace();
                               }
                           }
                       });
                       tr.start();
                        tr.join();
                        Toast.makeText(IntelligenceAddActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                        stopFtpClient();
                    String httpServer2=((AppApplication) getApplication()).getConfigures().GetLOGINURL("FeedbackSubmit");
                    sendInterface(httpServer2,interJson);
                 //   Thread.currentThread().sleep(2000);
                     pd.dismiss();
                    /*pd.cancel();*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                 catch (Exception e) {
                    e.printStackTrace();
                }
                /*rtmp://112.11.127.18:110/live/*/

            default:
                break;
        }
    }
    /**
     * 拍摄照片 存储照片  传输流
     */
    private Uri imageUri, soundUri, videoUri;
    public static final int MEDIA_PHOTO = 1;
    public static final int MEDIA_ALBUM = 2;
    public static final int MEDIA_RECORD = 3;
    public static final int MEDIA_SOUND = 4;
    public static final int MEDIA_SCREEN = 5;
    public static final int MEDIA_VIDEO = 6;

    public void takePhoto(int vId) throws IOException {
        //拍摄照片
        if (vId == R.id.menu3_pic1) {
            File photo = new File(getExternalCacheDir(), "output_image.jpg");

            if (photo.exists()) {
                photo.delete();
            }
            photo.createNewFile();
            if (Build.VERSION.SDK_INT >= 24) {
                imageUri = FileProvider.getUriForFile(IntelligenceAddActivity.this, "com.android.zhgf.zhgf.fileprovider", photo);
            } else {
                imageUri = Uri.fromFile(photo);
            }
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, MEDIA_PHOTO);
        }
        //来自图库
        if (vId == R.id.menu3_pic2) {

            if (ContextCompat.checkSelfPermission(IntelligenceAddActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(IntelligenceAddActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                openAlbum();
            }
        }

    }

    //MediaStore.Video.Media
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, MEDIA_ALBUM);
    }

    private void openVideoFile() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("video/*");
        videoUri=Uri.fromFile(video);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        startActivityForResult(intent, MEDIA_VIDEO);
    }

    private void openAudioFile() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("audio/*");
        soundUri=Uri.fromFile(sound);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, soundUri);
        startActivityForResult(intent, MEDIA_SOUND);
    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {//显示图片
            case MEDIA_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        saveBitmap(bitmap);
                        for (int i = 0; i < 9; i++) {
                            ImageView iv = getImageViews().get(i);
                            Log.d("222", "222");
                           /* Bitmap test = ((BitmapDrawable) getImageViews().get(8).getDrawable()).getBitmap();
                            if (((BitmapDrawable) iv.getDrawable()).getBitmap() == test) {
                                iv.setImageBitmap(bitmap);
                                break;*/
                            if((iv.getDrawable())!=null){
                                continue;
                            }else {
                                iv.setImageBitmap(bitmap);
                                break;
                            }
                            }
                        }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case MEDIA_ALBUM:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageKitkat(data);
                    } else {
                        handleImageBeforekitkat(data);
                    }
                }
                break;

            case MEDIA_SCREEN:

                break;
            case MEDIA_VIDEO:
                if (resultCode == RESULT_OK) {
                    String videoPath = null;
                    videoUri = data.getData();
                    videoPath = UriUtils.getPath(this, videoUri);
                    threadSave(videoPath,video);
                    videoFile.setVisibility(View.INVISIBLE);
                }
                break;
            case MEDIA_RECORD:
               /* if(resultCode==RESULT_OK){
                if( data.getData()!=null) {
                    String recordPath = null;
                    soundUri = data.getData();
                }*/
                break;
            case MEDIA_SOUND:
                if (resultCode == RESULT_OK) {
                    String soundPath = null;
                    soundUri = data.getData();
                    String v_data=null;
                    soundPath = UriUtils.getPath(this, soundUri);
                  //  displaySound(soundPath);
                    threadSave(soundPath,new File("/sdcard/tuo/sound/" + new RandomFile().GetDirByDate()+"_"+System.currentTimeMillis()+".mp3"));
                    wavFile.setVisibility(View.VISIBLE);
                }
                break;
            case 20:
                if (resultCode == RESULT_OK) {
                  wavFile.setVisibility(View.VISIBLE);
                    videoFile.setVisibility(View.VISIBLE);
                    Log.e("是否执行到","这里");
                }
                break;
            default:
                break;
        }
    }
    void threadSave(final String path, final File isWhat){
        Toast.makeText(IntelligenceAddActivity.this,"正在装填文件请稍后",Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File f_sound=new File(path);
                    int xValue=0;
                    FileInputStream fis=new FileInputStream(f_sound);
                   /* BufferedReader br=   new BufferedReader(new InputStreamReader(fis)) ;*/
                    if(!isWhat.exists()){
                        isWhat.getParentFile().mkdirs();
                        isWhat.createNewFile();
                    }else {
                        isWhat.delete();
                        isWhat.createNewFile();
                    }
                    byte []by=new byte[1024];

                    FileOutputStream fos=new FileOutputStream(isWhat);
                   /* while((xValue=br.readLine())!=null){
                        byte[] bytes=xValue.getBytes("ISO-8859-1");
                        fos.write(bytes);
                    }*/
                    while((xValue=fis.read(by))!=-1){
                        /*byte[] bytes=xValue.getBytes("ISO-8859-1");*/
                        fos.write(by,0,xValue);
                    }
                    fos.close();
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    //Toast.makeText(IntelligenceAddActivity.this,"装填成功",Toast.LENGTH_LONG).show();
                }

            }
        }).start();
        Toast.makeText(IntelligenceAddActivity.this,"装填结束",Toast.LENGTH_SHORT);
       // Thread.currentThread().interrupt();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "请同意权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }


        if (imagePath != null) {
            try {
                displayImage(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "空路径", Toast.LENGTH_LONG).show();
        }
    }

    private void handleImageBeforekitkat(Intent data) {
        Uri uri = data.getData();
        getImagePath(uri, null);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        Log.d("ff", path);
        return path;

    }

    private void displayImage(String imagePath) throws IOException {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            saveBitmap(bitmap);
            for (int p = 0; p < 9; p++) {
                ImageView iv = getImageViews().get(p);
              /*
                Bitmap test = ((BitmapDrawable) getImageViews().get(8).getDrawable()).getBitmap();
               if (((BitmapDrawable) iv.getDrawable()).getBitmap() == test) {
                    iv.setImageBitmap(bitmap);
                    break;
                }*/

                if((iv.getDrawable())!=null){
                    continue;
                }else {
                    iv.setImageBitmap(bitmap);
                    break;
                }
            }
        }
    }



    public static String saveBitmap(Bitmap mBitmap) {
        File filePic;
        try {

            filePic = new File("/sdcard/tuo/camera/" +new RandomFile().GetDirByDate()+"_"+System.currentTimeMillis()+".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            } else {
                filePic.delete();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(sound.exists()){
            wavFile.setVisibility(View.VISIBLE);
        }
        if (video.exists()){
            videoFile.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 拍摄视频 传输视频 传输流
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void openAudio() throws IOException {
        // Intent intent =new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
     /*   try {
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            intent.setType("audio*//*");
            if (!isaudio) {
                createSoundFile();
                isaudio = true;
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, soundUri);
            startActivityForResult(intent, MEDIA_RECORD);
        } catch (Exception e) {
            bottomMenu = new BottomMenu(7156, IntelligenceAddActivity.this, clickListener);
            bottomMenu.show();
            e.printStackTrace();
        }*/
        Intent intent = new Intent(IntelligenceAddActivity.this,RecordActivity.class);
        intent.putExtra("msounddata",mSoundData);
        intent.putExtra("sound",sound.getPath());
        startActivityForResult(intent,20);
    }

    private void openVideo() throws IOException {
        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        if (!isvideo) {
            createMediaFile();
            isvideo = true;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, MEDIA_SCREEN);
    }

    File video = new File("/sdcard/tuo/video/" + new RandomFile().GetDirByDate()+"_"+System.currentTimeMillis()+".mp4");//视频文件

    private void createMediaFile() throws IOException {

        if (!video.exists()) {
            video.getParentFile().mkdirs();
            video.createNewFile();
        } else {
            video.delete();
            video.createNewFile();
        }
        videoUri = Uri.fromFile(video);
    }



    private void createSoundFile() throws IOException {

        if (!sound.exists()) {
            sound.getParentFile().mkdirs();
            sound.createNewFile();
        } else {
            sound.delete();
            sound.createNewFile();
        }
        soundUri = Uri.fromFile(sound);
    }

    private void displayVideo(String videoPath) {
        Uri uri = Uri.parse("file://" + videoPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "video/*");
        startActivity(intent);
    }

    private void displaySound(String soundPath) {

        Uri uri = Uri.parse("file://" + soundPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/*");
        startActivity(intent);
    }

    void fileDelete(String path) {

        File dir = new File(path);
        if (dir.exists()) {
            File[] tmp = dir.listFiles();
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].isDirectory()) {
                    fileDelete(path + "/" + tmp[i].getName());
                } else {
                    tmp[i].delete();
                }
            }
            dir.delete();
        }
    }
    /*List <String>faildList=new ArrayList<>();*/
    Map <String,String>faildMap=new HashMap<>();
   File timeFile=null;
    FTPFile[] timeFile_f=null;
   private String filePath=null;
  private String fileName=null;
    String xyn=null;
    String ppath;
  private Map<String,Object>mapJson=new HashMap<>();
    private Map<String,Object>interJson=new HashMap<>();
    long x1,x2;
    int ij=0;

/**
*
*          ┌─┐       ┌─┐
*       ┌──┘ ┴───────┘ ┴──┐
*       │                 │
*       │       ───       │
*       │  ─┬┘       └┬─  │
*       │                 │
*       │       ─┴─       │
*       │                 │
*       └───┐         ┌───┘
*           │         │
*           │         │
*           │         │
*           │         └──────────────┐
*           │                        │
*           │                        ├─┐
*           │                        ┌─┘
*           │                        │
*           └─┐  ┐  ┌───────┬──┐  ┌──┘
*             │ ─┤ ─┤       │ ─┤ ─┤
*             └──┴──┘       └──┴──┘
*                 神兽保佑
*                 代码无BUG!
 *                不要改我的递归你会后悔的
*/
    void fileUp(String path)throws Exception{
        File dir = new File(path);
        if (dir.exists()) {
            File[] tmp = dir.listFiles();
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].isDirectory()) {
                    filePath=path + "/" + tmp[i].getName();
                    fileUp(filePath);
                } else {
                    fileName= tmp[i].getName();
                   Thread aThread= new Thread(new Runnable() {
                        @Override
                            public void run () {
                            timeFile=new File(filePath+"/"+fileName);
                             x1= timeFile.length();//本地file大小  难道不应该是size吗
                           //ftpUpload("192.168.10.117","21","zth","1",filePath,fileName);
                            Log.e("fileNameIn",fileName);
                               ftpUpToServer(filePath,fileName);
                            try {
                              xyn= ftpClient.printWorkingDirectory();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                           // interJson.put("climb","");
                           // if(new User(AppApplication.getApp()).getCache(User.CACHE_KEY_USER_PHONE)!=null)
                            Log.e("axx",new User(AppApplication.getApp()).getCache(User.CACHE_KEY_USER_PHONE)+"");
                            interJson.put("clientmb",new User(AppApplication.getApp()).getCache(User.CACHE_KEY_USER_PHONE)+"");
                                interJson.put("gps_lat",User.GPS_LAT);
                                interJson.put("gps_lon",User.GPS_LON);
                            if(fileName.substring(fileName.lastIndexOf("."),fileName.length()).equals(".txt")) {
                                interJson.put("content", et.getText().toString());
                                Log.e("执行结果",1 +fileName);
                            }
                            if (fileName.substring(fileName.lastIndexOf("."),fileName.length()).equals(".jpg")) {
                                if(ij==0){
                                    ppath=fileName;
                                    ij=1;
                                }
                                else{
                                    ppath = ppath + "|" + fileName;
                                }

                                Log.e("执行结果",2 +fileName);
                            }
                            if(fileName.substring(fileName.lastIndexOf("."),fileName.length()).equals(".amr")||
                                    fileName.substring(fileName.lastIndexOf("."),fileName.length()).equals(".mp3")){
                                interJson.put("audio",fileName);
                                Log.e("执行结果",3 + fileName);
                            }

                           if (fileName.substring(fileName.lastIndexOf("."),fileName.length()).equals(".mp4")){
                               interJson.put("video",fileName);
                               Log.e("执行结果",4 + fileName);
                           }

                            interJson.put("photos",ppath);
                                interJson.put("filesize", length);

                            Log.e("hashmap",interJson.toString());
                            mapJson.put(fileName, xyn + "/" + fileName);
                            Log.d("测试路径",xyn+"/"+fileName);
                            try {
                                timeFile_f = ftpClient.listFiles(fileName);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                          /*  x2 = timeFile_f[0].getSize();//服务器file大小
                                if (x1 == x2) {
                                    Log.e("成功!!", fileName);
                                } else {
                                    faildMap.put(fileName, filePath);
                                    Toast.makeText(IntelligenceAddActivity.this, "数据传输部分失败，重检文件大小，等待重传...稍候", Toast.LENGTH_SHORT).show();
                                    threadR.start();
                                }*/
                            //ftp连接在此关闭
                        //结构就是先解析json出来的map 就是这样
                        }

                    });
                    aThread.start();
                    try {
                        aThread.join();
                        Toast.makeText(IntelligenceAddActivity.this,"正在上传请稍后",Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //断点续传还得 再看一下
    JSONObject json=null;
    void makeJsonObject(Map <String,Object>map) throws JSONException {
        json=new JSONObject();//D:\ftp
        json.put("username",map);

    }
    void saveJson(String path,JSONObject json){
        try{
            FileOutputStream fos=new FileOutputStream(new File(path));
            PrintStream ps=new PrintStream(fos);
            ps.println(json.toString());
            ps.close();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    Runnable r1=new Runnable() {
        @Override
        public void run() {
              for(String fileN:faildMap.keySet()){
                 String fileP= faildMap.get(fileN);
                  try {
                      reFtp(x2,fileP+"/"+fileN,fileN);
                      while(x2!=x1){
                        x2=timeFile_f[0].getSize();
                         //第二次再断不管了
                      }
                     faildMap.remove(fileN);

                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
        }
    };

    Thread threadR=new Thread(r1);
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    MediaRecorder mRecorder;
    @RequiresApi(api = 26)
    public void record() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setOutputFile(mSoundData);//这里必须传入String对象
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            if (!sound.exists()) {
                sound.getParentFile().mkdirs();
                sound.createNewFile();
            } else {
                sound.delete();
                sound.createNewFile();
            }
            mRecorder.prepare();
        } catch (IOException e) {
            Log.i("recoder", "prepare() failed-Exception-" + e.toString());
        }
        mRecorder.start();
    }
    public void stop() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
    public void play(String mSoundData) {
        MediaPlayUtil mMediaPlayUtil = MediaPlayUtil.getInstance();
        try {
            String mVoiceData = StringUtil.encodeBase64File(mSoundData);
            mMediaPlayUtil.play(StringUtil.decoderBase64File(mVoiceData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stopPlay(){
        MediaPlayUtil mMediaPlayUtil = MediaPlayUtil.getInstance();
        try {
            mMediaPlayUtil.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("确认退出吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        IntelligenceAddActivity.this.finish();
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();
    }

    File textPath = new File("/sdcard/tuo/text/" + 7156 + ".txt");

    public void getEditText() {
        String text = et.getText().toString();
        try {
            if (!textPath.exists()) {
                textPath.getParentFile().mkdirs();
                textPath.createNewFile();
            } else {
                textPath.delete();
                textPath.createNewFile();
            }
            byte bytes[] = new byte[512];
            bytes = text.getBytes();
            int b = bytes.length;   //是字节的长度，不是字符串的长度
            FileOutputStream fos = new FileOutputStream(textPath);//file 与String真坑
            fos.write(bytes, 0, b);
            //fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("text/plain");//application/octet-stream
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    public void sendToServer() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File("/sdcard/tuo/text/" + 7156 + ".txt");
                //所以你看这边就是哦new一个指定路径的file亏我还震天在思考path路径怎么转换成File文件 非new File情况
                Request request = new Request.Builder()
                        .url("http://192.168.10.117:81")
                        .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.e("response", response.body().string() + 11 + "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        /**
         *  {"code":"200","message":"success","value":{"name":"uxip.meizu.com","targets":[{"ip":"183.232.2.144"}],"baks":[{"ip":"183.61.122.70"},{"ip":"221.5.35.70"},{"ip":"183.232.2.144"},{"ip":"14.152.79.145"},{"ip":"221.5.93.15"},{"ip":"183.240.46.145"}],"expire":120}}
         10-11 15:41:45.304 31778-32535/? W/MzGslbSdk: handle request response code:200
         */
    }


    FTPClient ftpClient;
    String directoryFile=null;
    public  String ftpUpload(String url, String port, String username,String password, String fileNamePath,String fileName){
        ftpClient = new FTPClient();
        FileInputStream fis = null;
        String returnMessage = "0";
        try {
            ftpClient.connect(url, Integer.parseInt(port));
            boolean loginResult = ftpClient.login(username, password);
            Log.e("loginResult",loginResult+"");
            int returnCode = ftpClient.getReplyCode();
            if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                fis = new FileInputStream(fileNamePath + "/"+fileName);
            /*    ftpClient.sendCommand("XMKD " + "user");//必须是英文目录
                ftpClient.makeDirectory("/user/date");   //两句在一起时成功的 单句*/
                DateFormat df3=DateFormat.getTimeInstance();
                directoryFile=df3.format(new Date());
                ftpClient.makeDirectory(directoryFile);
                ftpClient.changeWorkingDirectory(directoryFile);
                //这里弄复杂了 耦合度太盖了 应该把ftpClient的初始化与文件上传工作文件夹上传分开的就像王垠说的那样
                ftpClient.storeFile(fileName, fis);
               /* timeFile_f= ftpClient.listFiles(fileName);
                if(timeFile_f[0].getSize()==0) {
                }else if(){
                    return null;
                } 想想有点不靠谱计划冻结
                returnMessage = "1";   //上传成功*/
            } else {// 如果登录失败
                returnMessage = "0";
            }
        } catch (IOException e) {
            e.printStackTrace();
//  throw new RuntimeException("FTP客户端出错！", e);
            returnMessage = "-1";
        } finally {
            //IOUtils.closeQuietly(fis); 本来连接在这里关闭 但是呢
        }
        return returnMessage;
    }

    void ftpInit(String url, String port, String username,String password){
        ftpClient=new FTPClient();
        try {
         ftpClient.connect(url,Integer.parseInt(port));
            boolean request=ftpClient.login(username,password);
            Log.e("requestftp",request+"");
           int x= ftpClient.getReplyCode();
            if(request&&FTPReply.isPositiveCompletion(x)){
                Log.e("登陆成功","登录成功");
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    User mUser=new User(AppApplication.getApp());
    String ftpChangeWorkPath(){
        String lagalfile=null;
        try {
           /* DateFormat df3=DateFormat.getTimeInstance();
            directoryFile=df3.format(new Date());*/
            Date date=new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            lagalfile= sdf.format(date);
            Log.e("lagalfile",lagalfile);
            ftpClient.makeDirectory(lagalfile);
            ftpClient.changeWorkingDirectory(lagalfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "/"+userPhone+"/"+lagalfile;
    }
    long length=0;
    long ftpUpToServer(String mobileFilePath,String fileName){
        FileInputStream fiss=null;
        try{

            fiss=new FileInputStream( new File(mobileFilePath+"/"+fileName));
            ftpClient.storeFile(fileName,fiss);
         //   Log.e("workd",ftpClient.printWorkingDirectory());
            fiss.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        length=length+new File(mobileFilePath+"/"+fileName).length();
        return length/1000;
    }

   void reFtp(long x,String path,String fail_name)throws Exception{

  /*     FileInputStream fis=new FileInputStream(new File(path));
       fis.skip(x);
       Closeable rf=new RandomAccessFile(new File(path),"rw");
       (RandomAccessFile) rf.seek(x);
       ftpClient.storeFile(fail_name, (FileInputStream)rf);
       泛型后是父类对象 调用父类方法 虽然子类也调用了 无法调用seek
       */
       FileInputStream fis=new FileInputStream(new File(path));
       fis.skip(x);
       ftpClient.storeFile(fail_name, fis);
       fis.close();
    }
    void stopFtpClient(){
        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("关闭FTP连接发生异常！", e);
        }
    }


    private void sendInterface(String s,Map<String,Object> map){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //"http://20.150.180.188:81"+"/AppMobile/InformationManger"
                Log.e("某个事物", "submit:正在提交中");

                //(new NetworkUtil()).HttpSendPostData(httpServer, interJson, new INetwork.OnNetworkStatusListener() {
                (new NetworkUtil()).HttpSendPostData(s, map, new INetwork.OnNetworkStatusListener() {
                    @Override
                    public void onConnected(boolean isSuccess) {
                        Log.e("123321", "1");
                    }

                    @Override
                    public void onDisconnected(boolean isSuccess) {
                        Log.e("123321", "2");
                    }

                    @Override
                    public void onGetResult(boolean isSuccess, Object pData) {
                        Log.e("123321", "3");
                    }

                    @Override
                    public void onPostResult(boolean isSuccess, Object pData) {


                        NetworkUtil.HttpStatus mStatus = (NetworkUtil.HttpStatus)pData;
                        Log.e("appStatus", mStatus.getStatusCode()+"");
                        if (mStatus.getStatusCode()==200){
                            Log.e("某个app", "onPostResult: 提交成功!"+mStatus.getData());
                            //在这里因为安全考虑解析老朱给的json
                         if(map.get("photos")==null){
                             parseJson(mStatus.getData()+"");//这里不能toString 必须+“”
                         }

                        }else{
                            Log.e("某个事物", "onPostResult: 提交失败!" );
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMessage) {

                    }
                });
            }
        }).start();
    }

    /**
     * 不管点击右边 还是左边
     * 情报历史本来就是单独的  不需要
     * @param jsonData
     * @return
     */

    /**
     * [{"totalid":"ffbba7c7-32c3-4ac3-8f22-a23a7bc257fc","pid":"454e9e6a-e37c-4867-9ea7-47674b5b1be8","uploadtime":"2017-12-11T14:26:31.917","qbfl"
     * :"18857391232","wbnr":"报告首长，冷空气降临。梧桐民兵不忘坚守职责，巡逻卫桐。","zp":null,"ypwj":null,"spwj":null,"wjdx":0.07910156,"x":
     * "0.0","y":"0.0","isenabled":1,"CreateDate":"2017-12-11T14:26:31.92","CreateUserId":null,"CreateUserName":null,"ModifyDate":null,"ModifyUserId"
     * :null,"ModifyUserName":null},{"totalid":"0c2cb3b0-f142-459d-b0db-37e64772e77b","pid":"454e9e6a-e37c-4867-9ea7-47674b5b1be8","uploadtime":
     * "2017-12-07T17:14:56.347","qbfl":"18857391232","wbnr":"报告！！！","zp":null,"ypwj":null,"spwj":null,"wjdx":0.01464844,"x":"0.0","y":"0.0","
     * isenabled":1,"CreateDate":"2017-12-07T17:14:56.347","CreateUserId":null,"CreateUserName":null,"ModifyDate":null,"ModifyUserId":null,
     * "ModifyUserName":null},{"totalid":"01941028-7398-46cc-b5cb-a2995d26cf30","pid":"454e9e6a-e37c-4867-9ea7-47674b5b1be8","uploadtime":
     * "2017-12-07T16:43:30.013","qbfl":"18857391232","wbnr":"报告领导，地面一切正常","zp":"20171207_1512636145312.jpg","ypwj":"20171207_1512636108606.
     * amr","spwj":"20171207_1512636108611.mp4","wjdx":4967.32,"x":"0.0","y":"0.0","isenabled":1,"CreateDate":"2017-12-07T16:43:30.02","CreateUserId"
     * :null,"CreateUserName":null,"ModifyDate":null,"ModifyUserId":null,"ModifyUserName":null},{"totalid":"46f1548a-4cb3-47d6-b812-81d27b9de7d3",
     * "pid":"454e9e6a-e37c-4867-9ea7-47674b5b1be8","uploadtime":"2017-12-04T11:22:12.253","qbfl":"18857391232","wbnr":"吃个饭再巡逻","zp":
     * "20171204_1512357699950.jpg","ypwj":null,"spwj":null,"wjdx":63.29004,"x":"0.0","y":"0.0","isenabled":1,"CreateDate":"2017-12-04T11:22:12.273"
     * ,"CreateUserId":null,"CreateUserName":null,"ModifyDate":null,"ModifyUserId":null,"ModifyUserName":null}]
     json 是这么多 企业也不用解析json的 上传成功的时候吧 hashmap拿出来放到数据库做一条记录就ok了
     */
//由于json里面有许多null 会导致list有点乱套 所以必须在入口做好判断
    public static <T>T isObj(T obj){
        if(obj==null){
           throw new NullPointerException();
            //抛出直接终止 确实比return 美观一点
        }else {
            return obj;
        }
    }

    private ArrayList<IntelligenceHistoryBean>jsonList=new ArrayList<>();


    private ArrayList<IntelligenceHistoryBean> parseJson(String jsonData) {
/**
 * 我需要 内容 图片 音频 xy 视频 时间
 *
 */String head=""+((AppApplication) getApplication()).getConfigures().getM_sLoginGPSServer()+"/ftproot"+"/"+new User(AppApplication.getApp()).getCache(User.CACHE_KEY_USER_PHONE).toString();

        Log.e("jsonData",jsonData);
        try {
            JSONArray ja = new JSONArray(jsonData);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jb = ja.getJSONObject(i);
                //null值影响我的list的动态位置 由于null 都是本身对象 一堆null在list里面分不清谁应该在哪个位置
                String qbfl=jb.getString("qbfl");
            String wbnr=jb.getString("wbnr")==null?"":jb.getString("wbnr");
            String zp=jb.getString("zp")==null?"":jb.getString("zp");
                String updateTime=jb.getString("uploadtime").substring(0,10);
            String ypwj=jb.getString("ypwj")==null?"":jb.getString("ypwj");
            String spwj=jb.getString("spwj")==null?"":jb.getString("spwj");
                String x=jb.getString("x")==null?"":jb.getString("x");
                String y=jb.getString("y")==null?"":jb.getString("y");
                Log.e("test00000",wbnr+"%%%"+zp+"%%%"+updateTime+"%%%"+ypwj+spwj+x+y);
                String totalid=head;
                IntelligenceHistoryBean ihb=new IntelligenceHistoryBean(totalid,updateTime,qbfl,wbnr,zp,ypwj,spwj,x,y);
                jsonList.add(ihb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}