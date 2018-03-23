package com.android.zhgf.zhgf.util;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by colorful on 2017-10-23.
 * 工具思路 http请求获得输入流，通过输入流完成各种转换
 * 比如decodeBitmap的的位图，以及各种文件形式
 * 这就是读取url的最终目的
 */

public class ParseUrlUtil{


    public static InputStream HandlerData(String url) {
                     InputStream inStream = null;
                    try {
                           URL feedUrl = new URL(url);
                            URLConnection conn = feedUrl.openConnection();
                           conn.setConnectTimeout(10 * 1000);
                            inStream = conn.getInputStream();
                        } catch (Exception e) {
                            e.printStackTrace();
                    }
                    return inStream;
                }
    public static Drawable loadImageFromNetwork(String imageUrl) {
                Drawable drawable = null;
                 try {
                         // 可以在这里通过文件名来判断，是否本地有此图片
                        drawable = Drawable.createFromStream(
                                         new URL(imageUrl).openStream(), "image.jpg");
                    } catch (IOException e) {
                       Log.d("test", e.getMessage());
                    }
               if (drawable == null) {
                    Log.d("test", "null drawable");
                   } else {
                     Log.d("test", "not null drawable");
                 }

              return drawable;
           }
    }


