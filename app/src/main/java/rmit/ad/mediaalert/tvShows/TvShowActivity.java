package rmit.ad.mediaalert.tvShows;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import rmit.ad.mediaalert.Login;
import rmit.ad.mediaalert.MainActivity;
import rmit.ad.mediaalert.Movies;
import rmit.ad.mediaalert.ProfileActivity;
import rmit.ad.mediaalert.R;
import rmit.ad.mediaalert.Subs;
import rmit.ad.mediaalert.TvShows;
import rmit.ad.mediaalert.admin.ListUtils;

public class TvShowActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemSelectedListener {
    private static final String TAG = "TvShow";
    public ArrayList<TvShowItem> tvShowsTomorrow = new ArrayList<>();
    public ArrayList<TvShowItem> tvShowsNextWeek = new ArrayList<>();
    public ArrayList<TvShowItem> tvShowsLater = new ArrayList<>();
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    FirebaseListAdapter adapter;
    ListView tomorrowView;
    ListView nextWeekView;
    ListView laterView;
    TextView tomorrowHeader;
    TextView nextWeekHeader;
    TextView laterHeader;
    String searchText;
    EditText editText;
    Spinner spinner;
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;

    public static String theMonth(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tv_show_activity);

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

        //List headers
        tomorrowHeader = findViewById(R.id.headerTomorrow);
        nextWeekHeader = findViewById(R.id.headerNextWeek);
        laterHeader = findViewById(R.id.headerLater);

        //List Views
        tomorrowView = findViewById(R.id.ListViewTomorrow);
        nextWeekView = findViewById(R.id.ListViewNextWeek);
        laterView = findViewById(R.id.ListViewLater);


        try {
            loadTvShows();
        } catch (Exception ex) {
            Log.d(TAG, "Error occurred while loading tv shows.");
        }

