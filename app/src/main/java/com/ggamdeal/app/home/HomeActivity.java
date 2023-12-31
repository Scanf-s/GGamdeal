package com.ggamdeal.app.home;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.ggamdeal.app.R;
import com.ggamdeal.app.community.CommunityFragment;
import com.ggamdeal.app.news.NewsFragment;
import com.ggamdeal.app.wishlist.WishlistFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeActivity extends AppCompatActivity{
    private static final int BACK_PRESS_INTERVAL = 2000;
    private long backPressedTime;
    private BottomNavigationView navigationBarView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FirebaseUser currentUser;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        navigationBarView = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        toolbar = findViewById(R.id.toolbar);

        transferTo(HomeFragment.newInstance("param1", "param2"));

        navigationBarView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.item_home) {
                transferTo(HomeFragment.newInstance("param1", "param2"));
                return true;
            }

            if (itemId == R.id.item_community) {
                transferTo(CommunityFragment.newInstance("param1", "param2"));
                return true;
            }

            if (itemId == R.id.item_wishlist) {
                transferTo(WishlistFragment.newInstance("param1", "param2"));
                return true;
            }

            if (itemId == R.id.item_news) {
                transferTo(NewsFragment.newInstance("param1", "param2"));
                return true;
            }
            return false;
        });

        navigationBarView.setOnItemReselectedListener(item -> {
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.item_myinfo) {
                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
            if (id == R.id.item_logout) {
                Log.d("DrawerINFO", "로그아웃");
                signOut();
            }
            return false;
        });

        // Firebase Authentication 사용자 프로필 변경 리스너
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            FirebaseUser updatedUser = firebaseAuth.getCurrentUser();
            if (updatedUser != null) {
                updateNavHeader(updatedUser);
            }
        });

        //툴바, Drawer 설정
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        //Drawer 상단 사용자 이미지 및 이메일 정보 설정
        if (currentUser != null) {
            updateNavHeader(currentUser);
        }
    }

    private void transferTo(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        if (firebaseAuth.getCurrentUser() == null) {
            Log.d("Firebase", "signOut:success");
            finish();
        }
        else Log.d("Firebase", "signOut:failure");
    }

    private void updateNavHeader(FirebaseUser user) {
        if (isDestroyed()) {
            return;
        }
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.username);
        ImageView userProfileImage = headerView.findViewById(R.id.iv_image);

        Glide.with(this).load(user.getPhotoUrl()).into(userProfileImage);
        userName.setText(user.getEmail());
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + BACK_PRESS_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            moveTaskToBack(true);
            finishAndRemoveTask();
            System.exit(0);
            return;
        } else {
            Toast.makeText(getBaseContext(), "정말 종료하시겠습니까", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}