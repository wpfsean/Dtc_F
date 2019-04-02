package com.tehike.client.dtc.single.app.project.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tehike.client.dtc.single.app.project.App;
import com.tehike.client.dtc.single.app.project.R;
import com.tehike.client.dtc.single.app.project.global.AppConfig;
import com.tehike.client.dtc.single.app.project.ui.DtcDutyLoginActivity;
import com.tehike.client.dtc.single.app.project.utils.UIUtils;

import java.util.UUID;

/**
 * 描述：开机自启动（测试用）
 * ===============================
 * @author wpfse wpfsean@126.com
 * @Create at:2018/12/16 19:55
 * @version V1.0
 */

public class BootBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AppConfig.APP_UPDATE_ACTION)) {
            Intent i = new Intent(context, DtcDutyLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            App.startSpeaking(UIUtils.getString(R.string.str_device_start_sucuess));
        }
    }
}