package com.example.luispaulo.ibeacons.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.estimote.coresdk.common.config.EstimoteSDK;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.service.BeaconManager;
import com.example.luispaulo.ibeacons.Model.Beacon;
import com.example.luispaulo.ibeacons.R;
import com.example.luispaulo.ibeacons.Adapters.beacon_adapter;
import com.example.luispaulo.ibeacons.Utils.Projeto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScanPoolling extends AppCompatActivity {

    private List<Beacon> lstBk;
    private BeaconManager manager;
    private com.estimote.coresdk.recognition.packets.Beacon bk;
    private BeaconRegion region;
    private ListView lst;
    private boolean isEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanpoolling);
        EstimoteSDK.initialize(getApplicationContext(), Projeto.IBeacons.APP_ID, Projeto.IBeacons.APP_TOKEN);
        EstimoteSDK.enableDebugLogging(true);
        lstBk = new ArrayList<Beacon>(3);
        manager = new BeaconManager(getApplicationContext());
        region = new BeaconRegion("Beacons", UUID.fromString(Projeto.IBeacons.APP_UUID), null, null);

        final Button btnScan = (Button)findViewById(R.id.btnScan);
        Button btnLimpa = (Button)findViewById(R.id.btnLimpa);
        lst = (ListView) findViewById(R.id.lstBeacons);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.connect(new BeaconManager.ServiceReadyCallback() {
                    @Override
                    public void onServiceReady() {
                        if(isEnabled) {
                            lst.setAdapter(null);
                            lstBk.clear();
                        }
                        else {
                            manager.startRanging(region);
                            Toast.makeText(getApplicationContext(), "Escaneando..", Toast.LENGTH_SHORT).show();
                            btnScan.setBackgroundColor(0x9052A7D8);
                            btnScan.setTextColor(Color.WHITE);
                        }
                    }
                });
            }
        });

        manager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
                public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<com.estimote.coresdk.recognition.packets.Beacon> beacons) {
                if (beacons.size() != 0) {
                    if(lstBk.size() < 3) {
                    for (com.estimote.coresdk.recognition.packets.Beacon bc : beacons) {
                        Beacon m = new Beacon();
                        m.setName(bc.getUniqueKey().toString());
                        m.setKey(Integer.toString(bc.getRssi()));
                        lstBk.add(m);
                    }
                    Toast.makeText(getApplicationContext(), "Beacons: " + beacons.size(), Toast.LENGTH_SHORT).show();
                    AtualizaLista();
                    isEnabled = true;
//                        String key = beacons.get(3).getUniqueKey();
//                        boolean hasRecord = false;
//                        for (Beacon b : lstBk) {
//               /             if (!b.getKey().equals(key))
//                                hasRecord = false;
//                            else
//                                hasRecord = true;
//                        }
//                        if (!hasRecord) {
//                            Toast.makeText(getApplicationContext(), "Identificou", Toast.LENGTH_SHORT).show();
//                            Beacon modelo = new Beacon();
//                            modelo.setName(beacons.get(0).getUniqueKey());
//                            modelo.setKey(beacons.get(0).getUniqueKey());
//                            lstBk.add(modelo);
//                            AtualizaLista();
//                        }
                }
            }
            }
        });
        btnLimpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lstBk.size() > 0) {
                    lst.setAdapter(null);
                    lstBk.clear();
                }
                manager.stopRanging(region);
                btnScan.setBackgroundColor(Color.LTGRAY);
                btnScan.setTextColor(Color.BLACK);
                isEnabled = false;
            }
        });

    }

    private void AtualizaLista(){
        lst.setAdapter(new beacon_adapter(this, (ArrayList<Beacon>) lstBk));
    }

    protected void onResume(){
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

//        manager.connect(new BeaconManager.ServiceReadyCallback() {
//            @Override
//            public void onServiceReady() {
//                manager.startRanging(region);
//                //manager.startMonitoring(region);
//            }
//        });
    }

    protected void onPause() {
        manager.stopRanging(region);

        super.onPause();
    }
}
