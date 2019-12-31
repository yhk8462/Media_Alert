package rmit.ad.mediaalert;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Games extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemSelectedListener {
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

        //Search bar
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
                loadData(searchText);
            }
        });

        //Spinner
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapterM = ArrayAdapter.createFromResource(this,R.array.month,
                android.R.layout.simple_spinner_item);
        adapterM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterM);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        loadMonth(text);
        ((TextView) parent.getChildAt(0)).setTextSize(20);
        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    // for loading data ------------------------------------------------------------------------------
    public void loadMonth(String month){
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("Games");
        final Query query = myRef.orderByChild("date").startAt(month);
        //myRef = firebaseDatabase.getReference().child("Users").child(key).child(subs);
        gameList = findViewById(R.id.ListView);
        final FirebaseListOptions<GameList> options = new FirebaseListOptions.Builder<GameList>()
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
        gameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GameList list = (GameList) parent.getAdapter().getItem(position);
                Intent details = new Intent(Games.this, GameDetails.class);
                details.putExtra("name",list.getName());
                details.putExtra("date",list.getDate());
                details.putExtra("company",list.getCompany());
                details.putExtra("des",list.getDescription());
                details.putExtra("imageURL",list.getImage());
                details.putExtra("platform",list.getPlatform());
                startActivity(details);
            }
        });
        adapter.startListening();
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
        gameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GameList list = (GameList) parent.getAdapter().getItem(position);
                Intent details = new Intent(Games.this, GameDetails.class);
                details.putExtra("name",list.getName());
                details.putExtra("date",list.getDate());
                details.putExtra("company",list.getCompany());
                details.putExtra("des",list.getDescription());
                details.putExtra("imageURL",list.getImage());
                details.putExtra("platform",list.getPlatform());
                startActivity(details);
            }
        });
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