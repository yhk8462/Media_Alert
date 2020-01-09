package rmit.ad.mediaalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

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

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import rmit.ad.mediaalert.notification.SharedPrefManager;
import rmit.ad.mediaalert.tvShows.TvShowActivity;

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
        final String title=(String)intent.getExtras().get("title");
        double vote=(double)intent.getExtras().get("vote");
        final int ID=(int)intent.getExtras().get("ID");
        String img=(String)intent.getExtras().get("img");
        boolean isAdult=(boolean)intent.getExtras().get("adult");
        final String releaseDate=(String)intent.getExtras().get("releaseDate");
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

                    if (android.os.Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }

                    saveInServer(title, releaseDate);
                    SubscribeDialog subscribeDialog = new SubscribeDialog();
                    Bundle args = new Bundle();
                    args.putString("name", title);
                    subscribeDialog.setArguments(args);
                    subscribeDialog.show(getSupportFragmentManager(), "Sub");
                }
                button1.setVisibility(View.GONE);
            }
        });

        //button1.setVisibility(View.VISIBLE);
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
                                    firebaseDatabase.getReference().child("Users").child(key).child("ListOfSubsMovie")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                        Map<String,Object> movieID = (HashMap<String,Object>) dataSnapshot1.getValue();
                                                        if (Integer.valueOf(movieID.get("movieID").toString()).equals(ID)) {
                                                            button1.setVisibility(View.GONE);
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
            button1.setVisibility(View.GONE);
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
            jsonParam.put("type", "Movie");
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
