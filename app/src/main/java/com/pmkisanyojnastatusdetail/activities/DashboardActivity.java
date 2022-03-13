package com.pmkisanyojnastatusdetail.activities;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pmkisanyojnastatusdetail.BuildConfig;
import com.pmkisanyojnastatusdetail.R;
import com.pmkisanyojnastatusdetail.activities.PrivacyActivity;
import com.pmkisanyojnastatusdetail.activities.UpdateProfileActivity;
import com.pmkisanyojnastatusdetail.activities.ui.main.SectionsPagerAdapter;
import com.pmkisanyojnastatusdetail.databinding.ActivityDashboardBinding;
import com.pmkisanyojnastatusdetail.fragments.NewsFragment;
import com.pmkisanyojnastatusdetail.fragments.OthersFragment;
import com.pmkisanyojnastatusdetail.fragments.YojanaFragment;
import com.pmkisanyojnastatusdetail.models.ModelFactory;
import com.pmkisanyojnastatusdetail.models.PageViewModel;
import com.pmkisanyojnastatusdetail.models.ProfileModel;
import com.pmkisanyojnastatusdetail.utils.CommonMethod;
import com.pmkisanyojnastatusdetail.utils.MyReceiver;
import com.pmkisanyojnastatusdetail.utils.Prevalent;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String BroadCastStringForAction = "checkingInternet";
    private static final float END_SCALE = 0.7f;
    public static int count_ad = 1;
    TextView versionCode;
    CircleImageView userProfileImg;
    ActivityDashboardBinding binding;
    int count = 1;
    ImageView navMenu;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout categoryContainer;
    SectionsPagerAdapter sectionsPagerAdapter;
    ConstraintLayout userProfileLayout;
    TextView txtUserName;
    ImageView headerImage, editImg;
    ViewPager viewPager;
    String id, userImage;
    PageViewModel pageViewModel;

    Map<String, String> map = new HashMap<>();
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadCastStringForAction)) {
                if (intent.getStringExtra("online_status").equals("true")) {

                    Set_Visibility_ON();
                    count++;
                } else {
                    Set_Visibility_OFF();
                }
            }
        }
    };
    private IntentFilter intentFilter;

    public static void shareApp(Context context) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void Set_Visibility_ON() {
        binding.lottieHome.setVisibility(View.GONE);
        binding.tvNotConnected.setVisibility(View.GONE);
        binding.viewPager.setVisibility(View.VISIBLE);
        binding.tabs.setVisibility(View.VISIBLE);
        enableNavItems();
        if (count == 2) {
            viewPager.setAdapter(sectionsPagerAdapter);
            TabLayout tabs = binding.tabs;
            tabs.setupWithViewPager(viewPager);
            navigationDrawer();
        }
    }

    private void Set_Visibility_OFF() {
        binding.lottieHome.setVisibility(View.VISIBLE);
        binding.tvNotConnected.setVisibility(View.VISIBLE);
        binding.viewPager.setVisibility(View.GONE);
        binding.tabs.setVisibility(View.GONE);
        disableNavItems();
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        versionCode = binding.versionCode;
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        sectionsPagerAdapter.addFragment(new NewsFragment(), getString(R.string.tab_text_2));
        sectionsPagerAdapter.addFragment(new YojanaFragment(), getString(R.string.tab_text_1));
        sectionsPagerAdapter.addFragment(new OthersFragment(), getString(R.string.tab_text_3));
        viewPager = binding.viewPager;
        navigationView = binding.navigation;
        navMenu = binding.navMenu;
        drawerLayout = binding.drawerLayout;
        Paper.init(this);
        CommonMethod.interstitialAds(this);

        id = Paper.book().read(Prevalent.userId);

        if (id != null) {
            map.put("id", id);
            pageViewModel = new ViewModelProvider(this, new ModelFactory(this.getApplication(), map)).get(PageViewModel.class);
        }

        binding.lottieEmail.setOnClickListener(view -> CommonMethod.contactUs(DashboardActivity.this));
        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, MyReceiver.class);
        startService(serviceIntent);
        if (isOnline(getApplicationContext())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Set_Visibility_ON();
            }
        } else {
            Set_Visibility_OFF();
        }

        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            String version = pInfo.versionName;
            versionCode.setText("Version : " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void navigationDrawer() {
        navigationView = findViewById(R.id.navigation);
        navigationView.bringToFront();
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(DashboardActivity.this);
        navigationView.setCheckedItem(R.id.nav_home);
        categoryContainer = findViewById(R.id.container_lay);
        userProfileImg = navigationView.findViewById(R.id.user_profile_img);
        txtUserName = navigationView.findViewById(R.id.txt_user_name);
        headerImage = navigationView.findViewById(R.id.header_image);
        navigationView.setCheckedItem(R.id.nav_home);

        userProfileLayout = navigationView.findViewById(R.id.user_profile_layout);
        editImg = navigationView.findViewById(R.id.edit_img);


        if (id != null) {
            setImage(userProfileImg, txtUserName, headerImage, userProfileLayout, editImg);
            userProfileLayout.setVisibility(View.VISIBLE);
            headerImage.setVisibility(View.GONE);
        } else {
            userProfileLayout.setVisibility(View.GONE);
            headerImage.setVisibility(View.VISIBLE);
        }
        navMenu.setOnClickListener(v -> {

            id = Paper.book().read(Prevalent.userId);
            if (id != null) {
                setImage(userProfileImg, txtUserName, headerImage, userProfileLayout, editImg);
                userProfileLayout.setVisibility(View.VISIBLE);
                headerImage.setVisibility(View.GONE);
            } else {
                userProfileLayout.setVisibility(View.GONE);
                headerImage.setVisibility(View.VISIBLE);
            }
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else drawerLayout.openDrawer(GravityCompat.START);
        });
        animateNavigationDrawer();
    }

    private void setImage(CircleImageView userProfileImg, TextView uname, ImageView headerImage, ConstraintLayout userProfileLayout, ImageView editImg) {
        pageViewModel = new ViewModelProvider(this, new ModelFactory(this.getApplication()
                , map)).get(PageViewModel.class);
        pageViewModel.getUserData().observe(this, profileModelList -> {
            if (!profileModelList.getData().isEmpty()) {
                for (ProfileModel pm : profileModelList.getData()) {
                    uname.setText(pm.getUserName().trim());
                    userImage = pm.getUserImage();
                    Glide.with(this).load(
                            "https://gedgetsworld.in/PM_Kisan_Yojana/User_Profile_Images/"
                                    + userImage).into(userProfileImg);

                }
                userProfileLayout.setVisibility(View.VISIBLE);
                headerImage.setVisibility(View.GONE);
            }
        });
        userProfileImg.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, UpdateProfileActivity.class);
            intent.putExtra("img", userImage);
            startActivity(intent);
        });
        editImg.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, UpdateProfileActivity.class);
            intent.putExtra("img", userImage);
            startActivity(intent);
        });
    }

    private void animateNavigationDrawer() {
        drawerLayout.setScrimColor(Color.parseColor("#DEE4EA"));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                categoryContainer.setScaleX(offsetScale);
                categoryContainer.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = categoryContainer.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                categoryContainer.setTranslationX(xTranslation);
            }
        });
    }

    public boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(DashboardActivity.this, DashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.nav_contact:
                CommonMethod.contactUs(DashboardActivity.this);
                break;
            case R.id.nav_rate:
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                CommonMethod.rateApp(getApplicationContext());
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Rate");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Rate click");
                mFirebaseAnalytics.logEvent("Selected_rate_menu_item", bundle);
                break;
            case R.id.nav_privacy:
                startActivity(new Intent(DashboardActivity.this, PrivacyActivity.class));
                break;
            case R.id.nav_share:
                shareApp(this);
                break;
            default:
        }
        return true;
    }

    public void disableNavItems() {
        Menu navMenu = navigationView.getMenu();
        MenuItem nav_insta = navMenu.findItem(R.id.nav_share);
        nav_insta.setEnabled(false);

        MenuItem nav_policy = navMenu.findItem(R.id.nav_privacy);
        nav_policy.setEnabled(false);

        MenuItem nav_home = navMenu.findItem(R.id.nav_home);
        nav_home.setEnabled(false);

        MenuItem nav_rate = navMenu.findItem(R.id.nav_rate);
        nav_rate.setEnabled(false);

        MenuItem nav_contact = navMenu.findItem(R.id.nav_contact);
        nav_contact.setEnabled(false);
    }

    public void enableNavItems() {
        Menu navMenu = navigationView.getMenu();
        MenuItem nav_insta = navMenu.findItem(R.id.nav_share);
        nav_insta.setEnabled(true);

        MenuItem nav_policy = navMenu.findItem(R.id.nav_privacy);
        nav_policy.setEnabled(true);

        MenuItem nav_home = navMenu.findItem(R.id.nav_home);
        nav_home.setEnabled(true);

        MenuItem nav_rate = navMenu.findItem(R.id.nav_rate);
        nav_rate.setEnabled(true);

        MenuItem nav_contact = navMenu.findItem(R.id.nav_contact);
        nav_contact.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this,R.style.CustomAlertDialog);
        builder.setTitle(R.string.app_name)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Do You Really Want To Exit?\nAlso Rate Us 5 Star.")
                .setNeutralButton("CANCEL", (dialog, which) -> {
                });


        builder.setNegativeButton("RATE APP", (dialog, which) -> CommonMethod.rateApp(getApplicationContext()))
                .setPositiveButton("OK!!", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    moveTaskToBack(true);
                    System.exit(0);

                });
        builder.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}