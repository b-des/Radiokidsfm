package fm.radiokids.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import fm.radiokids.AboutActivity;
import fm.radiokids.ContactsActivity;
import fm.radiokids.R;
import fm.radiokids.TabsActivity;


public class DetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TabsActivity activity;

    Switch chatSwitch;

    private OnFragmentInteractionListener mListener;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        activity = (TabsActivity) getActivity();


        chatSwitch = view.findViewById(R.id.chat_switch);
        if(activity.preferences.getBoolean("chatIsEnabled")){
            chatSwitch.setChecked(true);
        }else{
            chatSwitch.setChecked(false);
        }

        chatSwitch.setOnCheckedChangeListener(onChatSwitch());



        view.findViewById(R.id.go_youtube).setOnClickListener(onSocialClickListener("youtube"));
        view.findViewById(R.id.go_instagram).setOnClickListener(onSocialClickListener("instagram"));
        view.findViewById(R.id.go_vk).setOnClickListener(onSocialClickListener("vk"));
        view.findViewById(R.id.go_ok).setOnClickListener(onSocialClickListener("ok"));


        view.findViewById(R.id.go_site).setOnClickListener(onPageLinkClickListener("site"));
        view.findViewById(R.id.go_contacts).setOnClickListener(onPageLinkClickListener("contacts"));
        view.findViewById(R.id.go_about).setOnClickListener(onPageLinkClickListener("about"));

        return view;
    }

    public CompoundButton.OnCheckedChangeListener onChatSwitch() {
        return new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    activity.preferences.putBoolean("chatIsEnabled",true);
                }else{
                    activity.preferences.putBoolean("chatIsEnabled",false);
                }

            }
        };
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(activity.preferences.getBoolean("chatIsEnabled")){
            chatSwitch.setChecked(true);
        }else{
            chatSwitch.setChecked(false);
        }
    }

    public  View.OnClickListener onSocialClickListener(final String social){
        return new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent go ;
                switch (social){
                    case "youtube":
                        go = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_youtube)));
                        startActivity(go);
                        break;
                    case "instagram":
                        go = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_instagram)));
                        startActivity(go);
                        break;
                    case "vk":
                        go = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_vk)));
                        startActivity(go);
                        break;
                    case "ok":
                        go = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_ok)));
                        startActivity(go);
                        break;
                }
            }
        };
    }

    public  View.OnClickListener onPageLinkClickListener(final String page){
        return new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent go ;
                switch (page){
                    case "site":
                        go = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_site)));
                        startActivity(go);
                        break;
                    case "contacts":
                        go = new Intent(getActivity(),ContactsActivity.class);
                        startActivity(go);
                        break;
                    case "about":
                        go = new Intent(getActivity(), AboutActivity.class);
                        startActivity(go);
                        break;

                }
            }
        };
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