        editText = findViewById(R.id.Search);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    searchTvShows(s.toString());
                } catch (Exception ex) {
                    Log.d(TAG, "Error occurred while searching tv shows.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Spinner
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapterM = ArrayAdapter.createFromResource(this, R.array.month,
                android.R.layout.simple_spinner_item);
        adapterM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterM);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0,false);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (spinner.getSelectedItemPosition() != 0) {
            String text = parent.getItemAtPosition(position).toString();
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
            loadByMonth(text);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void clearList() {
        tvShowsTomorrow.clear();
        tvShowsNextWeek.clear();
        tvShowsLater.clear();
    }

    private void searchTvShows(final String searchText) {
        clearList();
        if (searchText == null || searchText.equals("")) {
            loadTvShows();
            return;
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("TvShows");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    TvShowItem tvShowItem = postSnapshot.getValue(TvShowItem.class);

                    if (!tvShowItem.getName().toLowerCase().contains(searchText.toLowerCase())) {
                        continue;
                    }

                    Calendar calendar = Calendar.getInstance();
                    Date today = calendar.getTime();
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    Date tomorrow = calendar.getTime();

                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    String tomorrowDateFormatted = format1.format(tomorrow);

                    try {
                        Date relDate = format1.parse(tvShowItem.getReleaseDate());
                        // Do not list previously released TV shows
                        if (relDate.before(new Date())) {
                            continue;
                        }
                    } catch (Exception ex) {
                        Log.d(TAG, "Exception occurred while parsing the date");
                    }


                    if (tvShowItem.getReleaseDate().equals(tomorrowDateFormatted)) {
                        tvShowsTomorrow.add(tvShowItem);
                        continue;
                    }
                    Date releaseDate = null;
                    try {
                        releaseDate = format1.parse(tvShowItem.getReleaseDate());
                    } catch (Exception ex) {
                        Log.d(TAG, "Exception occurred while parsing the date");
                    }

                    calendar.add(Calendar.DATE, 7);

                    if (releaseDate.after(new Date()) && releaseDate.before(calendar.getTime())) {
                        tvShowsNextWeek.add(tvShowItem);
                        continue;
                    }

                    tvShowsLater.add(tvShowItem);
                }

                populateListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadByMonth(final String month) {
        clearList();
        if (month == null || month.equals("")) {
            loadTvShows();
            return;
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("TvShows");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    TvShowItem tvShowItem = postSnapshot.getValue(TvShowItem.class);

                    Calendar calendar = Calendar.getInstance();
                    Date today = calendar.getTime();
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    Date tomorrow = calendar.getTime();

                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    String tomorrowDateFormatted = format1.format(tomorrow);
                    Date relDate = null;
                    try {
                        relDate = format1.parse(tvShowItem.getReleaseDate());
                        // Do not list previously released TV shows
                        if (relDate.before(new Date())) {
                            continue;
                        }
                    } catch (Exception ex) {
                        Log.d(TAG, "Exception occurred while parsing the date");
                    }
                    Calendar relDatecalendar = Calendar.getInstance();
                    relDatecalendar.setTime(relDate);
                    int monthNumber = relDatecalendar.get(Calendar.MONTH);

                    if (!theMonth(monthNumber).toLowerCase().equals(month.toLowerCase())) {
                        continue;
                    }

                    if (tvShowItem.getReleaseDate().equals(tomorrowDateFormatted)) {
                        tvShowsTomorrow.add(tvShowItem);
                        continue;
                    }
                    Date releaseDate = null;
                    try {
                        releaseDate = format1.parse(tvShowItem.getReleaseDate());
                    } catch (Exception ex) {
                        Log.d(TAG, "Exception occurred while parsing the date");
                    }

                    calendar.add(Calendar.DATE, 7);

                    if (releaseDate.after(new Date()) && releaseDate.before(calendar.getTime())) {
                        tvShowsNextWeek.add(tvShowItem);
                        continue;
                    }

                    tvShowsLater.add(tvShowItem);
                }

                populateListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadTvShows() {
        clearList();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("TvShows");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    TvShowItem tvShowItem = postSnapshot.getValue(TvShowItem.class);
                    Calendar calendar = Calendar.getInstance();
                    Date today = calendar.getTime();
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    Date tomorrow = calendar.getTime();

                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    String tomorrowDateFormatted = format1.format(tomorrow);

                    if (tvShowItem.getReleaseDate().equals(tomorrowDateFormatted)) {
                        tvShowsTomorrow.add(tvShowItem);
                        continue;
                    }
                    Date releaseDate = null;
                    try {
                        releaseDate = format1.parse(tvShowItem.getReleaseDate());
                        if (releaseDate.before(new Date())) {
                            continue;
                        }
                    } catch (Exception ex) {
                        Log.d(TAG, "Exception occurred while parsing the date");
                    }

                    calendar.add(Calendar.DATE, 7);

                    if (releaseDate.after(new Date()) && releaseDate.before(calendar.getTime())) {
                        tvShowsNextWeek.add(tvShowItem);
                        continue;
                    }

                    tvShowsLater.add(tvShowItem);
                }

                populateListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void populateListView() {
        if (tvShowsTomorrow.isEmpty()) {
            tomorrowView.setVisibility(View.GONE);
            tomorrowHeader.setVisibility(View.GONE);
        } else {
            tomorrowView.setVisibility(View.VISIBLE);
            tomorrowHeader.setVisibility(View.VISIBLE);
            tomorrowView.setAdapter(new TvShowAdapter(TvShowActivity.this, tvShowsTomorrow));
            ListUtils.setDynamicHeight(tomorrowView);
        }

        if (tvShowsNextWeek.isEmpty()) {
            nextWeekView.setVisibility(View.GONE);
            nextWeekHeader.setVisibility(View.GONE);

        } else {
            nextWeekView.setVisibility(View.VISIBLE);
            nextWeekHeader.setVisibility(View.VISIBLE);
            nextWeekView.setAdapter(new TvShowAdapter(TvShowActivity.this, tvShowsNextWeek));
            ListUtils.setDynamicHeight(nextWeekView);
        }

        if (tvShowsLater.isEmpty()) {
            laterView.setVisibility(View.GONE);
            laterHeader.setVisibility(View.GONE);
        } else {
            laterView.setVisibility(View.VISIBLE);
            laterHeader.setVisibility(View.VISIBLE);
            laterView.setAdapter(new TvShowAdapter(TvShowActivity.this, tvShowsLater));
            ListUtils.setDynamicHeight(laterView);
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
                Intent intent3 = new Intent(this, rmit.ad.mediaalert.Games.class);
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
