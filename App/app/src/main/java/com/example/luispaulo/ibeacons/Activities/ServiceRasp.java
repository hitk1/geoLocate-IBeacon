package com.example.luispaulo.ibeacons.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.example.luispaulo.ibeacons.Model.Requests.Registros;
import com.example.luispaulo.ibeacons.R;
import com.example.luispaulo.ibeacons.Servicos_Broadcasts.Service_RaspII;
import com.example.luispaulo.ibeacons.Sqlite.SQL;
import com.example.luispaulo.ibeacons.Sqlite.SQL_Registros;
import com.example.luispaulo.ibeacons.Utils.Projeto;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;

public class ServiceRasp extends AppCompatActivity {

    private Button btnStart;
    private Button btnCancel;
    private RadioGroup group;
    public TextView lblRegistros;
    private boolean isEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_rasp);

        btnStart = (Button)findViewById(R.id.btnStart);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        group = (RadioGroup) findViewById(R.id.grpRadios);
        lblRegistros = (TextView) findViewById(R.id.lblServiceRegistros);

        isEnabled = (PendingIntent.getBroadcast(ServiceRasp.this, 11,
                new Intent("execute service"), PendingIntent.FLAG_NO_CREATE) == null);

        if(!isEnabled) { PaintBtn(); }


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEnabled){
                    Start();
                    PaintBtn();
                }
                else
                    Toast.makeText(ServiceRasp.this, "O Serviço já está em execução!", Toast.LENGTH_SHORT).show();
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarm.cancel(ReturnPending());

                DissPaintBtn();

                Projeto.Preferences pref = new Projeto.Preferences(ServiceRasp.this);
                pref.clearDelay();

                //Finalizo o Serviço
                Boolean status = false;
                status = stopService(new Intent(ServiceRasp.this, Service_RaspII.class));


            }
        });
    }

    public void AtualizaRegistros() {
        try{
            List<Registros> list = new SQL_Registros(ServiceRasp.this).CarregarLista();
            lblRegistros.setText(String.valueOf(list.size()) + " Registros Armazenados");
        }
        catch (SQLiteException sqlEx){
            sqlEx.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void DissPaintBtn() {
        btnStart.setBackgroundColor(Color.LTGRAY);
        btnStart.setTextColor(Color.BLACK);
    }

    private void Start() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);       //dalay

        int time;

        switch(group.getCheckedRadioButtonId()){

            case R.id.rdbCinco:
                time = 5;
                break;

            case R.id.rdbDez:
                time = 10;
                break;

            case R.id.rdbQuinze:
                time = 15;
                break;

            case R.id.rdbVinte:
                time = 20;
                break;

            default: time = 0; break;
        }
        long repeat = 1000*60*time;

        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeat, ReturnPending());

        Projeto.Preferences pref = new Projeto.Preferences(ServiceRasp.this);

        //Armazeno o delay para o relatório
        pref.saveDelay(time);

        isEnabled = true;
    }

    private PendingIntent ReturnPending(){
        return PendingIntent.getBroadcast(ServiceRasp.this, 11, new Intent("execute service"), 0);
    }

    private void PaintBtn(){
        btnStart.setBackgroundColor(0x9052A7D8);
        btnStart.setTextColor(Color.WHITE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AtualizaRegistros();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }
}
