package gamal.myappnew.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {
    EditText password,email;
    Button btn_login;
    FirebaseAuth auth;
    AlertDialog pd;
 TextView forgetpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        password=findViewById(R.id.password);
        email=findViewById(R.id.email);
        btn_login=findViewById(R.id.btn_login);
        pd=new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        auth=FirebaseAuth.getInstance();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sing In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Allfileds are required", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please, Enter your email...", Toast.LENGTH_SHORT).show();

                } else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please, Enter your password...", Toast.LENGTH_SHORT).show();

                } else if (password.getText().toString().length() < 6) {
                    Toast.makeText(LoginActivity.this, "Please, password must be at least 6 charchters.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    pd.show();
                    LoginUser( email.getText().toString(), password.getText().toString());
                }
            }
        });
       forgetpassword= findViewById(R.id.forget_password);
       forgetpassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
                    }
                });
    }

    private void LoginUser(String email, final String password) {

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    pd.dismiss();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }else {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "Authentication Failed !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
