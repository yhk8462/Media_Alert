package rmit.ad.mediaalert.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import rmit.ad.mediaalert.Games;
import rmit.ad.mediaalert.Login;
import rmit.ad.mediaalert.MainActivity;
import rmit.ad.mediaalert.Movies;
import rmit.ad.mediaalert.R;

public class AdminHomePage extends AppCompatActivity {
    Button btnMovie, btnGame, btnTv, btnSignout;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_home_activity);

        btnMovie = findViewById(R.id.btnMovie);
        btnGame = findViewById(R.id.btnGame);
        btnTv = findViewById(R.id.btnTv);
        btnSignout = findViewById(R.id.btnSignOut);

        btnMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AdminHomePage.this, AddMovieActivity.class);
                startActivity(intent1);
            }
        });
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(AdminHomePage.this, AddGamesActivity.class);
                startActivity(intent2);
            }
        });
        btnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(AdminHomePage.this, AddTvShowActivity.class);
                startActivity(intent3);
            }
        });
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(AdminHomePage.this, Login.class);
                startActivity(intent4);
            }
        });
    }
}