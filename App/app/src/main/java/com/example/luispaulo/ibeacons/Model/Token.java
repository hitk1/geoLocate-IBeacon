package com.example.luispaulo.ibeacons.Model;

public class Token {

    public Token() {
        this.setProntuario("");
        this.setToken_Grant("");
        this.setToken("");
    }

    public Token(String prontuario, String Grant, String Token){
        this.setProntuario(prontuario);
        this.setToken_Grant(Grant);
        this.setToken(Token);
    }

    private String Prontuario;
    private String Token_Grant;
    private String Token;

    public String getProntuario() {
        return Prontuario;
    }

    public void setProntuario(String prontuario) {
        Prontuario = prontuario;
    }

    public String getToken_Grant() {
        return Token_Grant;
    }

    public void setToken_Grant(String token_Grant) {
        Token_Grant = token_Grant;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }


}
