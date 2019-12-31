package rmit.ad.mediaalert.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import rmit.ad.mediaalert.R;
import rmit.ad.mediaalert.Subs;

public class AdminLogin extends AppCompatActivity {
    private static final String TAG = AdminLogin.class.getName();
    private final static String ADMIN_USERNAME = "admin@mediaalert.com";
    EditText mEmail, mPassword;
    Button mLogin;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);

        mEmail = findViewById(R.id.edtEmail);
        mPassword = findViewById(R.id.edtPassword);
        mLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(AdminLogin.this, "Successfully signed in with: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Invalid admin credentials!");
                }

            }
        };

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();
                if (!email.equals("") && !pass.equals("")) {
                    if (!email.equals(ADMIN_USERNAME)) {
                        Log.w(TAG, "Invalid admin credentials");
                        Toast.makeText(AdminLogin.this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(AdminLogin.this, "Logging in" + user, Toast.LENGTH_SHORT).show();
                                Intent iSub = new Intent(AdminLogin.this, AdminHomePage.class);
                                startActivity(iSub);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "Signing in to the admin account failed!", task.getException());
                                Toast.makeText(AdminLogin.this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(AdminLogin.this, "Empty username or password.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}