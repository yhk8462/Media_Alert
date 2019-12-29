package rmit.ad.mediaalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MoviesDetailActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private MovieListObject movie;
    private FirebaseAuth mAuth;
    private String email="";
    private String key="";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_detail);
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
        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("Movies");
        Intent intent = getIntent();
        String title=(String)intent.getExtras().get("title");
        double vote=(double)intent.getExtras().get("vote");
        final int ID=(int)intent.getExtras().get("ID");
        String img=(String)intent.getExtras().get("img");
        boolean isAdult=(boolean)intent.getExtras().get("adult");
        String releaseDate=(String)intent.getExtras().get("releaseDate");
        String overview=(String)intent.getExtras().get("overview");
        String language=(String)intent.getExtras().get("language");
        movie = new MovieListObject(img,title,releaseDate,ID,isAdult,language,vote,overview);

        myRef.child(String.valueOf(ID)).setValue(movie);

        TextView txtTitle = findViewById(R.id.title);
        TextView txtReleaseDate = findViewById(R.id.listDate);
        TextView txtIsAdult = findViewById(R.id.isAdult);
        TextView txtVoteAverage = findViewById(R.id.voteAverage);
        TextView txtOriginalLanguage = findViewById(R.id.originalLanguage);
        TextView txtOverview = findViewById(R.id.overview);
        ImageView Image = findViewById(R.id.Image);
        final Handler handler = new Handler();

        Picasso.with(MoviesDetailActivity.this).load("http://image.tmdb.org/t/p/w185/"+img).into(Image);

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
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser!=null) {
                    email = firebaseUser.getEmail();
                    Toast.makeText(MoviesDetailActivity.this,"Email: "+email,Toast.LENGTH_SHORT).show();
                    firebaseDatabase.getReference().child("Users")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        User user = ds.getValue(User.class);
                                        key = ds.getKey();

                                        if (user.getEmail().equals(email)) {
                                            break;

                                        }
                                        //firebaseDatabase.getReference().child("Users").child(key).child("ListOfSubsMovie").push().child("movieID").setValue(ID);

                                    }



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            firebaseDatabase.getReference().child("Users").child(key).child("ListOfSubsMovie").push().child("movieID").setValue(ID);

                        }
                    }, 5000);

                }
                button1.setVisibility(View.GONE);
            }
        });

        //button1.setVisibility(View.VISIBLE);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser!=null) {
            email = firebaseUser.getEmail();
            Toast.makeText(MoviesDetailActivity.this,"Email: "+email,Toast.LENGTH_SHORT).show();
            firebaseDatabase.getReference().child("Users")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                User user = ds.getValue(User.class);
                                String key = ds.getKey();

                                if (user.getEmail().equals(email)) {
                                    firebaseDatabase.getReference().child("Users").child(key).child("ListOfSubsMovie")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                        Map<String,Object> movieID = (HashMap<String,Object>) dataSnapshot1.getValue();
                                                        if (Integer.valueOf(movieID.get("movieID").toString()).equals(ID)) {
                                                            button1.setVisibility(View.GONE);
                                                            break;
                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                }
                                //firebaseDatabase.getReference().child("Users").child(key).child("ListOfSubsMovie").push().child("movieID").setValue(ID);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }
        else{
            button1.setVisibility(View.GONE);
        }
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
                Intent intent4 = new Intent(this,TvShows.class);
                startActivity(intent4);
                break;
            case R.id.sub:
                Toast.makeText(this,"sub selected",Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(this,Login.class);
                startActivity(intent5);
                break;
        }
        return false;
    }
}
