package com.example.luispaulo.ibeacons;

import android.app.Application;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Luis Paulo on 07/03/2018.
 */

public class MyApplication extends Application {

    public void onCreate(){
        super.onCreate();
    }
}
