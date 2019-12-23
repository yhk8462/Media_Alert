package rmit.ad.mediaalert;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Movies extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private String movie_json_string = "";
    private ListView listView;
    private ArrayAdapter adapter;
    private boolean loadingMore = true;
    private int currentPage = 0;
    private int totalPage = 1;
    private MovieAdapter movieAdapter;
    private String SearchBar="Search Movie";
    EditText editTextSearchMovieName;
    private MovieListObject movieListObject=new MovieListObject("","","");
    private ArrayList<MovieListObject> movieListObjectsList= new ArrayList<MovieListObject>();
    private boolean isStateChange = false;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);
        Button button = findViewById(R.id.btnSidebar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        final Handler handler = new Handler();
        listView = (ListView) findViewById(R.id.list);
        Context context = Movies.this;
        movieAdapter = new MovieAdapter(context,movieListObjectsList);
        editTextSearchMovieName = findViewById(R.id.editTextSearchMovieName);
        SearchBar= editTextSearchMovieName.getText().toString();
        editTextSearchMovieName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                SearchBar = editTextSearchMovieName.getText().toString();
                currentPage=0;
                totalPage=1;
                movieAdapter.clear();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                    if((lastInScreen == totalItemCount) && (loadingMore)){
                        if (currentPage<totalPage) {
                            currentPage++;
                            new GetMovies().execute();
                            Toast.makeText(Movies.this, String.valueOf(currentPage),Toast.LENGTH_SHORT).show();
                        }
                    }


            }
        });

        listView.setAdapter(movieAdapter);
        //new GetMovies().execute();
    }
    private class GetMovies extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            if(SearchBar.equals("Search Movie")||SearchBar.equals("")) {
                movie_json_string = HttpHandler.getUpcomingMovies("http://api.themoviedb.org/3/movie/upcoming?api_key=6660760b6f822ad32b1f7ceeb01b906b&page=" + currentPage);
            } else {
                movie_json_string = HttpHandler.getUpcomingMovies("http://api.themoviedb.org/3/search/movie?api_key=6660760b6f822ad32b1f7ceeb01b906b&page=" + currentPage + "&query=" + SearchBar);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            movieListObjectsList = new ArrayList<MovieListObject>();
            JSONObject root = null;
            try{
                JSONObject object = new JSONObject(movie_json_string);
                JSONArray movieArray = object.getJSONArray("results");
                for (int j = 0; j < movieArray.length(); j++) {
                    JSONObject movieObject = movieArray.getJSONObject(j);
                    //String data = movieObject.getString("original_title") + "\n" + movieObject.getString("release_date");
                    String imgURL = movieObject.getString("poster_path");
                    String originalTitle = movieObject.getString("original_title");
                    String releaseDate = movieObject.getString("release_date");
                    movieListObject = new MovieListObject(imgURL,originalTitle,releaseDate);

                    movieListObjectsList.add(movieListObject);



                }
                movieAdapter.addAll(movieListObjectsList);
                totalPage = object.getInt("total_pages");
            }catch (JSONException e){
                e.printStackTrace();
            }
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