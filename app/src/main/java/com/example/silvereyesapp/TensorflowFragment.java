package com.example.silvereyesapp;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class TensorflowFragment extends Fragment {
    private static final String MODEL_PATH = "mobilenet_quant_v1_224.tflite";
    private static final boolean QUANT = true;
    private static final String LABEL_PATH = "labels.txt";
    private static final int INPUT_SIZE = 224;

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textViewResult;
    private TextToSpeech textToSpeech;
    private Button btnDetectObject,btnToggleCamera, btn_speech;
    private CameraView cameraView;
    private ImageView imageViewResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tensorflow, container, false);
        cameraView = (CameraView) view.findViewById(R.id.cameraView);
        textViewResult = view.findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());
        btnDetectObject = view.findViewById(R.id.btnDetectObject);
        btn_speech = view.findViewById(R.id.btn_speech);
        imageViewResult = view.findViewById(R.id.imageView);

        //tts 기능
        //TTS를 생성하고 OnlnitListener로 초기화 한다.
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
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
                    Toast.makeText(getActivity(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //tts 기능

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
                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
                imageViewResult.setImageBitmap(bitmap);
                final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
                String tmp = results.toString();
                String object = tmp.substring(6,tmp.indexOf(")")+1);
                textViewResult.setText(object);

                String data = textViewResult.getText().toString();
                int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
                if(speechStatus == TextToSpeech.ERROR){
                    Log.e("TTS", "Error in converting Text to Speech!");
                }

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {
            }
        });

        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageViewResult.getVisibility() == View.GONE) {
                    imageViewResult.setImageResource(R.drawable.ic_camera);
                    cameraView.captureImage();
                    imageViewResult.setVisibility(View.VISIBLE);
                    cameraView.setVisibility(View.GONE);
                    imageViewResult.bringToFront();
                }else if (imageViewResult.getVisibility() == View.VISIBLE) {
                    imageViewResult.setVisibility(View.GONE);
                    cameraView.setVisibility(View.VISIBLE);
                    textViewResult.setText("");
                }
            }
        });


        btn_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = textViewResult.getText().toString();
                int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);

                if(speechStatus == TextToSpeech.ERROR){
                    Log.e("TTS", "Error in converting Text to Speech!");
                }
            }
        });

        initTensorFlowAndLoadModel();
        return view;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getContext().getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE,
                            QUANT);
                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    private void makeButtonVisible() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDetectObject.setVisibility(View.VISIBLE);
            }
        });
    }
}
