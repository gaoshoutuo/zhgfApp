package com.android.zhgf.zhgf.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.bean.NewsInfoEntity;
import com.android.zhgf.zhgf.util.DateUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.util.Locale;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TZXX001 on 2017/9/20.
 */

public class NewsInfoAdapter extends BaseAdapter {

    ArrayList<NewsInfoEntity> newsList;
    Activity activity;
    LayoutInflater inflater = null;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    //在消息队列中实现对控件的更改

    public NewsInfoAdapter(Activity activity, ArrayList<NewsInfoEntity> newsList) {
        this.activity = activity;
        this.newsList = newsList;
        inflater = LayoutInflater.from(activity);

    }
    @Override
    public int getCount() {
        return newsList == null ? 0 : newsList.size();
    }

    @Override
    public NewsInfoEntity getItem(int position) {
        if (newsList != null && newsList.size() != 0) {
            return newsList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        NewsInfoAdapter.ViewHolder mHolder = new NewsInfoAdapter.ViewHolder();;
        ViewHolder finalMHolder = mHolder;
        Handler handle = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        System.out.println("111");
                        Bitmap bmp=(Bitmap)msg.obj;
                        finalMHolder.right_image.setImageBitmap(bmp);
                        break;
                }
            };
        };
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.newslist_item, null);
            mHolder = new NewsInfoAdapter.ViewHolder();
            mHolder.newsInfo_layout = (LinearLayout)view.findViewById(R.id.newsInfo_layout);
            mHolder.right_image = (ImageView)view.findViewById(R.id.right_image);
            mHolder.item_title = (TextView)view.findViewById(R.id.item_title);
            mHolder.item_source = (TextView)view.findViewById(R.id.item_source);
            mHolder.list_item_local = (TextView)view.findViewById(R.id.list_item_local);//？
            mHolder.comment_count = (TextView)view.findViewById(R.id.comment_count);
            mHolder.publish_time = (TextView)view.findViewById(R.id.publish_time);
            mHolder.item_abstract = (TextView)view.findViewById(R.id.item_abstract);
            mHolder.item_image_layout = (LinearLayout)view.findViewById(R.id.item_image_layout);
            mHolder.item_image_0 = (ImageView)view.findViewById(R.id.item_image_0);
            mHolder.item_image_1 = (ImageView)view.findViewById(R.id.item_image_1);
            mHolder.item_image_2 = (ImageView)view.findViewById(R.id.item_image_2);
            mHolder.hits =  (TextView)view.findViewById(R.id.hits_tv);
            //mHolder.postTimeIv = (ImageView)view.findViewById(R.id.time_iv);
            view.setTag(mHolder);
        } else {
            mHolder = (NewsInfoAdapter.ViewHolder) view.getTag();
        }
        //获取position对应的数据
        NewsInfoEntity news = getItem(position);
        if(news == null){return view;}
        mHolder.item_title.setText(news.getTitle());
        mHolder.item_source.setText(news.getSource());
        int commentNum = 0;
        int hits = 0;
        try {
            commentNum = Integer.parseInt(news.getCommentNum());
            mHolder.comment_count.setText("评论 " + commentNum);
        } catch (NumberFormatException e) {
            mHolder.comment_count.setText("评论 0");
        }
        try {
            hits = Integer.parseInt(news.getHits());
            mHolder.hits.setText("" + hits);
        } catch (NumberFormatException e) {
            mHolder.hits.setText("0");
        }

        //mHolder.postTimeIv.setVisibility(View.VISIBLE);

        String timeDistance = DateUtil.getDistanceTime(Long.parseLong(news.getInputtime())*1000L, System.currentTimeMillis());
        mHolder.publish_time.setText(timeDistance);
        List<String> imgUrlList = news.getPicList();


        //mHolder.item_abstract.setVisibility(View.GONE);
        String url1 = "http://gyfpjh.com/uploads/image/201612/1480921212113664-lp.jpg";
        String url2 = "https://s9.rr.itc.cn/r/wapChange/20175_28_17/a0c54p8306958631745.jpg";
        String url3 = "http://img1.gtimg.com/news/pics/hv1/197/152/2205/143419082.jpg";
        //imgUrlList.clear();
        /*imgUrlList = new ArrayList<String>();
        if(position%2 == 0){
            imgUrlList.add(url1);
            imgUrlList.add(url2);
            imgUrlList.add(url3);
        }else {
            imgUrlList.add(url1);
        }*/

        if(imgUrlList !=null && imgUrlList.size() !=0){
            // 一张图
            if(imgUrlList.size() == 1){
                mHolder.item_image_layout.setVisibility(View.GONE);
                mHolder.right_image.setVisibility(View.VISIBLE);
                //mHolder.right_image.setImageResource(R.drawable.test);

                Glide.with(activity).load(imgUrlList.get(0)).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        //imageLoader.displayImage(imgUrlList.get(0), mHolder.right_image, options);


                       /* //新建线程加载图片信息，发送到消息队列中
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                //Bitmap bmp = getURLimage("http://20.150.180.188/uploadfile/2017/1108/20171108045107439.jpg");
                                Bitmap bitmap = null;
                                //try {
                                    //InputStream inputStream = getImageViewInputStream("http://20.150.180.188/uploadfile/2017/1108/20171108045107439.jpg");
                                    bitmap = downloadBitmap(imgUrlList.get(0));
                                    //finalMHolder.right_image.setImageBitmap(bitmap);
                               //} catch (IOException ioe) {

                               //}
                                *//*Message msg = new Message();
                                msg.what = 0;
                                msg.obj = bitmap;
                                handle.sendMessage(msg);*//*
                            }
                        }).start();*/

                        //Log.e("image",imgUrlList.get(0));
                        android.util.Log.d("GLIDE", String.format(Locale.ROOT,
                                "onException(%s, %s, %s, %s)", e, model, target, isFirstResource), e);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        //imageView.setImageDrawable(resource);

                        Log.e("image",imgUrlList.get(0));
                        return false;
                    }
                }).into(mHolder.right_image);
                //imageLoader.displayImage(imgUrlList.get(0), mHolder.right_image, options);

            }else{
                mHolder.item_image_layout.setVisibility(View.VISIBLE);
                mHolder.right_image.setVisibility(View.GONE);
                Glide.with(AppApplication.getApp()).load(imgUrlList.get(0)).into(mHolder.item_image_0);
                Glide.with(AppApplication.getApp()).load(imgUrlList.get(1)).into(mHolder.item_image_1);
                Glide.with(AppApplication.getApp()).load(imgUrlList.get(2)).into(mHolder.item_image_2);
                /*imageLoader.displayImage(imgUrlList.get(0), mHolder.item_image_0, options);
                imageLoader.displayImage(imgUrlList.get(1), mHolder.item_image_1, options);
                imageLoader.displayImage(imgUrlList.get(2), mHolder.item_image_2, options);*/
            }
        }else{
            mHolder.right_image.setVisibility(View.GONE);
            mHolder.item_image_layout.setVisibility(View.GONE);
        }

        return view;
    }

    static class ViewHolder {
        LinearLayout newsInfo_layout;
        //title
        TextView item_title;
        //图片源
        TextView item_source;
        //类似推广之类的标签
        TextView list_item_local;
        //评论数量
        TextView comment_count;
        //评论数量
        TextView hits;
        //发布时间
        TextView publish_time;
        //新闻摘要
        TextView item_abstract;
        //右上方TAG标记图片
        ImageView alt_mark;
        //右边图片
        ImageView right_image;
        //3张图片布局
        LinearLayout item_image_layout; //3张图片时候的布局
        ImageView item_image_0;
        ImageView item_image_1;
        ImageView item_image_2;
        ImageView postTimeIv;

    }

    //加载图片
    public Bitmap getURLimage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /**
     * 从网络中获取图片，以流的形式返回
     * @return
     */
    public static InputStream getImageViewInputStream(String URL_PATH) throws IOException {
        InputStream inputStream = null;
        URL url = new URL(URL_PATH);                    //服务器地址
        if (url != null) {
            //打开连接
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(3000);//设置网络连接超时的时间为3秒
            httpURLConnection.setRequestMethod("GET");        //设置请求方法为GET
            httpURLConnection.setDoInput(true);                //打开输入流
            int responseCode = httpURLConnection.getResponseCode();    // 获取服务器响应值
            if (responseCode == HttpURLConnection.HTTP_OK) {        //正常连接
                inputStream = httpURLConnection.getInputStream();        //获取输入流
            }
        }
        return inputStream;
    }
    public static Bitmap loadImageFromUrl(String url) {
        URL m;
        InputStream i = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream out =null;
        try {
            m = new URL(url);
            i = (InputStream) m.getContent();
            bis = new BufferedInputStream(i,1024 * 8);
            out = new ByteArrayOutputStream();
            int len=0;
            byte[] buffer = new byte[1024];
            while((len = bis.read(buffer)) != -1){
                out.write(buffer, 0, len);
            }
            out.close();
            bis.close();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        //Drawable d = Drawable.createFromStream(i, "src");
        return bitmap;
    }

    public static Bitmap loadimage(String url) throws IOException {
        HttpGet httpRequest = new HttpGet(url);
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
        HttpEntity entity = response.getEntity();
        BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
        InputStream is = bufferedHttpEntity.getContent();
        //Drawable d = Drawable.createFromStream(is, "");
//or bitmap
        Bitmap b = BitmapFactory.decodeStream(is);
        return b;
    }
    public Bitmap downloadBitmap(String url) {

        final HttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                /*if(DEBUG)Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + url);*/
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {

                    inputStream = entity.getContent();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4; // might try 8 also

                    Bitmap b = BitmapFactory.decodeStream(new FlushedInputStream(inputStream),null,options);
                    return b;

                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            getRequest.abort();
            //if(DEBUG)Log.w(TAG, "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {
            getRequest.abort();
            //if(DEBUG)Log.w(TAG, "Incorrect URL: " + url);
        } catch (Exception e) {
            getRequest.abort();
            //if(DEBUG)Log.w(TAG, "Error while retrieving bitmap from " + url, e);
        } finally {
            if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }
        }
        return null;
    }

    public Bitmap loadjpg(String url){
        // TODO Auto-generated method stub
        // 使用网络连接类HttpClient类王城对网络数据的提取
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        Bitmap bitmap = null;
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                byte[] data = EntityUtils.toByteArray(httpEntity);
                bitmap = BitmapFactory
                        .decodeByteArray(data, 0, data.length);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }
}
