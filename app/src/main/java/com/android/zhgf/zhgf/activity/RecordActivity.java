package com.android.zhgf.zhgf.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.tool.FastBlur;
import com.android.zhgf.zhgf.util.MediaPlayUtil;
import com.android.zhgf.zhgf.util.StringUtil;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView button1,button2,bkg;
    TextView recordText,recordTime;
    int time=0;
    private static final int THREADCOMMUNITY=123;
    private static final int THREADCOMMUNITY2=124;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        bkg=(ImageView)findViewById(R.id.backg) ;
        button1 =(ImageView) findViewById(R.id.record_start_a);
        button2=(ImageView) findViewById(R.id.record_stop_a);
        recordText=(TextView)findViewById(R.id.record_text);
        recordTime= (TextView) findViewById(R.id.record_time);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.header1);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmap,
                mBitmap.getWidth() / 5,
                mBitmap.getHeight() / 5,
                false);
        Bitmap blurBitmap = FastBlur.doBlur(scaledBitmap, 10, true);

        bkg.setImageBitmap(blurBitmap);
        Toast.makeText(RecordActivity.this,"点击小圆点开始录制",Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = 26)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_start_a:
              mSoundData=  getIntent().getStringExtra("msounddata");
                soundPath=getIntent().getStringExtra("sound");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (ContextCompat.checkSelfPermission(RecordActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(RecordActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            record();
                        }
                    }
                }).start();
                //recursive();
                button1.setEnabled(false);
                button1.setVisibility(View.INVISIBLE);
                button2.setVisibility(View.VISIBLE);
                recordText.setText("停止");
                Toast.makeText(RecordActivity.this,"开始录制请汇报情报",Toast.LENGTH_SHORT).show();
                break;
            case R.id.record_stop_a:
                stop();
                Toast.makeText(RecordActivity.this,"录制结束",Toast.LENGTH_SHORT).show();
               finish();
                break;
            default:break;
        }
    }
    MediaRecorder mRecorder;
    String mSoundData;
    String soundPath;
    File sound;
    @RequiresApi(api = 26)
    public void record() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setOutputFile(mSoundData);//这里必须传入String对象
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        sound=new File(soundPath);
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
        recursive();
}
    public void recursive(){
      new Thread(new Runnable() {
          @Override
          public void run() {
              time++;

              try {
                  Thread.sleep(1000);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
              Message message = Message.obtain();
              message.what = THREADCOMMUNITY;
              message.obj = time;
              handler.sendMessage(message);
              this.run();
      }
            }).start();
    }



    public void stop() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        time=0;
    }
    public void play() {
        MediaPlayUtil mMediaPlayUtil = MediaPlayUtil.getInstance();
        try {
            String mVoiceData = StringUtil.encodeBase64File(mSoundData);
            mMediaPlayUtil.play(StringUtil.decoderBase64File(mVoiceData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        if(msg.what==THREADCOMMUNITY){
                 recordTime.setText((int)msg.obj+"");
        }
        if(msg.what==THREADCOMMUNITY2){

        }
    }
};
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Thread(new Runnable() {
                        @RequiresApi(api = 26)
                        @Override
                        public void run() {
                            record();
                        }
                    }).start();
                } else {
                    Toast.makeText(this, "请同意权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
