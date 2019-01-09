package com.example.luispaulo.ibeacons.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.luispaulo.ibeacons.Model.Requests.UsuarioRegistro;
import com.example.luispaulo.ibeacons.Model.Token;
import com.example.luispaulo.ibeacons.Model.Usuario;
import com.example.luispaulo.ibeacons.R;
import com.example.luispaulo.ibeacons.Sqlite.SQL;
import com.example.luispaulo.ibeacons.Sqlite.SQL_Tokens;
import com.example.luispaulo.ibeacons.Sqlite.SQL_UserLogin;
import com.example.luispaulo.ibeacons.Sqlite.SQL_Usuario;
import com.example.luispaulo.ibeacons.Utils.Projeto;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Perfil extends AppCompatActivity {

    private Boolean boolGravou = false;
    private static final String PASSWD = "PASSWD";

    private TextView txtProntuario;
    private TextView lblToken;
    private EditText txtSenha;
    private EditText txtNome;
    private EditText txtSobrenome;
    private EditText txtData;
    private EditText txtCPF;
    private EditText txtTelefone;
    private EditText txtEmail;
    private AppCompatButton btnGerar;
    private ImageView imgGerado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        this.txtProntuario = (TextView) findViewById(R.id.txtPerfilProntuario);
        this.lblToken = (TextView) findViewById(R.id.lblPerfilToken);
        this.txtSenha = (EditText) findViewById(R.id.txtPerfilSenha);
        this.txtNome = (EditText) findViewById(R.id.txtPerfilNome);
        this.txtSobrenome = (EditText) findViewById(R.id.txtPerfilSobrenome);
        this.txtData = (EditText) findViewById(R.id.txtPerfilData);
        this.txtCPF = (EditText) findViewById(R.id.txtPerfilCPF);
        this.txtTelefone = (EditText) findViewById(R.id.txtPerfilTelefone);
        this.txtEmail = (EditText) findViewById(R.id.txtPerfilEmail);
        this.btnGerar = (AppCompatButton) findViewById(R.id.btnPerfilToken);
        this.imgGerado = (ImageView) findViewById(R.id.imgPerfilGerado);
        (findViewById(R.id.btnPerfilSalvar)).setOnClickListener(clickHandler);
        (findViewById(R.id.btnPerfilToken)).setOnClickListener(clickHandler);

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
                txtCPF.setTextColor((!Projeto.ValidaCPF(s.toString()) ? Color.RED : getResources().getColor(R.color.LabelsColor)));
            }
        });


        //Mascaras: Telefone, CPF e Data de nascimento
        txtData.addTextChangedListener(new MaskTextWatcher(txtData, new SimpleMaskFormatter("NN/NN/NNNN")));
        txtCPF.addTextChangedListener(new MaskTextWatcher(txtCPF, new SimpleMaskFormatter("NNN.NNN.NNN-NN")));
        txtTelefone.addTextChangedListener(new MaskTextWatcher(txtTelefone, new SimpleMaskFormatter("(NN) NNNNN-NNNN")));

        LimpaCampos();
    }

    View.OnClickListener clickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnPerfilSalvar:
                    Salvar();
                    break;

                case R.id.btnPerfilToken:
                    if(new Projeto(Perfil.this).isConnected())
                        GerarToken();
                    else
                        Toast.makeText(Perfil.this, "Não é possível conectar-se à internet.\nPor favor verifique sua conexão!", Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        }
    };

    private void GerarToken() {
        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Projeto.Request(Perfil.this).RetornaRequest(Projeto.Request.REGISTER);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(Perfil.this, "Erro na requisição.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String token = new Projeto.Request(Perfil.this).TrataRetornoRequest(response.body().string());
                        Perfil.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (token != null && !token.equals("null") && !token.equals(Projeto.Request.ERROR_REQUEST) && !token.equals(Projeto.Request.ERRO))
                                    InsereGrant(token);
                                else
                                    Toast.makeText(Perfil.this, "Ocorreu um erro na requisição", Toast.LENGTH_SHORT).show();
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

    private void InsereGrant(String token) {
        try {
            new SQL_Tokens(Perfil.this).Inserir(new Token(txtProntuario.getText().toString(), token, ""));

            this.btnGerar.setVisibility(View.GONE);
            this.imgGerado.setVisibility(View.VISIBLE);
        } catch (Exception ex) {
            Toast.makeText(Perfil.this, "Erro na inserção.", Toast.LENGTH_SHORT).show();
        }
    }

    public void Salvar() {
        String senha = "";

        if (ValidaCampos()) {
            senha = txtSenha.getText().toString().equals(PASSWD) ? RetornaSenhaAtual() : new Projeto(Perfil.this).Encode(txtSenha.getText().toString());
            Usuario user = PreencheUser();
            user.setUserSenha(senha);

            try {
                new SQL_Usuario(Perfil.this).Alterar(user);

                Toast.makeText(this, "Alterações Concluídas!", Toast.LENGTH_SHORT).show();
                this.finish();
            } catch (Exception ex) {
                Toast.makeText(this, "Ocorreu um erro durante a alteração", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Usuario PreencheUser() {
        Usuario user = new Usuario();

        user.setUserProntuario(txtProntuario.getText().toString());
        user.setUserNome(txtNome.getText().toString());
        user.setUserSobrenome(txtSobrenome.getText().toString());
        user.setUserDataNasc(txtData.getText().toString());
        user.setUserCPF(txtCPF.getText().toString());
        user.setUserTelefone(txtTelefone.getText().toString());
        user.setUserEmail(txtEmail.getText().toString());

        return user;
    }

    private String RetornaSenhaAtual() {
        try {
            return new SQL_UserLogin(Perfil.this).Pesquisar(txtProntuario.getText().toString()).getUserSenha();
        } catch (Exception ex) {
            Toast.makeText(this, "Erro ao recuperar a senha.", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    private boolean ValidaCampos() {
        if (!txtSenha.getText().toString().isEmpty()) {
            if (!txtNome.getText().toString().isEmpty()) {
                if (!txtSobrenome.getText().toString().isEmpty()) {
                    if (!txtData.getText().toString().isEmpty()) {
                        if (!txtCPF.getText().toString().isEmpty() && Projeto.ValidaCPF(txtCPF.getText().toString())) {
                            if (!txtTelefone.getText().toString().isEmpty()) {
                                if (!txtEmail.getText().toString().isEmpty())
                                    return true;
                                else {
                                    Toast.makeText(this, "O email é obrigatório.", Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            } else {
                                Toast.makeText(this, "O telefone é obrigatório.", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        } else {
                            Toast.makeText(this, "CPF é obrigatório ou está inválido.", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    } else {
                        Toast.makeText(this, "A data de nascimento é obrigatória.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(this, "O sobrenome é obrigatório.", Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else {
                Toast.makeText(this, "O nome é obrigatório.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(this, "A senha não pode estar em branco.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void PreencheCampos() {
        try {
            Usuario user = new SQL_Usuario(Perfil.this).Pesquisar(new Projeto.Preferences(Perfil.this).getUserLogado());

            this.txtProntuario.setText(user.getUserProntuario());
            this.txtSenha.setText(PASSWD);
            this.txtNome.setText(user.getUserNome());
            this.txtSobrenome.setText(user.getUserSobrenome());
            this.txtData.setText(user.getUserDataNasc());
            this.txtCPF.setText(user.getUserCPF());
            this.txtTelefone.setText(user.getUserTelefone());
            this.txtEmail.setText(user.getUserEmail());
            //Verifico o Token_Grant
            if (user.getToken() != null && !user.getToken().getToken_Grant().isEmpty()) {
                this.btnGerar.setVisibility(View.GONE);
                this.imgGerado.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Ocorreu um erro na pesquisa.", Toast.LENGTH_SHORT).show();
        }
    }

    private void LimpaCampos() {
        this.txtSenha.setText("");
        this.txtNome.setText("");
        this.txtSobrenome.setText("");
        this.txtData.setText("");
        this.txtCPF.setText("");
        this.txtTelefone.setText("");
        this.txtEmail.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreencheCampos();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_perfil_save) {
            Salvar();
        }
        return super.onOptionsItemSelected(item);
    }

}
