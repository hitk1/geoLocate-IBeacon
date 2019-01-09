package com.example.luispaulo.ibeacons.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.luispaulo.ibeacons.Model.Requests.UsuarioRegistro;
import com.example.luispaulo.ibeacons.Model.Token;
import com.example.luispaulo.ibeacons.Model.Usuario;
import com.example.luispaulo.ibeacons.R;
import com.example.luispaulo.ibeacons.Sqlite.SQL_Tokens;
import com.example.luispaulo.ibeacons.Sqlite.SQL_UserLogin;
import com.example.luispaulo.ibeacons.Sqlite.SQL_Usuario;
import com.example.luispaulo.ibeacons.Utils.Projeto;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.HttpException;
import retrofit2.http.Url;

public class CadastroConta_Informacoes extends AppCompatActivity {

    //Parâmetros recuperados da activity

    protected boolean boolGravou = false;
    private String pront;
    private String passwd;

    private EditText txtNome;
    private EditText txtSobrenome;
    private EditText txtData;
    private EditText txtCPF;
    private EditText txtTelefone;
    private EditText txtEmail;
    private Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_conta_informacoes);

        Projeto.Preferences shared = new Projeto.Preferences(CadastroConta_Informacoes.this);
        pront = shared.getProntuario();
        passwd = shared.getPassWd();

        txtNome = (EditText)findViewById(R.id.txtCadastroNome);
        txtSobrenome = (EditText)findViewById(R.id.txtCadastroSobrenome);
        txtTelefone = (EditText)findViewById(R.id.txtCadastroTelefone);
        txtEmail = (EditText)findViewById(R.id.txtCadastroEmail);
        txtData = (EditText)findViewById(R.id.txtCadastroData);
        txtCPF = (EditText)findViewById(R.id.txtCadastroCPF);
        btnCadastrar = (Button)findViewById(R.id.btnCadastrar);

        //Seto a cor do hint padrão para a data de nascimento e CPF
        txtData.setHintTextColor(getResources().getColor(R.color.LineShapes));
        txtCPF.setHintTextColor(getResources().getColor(R.color.LineShapes));

        //Listener para validar o CPF no onChange
        txtCPF.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                txtCPF.setTextColor((!Projeto.ValidaCPF(s.toString()) ? Color.RED : getResources().getColor(R.color.LineShapes)));
            }
        });


        //Mascaras: Telefone, CPF e Data de nascimento
        txtData.addTextChangedListener(new MaskTextWatcher(txtData, new SimpleMaskFormatter("NN/NN/NNNN")));
        txtCPF.addTextChangedListener(new MaskTextWatcher(txtCPF, new SimpleMaskFormatter("NNN.NNN.NNN-NN")));
        txtTelefone.addTextChangedListener(new MaskTextWatcher(txtTelefone, new SimpleMaskFormatter("(NN) NNNNN-NNNN")));

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Usuario user = null;
                if(ValidaCampos()){
                    user = PreencheUsuario();

                    try{
                        //Insiro o usuário nas tabelas respectivas
                        new SQL_Usuario(CadastroConta_Informacoes.this).Inserir(user);
                        //Limpo o SharedPreferences
                        new Projeto.Preferences(CadastroConta_Informacoes.this).clearCadastroUser();

                        Toast.makeText(CadastroConta_Informacoes.this, "A sua conta foi registrada com sucesso!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(CadastroConta_Informacoes.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        boolGravou = true;
                    }
                    catch (SQLException sqlEx){
                        Toast.makeText(CadastroConta_Informacoes.this, "Erro na inserção do usuário.", Toast.LENGTH_SHORT).show();
                        sqlEx.printStackTrace();
                    }
                    catch (Exception ex){
                        Toast.makeText(CadastroConta_Informacoes.this, "Ocorreu um erro.", Toast.LENGTH_SHORT).show();
                        ex.printStackTrace();
                    }
                }
            }
        });

        txtNome.requestFocus();
    }

    private Usuario PreencheUsuario() {
        Usuario user = new Usuario();
        user.setUserProntuario(pront);
        user.setUserSenha(passwd);
        user.setUserNome(txtNome.getText().toString());
        user.setUserSobrenome(txtSobrenome.getText().toString());
        user.setUserDataNasc(txtData.getText().toString());
        user.setUserCPF(txtCPF.getText().toString());
        user.setUserTelefone(txtTelefone.getText().toString());
        user.setUserEmail(txtEmail.getText().toString().toLowerCase());

        return user;
    }

    private void PreencheCampos(Usuario user){
        txtNome.setText(user.getUserNome());
        txtSobrenome.setText(user.getUserSobrenome());
        txtData.setText(user.getUserDataNasc());
        txtCPF.setText(user.getUserCPF());
        txtTelefone.setText(user.getUserTelefone());
        txtEmail.setText(user.getUserEmail());
    }

    private boolean ValidaCampos() {
        if(!txtNome.getText().toString().isEmpty()){
            if(!txtSobrenome.getText().toString().isEmpty()){
                if(!txtData.getText().toString().isEmpty()){
                    if(!txtCPF.getText().toString().isEmpty() && Projeto.ValidaCPF(txtCPF.getText().toString())){
                        if(!txtTelefone.getText().toString().isEmpty()){
                            if(!txtEmail.getText().toString().isEmpty())
                                return true;
                            else{
                                Toast.makeText(this, "O email é obrigatório.", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }
                        else{
                            Toast.makeText(this, "O telefone é obrigatório.", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                    else{
                        Toast.makeText(this, "CPF é obrigatório ou está inválido.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                else{
                    Toast.makeText(this, "A data de nascimento é obrigatória.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            else{
                Toast.makeText(this, "O sobrenome é obrigatório.", Toast.LENGTH_SHORT).show();
                return false;
            }

        }
        else {
            Toast.makeText(this, "O nome é obrigatório.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreencheCampos(new Projeto.Preferences(CadastroConta_Informacoes.this).getInfUser());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!boolGravou) {
            new Projeto.Preferences(CadastroConta_Informacoes.this).saveUserInformacoes(PreencheUsuario());
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CadastroConta_Informacoes.this, CadastroConta_Prontuario.class));
        overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
    }
}
