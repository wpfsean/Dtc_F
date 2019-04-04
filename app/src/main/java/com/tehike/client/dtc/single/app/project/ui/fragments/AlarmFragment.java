package com.tehike.client.dtc.single.app.project.ui.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.tehike.client.dtc.single.app.project.App;
import com.tehike.client.dtc.single.app.project.R;
import com.tehike.client.dtc.single.app.project.db.DbHelper;
import com.tehike.client.dtc.single.app.project.db.DbUtils;
import com.tehike.client.dtc.single.app.project.entity.AlarmTypeBean;
import com.tehike.client.dtc.single.app.project.entity.AlarmVideoSource;
import com.tehike.client.dtc.single.app.project.entity.EventSources;
import com.tehike.client.dtc.single.app.project.entity.OpenBoxParamater;
import com.tehike.client.dtc.single.app.project.entity.SipBean;
import com.tehike.client.dtc.single.app.project.entity.SipGroupInfoBean;
import com.tehike.client.dtc.single.app.project.entity.SipGroupItemInfoBean;
import com.tehike.client.dtc.single.app.project.entity.VideoBean;
import com.tehike.client.dtc.single.app.project.global.AppConfig;
import com.tehike.client.dtc.single.app.project.phone.Linphone;
import com.tehike.client.dtc.single.app.project.phone.PhoneCallback;
import com.tehike.client.dtc.single.app.project.phone.SipManager;
import com.tehike.client.dtc.single.app.project.phone.SipService;
import com.tehike.client.dtc.single.app.project.services.RemoteVoiceOperatService;
import com.tehike.client.dtc.single.app.project.thread.HandlerAlarmThread;
import com.tehike.client.dtc.single.app.project.thread.HandlerAmmoBoxThread;
import com.tehike.client.dtc.single.app.project.thread.SendAlarmToServerThread;
import com.tehike.client.dtc.single.app.project.ui.BaseFragment;
import com.tehike.client.dtc.single.app.project.ui.views.CustomViewPagerSlide;
import com.tehike.client.dtc.single.app.project.utils.ActivityUtils;
import com.tehike.client.dtc.single.app.project.utils.ByteUtil;
import com.tehike.client.dtc.single.app.project.utils.CryptoUtil;
import com.tehike.client.dtc.single.app.project.utils.FileUtil;
import com.tehike.client.dtc.single.app.project.utils.G711Utils;
import com.tehike.client.dtc.single.app.project.utils.GsonUtils;
import com.tehike.client.dtc.single.app.project.utils.HttpBasicRequest;
import com.tehike.client.dtc.single.app.project.utils.Logutil;
import com.tehike.client.dtc.single.app.project.utils.NetworkUtils;
import com.tehike.client.dtc.single.app.project.utils.RemoteVoiceRequestUtils;
import com.tehike.client.dtc.single.app.project.utils.ScreenUtils;
import com.tehike.client.dtc.single.app.project.utils.SharedPreferencesUtils;
import com.tehike.client.dtc.single.app.project.utils.SysinfoUtils;
import com.tehike.client.dtc.single.app.project.utils.TimeUtils;
import com.tehike.client.dtc.single.app.project.utils.ToastUtils;
import com.tehike.client.dtc.single.app.project.utils.UIUtils;
import com.tehike.client.dtc.single.app.project.utils.WriteLogToFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.LinphoneCall;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;

/**
 * 描述：用于显示报警信息的Fragment页面
 * ===============================
 *
 * @author wpfse wpfsean@126.com
 * @version V1.0
 * @Create at:2019/1/2 10:56
 */
public class AlarmFragment extends BaseFragment implements View.OnClickListener {

    /**
     * 报警界面的父布局
     */
    @BindView(R.id.secondary_screen_parent_layout)
    RelativeLayout secondaryScreenParentLayout;

    /**
     * 地图背景布局(用于显示地图)
     */
    @BindView(R.id.backgroup_map_view_layout)
    ImageView backGrooupMapLayou;

    /**
     * 根布局
     */
    @BindView(R.id.sh_police_image_relative)
    RelativeLayout parentLayout;

    /**
     * 显示报警点哨兵位置的图片
     */
    @BindView(R.id.police_sentinel_image_layout)
    ImageView sentinelPointLayout;

    /**
     * 左侧功能区的父布局
     */
    @BindView(R.id.left_function_parent_layout)
    RelativeLayout leftFunctionParentLayout;

    /**
     * 右侧功能区的父布局
     */
    @BindView(R.id.right_function_parent_layout)
    RelativeLayout rightFunctionParentLayout;

    /**
     * 左侧显示隐藏的按键
     */
    @BindView(R.id.left_hide_btn_layout)
    ImageButton leftHideBtn;

    /**
     * 左侧显示隐藏的按键
     */
    @BindView(R.id.right_hide_btn_layout)
    ImageButton rightHideBtn;

    /**
     * 侧边的根布局
     */
    @BindView(R.id.side_parent_layout)
    RelativeLayout sideParentLayout;

    /**
     * 显示当前报警信息的父布局
     */
    @BindView(R.id.display_alarm_parent_layout)
    LinearLayout alarmParentLayout;

    /**
     * 视频加载动画的View
     */
    @BindView(R.id.alarm_video_loading_icon_layout)
    ImageView loadingView;

    /**
     * 播放视频通话时的加载动画
     */
    @BindView(R.id.alarm_call_video_loading_icon_layout)
    ImageView alarmCallVideoLoadingView;

    /**
     * 视频加载提示的View
     */
    @BindView(R.id.alarm_video_loading_tv_layout)
    TextView loadingTv;

    /**
     * 播放视频通话时的文字提示
     */
    @BindView(R.id.alarm_call_video_loading_tv_layout)
    TextView alarmCallLoadingTv;

    /**
     * 哨位分组布局
     */
    @BindView(R.id.sentinel_group_listview_layout)
    ListView sentinelListViewLayout;

    /**
     * 哨位资源分组布局
     */
    @BindView(R.id.sentinel_resources_group_listview_layout)
    ListView sentinelResourcesListViewLayout;

    /**
     * 展示事件信息的ListView
     */
    @BindView(R.id.event_queue_listview_layout)
    ListView eventListViewLayout;

    /**
     * 右侧报警队表
     */
    @BindView(R.id.alarm_queue_listview_layout)
    ListView alarmQueueListViewLayout;

    /**
     * 已处理的报警队列
     */
    @BindView(R.id.processed_alarm_list_layout)
    ListView processedAlarmList;

    /**
     * 播放视频源的View
     */
    @BindView(R.id.alarm_video_view_layout)
    NodePlayerView alarmView;

    /**
     * 正在处理哪个哨位的报警信息
     */
    @BindView(R.id.alarm_handler_sentry_name_layout)
    TextView handlerSentryNameLayout;

    /**
     * 处理报警时的时间信息
     */
    @BindView(R.id.alarm_handler_sentry_time_layout)
    TextView handlerSenrtyTimeLayout;

    /**
     * 报警队表
     */
    @BindView(R.id.sentinel_request_queue_layout)
    GridView requestOpenBoxViewLayout;

    /**
     * 播放对话时对方的视频源
     */
    @BindView(R.id.alarm_call_video_view_layout)
    NodePlayerView alarmCallViewLayout;

    /**
     * 左侧功能布局是否隐藏的标识
     */
    boolean leftParentLayotHide = false;

    /**
     * 右侧功能布局是否隐藏的标识
     */
    boolean rightParentLayotHide = false;

    /**
     * 网络请求到的背景图片
     */
    Bitmap backGroupBitmap = null;

    /**
     * 状态报警队列的集合
     */
    public static LinkedList<AlarmVideoSource> alarmQueueList = new LinkedList<>();

    /**
     * 状态报警队列的集合
     */
    public static LinkedList<OpenBoxParamater> requestOpenBoxQueueList = new LinkedList<>();

    /**
     * 事件信息的队列
     */
    LinkedList<EventSources> eventQueueList = new LinkedList<>();

    /**
     * 接收报警信息的广播
     */
    public ReceiveAlarmBroadcast mReceiveAlarmBroadcast;

    /**
     * 展示报警信息的适配器
     */
    AlarmQueueAdapter mAlarmQueueAdapter;

    /**
     * 展示事件的适配器
     */
    EventQueueAdapter eventQueueAdapter;

    /**
     * 展示申请供弹的适配器
     */
    RequestOpenBoxQueueAdapter requestOpenBoxQueueAdapter;

    /**
     * 接收本地缓存的视频字典广播
     */
    public VideoSourcesBroadcast mVideoSourcesBroadcast;

    /**
     * 加载时的动画
     */
    Animation mLoadingAnim;

    /**
     * 报警视频源播放器
     */
    NodePlayer alarmPlayer;

    /**
     * 报警通话视频源播放器
     */
    NodePlayer alarmCallPlayer;

    /**
     * 本地缓存的所有的视频数制（视频字典）
     */
    List<VideoBean> allVideoList;

    /**
     * 本地缓存的所有的Sip数制（SIp字典）
     */
    List<SipBean> allSipList;

    /**
     * 展示已处理报警队列 的适配器
     */
    ProcessedAlarmQueueAdapter mProcessedAlarmQueueAdapter;

    /**
     * 盛放哨位分组的数据
     */
    List<SipGroupInfoBean> sentinelGroupItemList = new ArrayList<>();

    /**
     * d盛放哨位资源分组的适配器
     */
    List<SipGroupItemInfoBean> sentinelResourcesGroupItemList = new ArrayList<>();

    /**
     * 展示哨位分组的适配器
     */
    SentinelGroupAdapter mSentinelGroupAdapter;

    /**
     * 展示哨位资源分组的适配器
     */
    SentinelResourcesGroupItemAdapter mSentinelResourcesGroupItemAdapter;

    /**
     * 接收开箱申请的广播
     */
    ReceiveBoxBroadcast mReceiveBoxBroadcast;

    /**
     * 广播（Sip缓存完成）
     */
    SipSourcesBroadcast mSipSourcesBroadcast;

    /**
     * 用来存储所有哨位图标的集合
     */
    List<View> allView = new ArrayList<>();

    /**
     * 报警源的Sip号码
     */
    String alarmSipNumber = "";

    /**
     * 操作哨位时显示的Popuwindow
     */
    PopupWindow window;

    /**
     * 显示哨位名称
     */
    TextView popuSentinelNameLayout;

    /**
     * 语音电话按键
     */
    ImageButton voiceCallBtnLayout;

    /**
     * 视频电话按键
     */
    ImageButton popuVideoCallBtnLayout;

    /**
     * 查看面部视频按键
     */
    ImageButton popuVideoBtnLayout;

    /**
     * 点击哨位图标时产生的哨位对象
     */
    SipGroupItemInfoBean currentSentinelBean;

    /**
     * 点击哨位图标时产生的哨位面部视频对象
     */
    VideoBean setryVideoBean = null;

    /**
     * 哪个开启申请被选中
     */
    int whichOpenBoxPosition = -1;

    /**
     * 哪个报警对象被选中
     */
    int whichAlarmPosition = -1;

