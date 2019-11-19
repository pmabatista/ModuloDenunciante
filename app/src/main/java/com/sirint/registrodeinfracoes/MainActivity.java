package com.sirint.registrodeinfracoes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sirint.registrodeinfracoes.camera.Camera;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;
    private NavigationView navigationView;
    private ImageView mRegistrar;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView txt_perfil;
    private TextView txt_nome;
    private LinearLayout layoutHeader;
    SharedPreferences sharedPreferences;
    String fToken;
    String nome;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.INVISIBLE);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_content, Camera.newInstance(),"Camera");
        ft.commit();
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        layoutHeader = headerLayout.findViewById(R.id.nav_header);
        txt_perfil = headerLayout.findViewById(R.id.txt_nav_perfil);
        txt_nome = headerLayout.findViewById(R.id.txt_nav_nome);
        layoutHeader.setOnClickListener(v -> {
            FragmentTransaction ft1 = fm.beginTransaction();
            ft1.add(R.id.fragment_content, new ProfileFragment(), "Perfil");
            ft1.commit();
            DrawerLayout drawerLayout1 = findViewById(R.id.drawerLayout);
            drawerLayout1.closeDrawer(GravityCompat.START);
        });
        auth = Connection.getFirebaseAuth();
        user = Connection.getFirebaseUser();
        queryDados();
    }

    public void criaPreferences(){
            SharedPreferences sharedPreferences = getSharedPreferences(user.getUid(), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("nome_usuario", nome);
            editor.putString("email_usuario", user.getEmail());
            editor.putBoolean("email_verificado", user.isEmailVerified());
            editor.putString("ftoken_sinesp", fToken);
            editor.apply();
    }

    public void logout(){
        Connection.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_item_six) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragment_content, new AjudaFragment(), "Ajuda");
            ft.commit();
        } else if (id == R.id.nav_item_one) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment ajuda = fm.findFragmentByTag("Ajuda");
            Fragment perfil = fm.findFragmentByTag("Perfil");
            Fragment consulta = fm.findFragmentByTag("Consulta");
            if (ajuda != null){
                if(ajuda.isVisible())
                    ft.remove(ajuda);
            }if(consulta != null){
                if(consulta.isVisible())
                    ft.remove(consulta);
            }if(perfil != null){
                if(perfil.isVisible())
                    ft.remove(perfil);
            }
            ft.commit();
        } else if (id == R.id.nav_item_two) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragment_content, new ConsultaFragment(), "Consulta");
            ft.commit();

        }
        else if (id == R.id.nav_item_seven) {
            logout();
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        auth = Connection.getFirebaseAuth();
        user = Connection.getFirebaseUser();
        verificarUser();
    }

    private void queryDados(){
        Query query = ref.child("modulousuario/usuarios/" + user.getUid() + "/").orderByChild("email");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    nome = (String.valueOf(dataSnapshot.child("nome").getValue()));
                    fToken = String.valueOf(dataSnapshot.child("fToken").getValue());
                    if(nome != null && fToken != null) {
                        criaPreferences();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
    }

    private void verificarUser() {
        if (user == null){
            finish();
        } else{
            sharedPreferences = getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);
            txt_perfil.setText(sharedPreferences.getString("email_usuario", ""));
            txt_nome.setText(sharedPreferences.getString("nome_usuario", ""));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 1 ){
            getSupportFragmentManager().popBackStack();
        } else {
        }

    }
}

