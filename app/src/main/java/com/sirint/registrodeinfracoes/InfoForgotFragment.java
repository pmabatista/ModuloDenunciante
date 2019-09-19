package com.sirint.registrodeinfracoes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class InfoForgotFragment extends Fragment {

    @BindView(R.id.mVoltar)
    ImageView mVoltar;
    @BindView(R.id.txt_info_forgot)
    TextView txtInfoForgot;
    private FirebaseAuth auth;
    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_forgot, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Bundle mBundle;
        mBundle = getArguments();
        String email = mBundle.getString("email");
        txtInfoForgot.setText("Foi enviado para o seu e-mail " + email + ", um link para criar uma nova senha.");
    }

    @Override
    public void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
    }



    @OnClick({R.id.mVoltar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mVoltar:
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }
}
