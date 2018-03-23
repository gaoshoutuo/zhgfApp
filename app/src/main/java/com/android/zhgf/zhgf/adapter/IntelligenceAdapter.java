package com.android.zhgf.zhgf.adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zhgf.zhgf.JavaBean.IntelligenceInfo;
import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.activity.Intelligence;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.bean.IntelligenceHistoryBean;
import com.bumptech.glide.Glide;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;

import com.lzy.ninegrid.preview.NineGridViewClickAdapter;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

/**
 * Created by colorful on 2017-09-21.
 */

public class IntelligenceAdapter extends RecyclerView.Adapter<IntelligenceAdapter.IntelligenceViewHolder> implements View.OnClickListener{
    public ArrayList<IntelligenceHistoryBean>listInfo=new ArrayList<>();

    ArrayList <ArrayList<ImageInfo>>inmageList;
    IntelligenceViewHolder holder;
    File soundhttpFile,videoHttpFile,pictureFile;
    int positionq=0;
    public IntelligenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//通用的Adapter就是载入不同的view
      View view=  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_intelligence,parent,false);
        view.setOnClickListener(this);
       holder =new IntelligenceViewHolder(view);
        holder.gridView=view.findViewById(R.id.intelligence_picture);

     /*   IntelligenceHistoryBean info= listInfo.get(positionq);
        Log.e("info.getYpwj()",info.getYpwj()+"$$$$"+info.getSpwj());*/


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int posttion=holder.getAdapterPosition();
               // Log.e("position",posttion+""); 也不清楚为什么 position直接是2了 估计就错在position上
                String soundUrl=  listInfo.get(positionq).getTotalid()+"/"+listInfo.get(positionq).getUploadtime()+"/"+listInfo.get(positionq).getYpwj();
                Log.e("soundUrl",soundUrl+"");
                httpGetFile(positionq,view.getId(),soundUrl);
                Toast.makeText(AppApplication.getApp(),"正在下载音频",Toast.LENGTH_SHORT).show();
            }
        });


        holder.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   int posttion=holder.getAdapterPosition();
                Log.e("position",posttion+"");*/

                String videoUrl=  listInfo.get(positionq).getTotalid()+"/"+listInfo.get(positionq).getUploadtime()+"/"+listInfo.get(positionq).getSpwj();
                Log.e("videoUrl",videoUrl+"");
                httpGetFile(positionq,view.getId(),videoUrl);
                Toast.makeText(AppApplication.getApp(),"正在下载视频",Toast.LENGTH_SHORT).show();
            }
        });

        return holder;
    }

