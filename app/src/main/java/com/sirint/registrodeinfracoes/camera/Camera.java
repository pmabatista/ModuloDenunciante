package com.sirint.registrodeinfracoes.camera;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sirint.registrodeinfracoes.R;
import com.sirint.registrodeinfracoes.RegistryActivity;
import com.wonderkiln.camerakit.CameraView;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import butterknife.OnClick;
import butterknife.Unbinder;

public class Camera extends Fragment {

    private CameraView cameraKitView;
    @BindView(R.id.mRecordVideo)
    ImageView mRecordVideo;
    @BindView(R.id.mRegistrar)
    ImageView mRegistrar;
    @BindView(R.id.preview)
    ImageView preview;
    private boolean capturing = false;
    private String video;
    List<String> files = new ArrayList<>();
    Unbinder unbinder;
    int pos =0;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        cameraKitView = (CameraView) view.findViewById(R.id.camera);
    }

    public static Camera newInstance() {
        Camera fragment = new Camera();
        return fragment;
    }

    public List<String> getFiles() {
        return files;
    }

    @Override
    public void onStart() {
        super.onStart();
        requestPermission();
        cameraKitView.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraKitView.start();
    }

    @Override
    public void onPause() {
        cameraKitView.stop();
        super.onPause();
    }

    @Override
    public void onStop() {
        cameraKitView.stop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @OnClick({R.id.mRecordVideo, R.id.mRegistrar, R.id.preview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mRecordVideo:
                if(capturing == false) {
                    if(getFiles().size() < 3) {
                        startCapture();
                    }else{
                        Toast.makeText(getContext(), "Você já atingiu o limite de vídeos.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    stopCapture();
                }
                break;
            case R.id.mRegistrar:
                if (getFiles().size() >= 1) {
                    Intent intent = new Intent(getActivity(), RegistryActivity.class);
                    intent.putExtra("Links", (Serializable) getFiles());
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Grave pelo menos um vídeo.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.preview:
                if(files.size()>0) {
                    pos= 0;
                    final Dialog dialog = new Dialog(getContext());// add here your class name
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.videoplay);//add your own xml with defied with and height of videoview
                    dialog.show();
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    dialog.getWindow().setAttributes(lp);
                    final VideoView videoView = (VideoView) dialog.findViewById(R.id.videoPlay);
                    final Button mRemover = (Button) dialog.findViewById(R.id.mRemover);
                    final Button mOk = (Button) dialog.findViewById(R.id.mOk);
                    final Button mPlay = (Button) dialog.findViewById(R.id.mPlay);
                    final Button mProximo = (Button) dialog.findViewById(R.id.mProximo);
                    mOk.setOnClickListener(view1 -> dialog.dismiss());
                    mRemover.setOnClickListener(view12 -> {
                        files.remove(getFiles().get(pos));
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(files.get(getFiles().size()-1));
                        Bitmap bitmap = retriever.getFrameAtTime(1);
                        preview.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 85, 85, false));
                        dialog.dismiss();
                    });
                    mPlay.setOnClickListener(view13 -> {
                        if(videoView.isPlaying()){
                            videoView.pause();
                        }else{
                            videoView.start();
                        }
                    });
                    mProximo.setOnClickListener(view14 -> {
                        if(pos+1 > files.size()-1){
                            pos = 0;
                            videoView.setVideoURI(Uri.parse(getFiles().get(pos)));
                            videoView.start();
                        }else{
                            pos++;
                            videoView.setVideoURI(Uri.parse(getFiles().get(pos)));
                            videoView.start();
                        }
                    });
                    videoView.setVideoURI(Uri.parse(getFiles().get(pos)));
                    videoView.start();
                    MediaController mediaController = new MediaController(getActivity());
                    videoView.setMediaController(mediaController);
                }
                break;
        }
    }

    public void startCapture (){

        File folder = new File(Environment.getExternalStorageDirectory() + "/Videos");
        File file = new File(folder.getAbsolutePath(), "sirint" + System.currentTimeMillis() + ".mp4");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        cameraKitView.captureVideo(file);
        video = String.valueOf(file);
        mRecordVideo.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
        capturing = true;
    }

    public void stopCapture(){
        cameraKitView.stopVideo();
        mRecordVideo.setImageDrawable(getResources().getDrawable(R.drawable.ic_record));
        capturing = false;
        files.add(video);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(video);
        Bitmap bitmap = retriever.getFrameAtTime(1);
        preview.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 85, 85, false));
    }


    public void requestPermission() {
        Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted or not
                    }
                    @Override

                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> Toast.makeText(getActivity().getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }



}