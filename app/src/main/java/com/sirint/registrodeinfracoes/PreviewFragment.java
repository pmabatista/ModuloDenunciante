package com.sirint.registrodeinfracoes;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PreviewFragment extends Fragment{

    List<String> links = new ArrayList<>();
    Unbinder unbinder;
    ListView listView;
    @BindView(R.id.mVoltarPreview)
    ImageView mVoltar;
    List<Bitmap> bitmap = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
        listView = (ListView) view.findViewById(R.id.listview);
        links = (List<String>) getArguments().getSerializable("Links");
        unbinder = ButterKnife.bind(this, view);
        setVideos();
        return view;
    }

    public void setVideos(){
        for (String link : links) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(link);
            Bitmap preview = retriever.getFrameAtTime(1000);
            bitmap.add(preview);
        }
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

    }

    @OnClick({R.id.mVoltarPreview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mVoltarPreview:
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(this);
                ft.commit();
            break;
        }

    }




    public static PreviewFragment newInstance() {
        PreviewFragment fragment = new PreviewFragment();
        return fragment;
    }


    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bitmap.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }



        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.customlayoutpreview,null);
            ImageView delete = (ImageView) view.findViewById(R.id.mDelete);
            ImageView video = (ImageView) view.findViewById(R.id.video);
            video.setImageBitmap((bitmap.get(i)));
            view.setOnClickListener(view1 -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.videoplay, null))
                        .setPositiveButton("OK", (dialogInterface, i12) -> {

                        })
                        .setNegativeButton("EXCLUIR", (dialogInterface, i1) -> links.remove(i1));
                builder.create();
            });
            return view;
        }
    }





}
