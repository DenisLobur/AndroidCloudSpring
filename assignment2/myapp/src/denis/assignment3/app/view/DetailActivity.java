package denis.assignment3.app.view;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RatingBar;
import com.example.myapp.R;
import denis.assignment3.app.common.GenericActivity;
import denis.assignment3.app.presenter.VideoOps;
import denis.assignment3.app.utils.Constants;
import denis.assignment3.app.view.ui.FloatingActionButton;
import denis.assignment3.app.view.ui.VideoAdapter;

/**
 * Created by denis on 7/13/15.
 */
public class DetailActivity extends GenericActivity<VideoOps.View, VideoOps> implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        SurfaceHolder.Callback,
        VideoOps.View {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private FloatingActionButton mPlayButton;
    private FloatingActionButton mDownloadButton;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private String mPath;
    private Bundle mExtras;
    private boolean mIsVideoReadyToBePlayed;
    private int mVideoWidth;
    private int mVideoHeight;
    private boolean mIsVideoSizeKnown = false;
    private static final String MEDIA = "media";
    private RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_detail);

        mSurfaceView = (SurfaceView)findViewById(R.id.video_view);
        mSurfaceHolder = mSurfaceView.getHolder();
        mRatingBar = (RatingBar) findViewById(R.id.video_rating);
        mSurfaceHolder.addCallback(this);
        mExtras = getIntent().getExtras();
        createPlayButton();
        createDownloadButton();

        super.onCreate(savedInstanceState, VideoOps.class, this);
    }

    @SuppressWarnings("deprecation")
    private void createPlayButton() {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();

        mPlayButton = new FloatingActionButton
                .Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_play_black_24dp))
                .withButtonColor(getResources().getColor(R.color.fab))
                .withGravity(Gravity.RIGHT | Gravity.BOTTOM)
                .withMargins(0,0,50,0)
                .create();

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startVideoPlayback();
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void createDownloadButton() {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();

        mDownloadButton = new FloatingActionButton
                .Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_cloud_download_black_24dp))
                .withButtonColor(getResources().getColor(R.color.fab))
                .withGravity(Gravity.LEFT| Gravity.BOTTOM)
                .withMargins(50,0,0,0)
                .create();

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getOps().downloadVideo(mExtras.getLong(Constants.REMOTE_VIDEO_ID));
            }
        });
    }

    private void playVideo(Integer media) {
        doCleanUp();
        try {
            mPath = mExtras.getString(Constants.LOCAL_VIDEO_URI);

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mPath);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated called");
        playVideo(mExtras.getInt(MEDIA));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged called");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed called");
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.v(TAG, "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            //startVideoPlayback();
        }
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            //startVideoPlayback();
        }
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }


    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback");
        mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
        mMediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void setAdapter(VideoAdapter videoAdapter) {

    }
}
