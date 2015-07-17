package denis.assignment3.app.model.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import denis.assignment3.app.model.mediator.VideoDataMediator;
import denis.assignment3.app.model.mediator.webdata.Video;
import denis.assignment3.app.utils.Constants;

import java.util.Map;

/**
 * Created by denis on 7/17/15.
 */
public class DownloadVideoService extends IntentService {

    public static final String ACTION_DOWNLOAD_SERVICE_RESPONSE =
            "vandy.mooc.services.DownloadVideoService.RESPONSE";

    private static final int NOTIFICATION_ID = 1;
    private VideoDataMediator mVideoMediator;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    public DownloadVideoService(String name) {
        super("DownloadVideoService");
    }

    public DownloadVideoService() {
        super("DownloadVideoService");
    }

    public static Intent makeIntent(Context context, long id) {
        return new Intent(context, DownloadVideoService.class).putExtra(Constants.REMOTE_VIDEO_ID, id);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        startNotification();
        mVideoMediator =  new VideoDataMediator();

        /*finishNotification(mVideoMediator.uploadVideo(getApplicationContext(),
                intent.getData()));

        sendBroadcast();*/
        Map<Long, Video> m = mVideoMediator.getCollection();
        //String notifyWithThis = mVideoMediator.downloadVideo(getApplicationContext(), m.);
    }

    private void sendBroadcast(){
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_DOWNLOAD_SERVICE_RESPONSE).addCategory(Intent.CATEGORY_DEFAULT));
    }

    private void finishNotification(String status) {
        mBuilder.setContentTitle(status)
                .setProgress (0, 0, false)
                .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                .setContentText("")
                .setTicker(status);

        mNotifyManager.notify(NOTIFICATION_ID,
                mBuilder.build());
    }

    private void startNotification() {
        mNotifyManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat
                .Builder(this)
                .setContentTitle("Video Upload")
                .setContentText("Upload in progress")
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setTicker("Uploading video")
                .setProgress(0, 0, true);
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
