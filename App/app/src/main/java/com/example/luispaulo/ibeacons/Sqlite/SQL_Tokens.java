package com.example.luispaulo.ibeacons.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.luispaulo.ibeacons.Model.Token;
import com.example.luispaulo.ibeacons.Model.Usuario;
import com.example.luispaulo.ibeacons.Utils.Projeto;

import java.net.FileNameMap;

public class SQL_Tokens {

    private SQL dbhelper;
    private SQLiteDatabase db;

    private static final String PRONTUARIO = "Prontuario";
    private static final String GRANT = "Token_Grant";
    private static final String TOKEN = "Token";

    public SQL_Tokens(Context context) {
        dbhelper = new SQL(context);
    }

    private void openRead() {
        this.Close();
        db = dbhelper.getReadableDatabase();
    }

    private void openWrite() {
        this.Close();
        db = dbhelper.getWritableDatabase();
    }

    private void Close() {
        if (db != null)
            db.close();
    }

    public Token Pesquisar(String prontuario) {
        try {

            this.openRead();
            Token user = null;

            //Cursor cursor = db.query("Usuarios", TUPLA, "UserProntuario = '" + prontuario + "'", null, null, null, null);
            Cursor cursor = db.rawQuery("SELECT * FROM Tokens WHERE Prontuario = ? ", new String[]{prontuario});


            if (cursor != null)
                if (cursor.moveToFirst()) {
                    user = new Token();

                    user.setProntuario(cursor.getString(0));
                    user.setToken_Grant(cursor.getString(1));
                    user.setToken(cursor.getString(2));

                } else
                    return null;

            return user;
        } catch (SQLException sqlEx) {
            throw sqlEx;
        } catch (Exception ex) {
            throw ex;
        } finally {
            this.Close();
        }
    }

    public void Inserir(Token user) throws SQLException, Exception {
        try {
            this.openWrite();
            db.beginTransactionNonExclusive();

            ContentValues values = new ContentValues();
            values.put(PRONTUARIO, user.getProntuario());
            values.put(GRANT, user.getToken_Grant());
            values.put(TOKEN, user.getToken());

            db.insert("Tokens", null, values);
        } catch (SQLiteException sqlEx) {
            db.endTransaction();
            throw sqlEx;
        } catch (Exception Ex) {
            db.endTransaction();
            throw Ex;
        } finally {
            db.setTransactionSuccessful();
            db.endTransaction();
            this.Close();
        }
    }

    public void Alterar(Token user) throws SQLException, Exception {

        try {
            this.openWrite();

            ContentValues values = new ContentValues();
            values.put(TOKEN, user.getToken());

            db.update("Tokens", values, "Prontuario = '" + user.getProntuario() + "'", null);

        } catch (SQLiteException sqlEx) {
            throw sqlEx;
        } catch (Exception Ex) {
            throw Ex;
        } finally {
            this.Close();
        }
    }

    public String getGrant(String prontuario) {

        try {
            this.openRead();

            Cursor cursor = db.rawQuery("SELECT " + GRANT + " FROM Tokens WHERE Prontuario = ?", new String[]{prontuario});

            if (cursor != null) {
                if (cursor.moveToFirst())
                    return cursor.getString(0);
            }
            return Projeto.FAULT;
        } catch (SQLiteException sqlEx) {
            throw sqlEx;
        } catch (Exception Ex) {
            throw Ex;
        } finally {
            this.Close();
        }
    }

    public String getToken(String prontuario) {
        try {
            this.openRead();

            Cursor cursor = db.rawQuery("SELECT " + TOKEN + " FROM Tokens WHERE Prontuario = ?", new String[]{prontuario});

            if (cursor != null) {
                if (cursor.moveToFirst())
                    return (!cursor.getString(0).isEmpty() ? cursor.getString(0) : Projeto.FAULT);
            }

            return Projeto.FAULT;
        } catch (SQLiteException sqlEx) {
            throw sqlEx;
        } catch (Exception Ex) {
            throw Ex;
        } finally {
            this.Close();
        }
    }
}
