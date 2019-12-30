package rmit.ad.mediaalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import rmit.ad.mediaalert.tvShows.TvShowActivity;

public class MainActivity extends AppCompatActivity {
    Button btnMovie,btnGame,btnTv,btnSub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
        btnMovie = findViewById(R.id.btnMovie);
        btnGame = findViewById(R.id.btnGame);
        btnTv = findViewById(R.id.btnTv);
        btnSub = findViewById(R.id.btnSub);

        btnMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this,Movies.class);
                startActivity(intent1);
            }
        });
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this,Games.class);
                startActivity(intent2);
            }
        });
        btnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(MainActivity.this, TvShowActivity.class);
                startActivity(intent3);
            }
        });
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(MainActivity.this,Login.class);
                startActivity(intent4);
            }
        });
    }

}
