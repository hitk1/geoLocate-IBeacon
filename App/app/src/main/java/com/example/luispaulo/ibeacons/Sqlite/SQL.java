package com.example.luispaulo.ibeacons.Sqlite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Luis Paulo on 25/05/2018.
 */

public class SQL extends SQLiteOpenHelper{
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "IBeacon";


    public SQL(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String T_Usuario = "CREATE TABLE Usuarios (" +
                "    UserProntuario TEXT PRIMARY KEY NOT NULL," +
                "    UserNome       TEXT NOT NULL," +
                "    UserSobrenome  TEXT NOT NULL," +
                "    UserDataNasc   TEXT NOT NULL,"      +
                "    UserCpf        TEXT UNIQUE NOT NULL," +
                "    UserTelefone   TEXT NOT NULL," +
                "    UserEmail      TEXT NOT NULL " +
                ");";

        String T_UserLogin = "CREATE TABLE User_Login (" +
                "   UsLog_Prontuario TEXT PRIMARY KEY CONSTRAINT Fk_User_Pront REFERENCES Usuarios(UserProntuario) NOT NULL," +
                "   UsLog_Senha      TEXT NOT NULL );";

        String T_Tokens = "CREATE TABLE Tokens (" +
                "   Prontuario    TEXT NOT NULL PRIMARY KEY CONSTRAINT Fk_Tk_Pront REFERENCES Usuarios(UserProntuario)," +
                "   Token_Grant   TEXT NOT NULL," +
                "   Token         TEXT" +
                ");";

        String T_Registros = "CREATE TABLE Registros(" +
                "Id             INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "Prontuario     TEXT NOT NULL CONSTRAINT Fk_Pront REFERENCES Tokens(Prontuario)," +
                "ID_Beacon      TEXT NOT NULL," +
                "Beacon_Name    TEXT NOT NULL," +
                "Rssi           INTEGER NOT NULL," +
                "Dbm            INTEGER NOT NULL," +
                "Distancia      REAL NOT NULL," +
                "Bateria        INTEGER NOT NULL," +
                "Delay          INTEGER NOT NULL," +
                "DataHora       TEXT NOT NULL" +
                ");";

        try {
            db.execSQL(T_Usuario);
            db.execSQL(T_UserLogin);
            db.execSQL(T_Tokens);
            db.execSQL(T_Registros);
        }
        catch (SQLiteException SQLex){
            throw SQLex;
        }
        catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS Usuarios");
        this.onCreate(db);
    }
}
