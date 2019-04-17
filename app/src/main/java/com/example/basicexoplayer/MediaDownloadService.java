package com.example.basicexoplayer;

import android.app.Notification;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadManager.TaskState;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationUtil;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import com.struct.red.alltolearn.Utils.ConstantUtil;

import static com.struct.red.alltolearn.Utils.ConstantUtil.DOWNLOAD_CHANNEL_ID;
import static com.struct.red.alltolearn.Utils.ConstantUtil.DOWNLOAD_NOTIFICATION_ID;


public class MediaDownloadService extends DownloadService {

    public static String TAG="MediaDownloadService";

    private static final int FOREGROUND_NOTIFICATION_ID = 1;
    public MediaDownloadService() {
        super(
                DOWNLOAD_NOTIFICATION_ID,
                DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
                DOWNLOAD_CHANNEL_ID,
                R.string.download_channel_name);
    }



    @Override
    protected DownloadManager getDownloadManager() {
        return ((MyApplication) getApplication()).getDownloadManager();
    }

    @Nullable
    @Override
    protected Scheduler getScheduler() {
        return null;
    }

    @Override
    protected Notification getForegroundNotification(TaskState[] taskStates) {

//        float totalPercentage = 0;
//        int downloadTaskCount = 0;
//        boolean allDownloadPercentagesUnknown = true;
//        boolean haveDownloadedBytes = false;
//        boolean haveDownloadTasks = false;
//        boolean haveRemoveTasks = false;
//        for (TaskState taskState : taskStates) {
//            if (taskState.state != TaskState.STATE_STARTED
//                    && taskState.state != TaskState.STATE_COMPLETED) {
//                continue;
//            }
//            if (taskState.action.isRemoveAction) {
//                haveRemoveTasks = true;
//                continue;
//            }
//            haveDownloadTasks = true;
//            if (taskState.downloadPercentage != C.PERCENTAGE_UNSET) {
//                allDownloadPercentagesUnknown = false;
//                totalPercentage += taskState.downloadPercentage;
//            }
//            haveDownloadedBytes |= taskState.downloadedBytes > 0;
//            downloadTaskCount++;
//        }
//
//        int progress = 0;
//        boolean indeterminate = true;
//        if (haveDownloadTasks) {
//            progress = (int) (totalPercentage / downloadTaskCount);
//            indeterminate = allDownloadPercentagesUnknown && haveDownloadedBytes;
//
//            Log.e(TAG,"notifi "+progress);
//            sendIntent(progress);
//        }
        return DownloadNotificationUtil.buildProgressNotification(
                this,
                R.drawable.exo_icon_play,
                DOWNLOAD_CHANNEL_ID,
                null,
                "downloadingg",
                taskStates);
    }

    private void sendIntent(int progress){
        Intent intent = new Intent(ConstantUtil.MESSAGE_PROGRESS);
        intent.putExtra("progress",progress);
        LocalBroadcastManager.getInstance(MediaDownloadService.this).sendBroadcast(intent);
    }

    @Override
    protected void onTaskStateChanged(TaskState taskState) {
        if (taskState.action.isRemoveAction) {
            return;
        }

        Notification notification = null;
        if (taskState.state == TaskState.STATE_COMPLETED) {
            Log.e(TAG,"STATE_COMPLETED");
            notification =
                    DownloadNotificationUtil.buildDownloadCompletedNotification(
                            /* context= */ this,
                            R.drawable.exo_controls_play,
                            DOWNLOAD_CHANNEL_ID,
                            /* contentIntent= */ null,
                            Util.fromUtf8Bytes(taskState.action.data));
        } else if (taskState.state == TaskState.STATE_FAILED) {
            Log.e(TAG,"STATE_FAILED");
            notification =
                    DownloadNotificationUtil.buildDownloadFailedNotification(
                            /* context= */ this,
                            R.drawable.exo_controls_play,
                            DOWNLOAD_CHANNEL_ID,
                            /* contentIntent= */ null,
                            Util.fromUtf8Bytes(taskState.action.data));
        }
        int notificationId = FOREGROUND_NOTIFICATION_ID + 1 + taskState.taskId;
        NotificationUtil.setNotification(this, notificationId, notification);
    }
}
