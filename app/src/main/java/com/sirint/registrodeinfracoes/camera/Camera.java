package com.sirint.registrodeinfracoes.camera;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.sirint.registrodeinfracoes.R;
import com.sirint.registrodeinfracoes.RegistryActivity;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraView;
import com.wonderkiln.camerakit.Facing;

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
    private boolean capturing = false;
    private String video;
    private boolean hasFlash;
    private ImageView flashIcon;
    List<String> files = new ArrayList<>();
    Unbinder unbinder;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        cameraKitView = (CameraView) view.findViewById(R.id.camera);
        flashIcon = (ImageView) view.findViewById(R.id.flash);
        checkFlash();

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
        cameraKitView.start();
        checkFlash();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraKitView.start();
        checkFlash();
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

    @OnClick({R.id.mRecordVideo, R.id.mRegistrar, R.id.flash, R.id.change_cam})
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
            case R.id.flash:
                changeFlash(view);
                break;
            case R.id.change_cam:
                rotateCamera(view);
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
    }

    public void rotateCamera(View view) {
        if (capturing){
            Toast.makeText(getContext(), "Can't rotate Camera during recording !", Toast.LENGTH_SHORT).show();
        } else  {

            if (cameraKitView.getFacing() == CameraKit.Constants.FACING_BACK){
                cameraKitView.setFacing(CameraKit.Constants.FACING_FRONT);

            }else {
                cameraKitView.setFacing(CameraKit.Constants.FACING_BACK);

            }
        }

    }

    private void checkFlash() {

        hasFlash = getContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {

            Toast.makeText(getContext(), "Flash not supported on this device !", Toast.LENGTH_SHORT).show();
            flashIcon.setVisibility(View.GONE);

        } else {

            flashIcon.setVisibility(View.VISIBLE);
            if (cameraKitView.getFlash()== CameraKit.Constants.FLASH_ON) {
                flashIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_auto));
                cameraKitView.setFlash(CameraKit.Constants.FLASH_AUTO);
            } else if (cameraKitView.getFlash() == CameraKit.Constants.FLASH_AUTO) {
                flashIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off));
                cameraKitView.setFlash(CameraKit.Constants.FLASH_OFF);
            } else {
                flashIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on));
                cameraKitView.setFlash(CameraKit.Constants.FLASH_ON);
            }
        }

    }

    public void changeFlash(View view) {

        if (capturing){
            Toast.makeText(getContext(), "Can't change flash mode during recording !", Toast.LENGTH_SHORT).show();
        } else {
            if (cameraKitView.getFlash()== CameraKit.Constants.FLASH_ON) {
                flashIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_auto));
                cameraKitView.setFlash(CameraKit.Constants.FLASH_AUTO);
            } else if (cameraKitView.getFlash() == CameraKit.Constants.FLASH_AUTO) {
                flashIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off));
                cameraKitView.setFlash(CameraKit.Constants.FLASH_OFF);

            } else {
                flashIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on));
                cameraKitView.setFlash(CameraKit.Constants.FLASH_ON);
            }
        }


    }
}