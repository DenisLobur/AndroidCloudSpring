package denis.assignment3.app.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.example.myapp.R;

/**
 * Created by denis on 7/14/15.
 */
public class VideoChooseDialog extends DialogFragment {

    public enum OperationType {
        VIDEO_GALLERY,
        VIDEO_RECORD
    }

    private String [] items = {
            "Video Gallery",
            "Record a Video"
    };

    private Context mContext;
    private OnVideoSelectedListener mListener;


    public interface OnVideoSelectedListener {
        void onVideoSelected(OperationType which);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnVideoSelectedListener)activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.toString() + " must implement OnVideoSelectedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_upload_video)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VideoChooseDialog.OperationType type = VideoChooseDialog.OperationType.values()[which];
                        mListener.onVideoSelected(type);
                    }
                });
        return dialog.create();
    }
}
