package com.example.luispaulo.ibeacons.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luispaulo.ibeacons.Model.Requests.Registros;
import com.example.luispaulo.ibeacons.Model.Token;
import com.example.luispaulo.ibeacons.Model.Usuario;
import com.example.luispaulo.ibeacons.R;
import com.example.luispaulo.ibeacons.Sqlite.SQL_Registros;
import com.example.luispaulo.ibeacons.Sqlite.SQL_Tokens;
import com.example.luispaulo.ibeacons.Sqlite.SQL_Usuario;
import com.example.luispaulo.ibeacons.Utils.Projeto;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Recupero o header do drawer menu para colocar as informações do usuario logado
        View view = navigationView.getHeaderView(0);
        TextView txtMenuUserName = view.findViewById(R.id.menu_UserNome);
        TextView txtMenuUserEmail = view.findViewById(R.id.menu_UserEmail);

        try {
            Usuario user = new SQL_Usuario(Principal.this).Pesquisar(new Projeto.Preferences(Principal.this).getUserLogado());
            txtMenuUserName.setText(user.getUserNome());
            txtMenuUserEmail.setText(user.getUserEmail());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_deslogar:
                new Projeto(Principal.this).Deslogar();
                break;

            case R.id.menu_perfil:
                startActivity(new Intent(Principal.this, Perfil.class));
                break;

            case R.id.menu_sync:
                try {
                    //Verifica a conexão com a internet antes de realizar a transferência
                    if(new Projeto(Principal.this).isConnected()){
                        //Verifico se o token de concessão foi gerado
                        if (verificaGrant()) {
                            List<Registros> lstRegistros = new SQL_Registros(Principal.this).CarregarLista();

                            if (lstRegistros != null && lstRegistros.size() > 0)
                                new Transfer().execute(Principal.this);
                            else
                                Toast.makeText(Principal.this, "Não há registros a serem transferidos.", Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(Principal.this, R.style.DialogStyle);
                            dialog.setTitle("Atenção")
                                    .setMessage("Você não possui o token de concessão deseja gerá-lo?")
                                    .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            GerarToken();
                                        }
                                    })
                                    .setNeutralButton("NÃO", null)
                                    .create().show();
                        }
                    }
                    else
                        Toast.makeText(Principal.this, "Não é possível conectar-se à internet.\nPor favor verifique sua conexão!", Toast.LENGTH_LONG).show();
                } catch (SQLiteException sqliEx) {
                    Toast.makeText(this, "Erro ao consultar registros.", Toast.LENGTH_SHORT).show();
                    sqliEx.printStackTrace();
                } catch (Exception ex) {
                    Toast.makeText(this, "Erro ao consultar registros.", Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean verificaGrant() {
        try {
            Token token = new SQL_Tokens(Principal.this).Pesquisar(new Projeto.Preferences(Principal.this).getUserLogado());
            if (token != null && !token.getToken_Grant().isEmpty())
                return true;
            return false;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void GerarToken() {
        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Projeto.Request(Principal.this).RetornaRequest(Projeto.Request.REGISTER);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    final String erro = e.getMessage();
                    Principal.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (erro.contains("failed to connect") && erro.contains("after 10000ms")) {
                                Toast.makeText(Principal.this, "O servidor não está respondendo.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String token = new Projeto.Request(Principal.this).TrataRetornoRequest(response.body().string());

                        Principal.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (token != null && !token.equals("null") && !token.equals(Projeto.Request.ERROR_REQUEST) && !token.equals(Projeto.Request.ERRO)) {
                                        new SQL_Tokens(Principal.this)
                                                .Inserir(new Token(new Projeto.Preferences(Principal.this).getUserLogado(), token, ""));
                                        Toast.makeText(Principal.this, "Token gerado com sucesso!", Toast.LENGTH_LONG).show();
                                    } else
                                        Toast.makeText(Principal.this, "Ocorreu um erro na requisição", Toast.LENGTH_SHORT).show();

                                } catch (Exception ex) {
                                    Toast.makeText(Principal.this, "Erro na inserção.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            });
        } catch (Exception ex) {
            Toast.makeText(this, "Ocorreu um erro na requisição.", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_scan_poolling:
                startActivity(new Intent(this, ScanPoolling.class));
                break;

            case R.id.nav_service:
                startActivity(new Intent(this, ServiceRasp.class));
                break;

            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

class Transfer extends AsyncTask<Context, Void, Boolean> {


    @Override
    protected Boolean doInBackground(final Context... contexts) {
        String status = "";
        try {
            status = new Projeto.Request(contexts[0]).TransferirDados();

            //Se os dados foram transferidos, limpo os registros locais
            if (status.equals(Projeto.Request.ACCEPT)) {
                new SQL_Registros(contexts[0]).Delete();
                status = "Registros transferidos com sucesso!";
            }

            final String finalStatus = status;

            new Handler(contexts[0].getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(contexts[0], finalStatus, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SQLiteException sqlEx) {
            sqlEx.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

}
