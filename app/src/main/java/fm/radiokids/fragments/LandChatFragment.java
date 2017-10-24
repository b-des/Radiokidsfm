package fm.radiokids.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fm.radiokids.App;
import fm.radiokids.R;
import fm.radiokids.TabsActivity;
import fm.radiokids.adapters.ChatAdapter;
import fm.radiokids.models.ChatModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LandChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //chat
    ArrayList<ChatModel> dataModels;
    ListView chatList;
    private static ChatAdapter adapter;
    TabsActivity activity;
    LinearLayout chat;
    boolean inProcessCheckMessage = false;
    boolean chatIsVisible = true;

    Animation animationFadeOut;
    ImageButton btnSwitchChat;
    Thread thread;
    private OnFragmentInteractionListener mListener;
    Timer myTimer;

    public LandChatFragment() {
        // Required empty public constructor
    }

    public static LandChatFragment newInstance(String param1, String param2) {
        LandChatFragment fragment = new LandChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_land_chat, container, false);
        activity = (TabsActivity) getActivity();


        //setup chat messages list
        chatList = view.findViewById(R.id.land_chat_list);


        dataModels = new ArrayList<>();
        adapter = new ChatAdapter(dataModels, getActivity().getApplicationContext());
        chatList.setAdapter(adapter);
        chatList.setSelection(dataModels.size() - 1);

        //chatList.setOnScrollListener(onScrollChatListener());
        // chatList.setOnTouchListener(onTouchChatListener());
        //load last messages of the chat
       loadLastMessages();

        //setUpChatUpdate();
        //hideChat();

        btnSwitchChat = view.findViewById(R.id.btn_switch_chat);
        if (activity.preferences.getBoolean("chatIsEnabled")) {
            chatList.setVisibility(View.VISIBLE);
            btnSwitchChat.setImageResource(R.drawable.ic_visibility_white_24dp);
        } else {
            chatList.setVisibility(View.INVISIBLE);
            btnSwitchChat.setImageResource(R.drawable.ic_visibility_off_white_24dp);
        }
        btnSwitchChat.setOnClickListener(onSwitchChatButtonListener());

        return view;
    }

    private void loadLastMessages() {
        App.getApi().getMessages(0).enqueue(new Callback<List<ChatModel>>() {
            @Override
            public void onResponse(Call<List<ChatModel>> call, Response<List<ChatModel>> response) {
                try {
                    dataModels.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    chatList.setSelection(dataModels.size() - 1);
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(Call<List<ChatModel>> call, Throwable t) {
            }
        });
    }


    private View.OnTouchListener onTouchChatListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Toast.makeText(activity, "TOUCH", Toast.LENGTH_SHORT).show();
                if (!thread.isInterrupted()) {
                    thread.interrupt();
                    chatList.setVisibility(View.VISIBLE);
                    hideChat();
                }

                return false;
            }
        };
    }


    private View.OnClickListener onSwitchChatButtonListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (activity.preferences.getBoolean("chatIsEnabled")) {
                    chatList.setVisibility(View.INVISIBLE);
                    btnSwitchChat.setImageResource(R.drawable.ic_visibility_off_white_24dp);
                    activity.preferences.putBoolean("chatIsEnabled", false);

                } else {
                    chatList.setVisibility(View.VISIBLE);
                    btnSwitchChat.setImageResource(R.drawable.ic_visibility_white_24dp);
                    activity.preferences.putBoolean("chatIsEnabled", true);
                    hideChat();

                    //if chat is visible
                    //send command to hide after 5 seconds
                    //chat.setVisibility(View.INVISIBLE);
                    //chatIsVisible = false;
                }
            }
        };
    }

    //display/hide chat in landscape mode
    public void switchChat() {
        //if chat is showing
        //hide the chat and change button icon
        if (activity.preferences.getBoolean("chatIsEnabled")) {
            chatList.setVisibility(View.INVISIBLE);
            btnSwitchChat.setImageResource(R.drawable.ic_visibility_off_white_24dp);
            activity.preferences.putBoolean("chatIsEnabled", false);

        } else {
            chatList.setVisibility(View.VISIBLE);
            btnSwitchChat.setImageResource(R.drawable.ic_visibility_white_24dp);
            activity.preferences.putBoolean("chatIsEnabled", true);

            //if chat is visible
            //send command to hide after 5 seconds
            //hideChat();
            //chat.setVisibility(View.INVISIBLE);
            //chatIsVisible = false;
        }
    }


    //method for smooth hide chat after 5 seconds
    public void hideChat() {
        if (activity.preferences.getBoolean("chatIsEnabled")) {
            thread = new Thread() {
                @Override
                public void run() {
                    //Thread.currentThread().interrupt();
                    //if (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(5000);
                        if (!Thread.currentThread().isInterrupted()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Thread.currentThread().interrupt();
                                    animationFadeOut = AnimationUtils.loadAnimation(activity, R.anim.fadeout);
                                    chatList.setAnimation(animationFadeOut);
                                    chatList.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                    }
                    // Thread.currentThread().interrupt();

                    //if (!Thread.currentThread().isInterrupted()) {

                    // }
                    //}

                }
            };
            thread.start();
        }
    }

    //update messages in the chat every 2 seconds
    public void setUpChatUpdate() {

        myTimer = new Timer();
        final Handler uiHandler = new Handler();

        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!inProcessCheckMessage) {
                            inProcessCheckMessage = true;
                            App.getApi().getMessages(activity.preferences.getLong("lastCheck", 0)).enqueue(new Callback<List<ChatModel>>() {
                                @Override
                                public void onResponse(Call<List<ChatModel>> call, Response<List<ChatModel>> response) {
                                    if (response.body().size() != 0) {

                                        if (activity.preferences.getBoolean("chatIsEnabled")) {
                                            chatList.setVisibility(View.VISIBLE);
                                            chatIsVisible = true;
                                        }

                                        dataModels.addAll(response.body());
                                        adapter.notifyDataSetChanged();
                                        chatList.setSelection(adapter.getCount() - 1);
                                        //activity.preferences.putLong("lastCheck", System.currentTimeMillis() / 1000);
                                        ChatModel message = response.body().get(response.body().size() - 1);
                                        activity.preferences.putLong("lastCheck", Long.valueOf(message.geTime()));

                                        //Toast.makeText(activity,"has msg",Toast.LENGTH_LONG).show();
                                        //if chat is visible
                                        //send command to hide after 5 seconds
                                        if (chatIsVisible) {
                                            hideChat();
                                            chatIsVisible = false;
                                        }

                                    }
                                    inProcessCheckMessage = false;
                                }

                                @Override
                                public void onFailure(Call<List<ChatModel>> call, Throwable t) {
                                    inProcessCheckMessage = false;
                                }
                            });
                        }

                    }
                });
            }
        }, 0L, 1L * 1000);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            loadLastMessages();
            setUpChatUpdate();
            chatList.setVisibility(View.VISIBLE);
            hideChat();
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            if(myTimer != null){
                myTimer.cancel();
                myTimer.purge();
                myTimer = null;
            }

            if(thread != null){
                thread.interrupt();
                thread = null;
            }
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
        //Toast.makeText(getContext(),"DETACH",Toast.LENGTH_LONG).show();
        if(myTimer != null){
            myTimer.cancel();
            myTimer.purge();
            myTimer = null;
        }

        if(thread != null){
            thread.interrupt();
        }

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}