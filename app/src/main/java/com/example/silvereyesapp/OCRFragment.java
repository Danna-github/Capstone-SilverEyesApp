package com.example.silvereyesapp;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class OCRFragment extends Fragment {
    final String TAG = getClass().getSimpleName();
    Button cameraBtn, btnToggleCamera,btn_speech;

    private TessBaseAPI mTess; //Tess API reference
    String datapath = ""; //언어 데이터가 있는 경로


    Bitmap image;
    TextView edit_readText;
    private TextToSpeech textToSpeech;

    private CameraView cameraView;
    private static final int INPUT_SIZE = 224;
    private ImageView imageViewResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ocr, container, false);

        cameraBtn = (Button) view.findViewById(R.id.camera_button);
        btn_speech = (Button) view.findViewById(R.id.tts_button);
        edit_readText = (TextView) view.findViewById(R.id.OCRTextView);
        edit_readText.setMovementMethod(new ScrollingMovementMethod());
        cameraView = (CameraView) view.findViewById(R.id.cameraView);
        imageViewResult = view.findViewById(R.id.imageView);

        /*
        imageView = (ImageView) view.findViewById(R.id.imageview);
        BitmapDrawable drawable = (BitmapDrawable)((ImageView) view.findViewById(R.id.imageview)).getDrawable();
        image = drawable.getBitmap();

        //이미지 디코딩을 위한 초기화
        image = BitmapFactory.decodeResource(getResources(), R.drawable.sample);
        */

        //데이터 경로
        datapath = getContext().getFilesDir() + "/tessaract/";

        //한글 & 영어 데이터 체크
        checkFile(new File(datapath + "tessdata/"));

        //Tesseract API
        String lang = "eng";

        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);


        //TTS를 생성하고 OnlnitListener로 초기화 한다.
        textToSpeech = new TextToSpeech(getContext().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    //작업 성공
                    int language = textToSpeech.setLanguage(Locale.US); //언어 설정
                    if(language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language is not supported!");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    //작업 실패
                    Toast.makeText(getContext().getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {
            }

            @Override
            public void onError(CameraKitError cameraKitError) {
            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                Bitmap bitmap = cameraKitImage.getBitmap();
                imageViewResult.setImageBitmap(bitmap);
                //bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true);

                //image = BitmapFactory.decodeResource(getResources(), bitmap.getGenerationId());

                String OCRresult = null;
                mTess.setImage(bitmap);
                OCRresult = mTess.getUTF8Text();
                edit_readText.setText(OCRresult);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (imageViewResult.getVisibility() == View.GONE) {
                    imageViewResult.setImageResource(R.drawable.ic_camera);
                    cameraView.captureImage();
                    imageViewResult.setVisibility(View.VISIBLE);
                    cameraView.setVisibility(View.GONE);
                    imageViewResult.bringToFront();
                } else if (imageViewResult.getVisibility() == View.VISIBLE) {
                    imageViewResult.setVisibility(View.GONE);
                    cameraView.setVisibility(View.VISIBLE);
                    edit_readText.setText("");
                }
            }
        });

        btn_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = edit_readText.getText().toString();
                int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
                if(speechStatus == TextToSpeech.ERROR){
                    Log.e("TTS", "Error in converting Text to Speech!");
                }
            }
        });

        return view;
    }

    //Tesseract API//
    //Process an Image 문자 인식 및 결과 출력
    public void processImage(View view) {
        Toast.makeText(getActivity().getApplicationContext(),"이미지가 복잡할 경우 해석 시 많은 시간이 소요될 수 있습니다.", Toast.LENGTH_LONG).show();
        String OCRresult = null;
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        //TextView OCRTextView = (TextView) view.findViewById(R.id.OCRTextView);
        edit_readText.setText(OCRresult);
    }

    //copy file to device 파일복제
    private void copyFiles() {
        try{
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = getContext().getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //check file on the device 파일 존재 확인
    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if(!dir.exists()&& dir.mkdirs()) {
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if(!datafile.exists()) {
                copyFiles();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.stop();
    }
}
