package com.sirint.registrodeinfracoes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Denuncia {


    private String placaVeiculo;
    private String indLocal;
    private String indInfracao;
    private String observação;
    private String marcaVeiculo;
    private String especieVeiculo;
    private String denúncia;
    List<String> provas = new ArrayList<>();


    public Denuncia(String placaVeiculo, String indLocal, String indInfracao, String observação, String marcaVeiculo, String especieVeiculo) {
        this.placaVeiculo = placaVeiculo;
        this.indLocal = indLocal;
        this.indInfracao = indInfracao;
        this.observação = observação;
        this.marcaVeiculo = marcaVeiculo;
        this.especieVeiculo = especieVeiculo;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public String getIndLocal() {
        return indLocal;
    }

    public void setIndLocal(String indLocal) {
        this.indLocal = indLocal;
    }

    public String getIndInfracao() {
        return indInfracao;
    }

    public void setIndInfracao(String indInfracao) {
        this.indInfracao = indInfracao;
    }

    public String getObservação() {
        return observação;
    }

    public void setObservação(String observação) {
        this.observação = observação;
    }

    public String getMarcaVeiculo() {
        return marcaVeiculo;
    }

    public void setMarcaVeiculo(String marcaVeiculo) {
        this.marcaVeiculo = marcaVeiculo;
    }

    public String getEspecieVeiculo() {
        return especieVeiculo;
    }

    public void setEspecieVeiculo(String especieVeiculo) {
        this.especieVeiculo = especieVeiculo;
    }

    public List<String> getProvas() {
        return provas;
    }

    public void setProvas(List<String> provas) {
        this.provas = provas;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("placaVeiculo", placaVeiculo);
        result.put("marcaVeiculo", marcaVeiculo);
        result.put("especieVeiculo", especieVeiculo);
        result.put("indLocal", indLocal);
        result.put("indInfracao", indInfracao);
        result.put("observacao", observação);

        return result;
    }
}