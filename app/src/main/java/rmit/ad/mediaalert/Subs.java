package rmit.ad.mediaalert;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Subs extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    FirebaseListAdapter adapter;
    ListView gameList;
    MovieListObject movieListObject=new MovieListObject("","","");
    private MovieAdapter movieAdapter;
    private ArrayList<MovieListObject> movieListObjectsList= new ArrayList<MovieListObject>();
    private Handler handler;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subs);
        //side bar
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


        handler = new Handler();
        Context context = Subs.this;
        movieAdapter = new MovieAdapter(context,movieListObjectsList);
        //Spinner
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapterM = ArrayAdapter.createFromResource(this,R.array.subs,
                android.R.layout.simple_spinner_item);
        adapterM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterM);
        spinner.setOnItemSelectedListener(this);

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) parent.getChildAt(0)).setTextSize(20);
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, ""+text, Toast.LENGTH_SHORT).show();
        if(text.equals("Games")) {
            gameAdapter();
        } else if (text.equals("Movies")){
            movieAdapter();
        } else if (text.equals("Tv Shows")){
            //Tv adapter
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void movieAdapter(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String uid = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        gameList = findViewById(R.id.ListView);
        firebaseDatabase.getReference().child("Users").child(uid).child("ListOfSubsMovie")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Map<String,Object> movieID = (HashMap<String,Object>) dataSnapshot1.getValue();
                            final String IDOfMovie = movieID.get("movieID").toString();
                            firebaseDatabase.getReference().child("Movies").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                                        if (ds.getKey().equals(IDOfMovie)){
                                            Map<String,Object> movieObject = (HashMap<String, Object>) ds.getValue();
                                            String imgURL = movieObject.get("imgURL").toString();
                                            String title = movieObject.get("originalTitle").toString();
                                            String releaseDate = movieObject.get("releaseDate").toString();
                                            movieListObject = new MovieListObject(imgURL,title,releaseDate);
                                            //Toast.makeText(Subs.this,movieListObject.getOriginalTitle(),Toast.LENGTH_SHORT).show();
                                            movieAdapter.add(movieListObject);
                                            return;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameList.setAdapter(movieAdapter);
            }
        },5000);
    }
    public void gameAdapter(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String uid = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("Users").child(uid).child("ListOfSubsGames");
        gameList = findViewById(R.id.ListView);
        FirebaseListOptions<GameList> options = new FirebaseListOptions.Builder<GameList>()
                .setLayout(R.layout.game_list)
                .setQuery(myRef,GameList.class)
                .build();
        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View v, Object model, int position) {
                TextView listName = v.findViewById(R.id.listName);
                TextView listDate = v.findViewById(R.id.listDate);
                TextView listPlatform = v.findViewById(R.id.listPlatform);
                ImageView listImage = v.findViewById(R.id.listImage);

                GameList value = (GameList) model;
                listName.setText(value.getName());
                listPlatform.setText(value.getPlatform());
                listDate.setText("Due: "+value.getDate());
                Picasso.with(Subs.this).load(value.getImage()).into(listImage);
            }
        };
        gameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GameList list = (GameList) parent.getAdapter().getItem(position);
                Intent details = new Intent(Subs.this, GameDetails.class);
                details.putExtra("name",list.getName());
                details.putExtra("date",list.getDate());
                details.putExtra("company",list.getCompany());
                details.putExtra("des",list.getDescription());
                details.putExtra("imageURL",list.getImage());
                details.putExtra("platform",list.getPlatform());
                startActivity(details);
            }
        });

        gameList.setAdapter(adapter);
        adapter.startListening();
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
            case R.id.logout:
                Toast.makeText(this, "Loggin out", Toast.LENGTH_SHORT).show();
                Intent intent6 = new Intent(this,Login.class);
                startActivity(intent6);
                break;
        }
        return false;
    }



}