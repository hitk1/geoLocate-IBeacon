package com.example.luispaulo.ibeacons.Activities;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luispaulo.ibeacons.R;
import com.example.luispaulo.ibeacons.Sqlite.SQL_Usuario;
import com.example.luispaulo.ibeacons.Utils.Projeto;

public class CadastroConta_Prontuario extends AppCompatActivity {

    public static final String TAG_PRONT = "Prontuario";
    public static final String TAGP_PASS = "Password";

    private ImageView btnProsseguir;
    private EditText txtProntuario;
    private EditText txtSenha;
    private EditText txtConfSenha;
    private TextView lblSenhaConfir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_conta_prontuario);

        btnProsseguir = (ImageView)findViewById(R.id.btnCadastroProsseguir);
        txtProntuario = (EditText)findViewById(R.id.txtCadastroProntuario);
        txtSenha = (EditText)findViewById(R.id.txtCadastroSenha);
        txtConfSenha = (EditText)findViewById(R.id.txtCadastroConfirmSenha);
        lblSenhaConfir = (TextView)findViewById(R.id.lblCadastroInfSenha);

        btnProsseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(ValidaCampos())
                        if(!new SQL_Usuario(CadastroConta_Prontuario.this).VerificaUsuario(txtProntuario.getText().toString().toLowerCase())){

                            //Salvo o usuário e a senha nas preferências compartilhadas
                            new Projeto.Preferences(CadastroConta_Prontuario.this).saveProntPasswd(txtProntuario.getText().toString().toLowerCase(),
                                    new Projeto(CadastroConta_Prontuario.this).Encode(txtSenha.getText().toString()));

                            //Passo para o próximo passo de cadastro
                            startActivity(new Intent(CadastroConta_Prontuario.this, CadastroConta_Informacoes.class));
                            overridePendingTransition(R.anim.slide_in_go, R.anim.slide_out_go);
                        }
                        else{
                            Projeto.DialogMessage(CadastroConta_Prontuario.this, getString(R.string.Atencao), getString(R.string.DialogUserCadastrado));
                            return;
                        }

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        //Eventos
        txtConfSenha.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            //Volta o padrão da label de confirmação de senha e desaparece com a label de senhas diferentes
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    txtConfSenha.setTextColor(getResources().getColor(R.color.LineShapes));
                    lblSenhaConfir.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Projeto.Preferences shared = new Projeto.Preferences(CadastroConta_Prontuario.this);
        txtProntuario.setText(shared.getProntuario());
    }

    private boolean ValidaCampos() {
        if(!txtProntuario.getText().toString().isEmpty()){
            if(!txtSenha.getText().toString().isEmpty()){
                if(!txtConfSenha.getText().toString().isEmpty()){
                    String senha = txtSenha.getText().toString();
                    String confirmSenha = txtConfSenha.getText().toString();

                    if(!confirmSenha.equals(senha)){
                        txtConfSenha.setTextColor(Color.RED);
                        lblSenhaConfir.setVisibility(View.VISIBLE);
                        txtSenha.requestFocus();
                        return false;
                    }
                    return true;
                }
                else{
                    Toast.makeText(this, "A senha de confirmação é obrigatória", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            else{
                Toast.makeText(this, "A senha é obrigatório", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else{
            Toast.makeText(this, "O prontuário deve ser preenchido", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
