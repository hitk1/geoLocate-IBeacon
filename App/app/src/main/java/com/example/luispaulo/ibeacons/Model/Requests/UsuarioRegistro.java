package com.example.luispaulo.ibeacons.Model.Requests;

public class UsuarioRegistro {


    private String prontuario;
    private String passwd;
    private String grant;

    public UsuarioRegistro(){

    }

    public String getProntuario() {
        return prontuario;
    }

    public void setProntuario(String prontuario) {
        this.prontuario = prontuario;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getGrant() {
        return grant;
    }

    public void setGrant(String grant) {
        this.grant = grant;
    }
}
