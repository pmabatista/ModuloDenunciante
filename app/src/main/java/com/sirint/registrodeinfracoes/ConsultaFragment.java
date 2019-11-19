package com.sirint.registrodeinfracoes;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ConsultaFragment extends Fragment {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    private FirebaseUser user;
    private String data;
    private String status;
    private String indInfracao;
    private String placaVeiculo;
    private String marcaVeiculo;
    private String modeloVeiculo;
    private String indLocal;
    private String observacoes;
    private String especieVeiculo;
    private String denunciante;
    private List<Denuncia> listDenuncia = new ArrayList<>();
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consulta, container, false);
        listView = (ListView) view.findViewById(R.id.listview);

        user = Connection.getFirebaseUser();
        queryDados();
        return view;
    }

    public static ConsultaFragment newInstance() {
        ConsultaFragment fragment = new ConsultaFragment();
        return fragment;
    }

    public class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return listDenuncia.size();
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
            view = getLayoutInflater().inflate(R.layout.customlayout,null);
            TextView txt_data = (TextView)view.findViewById(R.id.txt_data);
            TextView txt_status = (TextView)view.findViewById(R.id.txt_status);
            TextView txt_infracao = (TextView)view.findViewById(R.id.txt_infracao);
            txt_data.setText(listDenuncia.get(i).getData());
            txt_status.setText(listDenuncia.get(i).getStatus());
            txt_infracao.setText(listDenuncia.get(i).getIndInfracao());
            return view;
        }
    }

    private void queryDados(){
        Query query = ref.child("modulousuario/denuncias").orderByChild("denunciante").equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot querySnapshot : dataSnapshot.getChildren()) {
                    data = String.valueOf(querySnapshot.child("data").getValue());
                    status = String.valueOf(querySnapshot.child("status").getValue());
                    indInfracao = String.valueOf(querySnapshot.child("indInfracao").getValue());
                    Denuncia denuncia = new Denuncia(placaVeiculo, indLocal, indInfracao, observacoes, marcaVeiculo, especieVeiculo, data, status, denunciante);
                    System.out.println("Deu certo");
                    listDenuncia.add(denuncia);
                }
                CustomAdapter customAdapter = new CustomAdapter();
                listView.setAdapter(customAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
