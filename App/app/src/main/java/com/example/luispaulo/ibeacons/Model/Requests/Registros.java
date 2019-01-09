package com.example.luispaulo.ibeacons.Model.Requests;

public class Registros {

    public Registros() {
    }

    //Lista de ID dos Beacons
    public static final String ICE = "IBEACON-d2ee302bdd7c";
    public static final String BLUEBERRY = "IBEACON-e2700cc77817";
    public static final String MINT = "IBEACON-f178173700b2";

    private String prontuario;
    private String idBeacon;
    private String beaconName;
    private int rssi;
    private int dbm;
    private Double distancia;
    private int bateria;
    private int delay;
    private String dataHora;

    public String getProntuario() {
        return prontuario;
    }

    public void setProntuario(String prontuario) {
        this.prontuario = prontuario;
    }

    public String getIdBeacon() {
        return idBeacon;
    }

    public void setIdBeacon(String idBeacon) {
        this.idBeacon = idBeacon;
    }

    public String getBeaconName() {
        return beaconName;
    }

    public void setBeaconName(String beaconName)    {
        switch (beaconName){
            case BLUEBERRY:
                this.beaconName = "Blueberry";
                break;
            case ICE:
                this.beaconName = "Ice";
                break;
            case MINT:
                this.beaconName = "Mint";
                break;
            default: this.beaconName = "";
                break;
        }
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getDbm() {
        return dbm;
    }

    public void setDbm(int dbm) {
        this.dbm = dbm;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }

    public int getBateria() {
        return bateria;
    }

    public void setBateria(int bateria) {
        this.bateria = bateria;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }
}
