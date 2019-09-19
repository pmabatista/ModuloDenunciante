package com.sirint.registrodeinfracoes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ForgotPasswordFragment extends Fragment {

    @BindView(R.id.mForgotPassword)
    ImageView mForgotPassword;
    @BindView(R.id.forgotEmail)
    EditText editForgot;
    private FirebaseAuth auth;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        auth = Connection.getFirebaseAuth();
    }

    private void alert(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void novaSenha(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    Fragment infoForgotFragment = new InfoForgotFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("email", editForgot.getText().toString().trim());
                    infoForgotFragment.setArguments(bundle);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragment_forgot, infoForgotFragment);
                    ft.commit();
                })
                .addOnFailureListener(e -> alert(String.valueOf(e)));
    }

    @OnClick({R.id.mForgotPassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mForgotPassword:
                novaSenha(editForgot.getText().toString().trim());
                break;
        }
    }
}
