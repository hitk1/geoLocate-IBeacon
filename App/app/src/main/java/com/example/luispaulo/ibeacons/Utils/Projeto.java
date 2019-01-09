package com.example.luispaulo.ibeacons.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.luispaulo.ibeacons.Activities.*;
import com.example.luispaulo.ibeacons.Model.Requests.Registros;
import com.example.luispaulo.ibeacons.Model.Requests.Solicitacao;
import com.example.luispaulo.ibeacons.Model.Requests.UsuarioRegistro;
import com.example.luispaulo.ibeacons.Model.Token;
import com.example.luispaulo.ibeacons.Model.Usuario;
import com.example.luispaulo.ibeacons.R;
import com.example.luispaulo.ibeacons.Sqlite.SQL_Registros;
import com.example.luispaulo.ibeacons.Sqlite.SQL_Tokens;
import com.example.luispaulo.ibeacons.Sqlite.SQL_UserLogin;
import com.google.gson.Gson;

import java.math.BigInteger;
import java.security.*;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Luis Paulo on 28/05/2018.
 */

public class Projeto {

    private Context context;

    //Constantes auxiliares
    public static final int TELA_LOGIN = 1;
    public static final String FAULT = "Fault";

    public Projeto(Context context){
        this.context = context;
    }

    public String Encode(String str){
        try{
            MessageDigest di = MessageDigest.getInstance("SHA-256");
            di.update(str.getBytes("ASCII"));
            byte[] strEncrypt = di.digest();
            return String.format("%0" + (strEncrypt.length*2) + "X", new BigInteger(1, strEncrypt));
        }
        catch (Exception ex){
            Toast.makeText(context, "Ocorreu um erro no Encrypt", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public static void DialogMessage(final Context context, String title, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.DialogStyle);
        dialog.setTitle(title)
                .setMessage(message)
                .setNeutralButton(context.getString(R.string.OK), null)
                .create().show();
    }

    public static Boolean ValidaCPF(String cpf){
        if (cpf.length() != 14)
            return false;
        else
        {
            int calculo = 0;

            for (int i = 0, j = 10; i < 11; i++)
                if (i != 3 && i != 7)
                    calculo += (cpf.charAt(i) - 48) * j--;

            calculo = 11 - calculo % 11;
            if (calculo == 10 || calculo == 11)
                calculo = 0;

            if ((cpf.charAt(12) - 48) == calculo)
            {
                calculo = 0;
                for (int i = 0, j = 11; i < 13; i++)
                    if (i != 3 && i != 7 && i != 11)
                        calculo += (cpf.charAt(i) - 48) * j--;
                calculo = 11 - calculo % 11;
                if (calculo == 10 || calculo == 11)
                    calculo = 0;
                if ((cpf.charAt(13) - 48) == calculo)
                    return true;
            }
            return false;
        }
    }

    public Boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
            return true;
        else
            return false;
    }

    public void Logar(String prontuario, String Passwd){
        try{
            if(new SQL_UserLogin(context).ValidaLogin(prontuario.toLowerCase(), new Projeto(context).Encode(Passwd))) {
                context.startActivity(new Intent(context, com.example.luispaulo.ibeacons.Activities.Principal.class));
                //Mantenho o usuário logado no aplicativo até que ele deslogue
                new Projeto.Preferences(context).saveUserLogado(prontuario.toLowerCase());
            }
            else
                DialogMessage(context, context.getString(R.string.Atencao), context.getString(R.string.LoginInvalido));
        }
        catch (Exception ex){
            Toast.makeText(context, "Ocorreu um erro no login.", Toast.LENGTH_SHORT).show();
        }
    }

    public void Deslogar(){
        //Limpo o prontuario do shared preferences
        new Projeto.Preferences(this.context).clearUserLogado();

        //Starto a activity de login
        this.context.startActivity(new Intent(this.context, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    //Classe que armazena as informações sobre os Beacons
    public static class IBeacons {
        //Constantes de inicialização do aplicativo (IBeacon)
        public static final String APP_ID = "SUBSTITUIR AQUI COM O APP_ID DA ESTIMOTE";
        public static final String APP_TOKEN = "SUBSTITUIR AQUI COM O APP_TOKEN DA ESTIMOTE";
        public static final String APP_UUID = "SUBSTITUIR AQUI COM O APP_UUID DA ESTIMOTE";

        //Constantes retornadas pela biblioteca PROXIMITY
        public static final String IMMEDIATE = "IMMEDIATE";
        public static final String NEAR = "NEAR";
        public static final String FAR = "FAR";

        public String DistanceFromProximity(String distance){
            switch(distance){
                case IMMEDIATE:
                    return "0-0,5m";
                case NEAR:
                    return "0,5-3m";
                case FAR:
                    return "Mais de 3m.";
                default: return "";
            }
        }
    }

    public static class Preferences{

        private Context context;
        private SharedPreferences Sh;
        private SharedPreferences.Editor edit;
        private static final String NOME_ARQUIVO = "Projeto.Preferencias";

        //Persistir as informações do ultimo usuário logado
        private static final String LAST_PRONTUARIO = "lastPront";
        private static final String SW_KEEPCONNECTED = "sw_lastPront";

        //Persistir o usuario que esta atualmente logado
        private static final String USER_PRONTUARIO = "userLogProntuario";

        //Persistir as informações no momento do cadastro de usuario
        private static final String ID_PRONT = "prontuario";
        private static final String ID_PASSWD = "passwd";
        private static final String ID_NOME = "userNome";
        private static final String ID_SOBRENOME= "userSobrenome";
        private static final String ID_DATA = "userData";
        private static final String ID_CPF = "userCPF";
        private static final String ID_TELEFONE = "userTelefone";
        private static final String ID_EMAIL = "userEmail";

        //Persistir o delay de repetição de scanner
        private static final String DELAY_SCAN = "delay";



        public Preferences(Context context){
            this.context = context;
            Sh = context.getSharedPreferences(NOME_ARQUIVO, 0);
            this.edit = Sh.edit();
        }

        public void clearCadastroUser(){
            edit.remove(ID_PRONT);
            edit.remove(ID_PASSWD);
            edit.remove(ID_NOME);
            edit.remove(ID_SOBRENOME);
            edit.remove(ID_DATA);
            edit.remove(ID_CPF);
            edit.remove(ID_TELEFONE);
            edit.remove(ID_EMAIL);
            edit.commit();
        }

        //--------------------------------------------------------------------------------
        //Tela de Login
        public void sw_keepConnected(boolean operacao){
            edit.putBoolean(SW_KEEPCONNECTED, operacao);
            edit.commit();
        }

        public boolean getSwUserConnected(){
            return (boolean) Sh.getBoolean(SW_KEEPCONNECTED, false);
        }
        //--------------------------------------------------------------------------------

        //--------------------------------------------------------------------------------
        //Tela de Cadastro (Prontuário)
        public void saveProntPasswd(String prontuario, String passwd){
            edit.putString(ID_PRONT, prontuario);
            edit.putString(ID_PASSWD, passwd);
            edit.commit();
        }

        public String getProntuario(){
            return (String)Sh.getString(ID_PRONT, "");
        }

        public String getPassWd(){
            return (String)Sh.getString(ID_PASSWD, "");
        }

        //---------------------------------------------------------------------------------

        //Tela de Cadastro (Informações)
        //---------------------------------------------------------------------------------
        public void saveUserInformacoes(Usuario user){
            edit.putString(ID_PRONT, user.getUserProntuario());
            edit.putString(ID_PASSWD, user.getUserSenha());
            edit.putString(ID_NOME, user.getUserNome());
            edit.putString(ID_SOBRENOME, user.getUserSobrenome());
            edit.putString(ID_DATA, user.getUserDataNasc());
            edit.putString(ID_CPF, user.getUserCPF());
            edit.putString(ID_TELEFONE, user.getUserTelefone());
            edit.putString(ID_EMAIL, user.getUserEmail());
            edit.commit();
        }

        public Usuario getInfUser(){
            Usuario user = new Usuario();

            user.setUserNome(Sh.getString(ID_NOME, ""));
            user.setUserSobrenome(Sh.getString(ID_SOBRENOME, ""));
            user.setUserDataNasc(Sh.getString(ID_DATA, ""));
            user.setUserCPF(Sh.getString(ID_CPF, ""));
            user.setUserTelefone(Sh.getString(ID_TELEFONE, ""));
            user.setUserEmail(Sh.getString(ID_EMAIL, ""));

            return user;
        }

        //---------------------------------------------------------------------------------

        //---------------------------------------------------------------------------------
        //Usuario Logado
        public void saveUserLogado(String prontuario){
            edit.putString(USER_PRONTUARIO, prontuario);
            edit.commit();
        }

        public String getUserLogado(){
            return Sh.getString(USER_PRONTUARIO, "");
        }

        public void clearUserLogado(){
            edit.remove(USER_PRONTUARIO);
            edit.apply();
        }
        //---------------------------------------------------------------------------------

        //Scanner
        public void saveDelay(int time){
            edit.putInt(DELAY_SCAN, time).commit();
        }

        public int getDelay(){
            return Sh.getInt(DELAY_SCAN, 0);
        }

        public void clearDelay(){
            edit.remove(DELAY_SCAN).apply();
        }
    }

    public static class Request{

        //Constantes de auxílio para comunicação entre cliente e servidor
        public static final String ERROR_REQUEST = "INVALID";
        public static final String INVALID_TOKEN = "INVALID_TOKEN";
        public static final String EXPIRES = "TOKEN_EXPIRADO";
        public static final String ERRO = "ERRO";
        public static final String ACCEPT = "ACCEPT";

        public static final String IP_SERVER = "http://192.168.0.99:5000";

        public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        public static final String REGISTER = "/register";
        public static final String REFRESH = "/refresh";
        public static final String TRANSFER = "/transfer";
        private Context context;

        public Request(Context context) {
            this.context = context;
        }

        public String TrataRetornoRequest(String request){
            request = request.replace("[","");
            request = request.replace("]","");
            request = request.replace("'", "");
            request = request.replace("\"", "");

            return request.trim();
        }

        public okhttp3.Request RetornaRequest(final String route){
            try{
                RequestBody body = RequestBody.create(JSON, returnJson(route));
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(IP_SERVER + route)
                        .post(body)
                        .build();

                return request;
            }
            catch (Exception ex){
                throw ex;
            }
        }

        public okhttp3.Request RetornaRequest(final String route, Solicitacao solicitacao){
            try{
                String json = new Gson().toJson(solicitacao);

                RequestBody body = RequestBody.create(JSON, json);
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(IP_SERVER + route)
                        .post(body)
                        .build();

                return request;
            }
            catch (Exception ex){
                throw ex;
            }
        }

        private String returnJson(final String route) {
            try {
                String request_json = "";
                switch (route){
                    case REGISTER:
                        request_json = jsonRegistro();
                        break;

                    case REFRESH:
                        request_json = jsonRefresh();
                        break;
                }
                return request_json;
            }
            catch (SQLiteException sqliteEx){
                sqliteEx.printStackTrace();
                throw sqliteEx;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        }

        private String jsonRefresh() {
            Gson gson = new Gson();
            String prontuario = new Projeto.Preferences(context).getUserLogado();

            UsuarioRegistro user = new UsuarioRegistro();
            user.setGrant(new SQL_Tokens(context).getGrant(prontuario));

            String json = gson.toJson(user);
            return json;
        }

        private String jsonRegistro() {
            Gson gson = new Gson();
            String prontuario = new Projeto.Preferences(context).getUserLogado();
            String passwd = new SQL_UserLogin(context).Pesquisar(prontuario).getUserSenha();

            UsuarioRegistro user = new UsuarioRegistro();
            user.setProntuario(prontuario);
            user.setPasswd(passwd);

            String json = gson.toJson(user);
            return json;
        }

        public String TransferirDados(){
            try{
                String prontuario = new Projeto.Preferences(context).getUserLogado();

                //Carrego a Lista de Registros armazenada localmente
                List<Registros> list = new SQL_Registros(context).CarregarLista();

                //Verifico se o Token está registrado no SQLite
                String Token = new SQL_Tokens(context).getToken(prontuario);

                //Se entrar no IF significa que não existe um token no SQLite
                if(Token.equals(Projeto.FAULT))
                    Token = Update(prontuario);

                //Verificação da resposta da atualização do Token
                if(!Token.equals(Projeto.FAULT) && !Token.equals(Projeto.Request.INVALID_TOKEN)) {
                    Solicitacao solicitacao = new Solicitacao(Token, list);

                    OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new Request(context).RetornaRequest(Request.TRANSFER, solicitacao);

                    Response response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        String retorno = new Projeto.Request(context).TrataRetornoRequest(response.body().string());

                        switch (retorno){
                            case INVALID_TOKEN:
//                                Update(prontuario);
//                                return TransferirDados();
                                return INVALID_TOKEN;

                            case EXPIRES:
                                //Atualizo o Token
                                Update(prontuario);
                                //Chamo a função recursivamente
                                return TransferirDados();

                            case ERRO:
                                //Tratar no método que chamou
                                return "Ocorreu um erro interno do servidor.";

                            case ACCEPT:
                                return ACCEPT;

                            default:
                                break;
                        }
                    }
                }
                else
                    return INVALID_TOKEN;
            }
            catch (SQLiteException sqliteEx){
                sqliteEx.printStackTrace();
                Log.e("Erro", "Erro no SQLite");
                return sqliteEx.toString();
            }
            catch (Exception ex){
                ex.printStackTrace();
                Log.e("Erro", "Erro ao enviar: " + ex.toString());
                return ex.toString();
            }
            return ACCEPT;
        }

        private String Update(String prontuario) throws Exception {
            try{
                int count = 0;
                String token = Projeto.FAULT;
                Boolean condicao = false;

                do{
                    OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new Request(context).RetornaRequest(Request.REFRESH);

                    Response response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        //Token de Autorização
                        token = new Projeto.Request(context).TrataRetornoRequest(response.body().string());

                        if(!token.equals(Projeto.Request.INVALID_TOKEN))
                            new SQL_Tokens(context).Alterar(new Token(prontuario, "", token));
                    }
                    count ++;
                    condicao = (!token.equals(Projeto.FAULT) && !token.equals(Projeto.Request.INVALID_TOKEN)) || count == 3;

                }while(!condicao);
                return token;
            }
            catch (Exception ex){
                ex.printStackTrace();
                throw ex;
            }
        }

    }

}