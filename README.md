# 最火Android开源项目ExoPlayer使用
---
开源地址：[https://github.com/open-android/ExoPlayer](https://github.com/open-android/ExoPlayer "开源项目地址")


# 运行效果
![](http://i.imgur.com/k3VtmWp.gif)

* 爱生活,爱学习,更爱做代码的搬运工,分类查找更方便请下载黑马助手app


![黑马助手.png](http://upload-images.jianshu.io/upload_images/4037105-f777f1214328dcc4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 使用步骤

### 1. 在project的build.gradle添加如下代码(如下图)

	allprojects {
	    repositories {
	        ...
	        maven { url "https://jitpack.io" }
	    }
	}

![](http://i.imgur.com/oCPpMNe.png)
	

	
### 2. 在Module的build.gradle添加依赖

     compile 'com.github.open-android:ExoPlayer:1.0'

### 3. 复制如下代码到xml

		<?xml version="1.0" encoding="utf-8"?>
		<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
		                xmlns:tools="http://schemas.android.com/tools" android:layout_width="match_parent"
		                android:layout_height="match_parent"
		                tools:context=".MainActivity"
		                android:background="#fff"
		    >
		    <RelativeLayout
		        android:id="@+id/video_skin"
		        android:layout_width="match_parent"
		        android:layout_height="match_parent"
		        android:background="#000">
		        <com.google.android.exoplayer.AspectRatioFrameLayout
		            android:id="@+id/video_frame"
		            android:layout_width="match_parent"
		            android:layout_height="match_parent"
		            android:layout_centerInParent="true"
		            >
		            <SurfaceView
		                android:id="@+id/surface_view"
		                android:layout_width="match_parent"
		                android:layout_height="match_parent"
		                android:layout_gravity="center"
		                />
		        </com.google.android.exoplayer.AspectRatioFrameLayout>
		
		        <RelativeLayout
		            android:id="@+id/video_control"
		            android:layout_width="match_parent"
		            android:layout_height="wrap_content"
		            android:layout_alignParentBottom="true"
		            android:background="#000"
		            >
		            <ImageButton
		                android:id="@+id/py"
		                android:layout_width="30dp"
		                android:layout_height="30dp"
		                android:padding="5dp"
		                android:background="@drawable/pause"/>
		            <SeekBar
		                android:id="@+id/sk"
		                android:layout_width="match_parent"
		                android:layout_height="wrap_content"
		                android:layout_centerVertical="true"
		                android:layout_toRightOf="@+id/py"
		                android:layout_toLeftOf="@+id/fs"/>
		            <ImageButton
		                android:id="@+id/fs"
		                android:layout_width="30dp"
		                android:layout_height="30dp"
		                android:padding="5dp"
		                android:layout_alignParentRight="true"
		                android:background="@drawable/full"/>
		        </RelativeLayout>
		    </RelativeLayout>
		</RelativeLayout>


### 4. 复制如下代码到Activity

		public class MainActivity extends Activity implements SurfaceHolder.Callback, DemoPlayer
		        .Listener, View.OnClickListener {
		
		    private AspectRatioFrameLayout videoFrame;//用来控制视频的宽高比
		    private SurfaceView surfaceView; //播放区
		    private RelativeLayout video_skin;
		
		    //视频控制的各个按钮
		    private RelativeLayout video_control;
		    private ImageButton py;
		    private ImageButton fs;
		    private SeekBar seekBar;
		
		    private DemoPlayer player;
		    private Uri contentUri; //视频的uri
		    private int contentType;//流媒体传输协议类型
		    private int Duration;//视频的大小
		    private int video_width;
		    private int video_heigth;
		
		    private long playerPosition;
		    private boolean playerNeedsPrepare;
		
		    @Override
		    protected void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_main);
		        //常亮
		        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		        initView();
		
		    }
		
		
		    @Override
		    public void onResume() {
		        super.onResume();
		        onShown();
		    }
		
		    @Override
		    public void onDestroy() {
		        super.onDestroy();
		        releasePlayer();
		    }
		
		    private void initView() {
		        videoFrame = (AspectRatioFrameLayout) findViewById(R.id.video_frame);
		        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
		        surfaceView.getHolder().addCallback(this);
		        video_skin = (RelativeLayout) findViewById(R.id.video_skin);
		
		        //视频控制按钮
		        video_control = (RelativeLayout) findViewById(R.id.video_control);
		        video_control.setVisibility(View.GONE);
		        py = (ImageButton) findViewById(R.id.py);
		        py.setOnClickListener(this);
		        fs = (ImageButton) findViewById(R.id.fs);
		        fs.setOnClickListener(this);
		        seekBar = (SeekBar) findViewById(R.id.sk);
		
		        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		            @Override
		            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
		            }
		
		            @Override
		            public void onStartTrackingTouch(SeekBar seekBar) {
		
		            }
		
		            @Override
		            public void onStopTrackingTouch(SeekBar seekBar) {//放开
		                player.seekTo(seekBar.getProgress());
		            }
		        });
		
		        video_skin.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                voidControlsVisibility();
		            }
		        });
		
		
		    }
		
		    //释放player
		    private void releasePlayer() {
		        if (player != null) {
		            playerPosition = player.getCurrentPosition();
		            player.release();
		            player = null;
		        }
		    }
		
		    //获取视频数据
		    private void onShown() {
		
		        contentUri = Uri.parse("http://covertness.qiniudn" +
		                ".com/android_zaixianyingyinbofangqi_test_baseline.mp4");
		
		        contentType = PlayerUtils.inferContentType(contentUri);
		        Log.e("TAG", "contentUri" + contentUri + "contentType" + contentType);
		        if (player == null) {
		            preparePlayer(true);
		        } else {
		            player.setBackgrounded(false);
		        }
		    }
		
		    //控制按钮的显示
		    private void voidControlsVisibility() {
		        int vs = video_control.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
		        video_control.setVisibility(vs);
		    }
		
		
		    private void preparePlayer(boolean playWhenReady) {
		        if (player == null) {
		            player = new DemoPlayer(PlayerUtils.getRendererBuilder(this, contentType, contentUri));
		            player.addListener(this);
		            player.seekTo(playerPosition);//播放进度的设置
		            playerNeedsPrepare = true; //是否立即播放
		        }
		        if (playerNeedsPrepare) {
		            player.prepare();
		            playerNeedsPrepare = false;
		        }
		        player.setSurface(surfaceView.getHolder().getSurface());
		        player.setPlayWhenReady(playWhenReady);
		    }
		
		    //surfaceView的监听
		    @Override
		    public void surfaceCreated(SurfaceHolder surfaceHolder) {
		        if (player != null) {
		            player.setSurface(surfaceHolder.getSurface());
		        }
		    }
		
		    @Override
		    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
		
		    }
		
		    @Override
		    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		        if (player != null) {
		            player.blockingClearSurface();
		        }
		    }
		
		    @Override
		    public void onStateChanged(boolean playWhenReady, int playbackState) {
		        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
		        switch (playbackState) {
		            case ExoPlayer.STATE_BUFFERING:
		
		                break;
		            case ExoPlayer.STATE_ENDED:
		
		                break;
		            case ExoPlayer.STATE_IDLE://空的
		
		                break;
		            case ExoPlayer.STATE_PREPARING:
		
		                surfaceView.setVisibility(View.VISIBLE);
		
		                break;
		            case ExoPlayer.STATE_READY:
		
		                boolean first = true;
		                if (first) {
		                    //记录视频的宽高
		                    video_width = videoFrame.getWidth();
		                    video_heigth = videoFrame.getHeight();
		                    //skin的宽高
		                    if (video_width != 0 && video_heigth != 0) {
		                        PlayerUtils.scaleLayout(this, video_skin, video_width, video_heigth);
		                    }
		                    //进度条的时间设置
		                    Duration = (int) player.getDuration();
		                    Log.e("TAG", "Duration--" + Duration);
		                    seekBar.setMax(Duration);
		                    videoTime seek = new videoTime();
		                    seek.start();
		                    first = false;
		                }
		                break;
		            default:
		
		                break;
		        }
		    }
		
		    @Override
		    public void onError(Exception e) {
		
		        playerNeedsPrepare = true;
		    }
		
		    //pixelWidthHeightRatio 显示器的宽高比
		    @Override
		    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float
		            pixelWidthHeightRatio) {
		        videoFrame.setAspectRatio(height == 0 ? 1 : (width * pixelWidthHeightRatio) / height);
		    }
		
		    //横竖屏切换
		//    @Override
		    public void onConfigurationChanged(Configuration newConfig) {
		        super.onConfigurationChanged(newConfig);
		
		        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
		            //隐藏状态栏
		            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		            PlayerUtils.scaleLayout(this, video_skin, 0, 0);
		
		        } else {//竖屏
		            //显示状态栏
		            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		            //skin的宽高
		            if (video_width != 0 && video_heigth != 0) {
		                PlayerUtils.scaleLayout(this, video_skin, video_width, video_heigth);
		            }
		        }
		    }
		
		    @Override
		    public void onClick(View v) {
		        switch (v.getId()) {
		            case R.id.py://播放键
		                if (player.getPlayWhenReady()) {
		                    py.setBackground(getResources().getDrawable(R.drawable.player));
		                    player.setPlayWhenReady(false);
		                } else {
		                    py.setBackground(getResources().getDrawable(R.drawable.pause));
		                    player.setPlayWhenReady(true);
		                }
		
		                break;
		            case R.id.fs://全屏键
		
		                if (this.getResources().getConfiguration().orientation == Configuration
		                        .ORIENTATION_LANDSCAPE) {//横屏
		                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		                    fs.setBackground(getResources().getDrawable(R.drawable.full));
		                } else if (this.getResources().getConfiguration().orientation == Configuration
		                        .ORIENTATION_PORTRAIT) {//竖屏
		                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		                    fs.setBackground(getResources().getDrawable(R.drawable.lessen));
		                }
		                break;
		        }
		    }
		
		    //更新进度条
		    private final int SEEKBAR = 111;
		    private Handler seekbarHandler = new Handler() {
		        public void handleMessage(Message msg) {
		            switch (msg.what) {
		                case SEEKBAR:
		                    seekBar.setProgress(msg.arg1);
		                    break;
		            }
		            super.handleMessage(msg);
		        }
		    };
		
		    class videoTime extends Thread {
		        public void run() {
		            while (player != null && player.getCurrentPosition() <= Duration) {
		                if (player.getPlayWhenReady()) {
		                    Message message = new Message();
		                    message.what = SEEKBAR;
		                    message.arg1 = (int) player.getCurrentPosition();
		                    seekbarHandler.sendMessage(message);
		                    try {
		                        Thread.sleep(1000);
		                    } catch (InterruptedException e) {
		                        e.printStackTrace();
		                    }
		
		                }
		            }
		        }
		    }
		}


### 5.添加权限

	<uses-permission android:name="android.permission.INTERNET"></uses-permission>


> 细节注意:
>
> contentUri:表示视频的uri
>
>




* 详细的使用方法在DEMO里面都演示啦,如果你觉得这个库还不错,请赏我一颗star吧~~~

* 欢迎关注微信公众号

![](http://upload-images.jianshu.io/upload_images/4037105-8f737b5104dd0b5d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


