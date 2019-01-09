package com.example.luispaulo.ibeacons.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.ColorSpace;
import android.util.Log;

import com.example.luispaulo.ibeacons.Model.Requests.Registros;

import java.util.ArrayList;
import java.util.List;

public class SQL_Registros {

    private SQL dbHelper;
    private SQLiteDatabase db;

    private static final String ID = "Id";
    private static final String PRONTUARIO = "Prontuario";
    private static final String ID_BEACON = "Id_Beacon";
    private static final String BEACON = "Beacon_Name";
    private static final String RSSI = "Rssi";
    private static final String DBM = "Dbm";
    private static final String DISTANCIA = "Distancia";
    private static final String BATERIA = "Bateria";
    private static final String DELAY = "Delay";
    private static final String DATA = "DataHora";

    public SQL_Registros(Context context) {
        this.dbHelper = new SQL(context);
    }

    private void openRead() {
        this.Close();
        db = dbHelper.getReadableDatabase();
    }

    private void openWrite() {
        this.Close();
        db = dbHelper.getWritableDatabase();
    }

    private void Close() {
        if (db != null)
            db.close();
    }

    public List<Registros> CarregarLista() {
        try {
            this.openRead();
            List<Registros> lst = null;
            Registros reg = null;

            Cursor cursor = db.rawQuery("SELECT * FROM Registros", null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    lst = new ArrayList<>();
                    do {
                        reg = new Registros();

                        reg.setProntuario(cursor.getString(1));
                        reg.setIdBeacon(cursor.getString(2));
                        reg.setBeaconName(cursor.getString(2));     //Passo o ID porque o método set é responsável por atribuir o nome correto
                        reg.setRssi(Integer.parseInt(cursor.getString(4)));
                        reg.setDbm(Integer.parseInt(cursor.getString(5)));
                        reg.setDistancia(Double.parseDouble(cursor.getString(6)));
                        reg.setBateria(Integer.parseInt(cursor.getString(7)));
                        reg.setDelay(Integer.parseInt(cursor.getString(8)));
                        reg.setDataHora(cursor.getString(9));

                        lst.add(reg);
                    } while (cursor.moveToNext());
                }
            }
            return lst;
        } catch (SQLiteException sqliteEx) {
            sqliteEx.printStackTrace();
            throw sqliteEx;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            this.Close();
        }
    }

    public void Inserir(List<Registros> list) {
        try {
            this.openWrite();
            ContentValues values = null;
            //db.beginTransactionNonExclusive();

            for (Registros reg : list) {
                values = new ContentValues();

                values.put(PRONTUARIO, reg.getProntuario());
                values.put(ID_BEACON, reg.getIdBeacon());
                values.put(BEACON, reg.getBeaconName());
                values.put(RSSI, reg.getRssi());
                values.put(DBM, reg.getDbm());
                values.put(DISTANCIA, reg.getDistancia());
                values.put(BATERIA, reg.getBateria());
                values.put(DELAY, reg.getDelay());
                values.put(DATA, reg.getDataHora());

                db.insert("Registros", null, values);
            }
        } catch (SQLiteException sqliteEx) {
            //db.endTransaction();
            sqliteEx.printStackTrace();
            Log.d("CAPTURA", sqliteEx.getMessage());
            throw sqliteEx;
        } catch (Exception ex) {
            //db.endTransaction();
            ex.printStackTrace();
            Log.d("CAPTURA", ex.getMessage());
            throw ex;
        } finally {
            //db.setTransactionSuccessful();
            //db.endTransaction();
            this.Close();
        }
    }

    public void Inserir(Registros registros) {
        try {
            this.openWrite();
            ContentValues values = new ContentValues();
            //db.beginTransactionNonExclusive();


            values.put(PRONTUARIO, registros.getProntuario());
            values.put(ID_BEACON, registros.getIdBeacon());
            values.put(BEACON, registros.getBeaconName());
            values.put(RSSI, registros.getRssi());
            values.put(DBM, registros.getDbm());
            values.put(DISTANCIA, registros.getDistancia());
            values.put(BATERIA, registros.getBateria());
            values.put(DELAY, registros.getDelay());
            values.put(DATA, registros.getDataHora());

            db.insert("Registros", null, values);
        } catch (SQLiteException sqliteEx){
            //db.endTransaction();
            sqliteEx.printStackTrace();
            Log.d("CAPTURA", sqliteEx.getMessage());
            throw sqliteEx;
        } catch (
                Exception ex)

        {
            //db.endTransaction();
            ex.printStackTrace();
            Log.d("CAPTURA", ex.getMessage());
            throw ex;
        } finally{
            //db.setTransactionSuccessful();
            //db.endTransaction();
            this.Close();
        }

    }

    public void Delete() {
        /*
         * Este método é responsável por deletar todos os registros da tabela Registros
         * */
        try {
            this.openWrite();

            //whereClause = 1  --> Deleta todos os registros
            db.delete("Registros", "1", null);
        } catch (SQLiteException sqliteEx) {
            sqliteEx.printStackTrace();
            throw sqliteEx;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            this.Close();
        }
    }

    public void Delete(int code) {
        /*
         * Este método é responsável por deletar todos os registros da tabela Registros
         * que contenham o ID menor ou igual ao passado por parâmetro
         * */
        try {
            this.openWrite();

            db.delete("Registros", ID + " <= " + String.valueOf(code), null);
        } catch (SQLiteException sqliteEx) {
            sqliteEx.printStackTrace();
            throw sqliteEx;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            this.Close();
        }
    }


}
