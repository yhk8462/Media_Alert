package rmit.ad.mediaalert;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Games extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Games";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    FirebaseListAdapter adapter;
    ListView gameList;
    String searchText;
    EditText editText;
    public ArrayList<String> mGames = new ArrayList<>();


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.games);
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
        loadData(searchText);
        editText = findViewById(R.id.Search);
        ImageView btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = editText.getText().toString();
                Toast.makeText(Games.this, "searching: " + searchText, Toast.LENGTH_SHORT).show();
                loadData(searchText);
            }
        });
    }
    public void loadData(String searchText){
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("Games");
        Query query = myRef.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
        //myRef = firebaseDatabase.getReference().child("Users").child(key).child(subs);
        gameList = findViewById(R.id.ListView);
        FirebaseListOptions<GameList> options = new FirebaseListOptions.Builder<GameList>()
                .setLayout(R.layout.game_list)
                .setQuery(query,GameList.class)
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
                Picasso.with(Games.this).load(value.getImage()).into(listImage);
            }
        };
        gameList.setAdapter(adapter);
        adapter.startListening();

        /* // -----------------------------------------------------Adding data--------------------------------------------------
        String name ="FINAL FANTASY VII REMAKE";
        String date ="March 3, 2020";
        String company="Square Enix";
        String platform = "PS4";
        String description = "This looks like one of the most glorious nostalgia bombs of all time. Unlike the various rezzed-up Final Fantasy remasters of the past, Final Fantasy VII Remake is a proper, modern reworking of one of the most iconic role-playing games of all time. The trailers look stunning, capturing the essence of the influential original with the power of the PlayStation 4 – hopefully without losing the weirdness of the PS1 classic. One weird quirk, however: given the immense scale of the game, Square Enix will release it in parts. This initial Remake release will include the part of the game that takes place in Midgar, with the other chunks to release later.";
        GameList games = new GameList(name, date, company, platform, description);
        //databaseReference
        myRef.child("4").setValue(games);
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
                Intent intent5 = new Intent(this,Login.class);
                startActivity(intent5);
                break;
        }
        return false;
    }


}