    /**
     * 定时的线程池任务
     */
    private ScheduledExecutorService timingPoolTaskService;

    /**
     * 定义是否正在处理报警
     */
    boolean isHandleringAlarm = false;

    /**
     * 是否正在通话中
     */
    boolean isCalling = false;

    /**
     * 当前的哨位名（与哪个哨位通话）
     */
    String sentryName = "";

    /**
     * 时间显示线程是否正在远行
     */
    boolean isTimingThreadWork = false;

    /**
     * 计时的子线程
     */
    Thread timingThread = null;

    /**
     * 计时
     */
    int timingNumber = 0;

    /**
     * 广播用于刷新供弹申请队列
     */
    ReceiveKeyBoardRequestOpenAmmoBoxBroadcast mReceiveKeyBoardRequestOpenAmmoBoxBroadcast;

    /**
     * 刷新已处理的报警列表和事件列表
     */
    ReceiveRefreshProcessedAlarmQueueAndEventQueueBroadcast mReceiveRefreshProcessedAlarmQueueAndEventQueueBroadcast;

    /**
     * 注册广播，监听键盘关闭报警
     */
    KeyBoardCloseAlarmBroadcast mKeyBoardCloseAlarmBroadcast;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_alarm_layout;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        //初始化viw
        initializeView();
        //加载所有的报警队列数据
        initlizeAlarmQueueAdapterData();
        //加载背景地图
        initLocationMapUrl();
        //注册广播接收报警信息
        registerReceiveAlarmBroadcast();
        //注册广播监听申请供弹
        registerReceiveBoxBroadcast();
        //加载已处理的报警信息
        initProcessedAlarmData();
        //加载事件信息
        initEventData();
        //加载本地的所有的视频资源
        initVideoSources();
        //加载本地的Sip资源
        initSipSources();
        //初始化哨位分组数据
        initSentinelGroupData();
        //初始化哨位状态刷新
        initTimingRefreshSentinelStatus();
        //注册广播监听键盘操作弹箱的动作
        registerKeyBoardOpenBoxBroadcast();
        //注册刷新右侧列表的广播
        registerReceiveRefreshProcessedAlarmQueueAndEventQueueBroadcast();
        //注册广播监听键盘操作报警队列的动作
        registerKeyBoardCloseAlarmLightBroadcast();
    }

    /**
     * 加载背景地图的url
     */
    private void initLocationMapUrl() {
        if (SysinfoUtils.getSysinfo()==null){
            return;
        }
        //测试
        final String requestUrl = AppConfig.WEB_HOST + SysinfoUtils.getSysinfo().getWebresourceServer() + AppConfig._LOCATIONS;
        //请求Locations资源
        HttpBasicRequest httpBasicRequest = new HttpBasicRequest(requestUrl, new HttpBasicRequest.GetHttpData() {
            @Override
            public void httpData(String result) {
                Message message = new Message();
                message.obj = result;
                message.what = 7;
                handler.sendMessage(message);
            }
        });
        new Thread(httpBasicRequest).start();
    }

    /**
     * 处理地图信息
     */
    private void handlerLocationsData(String locationsData) {

        String nativeGuid = SysinfoUtils.getSysinfo().getDeviceGuid();
        String mapUrl = "";
        if (TextUtils.isEmpty(nativeGuid)) {
            Logutil.e("nativeGuid is null");
            return;
        }

        if (!TextUtils.isEmpty(locationsData) && !locationsData.contains("Execption") && !locationsData.contains("errorCode")) {
            try {
                JSONObject jsonObject = new JSONObject(locationsData);
                JSONArray jsonArray = jsonObject.getJSONArray("terminals");
                if (jsonArray.length() == 0) {
                    return;
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                    String guid = jsonItem.getString("guid");
                    if (guid.equals(nativeGuid)) {
                        mapUrl = jsonItem.getString("mapUrl");
                    }
                }
                Logutil.e("mapurl:" + mapUrl);
                if (TextUtils.isEmpty(mapUrl)) {
                    Logutil.e("mapUrl is null");
                    return;
                }
                //从网络加载背景地图的图片
                initBackgroupBitmap(mapUrl);


            } catch (Exception e) {

            }
        }

    }

    /**
     * 加载事件信息
     */
    private void initEventData() {
        //清空事件队列
        if (eventQueueList != null && eventQueueList.size() > 0) {
            eventQueueList.clear();
        }
        //查询数据库
        Cursor c = new DbUtils(App.getApplication()).query(DbHelper.EVENT_TAB_NAME, null, null, null, null, null, null, null);
        if (c == null) {
            Logutil.e("c is null");
            return;
        }
        //遍历Cursor
        if (c.moveToFirst()) {
            do {
                EventSources mEventSources = new EventSources();
                String time = c.getString(c.getColumnIndex("time"));
                String event = c.getString(c.getColumnIndex("event"));
                mEventSources.setEvent(event);
                mEventSources.setTime(time);
                eventQueueList.add(mEventSources);
            } while (c.moveToNext());
        }
        //把事件队列反转一下，最新的放在上面
        if (eventQueueList != null && eventQueueList.size() > 0)
            eventQueueList = reverseLinkedList(eventQueueList);
        //适配器展示
        if (eventQueueAdapter == null) {
            eventQueueAdapter = new EventQueueAdapter();
            eventListViewLayout.setAdapter(eventQueueAdapter);
        }
        eventQueueAdapter.notifyDataSetChanged();

    }

    /**
     * 反转linkedlist
     */
    private LinkedList reverseLinkedList(LinkedList linkedList) {
        LinkedList<Object> newLinkedList = new LinkedList<>();
        for (Object object : linkedList) {
            newLinkedList.add(0, object);
        }
        return newLinkedList;
    }

    /**
     * 加载哨位分组数据
     */
    private void initSentinelGroupData() {
        //判断网络
        if (!NetworkUtils.isConnected()) {
            handler.sendEmptyMessage(11);
            return;
        }
        //请求sip分组数据的Url
        String sipGroupUrl = AppConfig.WEB_HOST + SysinfoUtils.getServerIp() + AppConfig._USIPGROUPS;

        //请求sip组数据
        HttpBasicRequest thread = new HttpBasicRequest(sipGroupUrl, new HttpBasicRequest.GetHttpData() {
            @Override
            public void httpData(String result) {
                //无数据
                if (TextUtils.isEmpty(result)) {
                    Logutil.e("请求sip组无数据");
                    handler.sendEmptyMessage(12);
                    return;
                }
                //数据异常
                if (result.contains("Execption")) {
                    Logutil.e("请求sip组数据异常" + result);
                    handler.sendEmptyMessage(12);
                    return;
                }
                Logutil.d("當前數據分組信息--->>>" + result);
                //让handler去处理数据
                Message sipGroupMess = new Message();
                sipGroupMess.what = 13;
                sipGroupMess.obj = result;
                handler.sendMessage(sipGroupMess);
            }
        });
        new Thread(thread).start();
    }

    /**
     * 加载所有的本地视频资源
     */
    private void initVideoSources() {
        try {
            allVideoList = GsonUtils.GsonToList(CryptoUtil.decodeBASE64(FileUtil.readFile(AppConfig.SOURCES_VIDEO).toString()), VideoBean.class);
            Logutil.d("我获取到数据了" + allVideoList.toString());
        } catch (Exception e) {
            Logutil.e("取video字典广播异常---->>>" + e.getMessage());
            registerAllVideoSourceDoneBroadcast();
        }
    }

    /**
     * 加载本地的已缓存完成的Sip
     */
    private void initSipSources() {
        try {
            allSipList = GsonUtils.GsonToList(CryptoUtil.decodeBASE64(FileUtil.readFile(AppConfig.SOURCES_SIP).toString()), SipBean.class);
        } catch (Exception e) {
            //异常后注册广播用来接收sip缓存完成的通知
            registerAllSipSourceDoneBroadcast();
        }

    }

    /**
     * 初始化所有的已处理的报警信息数据
     */
    private void initProcessedAlarmData() {
        //存放已处理的报警事件
        LinkedList<AlarmVideoSource> mlist = new LinkedList<>();
        //清空队列
        mlist.clear();
        //查询数据库
        Cursor c = new DbUtils(App.getApplication()).query(DbHelper.TAB_NAME, null, null, null, null, null, null, null);
        if (c == null) {
            Logutil.e("c is null");
            return;
        }
        //遍历
        if (c.moveToFirst()) {
            do {
                AlarmVideoSource alarmVideoSource = new AlarmVideoSource();
                alarmVideoSource.setSenderIp(c.getString(c.getColumnIndex("senderIp")));
                alarmVideoSource.setFaceVideoId(c.getString(c.getColumnIndex("faceVideoId")));
                alarmVideoSource.setAlarmType(c.getString(c.getColumnIndex("alarmType")));
                alarmVideoSource.setFaceVideoName(c.getString(c.getColumnIndex("faceVideoName")));
                alarmVideoSource.setTime(c.getString(c.getColumnIndex("time")));
                mlist.add(alarmVideoSource);
            } while (c.moveToNext());
        }
        if (mlist != null && mlist.size() > 0)
            mlist = reverseLinkedList(mlist);
        //适配数据
        if (mProcessedAlarmQueueAdapter == null) {
            mProcessedAlarmQueueAdapter = new ProcessedAlarmQueueAdapter(mlist);
            processedAlarmList.setAdapter(mProcessedAlarmQueueAdapter);
        }
        //刷新适配器
        mProcessedAlarmQueueAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化View
     */
    private void initializeView() {
        //加载动画
        mLoadingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.loading);
        //左侧按键监听
        leftHideBtn.setOnClickListener(this);
        //右侧按键监听
        rightHideBtn.setOnClickListener(this);
        //报警视频播放器
        alarmPlayer = new NodePlayer(getActivity());
        alarmPlayer.setPlayerView(alarmView);
        alarmPlayer.setVideoEnable(true);
        alarmPlayer.setAudioEnable(false);
        //接收到报警后的通话视频
        alarmCallPlayer = new NodePlayer(getActivity());
        alarmCallPlayer.setPlayerView(alarmCallViewLayout);
        alarmCallPlayer.setVideoEnable(true);
        alarmCallPlayer.setAudioEnable(false);
    }

    /**
     * 注册接收键盘操作开箱请求动作
     */
    private void registerKeyBoardOpenBoxBroadcast() {
        mReceiveKeyBoardRequestOpenAmmoBoxBroadcast = new ReceiveKeyBoardRequestOpenAmmoBoxBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConfig.REFRESH_REQUEST_OPEN_BOX_ACTION);
        getActivity().registerReceiver(mReceiveKeyBoardRequestOpenAmmoBoxBroadcast, intentFilter);
    }

    /**
     * 广播接收键盘发送的处理供弹请求的动作
     */
    class ReceiveKeyBoardRequestOpenAmmoBoxBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("action");
            if (!TextUtils.isEmpty(type)) {
                switch (type) {
                    case "accept":
                        acceptOpenAmmoBox();
                        break;
                    case "rejectAll":
                        rejectOpenAmmoBox();
                        break;
                    case "accpetAll":
                        accpetOpenAllAmmoBox();
                        break;
                }
            }

        }
    }

    /**
     * 注册接收刷新事件列表和已处理的报警列表
     */
    private void registerReceiveRefreshProcessedAlarmQueueAndEventQueueBroadcast() {
        mReceiveRefreshProcessedAlarmQueueAndEventQueueBroadcast = new ReceiveRefreshProcessedAlarmQueueAndEventQueueBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConfig.REFRESH_ACTION);
        getActivity().registerReceiver(mReceiveRefreshProcessedAlarmQueueAndEventQueueBroadcast, intentFilter);
    }

    /**
     * 广播刷新已处理的报警队列和事件队列
     */
    class ReceiveRefreshProcessedAlarmQueueAndEventQueueBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handler.sendEmptyMessage(27);
        }
    }

    /**
     * 加载背景地图
     */
    private void initBackgroupBitmap(final String s) {

        //开始子线程去请求背景地图
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL iconUrl = new URL(s);
                    URLConnection conn = iconUrl.openConnection();
                    HttpURLConnection http = (HttpURLConnection) conn;
                    int length = http.getContentLength();
                    conn.connect();
                    //获得图像的字符流
                    InputStream is = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is, length);
                    Bitmap bm = BitmapFactory.decodeStream(bis);
                    bis.close();
                    is.close();
                    if (bm != null) {
                        Message message = new Message();
                        message.what = 1;
                        message.obj = bm;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Log.e("TAG", "请求图片异常--" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 显示地图背景
     */
    private void disPlayBackGroupBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            backGrooupMapLayou.setImageBitmap(bitmap);
            backGroupBitmap = bitmap;
        } else {
            Logutil.e("请求到的背景图片为空---");
        }
    }

    /**
     * 计算所有布防点的位置
     */
    private void disPlayAllSentinelPoints() {
        if (backGroupBitmap == null) {
            Logutil.e("backGroupBitmap  is null");
            return;
        }
        //计算网络加载的背景图片的宽高
        int netBitmapWidth = backGroupBitmap.getWidth();
        int netBitmapHeight = backGroupBitmap.getHeight();
        //计算本身背景布局的宽高
        int nativeLayoutwidth = backGrooupMapLayou.getWidth();
        int nativeLayoutHeight = backGrooupMapLayou.getHeight();
        //算出宽高比例
        float percent_width = (float) netBitmapWidth / nativeLayoutwidth;
        float percent_height = (float) netBitmapHeight / nativeLayoutHeight;

        //宽高比例保留两位小数
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String width_format = decimalFormat.format(percent_width);
        String height_format = decimalFormat.format(percent_height);

        //最终的宽高比例
        float final_format_width = Float.parseFloat(width_format);
        float final_format_height = Float.parseFloat(height_format);


        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(sentinelPointLayout.getLayoutParams());

        //清除上次的所有的图标
        if (parentLayout != null) {
            if (allView != null && allView.size() > 0) {
                ViewGroup viewGroup = (ViewGroup) allView.get(0).getParent();
                for (int n = 0; n < allView.size(); n++) {
                    View view = allView.get(n);
                    if (view != null) {
                        viewGroup.removeView(view);
                    }
                }
                viewGroup.invalidate();
                allView.clear();
            }
        }
        //遍历显示所有的哨位图标
        if (sentinelResourcesGroupItemList != null && sentinelResourcesGroupItemList.size() > 0) {
            for (int i = 0; i < sentinelResourcesGroupItemList.size(); i++) {
                String location = sentinelResourcesGroupItemList.get(i).getLocation();
                if (!TextUtils.isEmpty(location)) {
                    String locationArry[] = location.split(",");
                    int x = Integer.parseInt(locationArry[0]);
                    int y = Integer.parseInt(locationArry[1]);
                    // Logutil.d("x-->>" + x + "\n y---->>" + y);
                    float sentinel_width = Float.parseFloat(decimalFormat.format(x / final_format_width)) - 15;
                    float sentinel_height = Float.parseFloat(decimalFormat.format(y / final_format_height)) - 48;
                    //定义显示其他哨兵的ImageView
                    ImageView other_image = new ImageView(App.getApplication());
                    allView.add(other_image);
                    displaySentinel(other_image, layoutParams, (int) sentinel_width, (int) sentinel_height);
                }
            }
        }
        //遍历加监听
        if (allView != null && allView.size() > 0) {
            for (int k = 0; k < allView.size(); k++) {
                final int finalK = k;
                allView.get(k).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        Logutil.d("sentinelResourcesGroupItemList-->>" + sentinelResourcesGroupItemList.size());
//                        Logutil.d("allView-->>" + allView.size());
//                        Logutil.d("KKKK--<<" + finalK);
//                        Logutil.d(sentinelResourcesGroupItemList.get(finalK).getId());
//                        Logutil.d("图标点击了" + sentinelResourcesGroupItemList.get(finalK).getLocation() + "\t" + sentinelResourcesGroupItemList.get(finalK).getName());
//                        Logutil.d(sentinelResourcesGroupItemList.get(finalK).toString());
                        String location = sentinelResourcesGroupItemList.get(finalK).getLocation();
                        final int x = Integer.parseInt(location.split(",")[0]);


                        String currentClickBeanId = sentinelResourcesGroupItemList.get(finalK).getId();
                        if (TextUtils.isEmpty(currentClickBeanId)) {
                            Logutil.d("为空了-->>" + currentClickBeanId);
                            return;
                        }
                        //查询当前点击对象的面部视频
                        if (allSipList != null && allSipList.size() > 0) {
                            for (int i = 0; i < allSipList.size(); i++) {
                                String deviceId = allSipList.get(i).getId();
                                if (allSipList.get(i).getId().equals(currentClickBeanId)) {
                                    setryVideoBean = allSipList.get(i).getSetryBean();
                                }
                            }
                        }
                        //显示当前Popuwindow
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSentinelPopuWindow(allView.get(finalK), x, sentinelResourcesGroupItemList.get(finalK));
                            }
                        });
                    }
                });
            }
        }
    }

    /**
     * 展示所有的布防点
     */
    private void displaySentinel(ImageView imageView, final ViewGroup.MarginLayoutParams layoutParams, final int sentinel_width, final int sentinel_height) {
        if (layoutParams != null) {
            imageView.setImageResource(R.mipmap.sentinel);

            //设置其他哨兵哨位点的位置
            layoutParams.setMargins(sentinel_width, sentinel_height, 0, 0);
            //将哨位点位置设置到RelativeLayout.LayoutParams
            RelativeLayout.LayoutParams rllps = new RelativeLayout.LayoutParams(layoutParams);
            //设置显示其他哨兵位置图片的宽高
            rllps.width = 60;
            rllps.height = 60;
            //显示图片
            parentLayout.addView(imageView, rllps);

        }
    }

    @Override
    public void onClick(View v) {
        if (window != null && window.isShowing()) {
            window.dismiss();
        }
        switch (v.getId()) {
            case R.id.make_sentinel_voice_call_layout:
                if (currentSentinelBean != null) {
                    Intent intent = new Intent();
                    intent.putExtra("call", false);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bean", currentSentinelBean);
                    intent.setAction("makeCall");
                    intent.putExtra("bundle", bundle);
                    getActivity().sendBroadcast(intent);
                }
                Logutil.d("语音电话" + currentSentinelBean.toString());
                break;
            case R.id.make_sentinel_video_call_layout:
                if (currentSentinelBean != null) {
                    Intent intent = new Intent();
                    intent.putExtra("call", true);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bean", currentSentinelBean);
                    intent.setAction("makeCall");
                    intent.putExtra("bundle", bundle);
                    getActivity().sendBroadcast(intent);
                }
                Logutil.d("视频电话" + currentSentinelBean.toString());
                break;
            case R.id.sentinel_video_layout:
                handler.sendEmptyMessage(18);
                // Logutil.d("视频" + mBean.toString());

                break;
            case R.id.left_hide_btn_layout:
                //隐藏或显示左侧的功能布局
                hideLeftParentLayout();
                break;
            case R.id.right_hide_btn_layout:
                //隐藏或显示右侧的功能布局
                hideRightParentLayout();
                break;
            case R.id.close_alarm_btn:
                isHandleringAlarm = false;
                alarmParentLayout.setVisibility(View.GONE);
                if (alarmCallPlayer != null) {
                    alarmCallPlayer.stop();
                }
                if (alarmPlayer != null) {
                    alarmPlayer.stop();
                }
                break;
        }
    }

    /**
     * 处理供弹请求
     */
    @OnClick({R.id.accpet_open_box_btn_layout, R.id.refuse_open_box_btn_layout, R.id.accpet_open_all_box_btn_layout})
    public void handlerRequestOpenBoxOperate(View view) {
        switch (view.getId()) {
            case R.id.accpet_open_box_btn_layout:
                //同意供弹
                acceptOpenAmmoBox();
                break;
            case R.id.refuse_open_box_btn_layout:
                //拒绝供弹
                rejectOpenAmmoBox();
                break;
            case R.id.accpet_open_all_box_btn_layout:
                //同意全部供弹
                accpetOpenAllAmmoBox();
                break;
        }
    }

    /**
     * 同意供弹
     */
    private void acceptOpenAmmoBox() {
        //判断申请供弹队列是否有数据
        if (requestOpenBoxQueueList != null && requestOpenBoxQueueList.size() > 0 && whichOpenBoxPosition != -1) {
            //子线程去申请供弹
            new Thread(new HandlerAmmoBoxThread(requestOpenBoxQueueList.get(whichOpenBoxPosition), 0)).start();
            //移除 队列
            requestOpenBoxQueueList.remove(whichOpenBoxPosition);
            //提示已同意供弹
            if (getActivity() != null && isVisible()) {
                showProgressSuccess("已同意供弹");
                //tts播报
                App.startSpeaking("同意供弹");
                if (!isCalling)
                    handlerSentryNameLayout.setText("");
            }
        }
        //刷新适配器
        if (requestOpenBoxQueueList.size() > 0 && requestOpenBoxQueueAdapter != null) {
            requestOpenBoxQueueAdapter.setSelectedItem(0);
            whichOpenBoxPosition = 0;
            requestOpenBoxQueueAdapter.notifyDataSetChanged();

        }
        //判断供弹队列和报警队列是否有数据
        if (alarmQueueList.size() == 0 && requestOpenBoxQueueList.size() == 0) {
            //隐藏弹窗
            alarmParentLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 拒绝供弹
     */
    private void rejectOpenAmmoBox() {
        //判断数据是否存在
        if (requestOpenBoxQueueList != null && requestOpenBoxQueueList.size() > 0 && whichOpenBoxPosition != -1) {
            //子线程发送拒绝供弹数据
            if (requestOpenBoxQueueList.size() > 0) {
                //循环的去拒绝
                for (OpenBoxParamater o : requestOpenBoxQueueList) {
                    new Thread(new HandlerAmmoBoxThread(o, 1)).start();
                }
            }
            //清除队列
            requestOpenBoxQueueList.clear();

            //提示忆拒绝
            if (getActivity() != null && isVisible()) {
                showProgressSuccess("已供弹");
                //tts播报
                App.startSpeaking("拒绝全部供弹");
                if (!isCalling)
                    handlerSentryNameLayout.setText("");
            }
        }
        //刷新适配器
        if (requestOpenBoxQueueAdapter != null) {
            requestOpenBoxQueueAdapter.setSelectedItem(0);
            whichOpenBoxPosition = 0;
            requestOpenBoxQueueAdapter.notifyDataSetChanged();
        }
        //判断供弹队列和报警队列是否有数据
        if (alarmQueueList.size() == 0 && requestOpenBoxQueueList.size() == 0) {
            //隐藏弹窗
            alarmParentLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 同意全部供弹
     */
    private void accpetOpenAllAmmoBox() {
        //判断队列中是否有数据
        if (requestOpenBoxQueueList.size() > 0) {
            //循环的去同意
            for (OpenBoxParamater o : requestOpenBoxQueueList) {
                new Thread(new HandlerAmmoBoxThread(o, 0)).start();
            }
        }
        //清除队列
        requestOpenBoxQueueList.clear();
        //刷新适配器
        if (requestOpenBoxQueueAdapter != null) {
            requestOpenBoxQueueAdapter.notifyDataSetChanged();
        }
        //提示已同意全部供弹
        if (getActivity() != null && isVisible()) {
            showProgressSuccess("同意全部供弹");
            App.startSpeaking("同意全部供弹");
        }
        //判断供弹队列和报警队列是否有数据
        if (alarmQueueList.size() == 0 && requestOpenBoxQueueList.size() == 0) {
            //隐藏弹窗
            alarmParentLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 注册广播，监听键盘关闭报警动作
     */
    private void registerKeyBoardCloseAlarmLightBroadcast() {
        mKeyBoardCloseAlarmBroadcast = new KeyBoardCloseAlarmBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConfig.CLOSE_ALAMR_ACTION);
        getActivity().registerReceiver(mKeyBoardCloseAlarmBroadcast, intentFilter);
    }

    /**
     * 广播监听键盘关闭报警
     */
    class KeyBoardCloseAlarmBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("closeAction");
            if (!TextUtils.isEmpty(type)) {
                switch (type) {
                    case "closeCurrentAlarm":
                        closeAlarm();
                        break;
                    case "closeAllAlarm":
                        closeAllAlarm();
                        break;
                }
            }
        }
    }


    /**
     * 处理报警信息
     */
    @OnClick({R.id.colse_alarm_btn_layout, R.id.handler_alarm_btn_layout, R.id.colse_all_alarm_btn_layout})
    public void handlerAlarmOperate(View view) {
        switch (view.getId()) {
            case R.id.colse_alarm_btn_layout:
                //关闭报警
                closeAlarm();
                break;
            case R.id.handler_alarm_btn_layout:
                //处理报警
                handlerAlarm();
                break;
            case R.id.colse_all_alarm_btn_layout:
                //关闭全部报警
                closeAllAlarm();
                break;
        }
    }

    /**
     * 关闭报警
     */
    private void closeAlarm() {

        Logutil.d("whichAlarmPosition--->>" + whichAlarmPosition);
        Logutil.d("isCalling---" + isCalling + "\n" + alarmQueueList.size());
        //如果正在通话中
        if (isCalling) {
            //从队列中移除
            if (alarmQueueList.size() > 0 && whichAlarmPosition != -1) {
                //执行关灯动作
                closeAlarmLight(alarmQueueList.get(whichAlarmPosition));
            }
            //刷新适配器
            if (mAlarmQueueAdapter != null) {
                //判断队列中是否还有数据
                if (alarmQueueList != null && alarmQueueList.size() > 0) {
                    mAlarmQueueAdapter.setSelectedItem(0);
                    whichAlarmPosition = 0;
                }
                mAlarmQueueAdapter.notifyDataSetChanged();
            }
            //提示报警已关闭
            if (getActivity() != null && isVisible()) {
                showProgressSuccess("报警已关闭");
            }
            //关闭播放器
            if (alarmCallPlayer != null)
                alarmCallPlayer.stop();
            if (alarmPlayer != null)
                alarmPlayer.stop();
            //挂断电话
            SipManager.getLc().terminateAllCalls();

            App.startSpeaking("关闭报警");
        }
        //判断队列中是否还有未处理的报警
        if (alarmQueueList.size() > 0) {

            handler.sendEmptyMessage(10);
            AlarmVideoSource alarm = null;
            //获取当前的报警对象
            if (whichAlarmPosition != -1) {
                alarm = alarmQueueList.get(whichAlarmPosition);
            }
            //判断当前报警对象是否为空
            if (alarm != null) {
                if (!isCalling) {
                    //遍历查找当前报警对象的Sip号码
                    for (int i = 0; i < allSipList.size(); i++) {
                        SipBean mSipBean = allSipList.get(i);
                        if (mSipBean.getIpAddress().equals(alarm.getSenderIp())) {
                            alarmSipNumber = mSipBean.getNumber();
                            sentryName = mSipBean.getName();
                        }
                    }
                    Logutil.d("alarmSipNum--->>" + alarmSipNumber);
                    if (!TextUtils.isEmpty(alarmSipNumber)) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        App.startSpeaking("正在呼叫" + sentryName);
                        Linphone.callTo(alarmSipNumber, false);
                    }
                    //播放报警视频源
                    playAlarmVideo(alarm);
                    //播放通话视频源
                    playAlarmCallVideo(alarm);
                }
            }
        } else {
            //判断申请供弹队列中是否还有未处理的
            if (requestOpenBoxQueueList.size() == 0) {
                alarmParentLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 关闭警灯
     */
    private void closeAlarmLight(AlarmVideoSource alarmVideoSource) {
        //判断当前报警对象
        if (alarmVideoSource == null) {
            return;
        }
        //移除队列
        alarmQueueList.remove(alarmVideoSource);
        //获取当前类型
        String currentAlarmType = alarmVideoSource.getAlarmType();
        Logutil.d("currentType:" + currentAlarmType);
        Logutil.d("size:" + alarmQueueList.size());
        //若队列无报警，警灯全关闭
        if (alarmQueueList != null && alarmQueueList.size() == 0) {
            //关闭全部警灯
            App.getSerialPortManager().sendBytes(AppConfig.CLOSE_ALL_ALARM_LIGHT);
        } else {
            int redAlarmSize = 0;
            int greenAlarmSize = 0;
            int yellowAlarmSize = 0;
            int blueAlarmSize = 0;
            int orangeAlarmSize = 0;
            int pinkAlarmSize = 0;

            for (int i = 0; i < alarmQueueList.size(); i++) {
                AlarmVideoSource alarmBean = alarmQueueList.get(i);
                if (alarmBean != null) {
                    String type = alarmBean.getAlarmType();
                    if (!TextUtils.isEmpty(type)) {
                        switch (type) {
                            case "应急":
                                redAlarmSize += 1;
                                break;
                            case "脱逃":
                                redAlarmSize += 1;
                                break;
                            case "暴狱":
                                greenAlarmSize += 1;
                                break;
                            case "袭击":
                                yellowAlarmSize += 1;
                                break;
                            case "灾害":
                                blueAlarmSize += 1;
                                break;
                            case "挟持":
                                orangeAlarmSize += 1;
                                break;
                            case "突发":
                                pinkAlarmSize += 1;
                                break;
                        }
                    }
                }
            }
            Logutil.d("*********************************************");
            Logutil.d("redAlarmSize-->>" + redAlarmSize + "\t");
            Logutil.d("greenAlarmSize-->>" + greenAlarmSize + "\t");
            Logutil.d("yellowAlarmSize-->>" + yellowAlarmSize + "\t");
            Logutil.d("blueAlarmSize-->>" + blueAlarmSize + "\t");
            Logutil.d("orangeAlarmSize-->>" + orangeAlarmSize + "\t");
            Logutil.d("pinkAlarmSize-->>" + pinkAlarmSize + "\t");
            Logutil.d("*********************************************");


            if (redAlarmSize == 0) {
                App.getSerialPortManager().sendBytes(AppConfig.FIRST_ClOSE);
            }
            if (yellowAlarmSize == 0) {
                App.getSerialPortManager().sendBytes(AppConfig.SECOND_ClOSE);
            }
            if (blueAlarmSize == 0) {
                App.getSerialPortManager().sendBytes(AppConfig.THIRD_ClOSE);
            }
            if (greenAlarmSize == 0) {
                App.getSerialPortManager().sendBytes(AppConfig.FORTH_ClOSE);
            }
            if (pinkAlarmSize == 0) {
                App.getSerialPortManager().sendBytes(AppConfig.FIFTH_ClOSE);
            }
            if (orangeAlarmSize == 0) {
                App.getSerialPortManager().sendBytes(AppConfig.SIXTH_ClOSE);
            }
        }
    }


    /**
     * 处理报警
     */
    private void handlerAlarm() {
        if (isCalling) {
            SipManager.getLc().terminateAllCalls();
        }

        //处理报警
        AlarmVideoSource mAlarmBean = null;
        if (alarmQueueList.size() > 0 && whichAlarmPosition != -1) {
            mAlarmBean = alarmQueueList.get(whichAlarmPosition);
        }
        if (mAlarmBean != null) {
            Logutil.d("mA-->>" + mAlarmBean.toString());
            if (!isHandleringAlarm) {
                for (int i = 0; i < allSipList.size(); i++) {
                    SipBean mSipBean = allSipList.get(i);
                    if (mSipBean.getIpAddress().equals(mAlarmBean.getSenderIp())) {
                        alarmSipNumber = mSipBean.getNumber();
                        sentryName = mSipBean.getName();
                    }
                }
                Logutil.d("alarmSipNum--->>" + alarmSipNumber);
                if (!TextUtils.isEmpty(alarmSipNumber)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    App.startSpeaking("正在呼叫" + sentryName);
                    Linphone.callTo(alarmSipNumber, false);
                }
                //播放报警视频源
                playAlarmVideo(mAlarmBean);
                playAlarmCallVideo(mAlarmBean);
            }
        }
    }

    /**
     * 关闭全部报警
     */
    private void closeAllAlarm() {
        //关闭所有灯
        boolean isCloseAllLight = App.getSerialPortManager().sendBytes(AppConfig.CLOSE_ALL_ALARM_LIGHT);
        Logutil.d("关闭所有灯" + isCloseAllLight);
        //子线程去处理报警
        if (alarmQueueList != null && alarmQueueList.size() > 0) {
            for (AlarmVideoSource alarm : alarmQueueList) {
                new Thread(new HandlerAlarmThread(alarm.getSenderIp())).start();
            }
        }
        //清空报警队列
        alarmQueueList.clear();
        //刷新适配器
        if (mAlarmQueueAdapter != null) {
            mAlarmQueueAdapter.notifyDataSetChanged();
        }
        //中断通话
        SipManager.getLc().terminateAllCalls();
        //停止播放报警源视频
        if (alarmPlayer != null) {
            alarmPlayer.stop();
        }
        //停止报警通话视频源播放
        if (alarmCallPlayer != null) {
            alarmCallPlayer.stop();
        }
        //判断申请供弹队列是否存在数据
        if (requestOpenBoxQueueList.size() == 0)
            alarmParentLayout.setVisibility(View.GONE);
        //语音提示
        App.startSpeaking("报警已全部关闭");
        //提示报警已全部关闭
        if (getActivity() != null && isVisible())
            showProgressSuccess("报警已关闭");
    }

    /**
     * 注册接收申请供弹信息广播
     */
    private void registerReceiveBoxBroadcast() {
        mReceiveBoxBroadcast = new ReceiveBoxBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConfig.BOX_ACTION);
        getActivity().registerReceiver(mReceiveBoxBroadcast, intentFilter);
    }

    /**
     * 广播接收申请开箱信息
     */
    class ReceiveBoxBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //重新加载已处理的报警事件
            initProcessedAlarmData();

            //重新加载事件信息
            initEventData();

            OpenBoxParamater boxbean = (OpenBoxParamater) intent.getSerializableExtra("box");
            Message message = new Message();
            message.what = 17;
            message.obj = boxbean;
            handler.sendMessage(message);

        }
    }

    /**
     * 注册接收报警信息广播
     */
    private void registerReceiveAlarmBroadcast() {
        mReceiveAlarmBroadcast = new ReceiveAlarmBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConfig.ALARM_ACTION);
        getActivity().registerReceiver(mReceiveAlarmBroadcast, intentFilter);
    }

    /**
     * 广播接收报警信息
     */
    class ReceiveAlarmBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收报警对象
            AlarmVideoSource alarm = (AlarmVideoSource) intent.getSerializableExtra("alarm");
            Logutil.i("Alarm-->>" + alarm);
            //判断报警对象是否为空
            if (TextUtils.isEmpty(alarm.getFaceVideoName()) && TextUtils.isEmpty(alarm.getAlarmType())) {
                Logutil.e("alarm--- is null");
                return;
            }
            //添加到集合
            alarmQueueList.add(alarm);
            //刷新适配器
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAlarmQueueAdapter != null) {
                        if (alarmQueueList.size() > 0) {
                            whichAlarmPosition = 0;
                            mAlarmQueueAdapter.setSelectedItem(0);
                        }
                        mAlarmQueueAdapter.notifyDataSetChanged();
                    }
                }
            });
            //加载已处理的报警数据
            initProcessedAlarmData();
            //加载所有的事件信息数据
            initEventData();
            //刷新当前页面可见
            handler.sendEmptyMessage(5);

            if (allSipList == null || allSipList.size() == 0) {
                WriteLogToFile.info("接收到报警时，无video资源的本地");
                return;
            }

            //遍历查获此报警来源的sip号码
            for (int i = 0; i < allSipList.size(); i++) {
                SipBean mSipBean = allSipList.get(i);
                if (mSipBean.getIpAddress().equals(alarm.getSenderIp())) {
                    alarmSipNumber = mSipBean.getNumber();
                    sentryName = mSipBean.getName();
                }
            }
            Logutil.d("alarmSipNum--->>" + alarmSipNumber);
            Logutil.d("isCalling" + isCalling);
            //如果非通话中，就直接拨打电话电话并

            if (!isCalling) {
                if (!TextUtils.isEmpty(alarmSipNumber)) {
                    try {
                        //延时三秒后执行，为了让语音把报警内存播报完成
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //播报
                    App.startSpeaking("正在呼叫" + sentryName);
                    //电话连接
                    Linphone.callTo(alarmSipNumber, false);
                }
                //播放报警视频源
                playAlarmVideo(alarm);

                //播放报警时的视频源
                playAlarmCallVideo(alarm);
            }
        }
    }

    /**
     * 注册广播监听所有的视频数据是否解析完成
     */
    private void registerAllVideoSourceDoneBroadcast() {
        mVideoSourcesBroadcast = new VideoSourcesBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConfig.RESOLVE_VIDEO_DONE_ACTION);
        getActivity().registerReceiver(mVideoSourcesBroadcast, intentFilter);
    }

    /**
     * Video字典广播
     */
    class VideoSourcesBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                //取出本地缓存的所有的Video数据
                allVideoList = GsonUtils.GsonToList(CryptoUtil.decodeBASE64(FileUtil.readFile(AppConfig.SOURCES_VIDEO).toString()), VideoBean.class);
            } catch (Exception e) {
                Logutil.e("取video字典广播异常---->>>" + e.getMessage());
            }
        }
    }

    /**
     * 注册广播监听Sip资源缓存完成
     */
    private void registerAllSipSourceDoneBroadcast() {
        mSipSourcesBroadcast = new SipSourcesBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SipDone");
        getActivity().registerReceiver(mSipSourcesBroadcast, intentFilter);
    }

    /**
     * Sip字典广播
     */
    class SipSourcesBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                allSipList = GsonUtils.GsonToList(CryptoUtil.decodeBASE64(FileUtil.readFile(AppConfig.SOURCES_SIP).toString()), SipBean.class);
            } catch (Exception e) {
                Logutil.e("取allSipList字典广播异常---->>>" + e.getMessage());
            }
        }
    }

    /**
     * 展示报警队列的适配器
     */
    class AlarmQueueAdapter extends BaseAdapter {

        private int selectedItem = -1;

        @Override
        public int getCount() {
            return alarmQueueList.size();
        }

        @Override
        public Object getItem(int position) {
            return alarmQueueList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setSelectedItem(int selectedItem) {
            this.selectedItem = selectedItem;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_alarm_listview_layout, null);
                viewHolder.alarmName = convertView.findViewById(R.id.alarm_list_item_name_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.alarmName.setText(alarmQueueList.get(position).getFaceVideoName() + "\t" + alarmQueueList.get(position).getAlarmType());

            if (position == selectedItem) {
                viewHolder.alarmName.setBackgroundResource(R.mipmap.dtc_btn1_bg_normal);
                viewHolder.alarmName.setTextColor(0xffffffff);
            } else {
                viewHolder.alarmName.setBackgroundResource(R.mipmap.dtc_btn1_bg_selected);

                viewHolder.alarmName.setTextColor(0xffff0000);
            }
            return convertView;
        }

        //内部类
        class ViewHolder {
            TextView alarmName;
        }
    }

    /**
     * 展示报警队列的适配器
     */
    class RequestOpenBoxQueueAdapter extends BaseAdapter {

        //item选中标识
        private int selectedItem = -1;

        @Override
        public int getCount() {
            return requestOpenBoxQueueList.size();
        }

        @Override
        public Object getItem(int position) {
            return requestOpenBoxQueueList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //item选中的方法
        public void setSelectedItem(int selectedItem) {
            this.selectedItem = selectedItem;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_open_box_listview_layout, null);
                viewHolder.requestOpenBoxSentryNameLayout = convertView.findViewById(R.id.request_open_ammo_box_sentry_name_layout);
                viewHolder.requestOpenAmmoBoxParentLayout = convertView.findViewById(R.id.request_open_ammo_box_parent_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //显示申请供弹的名称
            for (int i = 0; i < allSipList.size(); i++) {
                SipBean mSipBean = allSipList.get(i);
                if (mSipBean.getIpAddress().equals(requestOpenBoxQueueList.get(position).getSendIp())) {
                    viewHolder.requestOpenBoxSentryNameLayout.setText(mSipBean.getName());
                    break;
                }
            }
            //item选中时背景更改
            if (position == selectedItem) {
                viewHolder.requestOpenAmmoBoxParentLayout.setBackgroundResource(R.mipmap.dtc_bg_danxiang_yes_selected);
                viewHolder.requestOpenBoxSentryNameLayout.setTextColor(0xffff00ff);
            } else {
                viewHolder.requestOpenAmmoBoxParentLayout.setBackgroundResource(R.drawable.request_open_ammo_box_item_bg);
                viewHolder.requestOpenBoxSentryNameLayout.setTextColor(0xffffffff);
            }
            return convertView;
        }

        //内部类
        class ViewHolder {
            //申请供弹item父布局
            RelativeLayout requestOpenAmmoBoxParentLayout;
            //申请打开弹箱的哨位名称
            TextView requestOpenBoxSentryNameLayout;
        }
    }

    /**
     * 展示事件信息的适配器
     */
    class EventQueueAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return eventQueueList.size();
        }

        @Override
        public Object getItem(int position) {
            return eventQueueList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_processed_event_item_layout, null);
                viewHolder.eventName = convertView.findViewById(R.id.processed_event_name_layout);
                viewHolder.eventTime = convertView.findViewById(R.id.processed_event_time_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.eventName.setText(eventQueueList.get(position).getEvent());
            viewHolder.eventTime.setText(eventQueueList.get(position).getTime());

            return convertView;
        }

        //内部类
        class ViewHolder {

            //事件名称
            TextView eventName;
            //事件发生时间
            TextView eventTime;
        }
    }

    /**
     * 已处理的的报警队列的适配器
     */
    class ProcessedAlarmQueueAdapter extends BaseAdapter {

        LinkedList<AlarmVideoSource> mlist;


        public ProcessedAlarmQueueAdapter(LinkedList<AlarmVideoSource> mlist) {
            this.mlist = mlist;
        }

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public Object getItem(int position) {
            return mlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_alarm_processed_event_item_layout, null);
                viewHolder.alarmEventName = convertView.findViewById(R.id.alarm_processed_event_name_layout);
                viewHolder.alarmType = convertView.findViewById(R.id.alarm_processed_event_type_layout);
                viewHolder.alarmTime = convertView.findViewById(R.id.alarm_processed_event_time_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.alarmEventName.setText(mlist.get(position).getFaceVideoName());
            viewHolder.alarmType.setText(mlist.get(position).getAlarmType());
            viewHolder.alarmTime.setText(mlist.get(position).getTime());
            return convertView;
        }

        //内部类
        class ViewHolder {
            //报警地点
            TextView alarmEventName;
            //报警类型
            TextView alarmType;
            //报警发生时间
            TextView alarmTime;

        }
    }

    /**
     * 初始化数据
     */
    private void initlizeAlarmQueueAdapterData() {
        //显示报警列表的适配器
        mAlarmQueueAdapter = new AlarmQueueAdapter();
        alarmQueueListViewLayout.setAdapter(mAlarmQueueAdapter);
        //默认第一个选中
        if (alarmQueueList.size() > 0) {
            mAlarmQueueAdapter.setSelectedItem(0);
            whichAlarmPosition = 0;
        }
        mAlarmQueueAdapter.notifyDataSetChanged();
        alarmQueueListViewLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAlarmQueueAdapter.setSelectedItem(position);
                whichAlarmPosition = position;
                mAlarmQueueAdapter.notifyDataSetChanged();
                Logutil.d("positon" + position);
                // playAlarmVideo(alarmQueueList.get(whichAlarmPosition));
            }
        });

        //展示事件队列
        eventQueueAdapter = new EventQueueAdapter();
        eventListViewLayout.setAdapter(eventQueueAdapter);
    }

    /**
     * 隐藏或显示右侧的功能布局
     */
    private void hideRightParentLayout() {
        if (!rightParentLayotHide) {
            rightParentLayotHide = true;
            rightFunctionParentLayout.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rightHideBtn.getLayoutParams();
            layoutParams.setMargins(200, 0, 0, 0);
            rightHideBtn.setLayoutParams(layoutParams);
        } else {
            rightParentLayotHide = false;
            rightFunctionParentLayout.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rightHideBtn.getLayoutParams();
            layoutParams.setMargins(0, 0, 290, 0);
            rightHideBtn.setLayoutParams(layoutParams);
        }
    }

    /**
     * 隐藏或显示左侧的功能布局
     */
    private void hideLeftParentLayout() {
        if (!leftParentLayotHide) {
            leftParentLayotHide = true;
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) leftHideBtn.getLayoutParams();
            layoutParams.leftMargin = leftHideBtn.getLeft() - 290;
            leftHideBtn.setLayoutParams(layoutParams);
            TranslateAnimation animation = new TranslateAnimation(290, 0, 0, 0);
            animation.setDuration(2000);
            animation.setFillAfter(false);
            leftFunctionParentLayout.startAnimation(animation);
            leftFunctionParentLayout.clearAnimation();
            leftFunctionParentLayout.setVisibility(View.GONE);
        } else {
            leftParentLayotHide = false;
            leftFunctionParentLayout.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) leftHideBtn.getLayoutParams();
            layoutParams.leftMargin = leftHideBtn.getLeft() + 290;
            leftHideBtn.setLayoutParams(layoutParams);
        }
    }

    /**
     * 播放报警源的视频
     */
    private void playAlarmVideo(AlarmVideoSource mAlarmVideoSource) {
        loadingView.startAnimation(mLoadingAnim);
        if (alarmPlayer != null && alarmPlayer.isPlaying()) {
            alarmPlayer.stop();
        }
        //查询报警源的视频信息
        if (allVideoList != null && allVideoList.size() > 0) {
            for (VideoBean device : allVideoList) {
                if (device != null) {
                    if (device.getId().equals(mAlarmVideoSource.getFaceVideoId())) {
                        String rtsp = device.getRtsp();
                        if (!TextUtils.isEmpty(rtsp)) {
                            Logutil.d("Rtsp-->>>" + rtsp);
                            alarmPlayer.setInputUrl(rtsp);
                            alarmPlayer.setNodePlayerDelegate(new NodePlayerDelegate() {
                                @Override
                                public void onEventCallback(NodePlayer player, int event, String msg) {
                                    Message alarmPlayerMess = new Message();
                                    alarmPlayerMess.what = 6;
                                    alarmPlayerMess.arg1 = event;
                                    handler.sendMessage(alarmPlayerMess);
                                }
                            });
                            alarmPlayer.start();
                        } else {
                            handler.sendEmptyMessage(8);
                        }
                    } else {
                        handler.sendEmptyMessage(8);
                    }
                } else {
                    handler.sendEmptyMessage(8);
                }
            }
        }
    }

    /**
     * 播放报警时通话的视频源
     */
    private void playAlarmCallVideo(AlarmVideoSource alarm) {

        alarmCallVideoLoadingView.startAnimation(mLoadingAnim);

        //判断字典是否存在
        if (allSipList == null || allSipList.size() == 0) {
            return;
        }
        //播放通话视频的地址
        String rtsp = "";

        //遍历查询
        for (int i = 0; i < allSipList.size(); i++) {
            SipBean mSipBean = allSipList.get(i);
            if (mSipBean.getIpAddress().equals(alarm.getSenderIp())) {
                alarmSipNumber = mSipBean.getNumber();
                sentryName = mSipBean.getName();
                if (mSipBean.getVideoBean() != null) {
                    if (!TextUtils.isEmpty(mSipBean.getVideoBean().getRtsp())) {
                        rtsp = mSipBean.getVideoBean().getRtsp();
                    }
                }
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handlerSentryNameLayout.setText(sentryName);
            }
        });

        //选 判断播放地址
        if (TextUtils.isEmpty(rtsp)) {
            handler.sendEmptyMessage(24);
            return;
        }
        //是否正在播放
        if (alarmCallPlayer != null) {
            alarmCallPlayer.stop();
        }
        //加载地址
        alarmCallPlayer.setInputUrl(rtsp);
        //播放回调
        alarmCallPlayer.setNodePlayerDelegate(new NodePlayerDelegate() {
            @Override
            public void onEventCallback(NodePlayer player, int event, String msg) {
                Logutil.d("event-->>" + event);
                Message alarmCallPlayerMess = new Message();
                alarmCallPlayerMess.what = 23;
                alarmCallPlayerMess.arg1 = event;
                handler.sendMessage(alarmCallPlayerMess);
            }
        });
        //开始播放
        alarmCallPlayer.start();
    }

    /**
     * 处理哨位分组数据
     */
    private void handlerSentinelGroupData(String sentinelGroupDataResult) {
        //先清空集合防止
        if (sentinelGroupItemList != null && sentinelGroupItemList.size() > 0) {
            sentinelGroupItemList.clear();
        }

        try {
            JSONObject jsonObject = new JSONObject(sentinelGroupDataResult);
            if (!jsonObject.isNull("errorCode")) {
                Logutil.w("请求不到数据信息");
                return;
            }
            int sipCount = jsonObject.getInt("count");
            if (sipCount > 0) {
                JSONArray jsonArray = jsonObject.getJSONArray("groups");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                    SipGroupInfoBean sipGroupInfoBean = new SipGroupInfoBean();
                    sipGroupInfoBean.setId(jsonItem.getInt("id"));
                    sipGroupInfoBean.setMember_count(jsonItem.getString("member_count"));
                    sipGroupInfoBean.setName(jsonItem.getString("name"));
                    sentinelGroupItemList.add(sipGroupInfoBean);
                }
            }
            handler.sendEmptyMessage(14);
        } catch (Exception e) {
            Logutil.e("解析Sip分组数据异常" + e.getMessage());
            handler.sendEmptyMessage(12);
        }
    }

    /**
     * 展示哨位分组
     */
    private void displaySentinelListAdapter() {
        //判断是否有要适配的数据
        if (sentinelGroupItemList == null || sentinelGroupItemList.size() == 0) {
            handler.sendEmptyMessage(12);
            Logutil.e("适配的数据时无数据");
            return;
        }
        mSentinelGroupAdapter = new SentinelGroupAdapter(getActivity());
        //显示左侧的sip分组页面
        sentinelListViewLayout.setAdapter(mSentinelGroupAdapter);
        mSentinelGroupAdapter.setSeclection(0);
        mSentinelGroupAdapter.notifyDataSetChanged();

        //默认加载第一组的数据

        String groupId = sentinelGroupItemList.get(0).getId() + "";
        loadVideoGroupItemData(groupId);

        //点击事件
        sentinelListViewLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSentinelGroupAdapter.setSeclection(position);
                mSentinelGroupAdapter.notifyDataSetChanged();
                SipGroupInfoBean mSipGroupInfoBean = sentinelGroupItemList.get(position);
                Logutil.i("SipGroupInfoBean-->>" + mSipGroupInfoBean.toString());
                int groupId = mSipGroupInfoBean.getId();
                loadVideoGroupItemData(groupId + "");

            }
        });
    }

    /**
     * 哨位分组适配器
     */
    class SentinelGroupAdapter extends BaseAdapter {
        //选中对象的标识
        private int clickTemp = -1;
        //布局加载器
        private LayoutInflater layoutInflater;

        //构造函数
        public SentinelGroupAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return sentinelGroupItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return sentinelGroupItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setSeclection(int position) {
            clickTemp = position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_video_group_monifor_layout, null);
                viewHolder.videoGroupName = (TextView) convertView.findViewById(R.id.video_group_name_layout);
                viewHolder.videoGroupParentLayout = (RelativeLayout) convertView.findViewById(R.id.video_group_parent_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            SipGroupInfoBean videoGroupInfoBean = sentinelGroupItemList.get(position);

            if (videoGroupInfoBean != null)
                viewHolder.videoGroupName.setText(videoGroupInfoBean.getName());

            //选中状态
            if (clickTemp == position) {
                viewHolder.videoGroupName.setTextColor(0xffffffff);
                viewHolder.videoGroupParentLayout.setBackgroundResource(R.mipmap.dtc_bg_list_group_selected);
            } else {
                viewHolder.videoGroupName.setTextColor(0xff6adeff);
                viewHolder.videoGroupParentLayout.setBackgroundResource(R.mipmap.dtc_bg_list_group_normal);
            }
            return convertView;
        }

        /**
         * 内部类
         */
        class ViewHolder {
            //显示分组名
            TextView videoGroupName;
            //分组item的父布局
            RelativeLayout videoGroupParentLayout;
        }
    }

    /**
     * 加载哨位资源分组数据
     */
    private void loadVideoGroupItemData(final String id) {
        //判断组Id是否为空
        if (TextUtils.isEmpty(id)) {
            return;
        }
        String sipGroupItemUrl = AppConfig.WEB_HOST + SysinfoUtils.getServerIp() + AppConfig._USIPGROUPS_GROUP;

        //子线程根据组Id请求组数据
        HttpBasicRequest httpThread = new HttpBasicRequest(sipGroupItemUrl + id, new HttpBasicRequest.GetHttpData() {
            @Override
            public void httpData(String result) {
                //无数据
                if (TextUtils.isEmpty(result)) {
                    handler.sendEmptyMessage(12);
                    return;
                }
                //数据异常
                if (result.contains("Execption")) {
                    handler.sendEmptyMessage(12);
                    return;
                }

                if (sentinelResourcesGroupItemList != null && sentinelResourcesGroupItemList.size() > 0) {
                    sentinelResourcesGroupItemList.clear();
                }
                Logutil.d("组数据" + result);

                //解析sip资源
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (!jsonObject.isNull("errorCode")) {
                        Logutil.w("请求不到数据信息");
                        return;
                    }

                    int count = jsonObject.getInt("count");
                    if (count > 0) {
                        JSONArray jsonArray = jsonObject.getJSONArray("resources");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonItem = jsonArray.getJSONObject(i);
                            //解析
                            SipGroupItemInfoBean groupItemInfoBean = new SipGroupItemInfoBean();
                            groupItemInfoBean.setDeviceType(jsonItem.getString("deviceType"));
                            groupItemInfoBean.setId(jsonItem.getString("id"));
                            groupItemInfoBean.setIpAddress(jsonItem.getString("ipAddress"));
                            groupItemInfoBean.setLocation(jsonItem.getString("location"));
                            groupItemInfoBean.setName(jsonItem.getString("name"));
                            groupItemInfoBean.setNumber(jsonItem.getString("number"));
                            groupItemInfoBean.setSentryId(jsonItem.getInt("sentryId"));
                            //判断是否有面部视频
                            if (!jsonItem.isNull("videosource")) {
                                //解析面部视频
                                JSONObject jsonItemVideo = jsonItem.getJSONObject("videosource");
                                if (jsonItemVideo != null) {
                                    //封闭面部视频
                                    VideoBean videoBean = new VideoBean(
                                            jsonItemVideo.getString("channel"),
                                            jsonItemVideo.getString("devicetype"),
                                            jsonItemVideo.getString("id"),
                                            jsonItemVideo.getString("ipaddress"),
                                            jsonItemVideo.getString("name"),
                                            jsonItemVideo.getString("location"),
                                            jsonItemVideo.getString("password"),
                                            jsonItemVideo.getInt("port"),
                                            jsonItemVideo.getString("username"), "", "", "", "", "", "");
                                    groupItemInfoBean.setBean(videoBean);
                                }
                            }
                            sentinelResourcesGroupItemList.add(groupItemInfoBean);
                        }
                    }
                    Logutil.d(allSipList.size() + "\t" + allSipList.toString());
                    Logutil.d("sentinelResourcesGroupItemList--->>" + sentinelResourcesGroupItemList.size() + "\t" + sentinelResourcesGroupItemList.toString());
                    handler.sendEmptyMessage(15);
                } catch (JSONException e) {
                    WriteLogToFile.info("报警列表组内数据解析异常::" + e.getMessage());
                    Logutil.e("报警列表组内数据解析异常::" + e.getMessage());
                }
            }
        });
        new Thread(httpThread).start();
    }

    /**
     * 展示哨位资源分组
     */
    private void disPlaySentinelResourcesGroupItemAdapter() {
        if (mSentinelResourcesGroupItemAdapter == null) {
            mSentinelResourcesGroupItemAdapter = new SentinelResourcesGroupItemAdapter();
            sentinelResourcesListViewLayout.setAdapter(mSentinelResourcesGroupItemAdapter);
        }
        mSentinelResourcesGroupItemAdapter.notifyDataSetChanged();
    }

    /**
     * 展示哨位资源分组的适配器
     */
    class SentinelResourcesGroupItemAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return sentinelResourcesGroupItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return sentinelResourcesGroupItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_video_group_item_monifor_layout, null);
                viewHolder.sipItemName = convertView.findViewById(R.id.video_group_item_name_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            SipGroupItemInfoBean mDevice = sentinelResourcesGroupItemList.get(position);
            viewHolder.sipItemName.setText(mDevice.getName());
            return convertView;
        }

        class ViewHolder {
            TextView sipItemName;
        }
    }

    /**
     * 开启定时请求服务，用于请求哨位的状态信息（是否在线）
     */
    private void initTimingRefreshSentinelStatus() {
        //定时线程任务池
        if (timingPoolTaskService == null || timingPoolTaskService.isShutdown())
            timingPoolTaskService = Executors.newSingleThreadScheduledExecutor();
        //开户定时的线程滠
        if (!timingPoolTaskService.isShutdown()) {
            timingPoolTaskService.scheduleWithFixedDelay(new TimingRefreshSentinelStatus(), 0L, 10 * 1000, TimeUnit.MILLISECONDS);
        }

    }

    /**
     * 收到报警时修改UiuI效果
     */
    private void updateCurrentUi() {
        //使alarmFragemt可见
        RadioGroup bottomRadioGroupLayout = getActivity().findViewById(R.id.bottom_radio_group_layout);
        CustomViewPagerSlide customViewPagerLayout = getActivity().findViewById(R.id.main_viewpager_layout);
        bottomRadioGroupLayout.check(bottomRadioGroupLayout.getChildAt(3).getId());
        customViewPagerLayout.setCurrentItem(3);
        //显示报警弹窗
        alarmParentLayout.setVisibility(View.VISIBLE);

        sipStatusCallback();
    }

    /**
     * 定时请求哨位状态的子线程
     */
    class TimingRefreshSentinelStatus extends Thread {
        @Override
        public void run() {

//            /**
//             * 定时刷新哨位状态
//             */
//            if (BoxFragment.boxStatusList.size() > 0 && sentinelResourcesGroupItemList.size() > 0) {
//
//                if (BoxFragment.boxStatusList.size() >= sentinelResourcesGroupItemList.size()) {
//
//                    for (int i = 0; i < BoxFragment.boxStatusList.size(); i++) {
//                        for (int j = 0; j < sentinelResourcesGroupItemList.size(); j++) {
//                            if (BoxFragment.boxStatusList.get(i).getID().equals(sentinelResourcesGroupItemList.get(j).getId())) {
//                                Logutil.d("哈哈，大于>>>" + sentinelResourcesGroupItemList.get(j).getName());
//                            }
//                        }
//                    }
//                } else {
//                    for (int i = 0; i < sentinelResourcesGroupItemList.size(); i++) {
//                        for (int j = 0; j < BoxFragment.boxStatusList.size(); j++) {
//                            if (sentinelResourcesGroupItemList.get(i).getId().equals(BoxFragment.boxStatusList.get(j).getID())) {
//                                Logutil.d("哈哈，小于>>>" + sentinelResourcesGroupItemList.get(i).toString());
//                            }
//                        }
//                    }
//                }
//                //
//                // Logutil.d( BoxFragment.boxStatusList.size()+"AAAAAAAAA");
//            }
        }
    }

    /**
     * 预览哨位面部视频的播放器
     */
    NodePlayer preViewNodePlayer;

    /**
     * 预览视频时提示正在加载视频的父布局
     */
    RelativeLayout priViewLoadParentLayout;

    /**
     * 预览视频时哨位名称
     */
    TextView priViewSentinelVideoNameLayout;

    /**
     * 预览视频加载时的动画
     */
    ImageView preViewVideoloadingView;

    /**
     * 当前哨位面部视频的预览框
     */
    private void disPlaySentinelVideoPopuwindow(String rtsp) {
        Logutil.d("setryVideoBean-->>>" + setryVideoBean.toString());

        //加载的View
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_display_sentinel_video_layout, null);
//弹窗对象
        final AlertDialog.Builder builder = new AlertDialog.Builder(App.getApplication());
        builder.setCancelable(false);
        builder.setView(inflate);
        //播放器View
        NodePlayerView nodePlayerView = inflate.findViewById(R.id.popu_prompt_loading_video_view_layout);
        //提示正在加载视频的父布局
        priViewLoadParentLayout = inflate.findViewById(R.id.popu_prompt_loading_parent_layout);
        //显示哨位视频源的名称
        priViewSentinelVideoNameLayout = inflate.findViewById(R.id.popu_prompt_sentinel_name_layout);
        priViewSentinelVideoNameLayout.setText(currentSentinelBean.getName());
        //视频加载动画
        preViewVideoloadingView = inflate.findViewById(R.id.popu_sentinel_loading_icon_layout);

        //初始化播放器
        preViewNodePlayer = new NodePlayer(App.getApplication());
        preViewNodePlayer.setPlayerView(nodePlayerView);
        preViewNodePlayer.setAudioEnable(false);
        preViewNodePlayer.setVideoEnable(true);
        preViewNodePlayer.setInputUrl(rtsp);
        preViewNodePlayer.start();
        preViewNodePlayer.setNodePlayerDelegate(new NodePlayerDelegate() {
            @Override
            public void onEventCallback(NodePlayer player, int event, String msg) {
                if (event == 1001) {
                    handler.sendEmptyMessage(20);
                }
            }
        });
