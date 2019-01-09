package com.example.luispaulo.ibeacons.Model;

import com.example.luispaulo.ibeacons.Utils.Projeto;

/**
 * Created by Luis Paulo on 07/03/2018.
 */

public class Beacon {

    //Lista de ID dos Beacons
    public static final String ICE = "IBEACON-d2ee302bdd7c";
    public static final String BLUEBERRY = "IBEACON-e2700cc77817";
    public static final String MINT = "IBEACON-f178173700b2";


    private String Name;
    private String Key;
    private double Distance;
    private int Intensity;
    private int Rssi;
    private String ProximityDistance;

    public String getProximityDistance() {
        return ProximityDistance;
    }

    public void setProximityDistance(String proximityDistanc) {
        ProximityDistance = proximityDistanc;
    }

    public int getRssi() {
        return Rssi;
    }

    public void setRssi(int rssi) {
        Rssi = rssi;
    }

    public int getIntensity() {
        return Intensity;
    }

    public void setIntensity(int intensity) {
        Intensity = intensity;
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        switch (name){
            case BLUEBERRY:
                Name = "Blueberry";
                break;
            case ICE:
                Name = "Ice";
                break;
            case MINT:
                Name = "Mint";
                break;
            default: Name = "";
            break;
        }
    }
}
