package com.example.luispaulo.ibeacons.Model.Requests;

import java.util.List;

public class Solicitacao {

    public Solicitacao() {
    }

    public Solicitacao(String Token, List<Registros> lst) {
        this.setToken(Token);
        this.setRegistros(lst);
    }

    private String token;
    private List<Registros> registros;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Registros> getRegistros() {
        return registros;
    }

    public void setRegistros(List<Registros> registros) {
        this.registros = registros;
    }
}
