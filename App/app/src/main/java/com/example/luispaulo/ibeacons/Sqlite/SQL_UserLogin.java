package com.example.luispaulo.ibeacons.Sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.luispaulo.ibeacons.Model.Usuario;

/**
 * Created by Luis Paulo on 30/06/2018.
 */

public class SQL_UserLogin {

    private SQL dbHelper;
    private SQLiteDatabase db;

    public static final String LOGIN = "UsLog_Prontuario";
    public static final String PASSWD = "UsLog_Senha";

    public SQL_UserLogin(Context context){
        dbHelper = new SQL(context);
    }

    private void openRead(){
        this.Close();
        db = dbHelper.getReadableDatabase();
    }

    private void Close(){
        if(db != null)
            db.close();
    }

    public boolean ValidaLogin(String prontuario, String passwd){
        try {
            this.openRead();
            Cursor cursor = db.rawQuery("SELECT CASE WHEN UsLog_Senha = '" + passwd + "' THEN 1 ELSE 0 END AS Passwd FROM User_Login WHERE UsLog_Prontuario = '" + prontuario + "'", null);


            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    if(cursor.getString(0).equals("1"))
                        return true;
                    return false;
                }
                return false;
            }
            return false;
        }
        catch (SQLException sqlEx){
            throw sqlEx;
        }
        catch (Exception ex){
            throw ex;
        }
        finally {
            this.Close();
        }
    }

    public Usuario Pesquisar(String prontuario){

        try{
            this.openRead();
            Usuario user = null;

            Cursor cursor = db.rawQuery("SELECT * FROM User_Login WHERE UsLog_Prontuario = ?", new String[]{prontuario});

            if(cursor != null)
                if(cursor.moveToFirst()){
                user = new Usuario();
                    user.setUserProntuario(prontuario);
                    user.setUserSenha(cursor.getString(1));
                }


            return user;
        }
        catch (Exception ex){
            throw ex;
        }
        finally {
            this.Close();
        }
    }
}
