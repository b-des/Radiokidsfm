package fm.radiokids.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brouding.simpledialog.SimpleDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mukesh.tinydb.TinyDB;

import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.radiokids.App;
import fm.radiokids.LoginActivity;
import fm.radiokids.R;
import fm.radiokids.TabsActivity;
import fm.radiokids.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.app.Activity.RESULT_CANCELED;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageView avatar;

    CardView user_card_short;
    CardView user_card_full;
    CardView user_card_login;
    ImageView logout;
    LinearLayout btn_sign_in;
    LinearLayout btn_change_password;

    TabsActivity activity;

    private OnFragmentInteractionListener mListener;


    public UserFragment() {
        // Required empty public constructor
    }




    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        activity = (TabsActivity) getActivity();

        user_card_short = view.findViewById(R.id.user_card_short);
        user_card_full = view.findViewById(R.id.user_card_full);
        user_card_login = view.findViewById(R.id.user_card_login);

        checkAndShowUserInfo(view);

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(onLogoutClickListener());

        btn_sign_in = view.findViewById(R.id.btn_sign_in);
        btn_sign_in.setOnClickListener(onSignInClickListener());

        btn_change_password = view.findViewById(R.id.btn_change_password);
        btn_change_password.setOnClickListener(onChangePasswordButtonClickListener());

        return view;
    }

    private View.OnClickListener onChangePasswordButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
               final SimpleDialog dialog = new SimpleDialog.Builder(getActivity())
                        .setTitle(getString(R.string.text_title_change_password))
                        .setCustomView(R.layout.change_password_dialog)

                        .setBtnCancelText(getString(R.string.text_cancel), false)
                        .setBtnCancelTextColor("#555555")
                        .setBtnConfirmText("")
                        .show();

                        Button change = dialog.findViewById(R.id.change);
                        change.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EditText oldPassField = dialog.findViewById(R.id.old_pass);
                                EditText newPassField = dialog.findViewById(R.id.new_pass);
                                EditText confirmPassField = dialog.findViewById(R.id.confirm_pass);

                                String oldPass = oldPassField.getText().toString();
                                String newPass = newPassField.getText().toString();
                                String confirmPass = confirmPassField.getText().toString();



                                if(oldPass.equals("")){
                                    Toast.makeText(getActivity(),getString(R.string.text_enter_old_password),Toast.LENGTH_LONG).show();
                                }else if(newPass.equals("") || confirmPass.equals("")){
                                    Toast.makeText(getActivity(),getString(R.string.text_passwords_are_empty),Toast.LENGTH_LONG).show();
                                }else if(!newPass.equals(confirmPass)){
                                    Toast.makeText(getActivity(),getString(R.string.text_passwords_not_equal),Toast.LENGTH_LONG).show();
                                }else{
                                    User user = (User) activity.preferences.getObject("user",User.class);
                                    App.getApi().changePassword(user.getId(),oldPass,newPass).enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            if(response.body().toString().equals("ok")){
                                                Toast.makeText(getActivity(),getString(R.string.text_passwords_successful_changed),Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            }else{
                                                Toast.makeText(getActivity(),getString(R.string.text_error),Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            Toast.makeText(getActivity(),getString(R.string.text_error_network),Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        });
            }
        };
    }

    private View.OnClickListener onSignInClickListener() {
        return  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(login,activity.REQUEST_LOGIN);
            }
        };
    }


    public void checkAndShowUserInfo(View view) {
        if(activity.preferences.contains("user")){

            user_card_short.setVisibility(View.VISIBLE);
            user_card_full.setVisibility(View.VISIBLE);
            user_card_login.setVisibility(View.GONE);

            User user = (User) activity.preferences.getObject("user",User.class);

            ImageView avatar = view.findViewById(R.id.avatar);
            TextView nick = view.findViewById(R.id.nick);
            TextView nickname = view.findViewById(R.id.nickname);
            TextView name = view.findViewById(R.id.first_name);
            TextView email = view.findViewById(R.id.email);

            Glide.with(getContext()).load(user.getAvatar()).apply(RequestOptions.circleCropTransform()).into(avatar);
            nick.setText(user.getNickname());
            nickname.setText(user.getNickname());
            name.setText(user.getName());
            email.setText(user.getEmail());

        }else{
            user_card_short.setVisibility(View.GONE);
            user_card_full.setVisibility(View.GONE);
            user_card_login.setVisibility(View.VISIBLE);
        }
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

    private View.OnClickListener onLogoutClickListener() {
        return  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(view);
            }
        };
    }

    public void logout(View view){
        activity.preferences.remove("user");
        activity.setupViewPager(activity.viewPager);
        activity.setupTabIcons();
        activity.viewPager.setCurrentItem(2);
        checkAndShowUserInfo(view);
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
