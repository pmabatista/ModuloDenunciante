package com.sirint.registrodeinfracoes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import butterknife.OnClick;

public class ProfileFragment extends Fragment {

    TextView textLogin;
    TextView textID;
    TextView status_email;
    private FirebaseUser user = Connection.getFirebaseUser();
    private boolean email_verificado;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    SharedPreferences sharedPreferences;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        inicializarcomponentes(view);
        verificarPreferences();
        //checkStatusEmail();
        return view;
    }

    public void inicializarcomponentes(View view){
        textLogin = (TextView) view.findViewById(R.id.txt_usuario);
        textID = (TextView) view.findViewById(R.id.txt_id);
        status_email = (TextView) view.findViewById(R.id.status_email);
    }


    @Override
    public void onStart() {
        super.onStart();
        checkStatusEmail();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("ResourceAsColor")
    private void checkStatusEmail(){
        if(email_verificado) {
            status_email.setTextColor(Color.GREEN);
            status_email.setText("Email verificado.");
        }else{
            status_email.setTextColor(Color.RED);
            status_email.setText("Email não verificado, para verificar, clique aqui!");
        }
    }

    public void verificarPreferences(){
        sharedPreferences = getActivity().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);
        textID.setText(sharedPreferences.getString("nome_usuario", ""));
        textLogin.setText(sharedPreferences.getString("email_usuario", ""));
        status_email.setText(String.valueOf(sharedPreferences.getBoolean("email_verificado", true)));
    }


    public void logout(){
        Connection.logOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


    @OnClick({R.id.status_email,R.id.logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.status_email:
                if(!user.isEmailVerified()){
                    user.sendEmailVerification().addOnCompleteListener(task -> Toast.makeText(getContext(),"Email enviado para:" + user.getEmail(),Toast.LENGTH_SHORT).show());
                }
                break;
            case R.id.logout:
                logout();
                break;
        }
    }


}