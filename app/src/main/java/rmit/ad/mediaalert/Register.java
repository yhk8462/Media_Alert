package rmit.ad.mediaalert;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import rmit.ad.mediaalert.notification.SharedPrefManager;

public class Register extends AppCompatActivity {

    private final String TAG = "Register";
    private FirebaseAuth mAuth;
    EditText name, email, password, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();

        final Button create = findViewById(R.id.btnCreate);

        name = findViewById(R.id.edtName);
        password = findViewById(R.id.edtPassword);
        email = findViewById(R.id.edtEmail);
        phone = findViewById(R.id.edtPhone);
        final String strName = name.getText().toString();
        final String strPassword = password.getText().toString();
        final String strEmail = email.getText().toString();
        final String strPhone = phone.getText().toString();

        if(strName.isEmpty()){
            name.setError("Error no input");
        }
        if(strEmail.isEmpty()){
            email.setError("Error no input");
        }
        if(strPassword.isEmpty()){
            password.setError("Error no input");
        }
        if(strPhone.isEmpty()){
            phone.setError("Error no input");
        }
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().isEmpty()){
                    return;
                }
                if(email.getText().toString().isEmpty()){
                    return;
                }
                if(password.getText().toString().isEmpty()){
                    return;
                }
                if(phone.getText().toString().isEmpty()){
                    return;
                }

                create(); //Email and password auth
                Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();


            }
        });
    }


    private void saveUserInDb(String uuId,String email,String password,String name,String phone) {
        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        User user = new User(email,password,name,phone, token);
        FirebaseDatabase.getInstance().getReference().child("Users").child(uuId).setValue(user);

    }

    public void create() {
        //String NewEmail, NewPassword;

        EditText mEmail = findViewById(R.id.edtEmail);
        EditText mPassword = findViewById(R.id.edtPassword);

        final String NewEmail = mEmail.getText().toString();
        final String NewPassword = mPassword.getText().toString();

        name = findViewById(R.id.edtName);
        phone = findViewById(R.id.edtPhone);
        final String strName = name.getText().toString();
        final String strPassword = password.getText().toString();
        final String strEmail = email.getText().toString();
        final String strPhone = phone.getText().toString();

        mAuth.createUserWithEmailAndPassword(NewEmail, NewPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail: success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            String userId = mAuth.getUid();
                            saveUserInDb(userId,NewEmail,NewPassword,strName,strPhone);
                            Intent intent = new Intent(Register.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            Log.w(TAG, "createUserWithEmail: failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });

    }
}