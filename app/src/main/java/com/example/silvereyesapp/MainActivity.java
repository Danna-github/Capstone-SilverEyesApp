package com.example.silvereyesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private OCRFragment OCRfrag;
    private TensorflowFragment Tensorflowfrag;
    private MessageFragment Mypagefrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.mainactivity_bottomnavigationview);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_tensor:
                        setFrag(0);
                        break;
                    case R.id.action_ocr:
                        setFrag(1);
                        break;
                    case R.id.action_mypage:
                        setFrag(2);
                        break;
                }
                return true;
            }
        });
        OCRfrag = new OCRFragment();
        Tensorflowfrag = new TensorflowFragment();
        Mypagefrag = new MessageFragment();
        setFrag(1); //첫 Fragment 화면을 무엇으로 지정해줄 것인지 선택

    }

    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n){
            case 0:
                ft.replace(R.id.mainactivity_framelayout, Tensorflowfrag).commit();
                break;
            case 1:
                ft.replace(R.id.mainactivity_framelayout, OCRfrag).commit();
                break;
            case 2:
                ft.replace(R.id.mainactivity_framelayout, Mypagefrag).commit();
                break;
        }
    }

}
