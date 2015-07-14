package denis.assignment3.app.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import denis.assignment3.app.R;
import denis.assignment3.app.common.GenericActivity;
import denis.assignment3.app.presenter.VideoOps;
import denis.assignment3.app.utils.VideoStorageUtils;


public class MainListActivity extends GenericActivity<VideoOps.View, VideoOps> implements denis.assignment3.app.view.VideoChooseDialog.OnVideoSelectedListener, View.OnClickListener {

    private final int REQUEST_VIDEO_CAPTURE = 0;
    private final int REQUEST_GET_VIDEO = 1;

    private static final String KEY_RECORD_VIDEO_URI = "recordedVideoUri";

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.download);
        button.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        new denis.assignment3.app.view.VideoChooseDialog().show(getFragmentManager(), "");
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
}
