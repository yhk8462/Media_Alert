package rmit.ad.mediaalert;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

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

import static android.os.Build.ID;

public class GameDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private GameList gameList;
    private FirebaseAuth mAuth;
    private String email="";
    private String key="";
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_details);

        drawerLayout = findViewById(R.id.drawer);
        drawerLayout.addDrawerListener(toggle);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        final Button button = findViewById(R.id.btnSidebar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        TextView header = findViewById(R.id.header);
        TextView gameName = findViewById(R.id.game_name);
        TextView gameDate = findViewById(R.id.game_date);
        TextView gameCompany = findViewById(R.id.game_company);
        TextView gamePlatform = findViewById(R.id.game_platform);
        TextView gameDes = findViewById(R.id.game_des);
        ImageView gameImg = findViewById(R.id.game_image);

        //Get data and display ---------------------------------------------------------------------
        Intent details = getIntent();
        final String name = (String) details.getExtras().get("name");
        String date = (String) details.getExtras().get("date");
        String company = (String) details.getExtras().get("company");
        String des = (String) details.getExtras().get("des");
        String platform = (String) details.getExtras().get("platform");
        String imageURL = (String) details.getExtras().get("imageURL");
        gameList = new GameList(name, date, company, platform, des, imageURL);

        header.setText(name);
        Picasso.with(GameDetails.this).load(imageURL).into(gameImg);
        gameName.setText(name);
        gameDate.setText("Release Date: "+"\n"+date);
        gameCompany.setText("Produced by: "+"\n"+company);
        gamePlatform.setText("Available on: "+"\n"+platform);
        gameDes.setText(des);

        //Subscribe button -------------------------------------------------------------------------
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("Games");

        final Handler handler = new Handler();
        final Button btnSub = findViewById(R.id.button);
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null){
                    email = firebaseUser.getEmail();
                    Toast.makeText(GameDetails.this, "Email: "+email, Toast.LENGTH_SHORT).show();
                    firebaseDatabase.getReference().child("Users")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        User user = ds.getValue(User.class);
                                        Toast.makeText(GameDetails.this, "User "+user.getEmail(), Toast.LENGTH_SHORT).show();
                                        key = ds.getKey();

                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            firebaseDatabase.getReference().child("Users").child(key).child("ListOfSubsGames").child(name).setValue(gameList);
                        }
                    }, 5000);
                }
            }
        });
        /*
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser!=null) {
            email = firebaseUser.getEmail();
            Toast.makeText(GameDetails.this,"Email: "+email,Toast.LENGTH_SHORT).show();
            firebaseDatabase.getReference().child("Users")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                User user = ds.getValue(User.class);
                                String key = ds.getKey();

                                if (user.getEmail().equals(email)) {
                                    firebaseDatabase.getReference().child("Users").child(key).child("ListOfSubsGames")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                        Map<String,Object> gameName = (HashMap<String,Object>) dataSnapshot1.getValue();
                                                        if (gameName.get("name").toString().equals(name)) {
                                                            btnSub.setVisibility(View.GONE);
                                                            break;
                                                        }
                                                        else {
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
            btnSub.setVisibility(View.GONE);
        }

         */
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
                Intent intent5 = new Intent(this,Subs.class);
                startActivity(intent5);
                break;
        }
        return false;
    }


}