public void httpGetFile(int position,int viewID,String url){

    new Thread(new Runnable() {
        @Override
        public void run() {
            okhttp3.Request request=new okhttp3.Request.Builder().url(url).build();
            try {
                okhttp3.Response response=okHttpClient.newCall(request).execute();
                InputStream is= response.body().byteStream();
                if(url.indexOf("amr")!=0&&viewID==R.id.intelligence_sound){

                    soundhttpFile=new File("/sdcard/tuo2/sound/sound.amr");
                    createFile(soundhttpFile);
                    InputStreamToFile(is,soundhttpFile);
                }else if(url.indexOf("mp3")!=0&&viewID==R.id.intelligence_sound){
                    soundhttpFile=new File("/sdcard/tuo2/sound/sound.mp3");
                    createFile(soundhttpFile);
                    Log.e("mp3mp3mp3","mp3");
                    InputStreamToFile(is,soundhttpFile);
                }else if(url.indexOf("mp4")!=0&&viewID==R.id.intelligence_video){
                    videoHttpFile=new File("/sdcard/tuo2/video/video.mp4");
                    createFile(videoHttpFile);
                    Log.e("mp4mp4mp4","mp4");
                    InputStreamToFile(is,videoHttpFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }).start();
    }
    //remember
private void createFile(File file){
    try {
        if (!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }else {
            file.delete();
            file.createNewFile();
        }
    }catch (IOException e){e.printStackTrace();}
}

    public void InputStreamToFile(InputStream is,File file){
        try {

          OutputStream fos=  new FileOutputStream(file);
            int byteReader=0;
           byte []buffer=new byte[8192];
            while((byteReader=is.read(buffer,0,8192))!=-1){
                fos.write(buffer,0,byteReader);
            }

            fos.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onBindViewHolder(IntelligenceViewHolder holder, int position) {
        IntelligenceHistoryBean info= listInfo.get(position);
      //  info.getTextUrl();
      //  info.getVideoUrl();
       // info.getSoundUrl();
        //点击之后再开始解析
        if(positionq!=position){
            positionq=position;
        }

       // Double.toString(((AppApplication)getApplication()).getLongitude());
      //  Log.e("imageinfo",info.toString());

        holder.gridView.setAdapter(new NineGridViewClickAdapter(AppApplication.getApp(), inmageList.get(position)));
      if( !inmageList.get(position).get(0).getBigImageUrl().contains("jpg")) {
          holder.gridView.setVisibility(View.GONE);
      }

        if(info.getYpwj()==null){
            holder.relativeLayout1.setVisibility(View.GONE);
        }
        if(info.getSpwj()==null){
            holder.relativeLayout2.setVisibility(View.GONE);
        }
        Log.e("info.getYpwj()",info.getYpwj()+"$$$$"+info.getSpwj());
        Log.e("inmlist",inmageList.get(position).toString());
        holder.intText.setText("\u3000\u3000"+info.getWbnr());//是线程载入比较慢不是哪里出问题 线程要等待就好
      //  String LatitudeStr=Double.toString(AppApplication.getApp().getLatitude());//记住调用方法一定要在方法里 外面只能写字段
       // String LongitudeStr=Double.toString(AppApplication.getApp().getLongitude());
        String LatitudeStr=info.getX();
        String LongitudeStr=info.getY();
        holder.deleteLon.setText("经度:" + LongitudeStr + " 纬度:" + LatitudeStr);
        holder.createTime.setText(info.getUploadtime());
        Log.e("xiaoxiao","set");
    }
    Context context=null;

    public IntelligenceAdapter(ArrayList<IntelligenceHistoryBean>lists, Context context){
        this.context=context;
        this.listInfo=lists;
    }
    OkHttpClient okHttpClient=new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.SECONDS)
            .readTimeout(10000, TimeUnit.SECONDS)
            .writeTimeout(10000, TimeUnit.SECONDS)
            .build();
    @Override
    public int getItemCount() {
        return listInfo.size();
    }

    private OnItemClickListener mOnItemClickListener = null;
    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }
    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(view,(int)view.getTag());
        }
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    class IntelligenceViewHolder extends RecyclerView.ViewHolder{
        TextView intText,deleteLon,createTime;
       NineGridView gridView;
        ImageView sound,video;
        RelativeLayout relativeLayout1,relativeLayout2;
        View view;
        //Grid
        public IntelligenceViewHolder(View itemView){
            super(itemView);
            view=itemView;
            intText=itemView.findViewById(R.id.intell_text);
            deleteLon=itemView.findViewById(R.id.delete);
            createTime=itemView.findViewById(R.id.tv_createTime);
            sound=itemView.findViewById(R.id.intelligence_sound);
            video=itemView.findViewById(R.id.intelligence_video);

            NineGridView.setImageLoader(new Imageloader());
            relativeLayout1=itemView.findViewById(R.id.r_sound);
            relativeLayout2=itemView.findViewById(R.id.r_video);
          //根据不同的layoutId赋值不同
           // addToGridView();
            conversion();

         view.setTag(this);
        }
        private String getDate(){
            Date date=new Date();
            SimpleDateFormat sFormat=new SimpleDateFormat("yy-MM-dd HH:mm:ss");
           String currentDate= sFormat.format(date);
            return currentDate;
        }
    }
    void conversion(){
        inmageList=new ArrayList<>();
        for(int i=0;i<listInfo.size();i++){
            ArrayList<ImageInfo> imageInfo = new ArrayList<>();
           IntelligenceHistoryBean ihb= listInfo.get(i);
          String zp=  ihb.getZp();
            Log.e("zppath",zp);
           String[]zpArray= zp.split("\\|");//干你娘的要转义 害死我了
            String head=ihb.getTotalid();
            String uploadtime=ihb.getUploadtime();
            for (int j=0;j<zpArray.length;j++){
                ImageInfo info=new ImageInfo();
                    info.setThumbnailUrl(head+"/"+uploadtime+"/"+zpArray[j]);
                    info.setBigImageUrl(head+"/"+uploadtime+"/"+zpArray[j]);
                    Log.e("bigimage1",head+"/"+uploadtime+"/"+zpArray[j]);
                    imageInfo.add(info);
            }
            inmageList.add(imageInfo);
            //这里对容器开销不了解 去单元测试一下大规模的伪造数据的容器性能 写一个高阶的容器也要考虑怎么取出来的问题
        }
        //妈蛋果然不是我的问题 http请求的图片不能啊 即使加进去了 我本地的apache文件夹里都没关系


       /* tUrl.add("http://192.168.10.103:81/2.jpg");
        tUrl.add("http://192.168.10.103:81/3.jpg");
        tUrl.add("http://192.168.10.103:81/4.jpg");
        tUrl.add("http://192.168.10.103:81/5.jpg");
        tUrl.add("http://192.168.10.103:81/6.jpg");
        tUrl.add("http://192.168.10.103:81/7.jpg");
        tUrl.add("http://192.168.10.103:81/8.jpg");*/
    }
    //生成list已经包含bitmap
  /*  private void addUrl() {
        gpicList=new ArrayList<>();
        conversion();
        for(String url:tUrl){
           new ParseUrl().execute(url);
        }

    }*/
/*class pThread implements Runnable{
        String url;
        GridPicture gp;
        public pThread(String url,GridPicture gp) {
            super();
            this.url=url;
            this.gp=gp;
        }

        @Override
        public void run() {
            gp.setBitmap(BitmapFactory.decodeStream(ParseUrlUtil.HandlerData(gp.getUrl())));
        }
    }*/



    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.intelligence_sound:
                    //播放音频

                    break;
                case R.id.intelligence_video:
                    //播放视频
                    break;
            }
        }
    };
    private class Imageloader implements NineGridView.ImageLoader{

        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Glide.with(context).load(url)
                    .placeholder(R.drawable.ic_default_image)
                    .error(R.drawable.ic_default_image)
                    .into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }



     }
