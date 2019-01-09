package com.example.luispaulo.ibeacons.Servicos_Broadcasts;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SearchRecentSuggestionsProvider;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.estimote.coresdk.common.config.EstimoteSDK;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.observation.region.RegionUtils;
import com.estimote.coresdk.service.BeaconManager;
import com.example.luispaulo.ibeacons.Activities.ServiceRasp;
import com.example.luispaulo.ibeacons.Model.Beacon;
import com.example.luispaulo.ibeacons.Model.Requests.Registros;
import com.example.luispaulo.ibeacons.Model.Requests.Solicitacao;
import com.example.luispaulo.ibeacons.Model.Token;
import com.example.luispaulo.ibeacons.Sqlite.SQL_Registros;
import com.example.luispaulo.ibeacons.Sqlite.SQL_Tokens;
import com.example.luispaulo.ibeacons.Utils.Projeto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Luis Paulo on 05/07/2018.
 */

public class Service_RaspII extends Service {

    private BeaconRegion region;
    private BeaconManager manager;
    private int Id;
    public Context context;
    public String prontuario;
    private List<Registros> lstBeacon;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this.getApplicationContext();
        this.prontuario = new Projeto.Preferences(context).getUserLogado();

        EstimoteSDK.initialize(context, Projeto.IBeacons.APP_ID, Projeto.IBeacons.APP_TOKEN);
        EstimoteSDK.enableDebugLogging(true);
        manager = new BeaconManager(context);
        region = new BeaconRegion("Beacons", UUID.fromString(Projeto.IBeacons.APP_UUID), null, null);

        manager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<com.estimote.coresdk.recognition.packets.Beacon> beacons) {
                try {
                    Log.d("CAPTURA", "Captura ativada");
                    if (beacons.size() > 0) {
                        lstBeacon = new ArrayList<>();
                        Registros reg = null;

                        for (com.estimote.coresdk.recognition.packets.Beacon beacon : beacons) {
                            reg = new Registros();
                            reg.setProntuario(prontuario);
                            reg.setIdBeacon(beacon.getUniqueKey());
                            reg.setBeaconName(beacon.getUniqueKey());
                            reg.setRssi(beacon.getRssi());
                            reg.setDbm(beacon.getMeasuredPower());
                            reg.setDistancia(RegionUtils.computeAccuracy(beacon.getRssi(), beacon.getMeasuredPower()));
                            reg.setBateria(batteryLevel());
                            reg.setDelay(new Projeto.Preferences(context).getDelay());
                            reg.setDataHora(DataNow());

                            //Este método é responsável por nao deixar adicionar beacons repetidos no banco de dados
                            if (!lstBeacon.contains(reg))
                                lstBeacon.add(reg);
                        }


                        new SQL_Registros(context).Inserir(lstBeacon);
                        Log.d("CAPTURA", "Registros Inseridos!");
                        FinishService();
                    }
                } catch (SQLiteException sqliEx) {
                    sqliEx.printStackTrace();
                    Log.d("CAPTURA", sqliEx.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.d("CAPTURA", ex.getMessage());
                }
            }
        });
    }

    private String DataNow() {
        return (String) new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }

    private void FinishService() {
        stopService(new Intent(context, Service_RaspII.class));
    }

    private int batteryLevel() {
        Intent status = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        return status.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        manager.stopRanging(region);
        Log.d("CAPTURA", "Serviço Finalizado!");
    }

    //Classes do Serviço
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                manager.startRanging(region);
            }
        });
        Log.d("CAPTURA", "Serviço startado");
        this.Id = startId;
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
