package com.example.luispaulo.ibeacons.Model;

import java.util.Date;

/**
 * Created by Luis Paulo on 20/05/2018.
 */

public class Usuario {



    public Usuario() {
        this.UserProntuario = "";
        this.UserSenha = "";
        this.UserNome = "";
        this.UserSobrenome = "";
        this.UserDataNasc = "";
        this.UserCPF = "";
        this.UserTelefone = "";
        this.UserEmail = "";
        this.UserToken_Grant = "";
        this.UserToken = "";
        this.Token = new Token();
    }

    //Primeira Activity
    private String UserProntuario;
    private String UserSenha;

    //Segunda Activity
    private String UserNome;
    private String UserSobrenome;
    private String UserDataNasc;
    private String UserCPF;
    private String UserTelefone;
    private String UserEmail;
    private String UserToken_Grant;
    private String UserToken;
    private Token Token;

    public com.example.luispaulo.ibeacons.Model.Token getToken() {
        return Token;
    }

    public void setToken(com.example.luispaulo.ibeacons.Model.Token token) {
        Token = token;
    }

    public String getUserTelefone() {
        return UserTelefone;
    }

    public void setUserTelefone(String userTelefone) {
        UserTelefone = userTelefone;
    }

    public String getUserProntuario() {
            return UserProntuario;
    }

    public void setUserProntuario(String userProntuario) {
        UserProntuario = userProntuario;
    }

    public String getUserSenha() {
        return UserSenha;
    }

    public void setUserSenha(String userSenha) {
        UserSenha = userSenha;
    }

    public String getUserNome() {
        return UserNome;
    }

    public void setUserNome(String userNome) {
        UserNome = userNome;
    }

    public String getUserSobrenome() {
        return UserSobrenome;
    }

    public void setUserSobrenome(String userSobrenome) {
        UserSobrenome = userSobrenome;
    }

    public String getUserDataNasc() {
        return UserDataNasc;
    }

    public void setUserDataNasc(String userDataNasc) {
        UserDataNasc = userDataNasc;
    }

    public String getUserCPF() {
        return UserCPF;
    }

    public void setUserCPF(String userCPF) {
        UserCPF = userCPF;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserToken_Grant() {
        return UserToken_Grant;
    }

    public void setUserToken_Grant(String userToken_Grant) {
        UserToken_Grant = userToken_Grant;
    }

    public String getUserToken() {
        return UserToken;
    }

    public void setUserToken(String userToken) {
        UserToken = userToken;
    }
}
