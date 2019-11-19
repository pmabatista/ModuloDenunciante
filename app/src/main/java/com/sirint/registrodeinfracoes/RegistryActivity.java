package com.sirint.registrodeinfracoes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sirint.registrodeinfracoes.jsinespclient.Result;
import com.sirint.registrodeinfracoes.jsinespclient.SinespApi;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegistryActivity extends Activity implements IGetUrl {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    private FirebaseAuth auth;
    private FirebaseUser user;
    List<String> links = new ArrayList<>();
    List<String> urls = new ArrayList<>();
    List<String> hashes = new ArrayList<>();
    private EditText editPlacaVeiculo;
    private EditText editModeloVeiculo;
    private EditText editIndLocal;
    private EditText editIndInfracao;
    private EditText editObservacoes;
    private EditText editMarcaVeiculo;
    private TextView txtSituacaoVeiculo;
    private FusedLocationProviderClient fusedLocationClient;
    private String latitude;
    private String longitude;
    private String fToken;
    private ProgressBar progressBar;
    private StorageReference mStorageRef;
    private String status = "em aberto";
    SharedPreferences sharedPreferences;



    @Override
    public void getUrl(String url) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registry);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        editPlacaVeiculo = (EditText) findViewById(R.id.placa_veiculo);
        //editPlacaVeiculo.addTextChangedListener(MaskEditUtil.mask(editPlacaVeiculo, MaskEditUtil.FORMAT_PLACA));
        editPlacaVeiculo.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        inicializaComponentes();
        Intent intent = getIntent();
        links = (List<String>) intent.getSerializableExtra("Links");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        auth = Connection.getFirebaseAuth();
        user = Connection.getFirebaseUser();
        sharedPreferences = getSharedPreferences(user.getUid(),MODE_PRIVATE);
        fToken = sharedPreferences.getString("ftoken_sinesp", "");
    }

    @SuppressLint("ResourceAsColor")
    private void consultar() throws IOException {
        SinespApi sinespApi = new SinespApi();
        String placa = editPlacaVeiculo.getText().toString().trim();
        int tamanhoPlaca = placa.length();
        if(placa != null && tamanhoPlaca == 7) {
            Result result = sinespApi.Consulta(fToken, placa);
            if(result.getReturnCode() != 3 && result.getReturnCode() != 1) {
                System.out.println(result.getReturnCode());
                int i = result.getModel().indexOf("/");
                String modeloVeiculo = result.getModel().substring((i + 1));
                String marcaVeiculo = result.getModel().substring(0, (i));
                String situacao = result.getStatusMessage();
                if (situacao.contains("Sem")) {
                    situacao = "Sem restrições";
                    txtSituacaoVeiculo.setTextColor(Color.GREEN);
                } else {
                    situacao = "Com restrições";
                    txtSituacaoVeiculo.setTextColor(Color.RED);

                }
                editMarcaVeiculo.setText(marcaVeiculo);
                editModeloVeiculo.setText(modeloVeiculo);
                txtSituacaoVeiculo.setText(situacao);
                editMarcaVeiculo.setFocusableInTouchMode(false);
                editModeloVeiculo.setFocusable(false);
            }
            else{
                Toast.makeText(getApplicationContext(),"Veiculo não econtrado", Toast.LENGTH_LONG);
                editMarcaVeiculo.setText("");
                editModeloVeiculo.setText("");
                txtSituacaoVeiculo.setText("Situação");
            }

        }else{
            Toast.makeText(getApplicationContext(),"Placa incorreta", Toast.LENGTH_LONG);
            editMarcaVeiculo.setText("");
            editModeloVeiculo.setText("");
            txtSituacaoVeiculo.setText("Situação");
        }
        onProgressBar(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = Connection.getFirebaseAuth();
        user = Connection.getFirebaseUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth = Connection.getFirebaseAuth();
        user = Connection.getFirebaseUser();
    }

    private static void hideKeyboard(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    private void inicializaComponentes() {
        editMarcaVeiculo = (EditText) findViewById(R.id.marca_veiculo);
        editPlacaVeiculo = (EditText) findViewById(R.id.placa_veiculo);
        editModeloVeiculo = (EditText) findViewById(R.id.especie_veiculo);
        editIndLocal = (EditText) findViewById(R.id.identificacao_local);
        editIndInfracao = (EditText) findViewById(R.id.identificacao_infracao);
        editObservacoes = (EditText) findViewById(R.id.observacoes);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_registro);
        txtSituacaoVeiculo = (TextView) findViewById(R.id.situacao);
        editPlacaVeiculo.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                try {
                    hideKeyboard(getApplicationContext(),editPlacaVeiculo);
                    onProgressBar(true);
                    consultar();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String gerarHash(File f) {
        String hash = null;
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte [] file = Files.readAllBytes(f.toPath());
            m.update(file, 0, file.length);
            byte[] digest = m.digest();
            hash = new BigInteger(1, digest).toString(16);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hash;
    }


    public void onProgressBar(boolean visibility) {
        progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadVideo() {

        for (String uri : links) {
            onProgressBar(true);
            mStorageRef = FirebaseStorage.getInstance().getReference();
            Uri file = Uri.fromFile(new File(uri));
            String path = uri;
            // Create the file metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("video/mp4")
                    .build();
            // Upload file and metadata to the path 'audio/audio.mp3'
            UploadTask uploadTask = mStorageRef.child("Videos/" + file.getLastPathSegment()).putFile(file, metadata);

            // Listen for state changes, errors, and completion of the upload.
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }).addOnPausedListener(taskSnapshot -> System.out.println("Upload is paused")).
                    addOnFailureListener(exception -> {
                // Handle unsuccessful uploads
            }).addOnSuccessListener(taskSnapshot -> {
                // Handle successful uploads on complete
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri1 -> {
                    String url = uri1.toString();
                    System.out.println(url);
                    urls.add(url);
                    int tokenPosition = 0;
                    tokenPosition = url.lastIndexOf("=");
                    String tokenVideo = "";
                    tokenVideo = url.substring(tokenPosition+1);
                    hashes.add(tokenVideo);
                    File arquivo = new File(path);
                    arquivo.delete();
                    if (urls.size() == links.size()) {
                        insertDB();
                        try {
                            Thread.sleep(3000);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            });
        }

    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void insertDB() {
        String placaVeiculo = editPlacaVeiculo.getText().toString().trim();
        String marcaVeiculo = editMarcaVeiculo.getText().toString().trim();
        String modeloVeiculo = editModeloVeiculo.getText().toString().trim();
        String indLocal = editIndLocal.getText().toString().trim();
        String indInfracao = editIndInfracao.getText().toString().trim();
        String observacoes = editObservacoes.getText().toString().trim();
        if(placaVeiculo != null && marcaVeiculo !=null && modeloVeiculo != null && indLocal != null && indInfracao != null) {
            String key = ref.child("denuncias").push().getKey();
            Denuncia denuncia = new Denuncia(placaVeiculo, indLocal, indInfracao, observacoes, marcaVeiculo, modeloVeiculo, getDateTime(), status, user.getUid());
            Map<String, Object> denunciaValues = denuncia.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("modulousuario/denuncias/" + key, denunciaValues);
            ref.updateChildren(childUpdates);
            Map<String, Object> provaUpdates = new HashMap<>();
            for (String s : urls) {
                for (String f : hashes)
                    provaUpdates.put(f, s);
            }
            ref.child("modulousuario/denuncias/" + key + "/provas").updateChildren(provaUpdates);
            onProgressBar(false);
        }
        else{
            Toast.makeText(getApplicationContext(), "Favor preencher todos os dados", Toast.LENGTH_LONG);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void gravar(View view) {
        uploadVideo();
    }

    public void voltar(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        final String[] address = new String[1];
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(Double.valueOf(latitude),Double.valueOf(longitude), 1);
                            System.out.println(addresses.get(0));
                            address[0] = addresses.get(0).getAddressLine(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        editIndLocal.setText(getAddress(address[0]));
                        editIndLocal.setFocusableInTouchMode(false);
                        editIndLocal.setFocusable(false);
                    }
                });
    }

    public String getAddress(String address){
        String rua;
        String setor;
        String cidade;
        if (address != null) {
            int pos = address.indexOf(",");
            rua = address.substring(0, pos);
            address = address.replaceAll(rua + ",", "");
            pos = address.indexOf(",");
            setor = address.substring(0, pos);
            address = address.replaceAll(setor + ",", "");
            pos = address.indexOf(",");
            cidade = address.substring(0, pos);
            address = address.replaceAll(cidade + ",", "");
            return rua + ", " + setor + ", " + cidade;
        }
        else{
            return address;
        }
    }

    public void mapa(View view) {
        Uri mapUri = Uri.parse("geo:" + latitude + ","
                + longitude + "?q="
                + latitude + ","
                + longitude
                + "(" + Uri.encode("nome do local") + ")");
        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        startActivity(intent);
    }


}