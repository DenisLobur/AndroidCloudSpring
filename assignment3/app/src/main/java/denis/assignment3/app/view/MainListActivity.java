package denis.assignment3.app.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import denis.assignment3.app.R;
import denis.assignment3.app.common.GenericActivity;
import denis.assignment3.app.common.Utils;
import denis.assignment3.app.model.services.UploadVideoService;
import denis.assignment3.app.presenter.VideoOps;
import denis.assignment3.app.utils.VideoStorageUtils;
import denis.assignment3.app.view.ui.FloatingActionButton;
import denis.assignment3.app.view.ui.VideoAdapter;


public class MainListActivity extends GenericActivity<VideoOps.View, VideoOps> implements denis.assignment3.app.view.VideoChooseDialog.OnVideoSelectedListener, View.OnClickListener, VideoOps.View {

    private final int REQUEST_VIDEO_CAPTURE = 0;
    private final int REQUEST_GET_VIDEO = 1;

    private static final String KEY_RECORD_VIDEO_URI = "recordedVideoUri";

    private Button button;
    private FloatingActionButton mUploadVideoButton;
    private ListView mVideoList;
    private Uri mRecordVideoUri;
    private UploadResultReceiver mUploadResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        mVideoList = (ListView)findViewById(R.id.video_list);
        createUploadButton();
        super.onCreate(savedInstanceState, VideoOps.class, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    private void registerReceiver() {

        // Create an Intent filter that handles Intents from the
        // UploadVideoService.
        IntentFilter intentFilter =
                new IntentFilter(UploadVideoService.ACTION_UPLOAD_SERVICE_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Register the BroadcastReceiver.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mUploadResultReceiver,
                        intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUploadResultReceiver);
    }

    private class UploadResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getOps().getVideoList();
        }
    }

    @Override
    public void onClick(View v) {
        new denis.assignment3.app.view.VideoChooseDialog().show(getFragmentManager(), "");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri videoUri = null;

        // Check if the Result is Ok and upload the Video to the Video
        // Service.
        if (resultCode == Activity.RESULT_OK) {
            // Video picked from the Gallery.
            if (requestCode == REQUEST_GET_VIDEO)
                videoUri = data.getData();

                // Video is recorded.
            else if (requestCode == REQUEST_VIDEO_CAPTURE)
                videoUri = mRecordVideoUri;

            if (videoUri != null){
                Utils.showToast(this, "Uploading video");

                // Upload the Video.
                getOps().uploadVideo(videoUri);
            }
        }

        // Pop a toast if we couldn't get a video to upload.
        if (videoUri == null)
            Utils.showToast(this, "Could not get video to upload");
    }

    @SuppressWarnings("deprecation")
    private void createUploadButton() {
        final DisplayMetrics metrics =
                getResources().getDisplayMetrics();
        final int position =
                (metrics.widthPixels / 4) + 5;

        // Create Floating Action Button using the Builder pattern.
        mUploadVideoButton =
                new FloatingActionButton
                        .Builder(this)
                        .withDrawable(getResources()
                                .getDrawable(R.drawable.abc_ic_go_search_api_mtrl_alpha))
                        .withButtonColor(getResources()
                                .getColor(R.color.button_material_light))
                        .withGravity(Gravity.BOTTOM | Gravity.END)
                        .withMargins(0,
                                0,
                                position,
                                0)
                        .create();

        // Show the UploadVideoDialog Fragment when user clicks the
        // button.
        mUploadVideoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new denis.assignment3.app.view.VideoChooseDialog().show(getFragmentManager(), "uploadVideo");
            }
        });
    }

    @Override
    public void onVideoSelected(denis.assignment3.app.view.VideoChooseDialog.OperationType which) {
        switch(which) {
            case VIDEO_GALLERY:
                final Intent videoGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT)
                                .setType("video/*")
                                .putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                if (videoGalleryIntent.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(videoGalleryIntent,
                            REQUEST_GET_VIDEO);
                break;
            case VIDEO_RECORD:
                Uri mRecordVideoUri = VideoStorageUtils.getRecordedVideoUri(getApplicationContext());
                getRetainedFragmentManager().put(KEY_RECORD_VIDEO_URI, mRecordVideoUri);

                final Intent recordVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                                .putExtra(MediaStore.EXTRA_OUTPUT, mRecordVideoUri);

                if (recordVideoIntent.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(recordVideoIntent,
                            REQUEST_VIDEO_CAPTURE);
                break;
        }
    }

    @Override
    public void setAdapter(VideoAdapter videoAdapter) {
        mVideoList.setAdapter(videoAdapter);
    }

    @Override
    public void finish() {
        super.finish();
    }
}
