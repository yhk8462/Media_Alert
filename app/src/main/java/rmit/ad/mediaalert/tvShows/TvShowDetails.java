package rmit.ad.mediaalert.tvShows;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rmit.ad.mediaalert.Games;
import rmit.ad.mediaalert.Login;
import rmit.ad.mediaalert.MainActivity;
import rmit.ad.mediaalert.Movies;
import rmit.ad.mediaalert.ProfileActivity;
import rmit.ad.mediaalert.R;
import rmit.ad.mediaalert.Subs;
import rmit.ad.mediaalert.SubscribeDialog;
import rmit.ad.mediaalert.UnSubscribeDialog;
import rmit.ad.mediaalert.User;
import rmit.ad.mediaalert.notification.SharedPrefManager;

public class TvShowDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private TvShowItem tvShowItem;
    private FirebaseAuth mAuth;
    private String email = "";
    private String key = "";
    private boolean showSubButton = true;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tv_show_description);

        drawerLayout = findViewById(R.id.drawer);
        drawerLayout.addDrawerListener(toggle);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        final Button button = findViewById(R.id.btnSidebar);
        final Button unsubscribe = findViewById(R.id.unsubscribe);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        TextView header = findViewById(R.id.header);
        TextView releaseDate = findViewById(R.id.txtReleaseDate);
        TextView category = findViewById(R.id.txtTvShowType);
        TextView description = findViewById(R.id.tvShowDescription);
        ImageView tvShowImg = findViewById(R.id.tvshowImage);


        //Get data and display ---------------------------------------------------------------------
        Intent details = getIntent();
        final String name = (String) details.getExtras().get("name");
        final String date = (String) details.getExtras().get("releaseDate");
        String tvshowCategory = (String) details.getExtras().get("type");
        String des = (String) details.getExtras().get("description");
        String imageURL = (String) details.getExtras().get("imageURL");

        tvShowItem = new TvShowItem(name, imageURL, des, date, tvshowCategory);

        Glide.with(TvShowDetails.this).load(imageURL).into(tvShowImg);
        header.setText(name);

        releaseDate.setText(date);
        category.setText(tvshowCategory);
        description.setText(des);

        //Subscribe button -------------------------------------------------------------------------
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("Games");

        final Handler handler = new Handler();
        final Button btnSub = findViewById(R.id.button);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final String uid = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("Users").child(uid).child("ListOfSubsTvShows");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(name)) {
                    showSubButton = false;
                    btnSub.setVisibility(View.GONE);
                    unsubscribe.setVisibility(View.VISIBLE);
                } else {
                    btnSub.setVisibility(View.VISIBLE);
                    unsubscribe.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (showSubButton) {
            btnSub.setVisibility(View.VISIBLE);
            unsubscribe.setVisibility(View.GONE);
        } else {
            btnSub.setVisibility(View.GONE);
            unsubscribe.setVisibility(View.VISIBLE);
        }

        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show success dialog
                SubscribeDialog subscribeDialog = new SubscribeDialog();
                Bundle args = new Bundle();
                args.putString("name", name);
                subscribeDialog.setArguments(args);
                subscribeDialog.show(getSupportFragmentManager(), "Sub");

                btnSub.setVisibility(View.GONE);
                unsubscribe.setVisibility(View.VISIBLE);

                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }

                // Save in server
                saveInServer(name, date);

                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
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
                            firebaseDatabase.getReference().child("Users").child(key).child("ListOfSubsTvShows").child(name).setValue(tvShowItem);
                        }
                    }, 1000);
                }
            }

        });


        unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show success dialog
                Intent intent5 = new Intent(TvShowDetails.this,Subs.class);
                startActivity(intent5);
                unsubscribe.setVisibility(View.GONE);
                btnSub.setVisibility(View.VISIBLE);

                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
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
                            firebaseDatabase.getReference().child("Users").child(key).child("ListOfSubsTvShows").child(name).removeValue();
                        }
                    }, 1000);
                }


            }
        });
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
            jsonParam.put("type", "TV Show");
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
        switch (menuItem.getItemId()) {
            case R.id.home:
                Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.movie:
                Toast.makeText(this, "movie selected", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, Movies.class);
                startActivity(intent2);
                break;
            case R.id.games:
                Toast.makeText(this, "games selected", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this, Games.class);
                startActivity(intent3);
                break;
            case R.id.tvshows:
                Toast.makeText(this, "tvshows selected", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(this, TvShowActivity.class);
                startActivity(intent4);
                break;
            case R.id.sub:
                Toast.makeText(this, "sub selected", Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(this, Subs.class);
                startActivity(intent5);
                break;
            case R.id.profile:
                Toast.makeText(this, "profile selected", Toast.LENGTH_SHORT).show();
                Intent intent7 = new Intent(this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.logout:
                Toast.makeText(this, "Loggin out", Toast.LENGTH_SHORT).show();
                Intent intent6 = new Intent(this, Login.class);
                startActivity(intent6);
                break;
        }
        return false;
    }


}