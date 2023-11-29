package com.android.safeband.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


import com.android.safebandproject.R;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {
    private DrawerLayout drawerLayout;
    private View drawerView;

    private MapView mapView;
    private ViewGroup mapViewContainer;

    private Button btn_call, btn_announcement, btn_bluetooth, btn_inquriy;
    private ImageButton btn_profile_setting;
    private Intent data;

    Dialog ReCheckDeleteAccount;

    public MainActivity() {
    }
    public static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);

        //메뉴를 눌렀을 때
        ImageButton menu_open = (ImageButton)findViewById(R.id.menu_button);
        menu_open.setOnClickListener(v -> drawerLayout.openDrawer(drawerView));

        drawerView.setOnTouchListener((view, motionEvent) -> true);

        //메뉴 닫기를 눌렀을 때
        ImageButton btn_close = (ImageButton)findViewById(R.id.btn_close);
        btn_close.setOnClickListener(view -> drawerLayout.closeDrawers());

        // 회원 탈퇴를 눌렀을 때
        Button btn_out = findViewById(R.id.btn_out);
        btn_out.setOnClickListener(view -> {
            showDialog01();
        });
        DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() { ///drawer 오픈됐을 때 작동함
            @Override
            public void onDrawerSlide(@NonNull @org.jetbrains.annotations.NotNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull @org.jetbrains.annotations.NotNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull @org.jetbrains.annotations.NotNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        };
        //공지사항 버튼
        btn_announcement = (Button)findViewById(R.id.btn_announcement);
        btn_announcement.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), announcementActivity.class);
            startActivityForResult(intent,REQUEST_CODE);  //intent를 넣어 실행시키게 됩니다.
        });

//        //블루투스 버튼
//        btn_bluetooth = (Button) findViewById(R.id.btn_bluetooth);
//        btn_bluetooth.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Intent intent = new Intent(getApplicationContext(), bluetooth.class);
//                startActivityForResult(intent,REQUEST_CODE);  //intent를 넣어 실행시키게 됩니다.
//            }
//        });

        //문의하기 버튼
        btn_inquriy = (Button)findViewById(R.id.btn_inquiry);
        btn_inquriy.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), inquiryActivity.class);
            startActivityForResult(intent,REQUEST_CODE);  //intent를 넣어 실행시키게 됩니다.
        });

        // 비상연락망
        btn_call = (Button)findViewById(R.id.btn_call);
        btn_call.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), emergencyCallActivity.class);
            startActivityForResult(intent,REQUEST_CODE);  //intent를 넣어 실행시키게 됩니다.
        });

        //프로필 설정
        btn_profile_setting = (ImageButton)findViewById(R.id.btn_profile_setting);
        btn_profile_setting.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), profileSettingActivity.class);
            startActivityForResult(intent,REQUEST_CODE);  //intent를 넣어 실행시키게 됩니다.
        });

        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("키해시는 :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // 권한ID를 가져옵니다
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);

        int permission2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permission3 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED || permission3 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(
                        new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1000);
            }
            return;
        }

        //지도를 띄우자
        // java code

    }


    // 권한 체크 이후로직
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        // READ_PHONE_STATE의 권한 체크 결과를 불러온다
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);
        if (requestCode == 1000) {
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            // 권한 체크에 동의를 하지 않으면 안드로이드 종료
            if (check_result == false) {
                finish();
            }
        }
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    public void showDialog01() {
        ReCheckDeleteAccount.show();
        Objects.requireNonNull(ReCheckDeleteAccount.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 아니요 버튼
        Button noBtn = ReCheckDeleteAccount.findViewById(R.id.noButton);
        noBtn.setOnClickListener(view -> {
            ReCheckDeleteAccount.dismiss(); // 닫기
        });

        // 네 버튼
        Button yesBtn = ReCheckDeleteAccount.findViewById(R.id.yesButton);
        yesBtn.setOnClickListener(view -> {
            // 원하는 기능


            // finish(); // 앱 종료
        });

    }
}