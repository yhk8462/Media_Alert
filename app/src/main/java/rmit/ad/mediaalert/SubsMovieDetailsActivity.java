package rmit.ad.mediaalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import rmit.ad.mediaalert.tvShows.TvShowActivity;

public class SubsMovieDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private String subsKey;
    private Handler handler;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subs_movie_details);

        drawerLayout = findViewById(R.id.drawer);
        drawerLayout.addDrawerListener(toggle);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        Button button = findViewById(R.id.btnSidebar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        Intent intent = getIntent();
        final String title=(String)intent.getExtras().get("title");
        double vote=(double)intent.getExtras().get("vote");
        final int ID=(int)intent.getExtras().get("ID");
        String img=(String)intent.getExtras().get("img");
        boolean isAdult=(boolean)intent.getExtras().get("adult");
        String releaseDate=(String)intent.getExtras().get("releaseDate");
        String overview=(String)intent.getExtras().get("overview");
        String language=(String)intent.getExtras().get("language");

        handler = new Handler();


        TextView txtTitle = findViewById(R.id.title);
        TextView txtReleaseDate = findViewById(R.id.listDate);
        TextView txtIsAdult = findViewById(R.id.isAdult);
        TextView txtVoteAverage = findViewById(R.id.voteAverage);
        TextView txtOriginalLanguage = findViewById(R.id.originalLanguage);
        TextView txtOverview = findViewById(R.id.overview);
        ImageView Image = findViewById(R.id.Image);

        Picasso.with(SubsMovieDetailsActivity.this).load("http://image.tmdb.org/t/p/w185/"+img).into(Image);

        txtTitle.setText(title);
        txtReleaseDate.setText("Release Date: "+releaseDate);
        txtOverview.setText("Overview: "+overview);
        txtOriginalLanguage.setText("Original Language: "+language);
        txtVoteAverage.setText("Vote Average: "+vote);
        if(isAdult){
            txtIsAdult.setText("This movie is for people older than 18");
        }
        else{
            txtIsAdult.setText("This movie is for everyone");
        }

        final Button button1 = findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                final String uid = firebaseUser.getUid();
                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference().child("Users").child(uid).child("ListOfSubsMovie")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                    Map<String,Object> movieID = (HashMap<String,Object>) dataSnapshot1.getValue();
                                    final String IDOfMovie = movieID.get("movieID").toString();
                                    if(IDOfMovie.equals(String.valueOf(ID))){
                                        subsKey=dataSnapshot1.getKey();
                                        return;

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                button1.setVisibility(View.GONE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        firebaseDatabase.getReference().child("Users").child(uid).child("ListOfSubsMovie").child(subsKey).removeValue();

                    }
                }, 1000);
                Intent sub = new Intent(SubsMovieDetailsActivity.this,Subs.class);
                startActivity(sub);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.home:
                Toast.makeText(this,"Home selected",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.movie:
                Toast.makeText(this,"movie selected",Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this,Movies.class);
                startActivity(intent2);
                break;
            case R.id.games:
                Toast.makeText(this,"games selected",Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this,Games.class);
                startActivity(intent3);
                break;
            case R.id.tvshows:
                Toast.makeText(this,"tvshows selected",Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(this, TvShowActivity.class);
                startActivity(intent4);
                break;
            case R.id.sub:
                Toast.makeText(this,"sub selected",Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(this,Subs.class);
                startActivity(intent5);
                break;
            case R.id.profile:
                Toast.makeText(this, "profile selected", Toast.LENGTH_SHORT).show();
                Intent intent7 = new Intent(this,ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.logout:
                Toast.makeText(this, "Loggin out", Toast.LENGTH_SHORT).show();
                Intent intent6 = new Intent(this,Login.class);
                startActivity(intent6);
                break;
        }
        return false;
    }
}
