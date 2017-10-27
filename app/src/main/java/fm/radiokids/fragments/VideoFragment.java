package fm.radiokids.fragments;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.brouding.simpledialog.SimpleDialog;

import fm.radiokids.R;
import fm.radiokids.TabsActivity;
import fm.radiokids.interfaces.FragmentCommunicator;


public class VideoFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        //String streamUrl = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_10mb.mp4";

 /*       final SimpleDialog loader =  new SimpleDialog.Builder(getContext())
                .setContent(getString(R.string.text_loading), 7)
                .showProgress(true)
                .setBtnCancelText(getString(R.string.text_close))
                .setBtnCancelTextColor("#2861b0")
                .show();*/

        String streamUrl = getString(R.string.settings_stream_url);
        final VideoView videoView =  view.findViewById(R.id.video);
        videoView.setVideoPath(streamUrl);
        videoView.requestFocus();
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                FragmentCommunicator inter = (FragmentCommunicator) getActivity();
               // loader.dismiss();
                inter.updateChat();
            }
        });

        //on player error listener
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return true;
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        //void onLoadVideo();
    }
}
