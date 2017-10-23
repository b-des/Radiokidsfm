package fm.radiokids;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.brouding.simpledialog.SimpleDialog;
import com.mukesh.tinydb.TinyDB;

import java.util.ArrayList;
import java.util.List;

import fm.radiokids.fragments.ChatFragment;
import fm.radiokids.fragments.DetailsFragment;
import fm.radiokids.fragments.LandChatFragment;
import fm.radiokids.fragments.NewsFragment;
import fm.radiokids.fragments.UserFragment;
import fm.radiokids.fragments.VideoFragment;
import fm.radiokids.interfaces.FragmentCommunicator;


public class TabsActivity extends AppCompatActivity implements VideoFragment.OnFragmentInteractionListener,
ChatFragment.OnFragmentInteractionListener,
UserFragment.OnFragmentInteractionListener,
DetailsFragment.OnFragmentInteractionListener,
LandChatFragment.OnFragmentInteractionListener,
NewsFragment.OnFragmentInteractionListener,
        FragmentCommunicator{

    public final int REQUEST_LOGIN = 1;

    private TabLayout tabLayout;
    public ViewPager viewPager;
    Fragment landChatFragment;
    FragmentTransaction landChatTransaction;

    Fragment videoFragment;
    FragmentTransaction transaction;

    public TinyDB preferences;

    ImageButton btnSwitchChat;

    public FrameLayout chat;
    //Animations
    Animation animationFadeOut;

    public ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);



        //config preferences
        preferences = new TinyDB(this);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        // Create new fragment and transaction
        //fragment for landscape chat
        landChatFragment = new LandChatFragment();
        landChatTransaction = getSupportFragmentManager().beginTransaction();
        landChatTransaction.add(R.id.chat_landscape, landChatFragment, "landscape-chat");
        //landChatTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        landChatTransaction.commit();


        chat = findViewById(R.id.chat_landscape);
        animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);

        //check internet connection
        //if disconnected show dialog
        if (!App.isOnline()) {
            new SimpleDialog.Builder(TabsActivity.this)
                    .setTitle(getString(R.string.text_error))
                    .setBtnConfirmText(getString(R.string.text_close))
                    .setContent(getString(R.string.text_error_network), 3)
                    .show();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Create new fragment and transaction
        //video fragment
        videoFragment = new VideoFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.videoFrame, videoFragment);
        transaction.commit();
    }


    //method for smooth hide chat after 5 seconds
    public void hideChat() {
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE && preferences.getBoolean("chatIsEnabled")) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animationFadeOut = AnimationUtils.loadAnimation(TabsActivity.this, R.anim.fadeout);
                            chat.setAnimation(animationFadeOut);
                            chat.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            };
            thread.start();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

/*
        Fragment lndChatF = getSupportFragmentManager().findFragmentByTag("landscape-chat");
*/

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            //hide action bar
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //hide tabs
            findViewById(R.id.appbar).setVisibility(View.GONE);

            //hide portrait chat
            //findViewById(R.id.viewpager).setVisibility(View.INVISIBLE);

            //show chat if enabled
            //if(preferences.getBoolean("chatIsEnabled")){
                chat.setVisibility(View.VISIBLE);
                //smoothly hide chat after 5 seconds
                //hideChat();
            //}

            //show button shat switch
           // btnSwitchChat.setVisibility(View.VISIBLE);

           /* if(lndChatF == null){
                landChatTransaction.add(R.id.chat_landscape, landChatFragment, "landscape-chat");
                landChatTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                landChatTransaction.commit();
            }*/

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

            //show action bar
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //show tabs
            findViewById(R.id.appbar).setVisibility(View.VISIBLE);

            //show portrait chat
            findViewById(R.id.viewpager).setVisibility(View.VISIBLE);

            //hide chat
            //chat.setVisibility(View.INVISIBLE);
            chat.setVisibility(View.GONE);


            //remove landscape chat fragment
            //landChatTransaction.detach(landChatFragment);
           /* if(lndChatF instanceof LandChatFragment){
                landChatTransaction.remove(landChatFragment);
                landChatTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                landChatTransaction.commit();
            }*/

        }


    }


    public void setupTabIcons() {
        View myCustomIcon ;

        myCustomIcon =  getLayoutInflater().inflate(R.layout.custom_tab, null);
        myCustomIcon.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_chat_logo);
        tabLayout.getTabAt(0).setCustomView(myCustomIcon);


        myCustomIcon =  getLayoutInflater().inflate(R.layout.custom_tab, null);
        myCustomIcon.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_news_logo);
        tabLayout.getTabAt(1).setCustomView(myCustomIcon);


        myCustomIcon =  getLayoutInflater().inflate(R.layout.custom_tab, null);
        myCustomIcon.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_user_logo);
        tabLayout.getTabAt(2).setCustomView(myCustomIcon);


        myCustomIcon =  getLayoutInflater().inflate(R.layout.custom_tab, null);
        myCustomIcon.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_menu_logo);
        tabLayout.getTabAt(3).setCustomView(myCustomIcon);


    }


    public void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new ChatFragment(), "");
        adapter.addFragment(new NewsFragment(), "");
        adapter.addFragment(new UserFragment(), "");
        adapter.addFragment(new DetailsFragment(), "");
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    @Override
    public void updateChat() {
        ViewPagerAdapter adapter = ((ViewPagerAdapter)viewPager.getAdapter());
        ChatFragment fragment = (ChatFragment) adapter.getItem(0);
        fragment.updateChat();


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public int getItemPosition(Object object) {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }


}