//显示dialog
        final Dialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
        //通过当前的dialog获取window对象
        Window window = dialog.getWindow();
        //设置背景，防止变形
        window.setBackgroundDrawableResource(android.R.color.transparent);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ScreenUtils.getInstance(App.getApplication()).getWidth() - 544;//两边设置的间隙相当于margin
        lp.alpha = 1.0f;
        window.setDimAmount(0.2f);//使用时设置窗口后面的暗淡量
        window.setAttributes(lp);
        preViewVideoloadingView.startAnimation(mLoadingAnim);

        //关闭面部视频的预览
        inflate.findViewById(R.id.popu_prompt_close_btn_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    setryVideoBean = null;
                    handler.sendEmptyMessage(21);
                    preViewNodePlayer.stop();
                    preViewNodePlayer.release();
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 根据View位置显示Popuwindow
     */
    private void showSentinelPopuWindow(View v, int x, SipGroupItemInfoBean
            mSipGroupItemInfoBean) {
        currentSentinelBean = mSipGroupItemInfoBean;
        //  currentSentinelBean.setBean(videoBean);

        //显示View
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.popu_layout, null);
        //popu上显示的哨位名称
        popuSentinelNameLayout = inflate.findViewById(R.id.sentinel_name_layout);
        popuSentinelNameLayout.setText(mSipGroupItemInfoBean.getName());
        //语音通话按键
        voiceCallBtnLayout = inflate.findViewById(R.id.make_sentinel_voice_call_layout);
        //视频电话按键
        popuVideoCallBtnLayout = inflate.findViewById(R.id.make_sentinel_video_call_layout);
        //哨位面部视频
        popuVideoBtnLayout = inflate.findViewById(R.id.sentinel_video_layout);
        //Popuwindow
        window = new PopupWindow(inflate, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //背景
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        window.setOutsideTouchable(true);
        //位置
        window.showAsDropDown(v, 35, -100);
        window.update();
        //三个按键加监听
        voiceCallBtnLayout.setOnClickListener(this);
        popuVideoCallBtnLayout.setOnClickListener(this);
        popuVideoBtnLayout.setOnClickListener(this);

    }

    /**
     * 处理申请供弹请求
     */
    private void handlerOpenBoxInfo(OpenBoxParamater boxbean) {
        //接收到申请供弹时修改选中的效果
        RadioGroup bottomRadioGroupLayout1 = getActivity().findViewById(R.id.bottom_radio_group_layout);
        CustomViewPagerSlide customViewPagerLayout1 = getActivity().findViewById(R.id.main_viewpager_layout);
        bottomRadioGroupLayout1.check(bottomRadioGroupLayout1.getChildAt(3).getId());
        customViewPagerLayout1.setCurrentItem(3);

        //显示报警弹窗
        alarmParentLayout.setVisibility(View.VISIBLE);
        //申请对象添加到集合
        requestOpenBoxQueueList.add(boxbean);
        //谁发起的申请
        String requestIp = boxbean.getSendIp();
        //把打开的哪个弹箱的ID
        String requestOpenBoxId = boxbean.getBoxId();

        String requestDeviceName = "";

        String requestOpenBoxName = "";

        //要播报的内容
        String voiceContent = "";

        //判断字典是否存在
        if (allSipList == null || allSipList.size() == 0) {
            return;
        }
        //遍历查询
        for (int i = 0; i < allSipList.size(); i++) {
            SipBean mSipBean = allSipList.get(i);
            if (mSipBean.getIpAddress().equals(requestIp)) {
                requestDeviceName = mSipBean.getName();
            }
            if (mSipBean.getId().equals(requestOpenBoxId)) {
                requestOpenBoxName = mSipBean.getName();
            }
        }
        if (!isCalling)
            handlerSentryNameLayout.setText(requestDeviceName + "申请打开" + requestOpenBoxName + "弹箱");
        //  voiceContent = requestDeviceName + "申请打开" + requestOpenBoxName + "的子弹箱！";
        // App.startSpeaking(requestIp + "设备申请开启弹箱");

        Logutil.d("requestIp" + requestIp);
        Logutil.d("requestOpenBoxId" + requestOpenBoxId);
        Logutil.d("requestDeviceName" + requestDeviceName);
        Logutil.d("requestOpenBoxName" + requestOpenBoxName);

        //展示供弹申请队列
        if (requestOpenBoxQueueAdapter == null) {
            requestOpenBoxQueueAdapter = new RequestOpenBoxQueueAdapter();
            requestOpenBoxViewLayout.setAdapter(requestOpenBoxQueueAdapter);
        }
        //第一个选中
        requestOpenBoxQueueAdapter.setSelectedItem(0);
        whichOpenBoxPosition = 0;
        //刷新队列
        requestOpenBoxQueueAdapter.notifyDataSetChanged();
        //列表item加监听
        requestOpenBoxViewLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logutil.d("Position-->>" + position);
                whichOpenBoxPosition = position;
                requestOpenBoxQueueAdapter.setSelectedItem(position);
                requestOpenBoxQueueAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 计时线程开启
     */
    public void threadStart() {
        isTimingThreadWork = true;
        if (timingThread != null && timingThread.isAlive()) {
        } else {
            timingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isTimingThreadWork) {

                        try {
                            Thread.sleep(1 * 1000);
                            handler.sendEmptyMessage(9);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            });
            timingThread.start();
        }
    }

    /**
     * 计时线程停止
     */
    public void threadStop() {
        if (isTimingThreadWork) {
            if (timingThread != null && timingThread.isAlive()) {
                timingThread.interrupt();
                timingThread = null;
            }
            timingNumber = 0;
            isTimingThreadWork = false;
            handler.sendEmptyMessage(10);
        }
    }

    /**
     * 刷新右侧表列
     */
    private void refreshQueue() {
        //重新加载事件队列
        initEventData();
        //重新加载已处理的报警队列
        initProcessedAlarmData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //页面可见时
        if (isVisibleToUser) {
            //刷新事件列表
            initEventData();
            //刷新已处理的报警事件
            initProcessedAlarmData();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    /**
     * Sip状态回调
     */
    private void sipStatusCallback() {

        SipService.removePhoneCallback();

        //添加电话状态回调
        SipService.addPhoneCallback(new PhoneCallback() {
            @Override
            public void incomingCall(LinphoneCall linphoneCall) {
                super.incomingCall(linphoneCall);
            }

            @Override
            public void outgoingInit() {
                Logutil.d("向外拨打电话");
                isCalling = true;
                isHandleringAlarm = true;
                super.outgoingInit();
            }

            @Override
            public void callConnected() {
                Logutil.d("电话已接通");
                isCalling = true;
                isHandleringAlarm = true;
                threadStart();
                super.callConnected();
            }

            @Override
            public void callEnd() {
                Logutil.d("电话挂断");
                isHandleringAlarm = false;
                isCalling = false;
                super.callEnd();
            }

            @Override
            public void callReleased() {
                isCalling = false;
                Logutil.d("电话释放");
                isHandleringAlarm = false;
                threadStop();
                super.callReleased();
            }

            @Override
            public void error() {
                isCalling = false;
                threadStop();
                super.error();
            }
        });
    }

    @Override
    public void onDestroyView() {
        //销毁接收报警的广播
        if (mReceiveAlarmBroadcast != null) {
            getActivity().unregisterReceiver(mReceiveAlarmBroadcast);
        }
        //销毁video资源缓存完成的广播
        if (mVideoSourcesBroadcast != null) {
            getActivity().unregisterReceiver(mVideoSourcesBroadcast);
        }
        //销毁sip资源缓存完成的广播
        if (mSipSourcesBroadcast != null) {
            getActivity().unregisterReceiver(mSipSourcesBroadcast);
        }
        //销毁申请开箱的广播
        if (mReceiveBoxBroadcast != null) {
            getActivity().unregisterReceiver(mReceiveBoxBroadcast);
        }
        //注销刷新申请供弹队列的广播
        if (mReceiveKeyBoardRequestOpenAmmoBoxBroadcast != null) {
            getActivity().unregisterReceiver(mReceiveKeyBoardRequestOpenAmmoBoxBroadcast);
        }
        //注销刷新右侧list列表的广播
        if (mReceiveRefreshProcessedAlarmQueueAndEventQueueBroadcast != null) {
            getActivity().unregisterReceiver(mReceiveRefreshProcessedAlarmQueueAndEventQueueBroadcast);
        }
        if (mKeyBoardCloseAlarmBroadcast != null)
            getActivity().unregisterReceiver(mKeyBoardCloseAlarmBroadcast);
        //移除handler监听
        if (handler != null)
            handler.removeCallbacksAndMessages(null);

        super.onDestroyView();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //显示报警地图
                    Bitmap bitmap = (Bitmap) msg.obj;
                    disPlayBackGroupBitmap(bitmap);
                    break;
                case 3:
                    //显示所有的哨位点
                    disPlayAllSentinelPoints();
                    break;
                case 5:
                    //接收到报警时修改选中的效果
                    updateCurrentUi();
                    break;
                case 6:
                    //报警源播放器状态回调
                    int alarmStatusEvent = msg.arg1;
                    alarmPlayerPlayStatusCallback(alarmStatusEvent);
                    break;
                case 7:
                    //加载 locations资源数据
                    String locationsData = (String) msg.obj;
                    handlerLocationsData(locationsData);
                    break;
                case 8:
                    //提示报警源视频无法加载
                    if (getActivity() != null && isVisible()) {
                        loadingView.setVisibility(View.INVISIBLE);
                        loadingView.clearAnimation();
                        loadingTv.setVisibility(View.VISIBLE);
                        loadingTv.setTextSize(12);
                        loadingTv.setText("无视频源...");
                        loadingTv.setTextColor(UIUtils.getColor(R.color.red));
                    }
                    break;
                case 9:
                    //报警时的通话计时
                    timingNumber++;
                    if (isVisible() && getActivity() != null) {
                        handlerSenrtyTimeLayout.setText(TimeUtils.getTime(timingNumber) + "");
                    }
                    break;
                case 10:
                    //通话计时归零
                    if (isVisible() && getActivity() != null) {
                        handlerSenrtyTimeLayout.setText("00:00");
                    }
                    break;
                case 11:
                    if (isVisible() && getActivity() != null)
                        //提示网络异常
                        ToastUtils.showShort("网络异常!");
                    break;
                case 12:
                    if (isVisible() && getActivity() != null)
                        //提示未加载到哨位分组数据
                        //ToastUtils.showShort("未获取到数据!");
                        break;
                case 13:
                    //处理哨位分组数据
                    String sentinelGroupDataResult = (String) msg.obj;
                    handlerSentinelGroupData(sentinelGroupDataResult);
                    break;
                case 14:
                    //展示哨位分组的适配器
                    displaySentinelListAdapter();
                    break;
                case 15:
                    //加载哨位资源分组数据
                    disPlaySentinelResourcesGroupItemAdapter();
                    disPlayAllSentinelPoints();
                    break;
                case 17:
                    //处理申请供弹请求
                    OpenBoxParamater boxbean = (OpenBoxParamater) msg.obj;
                    handlerOpenBoxInfo(boxbean);
                    break;
                case 18:
                    //显示哨位面部视频
                    if (setryVideoBean != null) {
                        String rtsp = setryVideoBean.getRtsp();
                        if (!TextUtils.isEmpty(rtsp)) {
                            disPlaySentinelVideoPopuwindow(rtsp);
                        } else {
                            handler.sendEmptyMessage(22);
                        }
                    } else {
                        handler.sendEmptyMessage(22);
                    }
                    break;
                case 20:
                    //延时一秒后隐藏预览视频的父布局
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    priViewLoadParentLayout.setVisibility(View.GONE);
                    break;
                case 21:
                    //显示预览视频的父布局
                    priViewLoadParentLayout.setVisibility(View.VISIBLE);
                    preViewVideoloadingView.clearAnimation();
                    break;
                case 22:
                    //预览时，提示无视频源
                    if (getActivity() != null && isVisible()) {
                        showProgressFail("未配置视频源！");
                    }
                    break;
                case 23:
                    //提示报警源视频正在加载
                    int event = msg.arg1;
                    alarmCallPlayerPlayStatusCallback(event);
                    break;
                case 24:
                    //提示无报警通话视频
                    if (getActivity() != null && isVisible()) {
                        alarmCallVideoLoadingView.setVisibility(View.INVISIBLE);
                        alarmCallVideoLoadingView.clearAnimation();
                        alarmCallLoadingTv.setVisibility(View.VISIBLE);
                        alarmCallLoadingTv.setTextSize(12);
                        alarmCallLoadingTv.setText("无视频源...");
                        alarmCallLoadingTv.setTextColor(UIUtils.getColor(R.color.red));
                    }
                    break;
                case 27:
                    //刷新事件队列
                    refreshQueue();
                    break;
            }
        }
    };

    /**
     * 报警源播放器状态回调
     */
    private void alarmPlayerPlayStatusCallback(int alarmStatusEvent) {
        //判断是否可见
        if (getActivity() == null || !isVisible()) {
            return;
        }
        //状态回调判断
        if (alarmStatusEvent == 1102) {
            loadingView.setVisibility(View.GONE);
            loadingTv.setVisibility(View.GONE);
            loadingView.clearAnimation();
        } else {
            loadingView.setVisibility(View.VISIBLE);
            loadingView.startAnimation(mLoadingAnim);
            loadingTv.setVisibility(View.VISIBLE);
            loadingTv.setTextSize(12);
            loadingTv.setTextColor(UIUtils.getColor(R.color.red));
            if (alarmStatusEvent == 1000) {
                loadingTv.setText("正在连接...");
            } else if (alarmStatusEvent == 1001) {
                loadingTv.setText("连接成功...");
            } else if (alarmStatusEvent == 1104) {
                loadingTv.setText("切换视频...");
            } else {
                loadingTv.setText("重新连接...");
            }
        }
    }

    /**
     * 报警通话播放器状态回调
     */
    private void alarmCallPlayerPlayStatusCallback(int event) {
        //判断是否可见
        if (getActivity() == null || !isVisible()) {
            return;
        }
        //状态回调判断
        if (event == 1102) {
            alarmCallVideoLoadingView.setVisibility(View.GONE);
            alarmCallLoadingTv.setVisibility(View.GONE);
            alarmCallVideoLoadingView.clearAnimation();
        } else {
            alarmCallVideoLoadingView.setVisibility(View.VISIBLE);
            alarmCallVideoLoadingView.startAnimation(mLoadingAnim);
            alarmCallLoadingTv.setVisibility(View.VISIBLE);
            alarmCallLoadingTv.setTextSize(12);
            alarmCallLoadingTv.setTextColor(UIUtils.getColor(R.color.red));
            if (event == 1000) {
                alarmCallLoadingTv.setText("正在连接...");
            } else if (event == 1001) {
                alarmCallLoadingTv.setText("连接成功...");
            } else if (event == 1104) {
                alarmCallLoadingTv.setText("切换视频...");
            } else {
                alarmCallLoadingTv.setText("重新连接...");
            }
        }
    }


}
