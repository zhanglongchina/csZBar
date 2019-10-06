package org.cloudsky.cordovaPlugins;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class SoundPoolUtils {

    private static final int MAX_STREAMS = 1;
    private static final int DEFAULT_QUALITY = 0;
    private static final int DEFAULT_PRIORITY = 1;
    private static final int LEFT_VOLUME = 1;
    private static final int RIGHT_VOLUME = 1;
    private static final int LOOP = 0;
    private static final float RATE = 1.0f;

    private static SoundPoolUtils sSoundPoolUtils;

    /**
     * 音频的相关类
     */
    private SoundPool mSoundPool;
    private Context mContext;
    private Vibrator mVibrator;


    public SoundPoolUtils(Context context) {
        mContext = context;
        //初始化行营的音频类
        intSoundPool();
        initVibrator();
    }

    /**
     * @author Angle
     * 创建时间: 2018/11/4 13:02
     * 方法描述: 初始化短音频的内容
     */
    private void intSoundPool() {
        //根据不同的版本进行相应的创建
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder()
                    //传入最多播放音频数量
                    .setMaxStreams(MAX_STREAMS)
                    //设置音频流的合适的属性，AudioAttributes是一个封装音频各种属性的方法，加载一个AudioAttributes
                    .setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build())
                    .build();
        } else {
            /**
             * 第一个参数：int maxStreams：SoundPool对象的最大并发流数
             * 第二个参数：int streamType：AudioManager中描述的音频流类型
             *第三个参数：int srcQuality：采样率转换器的质量。 目前没有效果。 使用0作为默认值。
             */
            mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, DEFAULT_QUALITY);
        }
    }

    /**
     * @author Angle
     * 创建时间: 2018/11/4 13:03
     * 方法描述: 初始化震动的对象
     */
    private void initVibrator() {
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static SoundPoolUtils getInstance(Context context) {
        if (sSoundPoolUtils == null) {
            synchronized (SoundPoolUtils.class) {
                if (sSoundPoolUtils == null) {
                    sSoundPoolUtils = new SoundPoolUtils(context);
                }
            }
        }
        return sSoundPoolUtils;
    }

    /**
     * @param resId 音频的资源ID
     * @author Angle
     * 创建时间: 2018/11/4 13:03
     * 方法描述: 开始播放音频
     */
    public void playVideo(int resId) {
        if (mSoundPool == null) {
            intSoundPool();
        }
        //  可以通过四种途径来记载一个音频资源：
        //  1.通过一个AssetFileDescriptor对象
        //  int load(AssetFileDescriptor afd, int priority)
        //  2.通过一个资源ID
        //  int load(Context context, int resId, int priority)
        //  3.通过指定的路径加载
        //  int load(String path, int priority)
        //  4.通过FileDescriptor加载
        //  int load(FileDescriptor fd, long offset, long length, int priority)

        //  声音ID 加载音频资源,这里用的是第二种，第三个参数为priority，声音的优先级*API中指出，priority参数目前没有效果，建议设置为1。
        final int load = mSoundPool.load(mContext, resId, DEFAULT_PRIORITY);
        //异步需要等待加载完成，音频才能播放成功
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    //第一个参数soundID
                    //第二个参数leftVolume为左侧音量值（范围= 0.0到1.0）
                    //第三个参数rightVolume为右的音量值（范围= 0.0到1.0）
                    //第四个参数priority 为流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理
                    //第五个参数loop 为音频重复播放次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次
                    //第六个参数 rate为播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率)
                    soundPool.play(load, 1, 1, 1, 0, 1);
                }
            }
        });
    }

    /**
     * @param milliseconds 震动时间
     * @author Angle
     * 创建时间: 2018/11/4 13:04
     * 方法描述: 开启相应的震动
     */
    public void startVibrator(long milliseconds) {
        if (mVibrator == null) {
            initVibrator();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(milliseconds, 100);
            mVibrator.vibrate(vibrationEffect);
        } else {
            mVibrator.vibrate(1000);
        }
    }

    /**
     * @param resId        资源id
     * @param milliseconds 震动时间
     * @author Angle
     * 创建时间: 2018/11/4 13:06
     * 方法描述: 同时开始音乐和震动
     */
    public void startVideoAndVibrator(int resId, long milliseconds) {
        playVideo(resId);
        startVibrator(milliseconds);
    }

    /**
     * @author Angle
     * 创建时间: 2018/11/4 13:05
     * 方法描述:  释放相应的资源
     */
    public void release() {
        //释放所有的资源
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }

        if (mVibrator != null) {
            mVibrator.cancel();
            mVibrator = null;
        }
    }
}

