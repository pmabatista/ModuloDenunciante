package com.sirint.registrodeinfracoes.camera;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sirint.registrodeinfracoes.ConsultaFragment;
import com.sirint.registrodeinfracoes.MainActivity;
import com.sirint.registrodeinfracoes.PreviewFragment;
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
                    startCapture();
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
                }
                break;
            case R.id.preview:
                Fragment fragment = new PreviewFragment();
                Bundle args = new Bundle();
                args.putSerializable("Links", (Serializable) getFiles());
                fragment.setArguments(args);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.fragment_content, fragment);
                ft.commit();
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
        Bitmap bitmap = retriever.getFrameAtTime(1000);
        preview.setImageBitmap(bitmap);
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