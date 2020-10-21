package com.example.silvereyesapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class MypageFragment extends Fragment {

    //asking permission
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    static TextView messageTV, numberTV;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        messageTV = view.findViewById(R.id.message_text);
        numberTV = view.findViewById(R.id.message_phone);

        //check if the permission is not granted
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECEIVE_SMS)){
                //Do nothing as user has denied
            } else {
                //a pop up will appear asking for required permission i.e Allow or Deny
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch(requestCode){
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS:{
                //check whether the length of grantResults is greater than 0 and is equal to PERMISSION_GRANTED
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //Now broadcastreceiver will work in background
                    Toast.makeText(getContext(),"Thankyou for permitting!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Well I can't do anything untill you permit me.", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

}
