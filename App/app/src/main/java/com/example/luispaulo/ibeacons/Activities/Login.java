package com.example.luispaulo.ibeacons.Activities;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.luispaulo.ibeacons.R;
import com.example.luispaulo.ibeacons.Utils.Projeto;

public class Login extends AppCompatActivity {

    private Button btnLogar;
    private TextView lblCadastrar;
    private TextView lblSenha;
    private EditText txtProntuario;
    private EditText txtSenha;
    private SwitchCompat swKeepConnect;
    boolean keepConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Projeto.Preferences pref = new Projeto.Preferences(Login.this);
        keepConnected = pref.getSwUserConnected();

        if (keepConnected && !pref.getUserLogado().isEmpty()) {
            startActivity(new Intent(Login.this, Principal.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
        else {

            btnLogar = (Button) findViewById(R.id.btnLogar);
            lblCadastrar = (TextView) findViewById(R.id.lblCadastrar);
            lblSenha = (TextView) findViewById(R.id.lblEsqueciSenha);
            txtProntuario = (EditText) findViewById(R.id.txtProntuario);
            txtSenha = (EditText) findViewById(R.id.txtSenha);
            swKeepConnect = (SwitchCompat) findViewById(R.id.swKeepConnect);

            //Sublinho as labels
            lblCadastrar.setPaintFlags(lblCadastrar.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            lblSenha.setPaintFlags(lblSenha.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


            lblCadastrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), CadastroConta_Prontuario.class));
                }
            });

            btnLogar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Projeto.Preferences pref = new Projeto.Preferences(Login.this);
                        pref.sw_keepConnected(swKeepConnect.isChecked());
                        new Projeto(Login.this).Logar(txtProntuario.getText().toString(), txtSenha.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        swKeepConnect.setChecked(keepConnected);
    }
}
