package gamal.myappnew.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class ResetPasswordActivity extends AppCompatActivity {
EditText send_email;
Button reset;
AlertDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Your Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         send_email=findViewById(R.id.send_email);
         reset=findViewById(R.id.btn_rest);
        pd=new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

        reset.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (send_email.getText().toString().isEmpty())
                 {
                     Toast.makeText(ResetPasswordActivity.this, "Please ,Wriet email...", Toast.LENGTH_SHORT).show();
                 }else {
                     pd.show();
                     FirebaseAuth.getInstance().sendPasswordResetEmail(send_email.getText().toString())
                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             pd.dismiss();
                             if (task.isSuccessful())
                             {
                                 Toast.makeText(ResetPasswordActivity.this, "Please check your email...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                             }else {
                                 Toast.makeText(ResetPasswordActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
                 }
             }
         });
    }
}
