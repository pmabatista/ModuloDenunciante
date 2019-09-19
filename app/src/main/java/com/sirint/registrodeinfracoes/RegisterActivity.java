package com.sirint.registrodeinfracoes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sirint.registrodeinfracoes.jsinespclient.Checkin;
import com.sirint.registrodeinfracoes.jsinespclient.SinespApi;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends Activity {

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private EditText editLogin;
    private EditText editSenha;
    private EditText editNome;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void inicializaComponentes() {
        editLogin = (EditText) findViewById(R.id.login_novo);
        editSenha = (EditText) findViewById(R.id.senha_novo);
        editNome = (EditText) findViewById(R.id.nome_usuario);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_cadastro);
    }

    public void onProgressBar(boolean visibility) {
        progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = Connection.getFirebaseAuth();
    }

    public void cadastrar(View view) {
        inicializaComponentes();
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        onProgressBar(true);
        String login = editLogin.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();
        String nome = editNome.getText().toString().trim();
        SinespApi sinespApi = new SinespApi();
        Checkin checkin = sinespApi.FireCheckin();
        try{
            Thread.sleep(10000);
        }catch(InterruptedException ex){
        }

        String fToken = sinespApi.FirebaseAuth(checkin.getAndroid_id(), checkin.getSecurity_token());
        criarUser(login, senha, nome, fToken);
    }

    private void limpar() {
        editNome.setText("");
        editLogin.setText("");
        editSenha.setText("");
    }


    private void criarUser(String login, String senha, String nome, String fToken) {
        auth.createUserWithEmailAndPassword(login, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String key = task.getResult().getUser().getUid();
                        Usuario usuario = new Usuario(nome, login, fToken);
                        Map<String, Object> usuarioValues = usuario.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("modulousuario/usuarios/" + key, usuarioValues);
                        ref.updateChildren(childUpdates);
                        limpar();
                        onProgressBar(false);
                        alert("UsuĂ¡rio cadastrado com sucesso.");

                    } else {
                        limpar();
                        onProgressBar(false);
                        alert("Erro no cadastro!");
                    }
                });
    }

    private void alert(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void voltarLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
