package com.sirint.registrodeinfracoes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends Activity {

    EditText editLogin;
    EditText editSenha;
    ProgressBar progressBar;
    private FirebaseAuth auth;

    private void inicializaComponentes() {
        editLogin = (EditText) findViewById(R.id.login);
        editSenha = (EditText) findViewById(R.id.senha);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_login);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_login);
        }


    }

    public void onProgressBar(boolean visibility){
        progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    private void limpar(){
        editLogin.setText("");
        editSenha.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = Connection.getFirebaseAuth();
    }

    private void alert(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public void entrar(View view) {
        inicializaComponentes();
        if(editLogin.getText().toString().isEmpty()  || editSenha.getText().toString().isEmpty()) {
            alert("Insira os dados e tente novamente!");
        }else{
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm.isActive())
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            onProgressBar(true);
            auth.signInWithEmailAndPassword(editLogin.getText().toString(), editSenha.getText().toString()).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    alert("Login realizado com sucesso!");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                } else {
                    editSenha.setText("");
                    onProgressBar(false);
                    alert("Email ou senha incorreta.");
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void forgotPassword(View view){
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    public void novoUsuario(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
