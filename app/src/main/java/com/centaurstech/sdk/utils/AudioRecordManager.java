package com.centaurstech.sdk.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

import com.centaurstech.qiwu.common.ThreadPoolFactory;
import com.centaurstech.qiwu.common.ThreadPoolProxy;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.sdk.listener.OnAudioStatusUpdateListener;
import com.centaurstech.voice.QiWuVoice;
import com.qiwu.ui.common.AppConfig;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
public class AudioRecordManager {

    public static final String TAG = "AudioRecordManager";
    private AudioRecord mRecorder;
    private Thread recordThread;
    private boolean isStart = false;
    private static AudioRecordManager mInstance;
    private int bufferSize;
    private DataOutputStream dos;
    private long startTime;
    private String folderPath;
    private int lenght;
    private String uuid = "";
    private File file;

    private OnAudioStatusUpdateListener mOnAudioStatusUpdateListener;

    public AudioRecordManager() {
        bufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSize * 2);
    }


    /**
     * 获取单例引用
     *
     * @return
     */
    //单例模式
    public static AudioRecordManager getInstance() {
        if (mInstance == null) {
            mInstance = new AudioRecordManager();
        }
        return mInstance;
    }

    /**
     * 启动录音线程
     */
    private void startThread() {
        destroyThread();
        isStart = true;
        startTime = System.currentTimeMillis();
        if (recordThread == null) {
            recordThread = new Thread(recordRunnable);
            recordThread.start();
        }
    }

    /**
     * 启动录音
     */
    public void startRecord() {
        try {
            folderPath = AppConfig.getContext().getExternalFilesDir("test.pcm").toString();
            setPath();
            startThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 停止录音
     */
    public void stopRecord() {
        try {
            destroyThread();
            if (mRecorder != null) {
                if (mRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                    mRecorder.stop();
                }
                if (mRecorder != null) {
                    mRecorder.release();
                }
            }
            if (dos != null) {
                dos.flush();
                dos.close();
            }
            mRecorder = null;
            if (mOnAudioStatusUpdateListener != null) {
                mOnAudioStatusUpdateListener.onFinish(System.currentTimeMillis() - startTime, folderPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 录音线程
     */
    Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                startTime = System.currentTimeMillis();
                int bytesRecord;
                //int bufferSize = 320;
                byte[] tempBuffer = new byte[bufferSize];
                QiWuVoice.getWakeup().dormant();
                if (mRecorder == null) {
                    bufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                    mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize * 2);
                }
                mRecorder.startRecording();
                //writeToFileHead();
                while (isStart) {
                    if (null != mRecorder) {
                        bytesRecord = mRecorder.read(tempBuffer, 0, bufferSize);
                        if (bytesRecord == AudioRecord.ERROR_INVALID_OPERATION || bytesRecord == AudioRecord.ERROR_BAD_VALUE) {
                            continue;
                        }
                        if (bytesRecord != 0 && bytesRecord != -1) {
                            //在此可以对录制音频的数据进行二次处理 比如变声，压缩，降噪，增益等操作
                            //我们这里直接将pcm音频原数据写入文件 这里可以直接发送至服务器 对方采用AudioTrack进行播放原数据
                            dos.write(tempBuffer);
                            if (mOnAudioStatusUpdateListener != null) {
                                mOnAudioStatusUpdateListener.onUpdate(tempBuffer, System.currentTimeMillis() - startTime);
                            }
                        } else {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 销毁线程方法
     */
    private void destroyThread() {
        try {
            isStart = false;
            if (null != recordThread && Thread.State.RUNNABLE == recordThread.getState()) {
                try {
                    Thread.sleep(500);
                    recordThread.interrupt();
                } catch (Exception e) {
                    recordThread = null;
                }
            }
            recordThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            recordThread = null;
        }
    }

    /**
     * 保存文件
     *
     * @throws Exception
     */
    public void setPath() {
        try {
            file = new File(folderPath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            dos = new DataOutputStream(new FileOutputStream(file, true));
        } catch (Exception e) {

        }
    }

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener onAudioStatusUpdateListener) {
        mOnAudioStatusUpdateListener = onAudioStatusUpdateListener;
    }

    /**
     * pcm文件转wav文件
     *
     * @param inFilename  源文件路径
     * @param outFilename 目标文件路径
     */
    public void pcmToWav(String inFilename, String outFilename) {
        FileInputStream in;
        FileOutputStream out;
        long totalAudioLen;
        long totalDataLen;
        long longSampleRate = 8000;
        int channels = 2;
        long byteRate = 16 * 8000 * channels / 8;
        byte[] data = new byte[bufferSize];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            writeWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加入wav文件头
     */
    private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W'; //WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd'; //data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}
