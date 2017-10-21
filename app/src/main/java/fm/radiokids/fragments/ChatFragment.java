package fm.radiokids.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fm.radiokids.App;
import fm.radiokids.LoginActivity;
import fm.radiokids.R;
import fm.radiokids.TabsActivity;
import fm.radiokids.adapters.ChatAdapter;
import fm.radiokids.classes.SmileyTools;
import fm.radiokids.models.ChatModel;
import fm.radiokids.models.Message;
import fm.radiokids.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class ChatFragment extends Fragment {

    //chat
    ArrayList<ChatModel> dataModels;
    ListView listView;
    public static ChatAdapter adapter;
    LinearLayout chat;
    TabsActivity activity;
    EditText messageInput;
    ImageButton btnSend;

    boolean inProcessCheckMessage = false;


    private OnFragmentInteractionListener mListener;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        activity = (TabsActivity) getActivity();

        //setup chat messages list
        listView = view.findViewById(R.id.chat_list);
        dataModels = new ArrayList<>();
        adapter = new ChatAdapter(dataModels, getActivity().getApplicationContext());
        listView.setAdapter(adapter);
        //load last messages of the chat
        loadLastMessages();
        setUpChatUpdate();
        messageInput = view.findViewById(R.id.message_input);
        btnSend = view.findViewById(R.id.btn_send);

        view.findViewById(R.id.btn_smiley).setOnClickListener(onEmoticonsClickListener(view));
        view.findViewById(R.id.btn_send).setOnClickListener(onClickSendMessageButton());
        view.findViewById(R.id.btn_call).setOnClickListener(onClickCallButton());

        messageInput.addTextChangedListener(onTextChangeListener(btnSend));
        view.findViewById(R.id.btn_login_chat).setOnClickListener(onLoginChatButtonClickListener());

        if (activity.preferences.contains("user")) {
            view.findViewById(R.id.message_input_group).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btn_login_chat).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.message_input_group).setVisibility(View.GONE);
            view.findViewById(R.id.btn_login_chat).setVisibility(View.VISIBLE);
        }


        return view;
    }

    private View.OnClickListener onLoginChatButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(login, activity.REQUEST_LOGIN);
            }
        };
    }

    private TextWatcher onTextChangeListener(final ImageButton btn) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    btn.setImageResource(R.drawable.ic_send_active);
                } else {
                    btn.setImageResource(R.drawable.ic_send_inactive);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
    }


    /**
     * Change listener
     * #target input message field
     *
     * @return
     */
    private View.OnClickListener onEmoticonsClickListener(final View layout) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater ltInflater = getLayoutInflater();
                SmileyTools.showSmileyChoosePopup(getActivity(), getContext(), ltInflater, layout);
            }
        };
    }

    /**
     * Click listener
     * #target Send message button
     *
     * @return
     */
    public View.OnClickListener onClickSendMessageButton() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String message = messageInput.getText().toString();
                User user = (User) activity.preferences.getObject("user", User.class);
                messageInput.setText("");
                messageInput.clearFocus();
                if (!message.equals("")) {
                    App.getApi().sendMessage(user.getId(), message, user.getAvatar()).enqueue(new Callback<ChatModel>() {
                        @Override
                        public void onResponse(Call<ChatModel> call, Response<ChatModel> response) {
                        }

                        @Override
                        public void onFailure(Call<ChatModel> call, Throwable t) {
                            Toast.makeText(getContext(), getString(R.string.text_error), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        };
    }

    /**
     * Click listener
     * #target call to skype button
     *
     * @return
     */
    private View.OnClickListener onClickCallButton() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent sky = new Intent("android.intent.action.VIEW");
                    sky.setData(Uri.parse("skype:" + getString(R.string.settings_skype_name)));
                    startActivity(sky);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), getString(R.string.text_skype_dismiss), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    //load last messages
    private void loadLastMessages() {
        App.getApi().getMessages(0).enqueue(new Callback<List<ChatModel>>() {
            @Override
            public void onResponse(Call<List<ChatModel>> call, Response<List<ChatModel>> response) {
                try {
                    dataModels.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    listView.setSelection(adapter.getCount() - 1);

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(Call<List<ChatModel>> call, Throwable t) {
            }
        });
    }

    //update messages in the chat every 2 seconds
    public void setUpChatUpdate() {

        Timer myTimer = new Timer();
        final Handler uiHandler = new Handler();

        myTimer.schedule(new TimerTask() {// Определяем задачу
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
                                        dataModels.addAll(response.body());
                                        adapter.notifyDataSetChanged();
                                        listView.setSelection(adapter.getCount() - 1);
                                        ChatModel message = response.body().get(response.body().size() - 1);
                                        activity.preferences.putLong("lastCheck", Long.valueOf(message.geTime()));

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
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() - 1);
    }

    public void updateChat() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    listView.setSelection(adapter.getCount() - 1);
                } catch (Exception ignored) {
                }
            }
        }, 1000);

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == activity.REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {

                User user = (User) intent.getSerializableExtra("user");
                activity.preferences.putObject("user", user);
                // FragmentTransaction ft = getFragmentManager().beginTransaction();
                //ft.detach(this).attach(this).commit();
                activity.setupViewPager(activity.viewPager);
                activity.setupTabIcons();
                Toast.makeText(getContext(), getString(R.string.text_authorization_successful), Toast.LENGTH_LONG).show();
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
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
