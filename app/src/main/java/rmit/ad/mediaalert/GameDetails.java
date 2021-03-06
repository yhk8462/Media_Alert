package rmit.ad.mediaalert;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
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

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import rmit.ad.mediaalert.notification.SharedPrefManager;
import rmit.ad.mediaalert.tvShows.TvShowActivity;

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
        final String date = (String) details.getExtras().get("date");
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
                    firebaseDatabase.getReference().child("Users")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        User user = ds.getValue(User.class);
                                        key = ds.getKey();
                                        if (user.getEmail().equals(email)) {
                                            return;
                                        }
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
                    }, 1000);

                    if (android.os.Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }

                    saveInServer(name, date);
                    SubscribeDialog subscribeDialog = new SubscribeDialog();
                    Bundle args = new Bundle();
                    args.putString("name", name);
                    subscribeDialog.setArguments(args);
                    subscribeDialog.show(getSupportFragmentManager(), "Sub");
                }
            }
        });

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser!=null) {
            email = firebaseUser.getEmail();
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
                                                            return;
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


    }

    private void saveInServer(String name, String releaseDate) {
        try {
            URL url = new URL("http://13.229.128.235:8080/api/v1/sub/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            final String token = SharedPrefManager.getInstance(this).getDeviceToken();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("instanceId", token);
            jsonParam.put("name", name);
            jsonParam.put("type", "Game");
            jsonParam.put("releaseDate", releaseDate);


            Log.i("JSON", jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG", conn.getResponseMessage());

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
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