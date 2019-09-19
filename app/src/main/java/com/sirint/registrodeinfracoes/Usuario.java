package com.sirint.registrodeinfracoes;

import java.util.HashMap;
import java.util.Map;

public class Usuario {

    String nome;
    String email;
    String fToken;

    public Usuario(String nome, String email, String fToken) {
        this.nome = nome;
        this.email = email;
        this.fToken = fToken;
    }

    public Map<String,Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nome", nome);
        result.put("email", email);
        result.put("fToken", fToken);

        return result;
    }
}
