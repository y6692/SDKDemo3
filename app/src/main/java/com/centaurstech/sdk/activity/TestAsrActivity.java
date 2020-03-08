package com.centaurstech.sdk.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;

import com.centaurstech.qiwu.common.ThreadPoolFactory;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ActivityAsrBinding;
import com.centaurstech.sdk.listener.OnAudioStatusUpdateListener;
import com.centaurstech.sdk.utils.AudioRecordManager;
import com.qiwu.ui.common.AppConfig;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Time:2020/1/3
 * Author: 樊德鹏
 * Description:
 */
public class TestAsrActivity extends BaseActivity {

    private ActivityAsrBinding mBinding;

    private int lenght;

    private String uuid = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_asr);
        initView();
    }

    private void initView() {
        AudioRecordManager.getInstance().setOnAudioStatusUpdateListener(mOnAudioStatusUpdateListener);
        mBinding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uuid = UUID.randomUUID().toString();
                AudioRecordManager.getInstance().startRecord();
            }
        });

        mBinding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioRecordManager.getInstance().stopRecord();
            }
        });
    }

    private OnAudioStatusUpdateListener mOnAudioStatusUpdateListener = new OnAudioStatusUpdateListener() {
        @Override
        public void onUpdate(byte[] bytes, long time) {

        }

        @Override
        public void onFinish(long time, String filePath) {
            String folderPath = filePath.replace(".pcm", ".wav");
            AudioRecordManager.getInstance().pcmToWav(filePath, folderPath);
            uploadFile(new File(folderPath));
        }

        @Override
        public void onError() {

        }
    };


    public void uploadFile(File file) {
        ThreadPoolFactory.getNormalProxy().execute(() -> {
            try {
                upload(getUrl(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * @param url 服务器地址
     * @return 响应结果
     * @throws IOException
     */
    public ResponseBody upload(String url, File file) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/octet-stream");//设置类型，类型为八位字节流
        RequestBody requestBody = RequestBody.create(mediaType, file);//把文件与类型放入请求体
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);
        LogUtils.sf(response.body().string());
        return response.body();
    }


    private String getUrl() {
        return "http://192.168.1.217:8080/speech/chat/realtime?voice_id=" + uuid + "&seq=" + 0 + "&voice_format=" + 1;
    }
}
