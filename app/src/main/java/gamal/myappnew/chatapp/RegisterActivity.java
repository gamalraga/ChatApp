package gamal.myappnew.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {
EditText username,password,email;
Button btn_register;
FirebaseAuth auth;
AlertDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        email=findViewById(R.id.email);
        btn_register=findViewById(R.id.btn_register);
        pd=new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        auth=FirebaseAuth.getInstance();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().isEmpty() || email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Allfileds are required", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please, Enter your email...", Toast.LENGTH_SHORT).show();

                } else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please, Enter your password...", Toast.LENGTH_SHORT).show();

                } else if (password.getText().toString().length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Please, password must be at least 6 charchters.", Toast.LENGTH_SHORT).show();
                }
                else if (username.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please, Enter your name...", Toast.LENGTH_SHORT).show();

                }
                    else
                 {
                    RegisternewUser(username.getText().toString(), email.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    private void RegisternewUser(final String username, String email, String password) {
        pd.show();
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("id",auth.getCurrentUser().getUid());
                            hashMap.put("username",username);
                            hashMap.put("imageURL","default");
                            hashMap.put("status","offline");
                            hashMap.put("search",username.toLowerCase());
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(auth.getCurrentUser().getUid())
                            .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    pd.dismiss();
                                    if (task.isSuccessful())
                                    {
                                        startActivity(new Intent(RegisterActivity.this,MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                                        finish();
                                    }
                                }
                            });


                        }else {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "You can't Register with this email and password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
