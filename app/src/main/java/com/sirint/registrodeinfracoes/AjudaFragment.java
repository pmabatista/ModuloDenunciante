package com.sirint.registrodeinfracoes;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sirint.registrodeinfracoes.camera.Camera;

public class AjudaFragment extends Fragment {

    public AjudaFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ajuda, container, false);
    }

    public static AjudaFragment newInstance() {
        AjudaFragment fragment = new AjudaFragment();
        return fragment;
    }

}
