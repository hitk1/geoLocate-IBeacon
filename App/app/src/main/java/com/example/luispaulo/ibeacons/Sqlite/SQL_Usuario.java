package com.example.luispaulo.ibeacons.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteTransactionListener;
import android.util.Log;

import com.example.luispaulo.ibeacons.Model.Usuario;
import com.example.luispaulo.ibeacons.Utils.Projeto;

import org.intellij.lang.annotations.Identifier;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Luis Paulo on 25/05/2018.
 */

public class SQL_Usuario {

    private static final String PRONT = "UserProntuario";
    private static final String NOME = "UserNome";
    private static final String SOBRENOME = "UserSobrenome";
    private static final String DATA = "UserDataNasc";
    private static final String CPF = "UserCpf";
    private static final String TELEFONE = "UserTelefone";
    private static final String EMAIL = "UserEmail";
    private static final String[] TUPLA = {"UserProntuario", "UserNome", "UserSobrenome", "UserDataNasc",
            "UserCpf", "UserTelefone", "UserEmail"};

    private Context context;
    private SQL dbHelper;
    private SQLiteDatabase db;


    public SQL_Usuario(Context context) {
        dbHelper = new SQL(context);
        this.context = context;
    }

    private void openWrite() throws SQLException, Exception {
        db = dbHelper.getWritableDatabase();
    }

    private void openRead() throws SQLException, Exception {
        db = dbHelper.getReadableDatabase();
    }

    private void Close() throws SQLException, Exception {
        if (db != null)
            db.close();
    }

    public Usuario Pesquisar(String prontuario) throws SQLException, Exception {
        try {
            this.openRead();
            Usuario user = null;

            //Cursor cursor = db.query("Usuarios", TUPLA, "UserProntuario = '" + prontuario + "'", null, null, null, null);
            Cursor cursor = db.rawQuery("SELECT * FROM Usuarios WHERE UserProntuario = ? ", new String[]{prontuario});


            if (cursor != null)
                if (cursor.moveToFirst()) {
                    user = new Usuario();
                    user.setUserProntuario(cursor.getString(0));
                    user.setUserNome(cursor.getString(1));
                    user.setUserSobrenome(cursor.getString(2));
                    user.setUserDataNasc(cursor.getString(3));
                    user.setUserCPF(cursor.getString(4));
                    user.setUserTelefone(cursor.getString(5));
                    user.setUserEmail(cursor.getString(6));
                }
            else
                return null;

            user.setToken(new SQL_Tokens(this.context).Pesquisar(user.getUserProntuario()));

            return user;
        } catch (SQLException sqlEx) {
            throw sqlEx;
        } catch (Exception ex) {
            throw ex;
        } finally {
            this.Close();
        }
    }

    public boolean VerificaUsuario(String prontuario) throws SQLException, Exception {
        try {
            this.openRead();

            Cursor cursor = db.query("Usuarios", TUPLA, "UserProntuario = '" + prontuario + "'", null, null, null, null);

            if (cursor.moveToFirst())
                return true;
            return false;
        } catch (SQLException sqlEx) {
            throw sqlEx;
        } catch (Exception ex) {
            throw ex;
        } finally {
            this.Close();
        }
    }

    public void Inserir(Usuario user) throws SQLException, Exception {
//        try {
//            this.openWrite();
//            ContentValues obj = new ContentValues();
//            obj.put("UserProntuario", user.getUserProntuario());
//            obj.put("UserSenha", user.getUserSenha());
//            obj.put("UserNome", user.getUserNome());
//            obj.put("UserSobrenome", user.getUserSobrenome());
//            obj.put("UserDataNasc", String.valueOf(user.getUserDataNasc()));
//            obj.put("UserCpf", user.getUserCPF());
//            obj.put("UserTelefone", user.getUserTelefone());
//            obj.put("UserEmail", user.getUserEmail());
//
//            db.insert("Usuarios", null, obj);
//        } catch (SQLException sqlEx) {
//            throw sqlEx;
//        } catch (Exception ex) {
//            throw ex;
//        } finally {
//            this.Close();
//        }
        try {
            this.openWrite();
            db.beginTransactionNonExclusive();

            ContentValues values = new ContentValues();

            values.put(PRONT, user.getUserProntuario());
            values.put(NOME, user.getUserNome());
            values.put(SOBRENOME, user.getUserSobrenome());
            values.put(DATA, user.getUserDataNasc());
            values.put(CPF, user.getUserCPF());
            values.put(TELEFONE, user.getUserTelefone());
            values.put(EMAIL, user.getUserEmail());


            db.insert("Usuarios", null, values);

            values = new ContentValues();
            values.put(SQL_UserLogin.LOGIN, user.getUserProntuario());
            values.put(SQL_UserLogin.PASSWD, user.getUserSenha());

//            StringBuilder sbLogin = new StringBuilder();
//            sbLogin.append("INSERT INTO User_Login (UsLog_Prontuario, UsLog_Senha) VALUES('" + user.getUserProntuario() + "', '" + user.getUserSenha() + "'); ");

            db.insert("User_Login", null, values);

        }
        catch (SQLiteConstraintException constEx){
            db.endTransaction();
            throw constEx;
        }
        catch (SQLiteException sqlLiteEx) {
            db.endTransaction();
            throw sqlLiteEx;
        }
        catch (SQLException sqlEx){
            db.endTransaction();
            throw sqlEx;
        }
        catch (Exception ex) {
            db.endTransaction();
            throw ex;
        } finally {
            db.setTransactionSuccessful();
            db.endTransaction();
            this.Close();
        }
    }

    public void Alterar(Usuario user) throws SQLException, Exception {
        try {
            this.openWrite();
            db.beginTransactionNonExclusive();

            ContentValues obj = new ContentValues();
            obj.put(NOME, user.getUserNome());
            obj.put(SOBRENOME, user.getUserSobrenome());
            obj.put(DATA, String.valueOf(user.getUserDataNasc()));
            obj.put(CPF, user.getUserCPF());
            obj.put(TELEFONE, user.getUserTelefone());
            obj.put(EMAIL, user.getUserEmail());

            db.update("Usuarios", obj, "UserProntuario = '" + user.getUserProntuario() + "'", null);

            obj = new ContentValues();
            obj.put(SQL_UserLogin.PASSWD, user.getUserSenha());

            db.update("User_Login", obj, "UsLog_Prontuario = '" + user.getUserProntuario() + "'", null);

        } catch (SQLException sqlEx) {
            db.endTransaction();
            throw sqlEx;
        } catch (Exception ex) {
            db.endTransaction();
            throw ex;
        } finally {
            db.setTransactionSuccessful();
            db.endTransaction();
            this.Close();
        }
    }
}
