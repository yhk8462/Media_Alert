package rmit.ad.mediaalert;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.List;

import rmit.ad.mediaalert.tvShows.TvShowActivity;

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
    private int curMonth;
    private int curYear;
    private String filterMonth="";


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
        Calendar calendar = Calendar.getInstance();
        curMonth = calendar.get(Calendar.MONTH);
        curMonth = curMonth+1;
        //Toast.makeText(Movies.this,String.valueOf(curMonth),Toast.LENGTH_SHORT).show();
        curYear = calendar.get(Calendar.YEAR);
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
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapterM = ArrayAdapter.createFromResource(this,R.array.month2,
                android.R.layout.simple_spinner_item);
        adapterM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterM);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                if (text.equals("January")){
                    filterMonth = "01";
                    currentPage=0;
                    totalPage=1;
                    movieAdapter.clear();
                    editTextSearchMovieName.setText("");
                    SearchBar = editTextSearchMovieName.getText().toString();

                }else if (text.equals("February")){
                    filterMonth = "02";
                    currentPage=0;
                    totalPage=1;
                    movieAdapter.clear();
                    editTextSearchMovieName.setText("");
                    SearchBar = editTextSearchMovieName.getText().toString();

                }else if (text.equals("March")){
                    filterMonth = "03";
                    currentPage=0;
                    totalPage=1;
                    movieAdapter.clear();
                    editTextSearchMovieName.setText("");
                    SearchBar = editTextSearchMovieName.getText().toString();

                }else if (text.equals("April")){
                    filterMonth = "04";
                    currentPage=0;
                    totalPage=1;
                    movieAdapter.clear();
                    editTextSearchMovieName.setText("");
                    SearchBar = editTextSearchMovieName.getText().toString();

                }else if (text.equals("May")){
                    filterMonth = "05";
                    currentPage=0;
                    totalPage=1;
                    movieAdapter.clear();
                    editTextSearchMovieName.setText("");
                    SearchBar = editTextSearchMovieName.getText().toString();

                }else if (text.equals("June")){
                    filterMonth = "06";
                    currentPage=0;
                    totalPage=1;
                    movieAdapter.clear();
                    editTextSearchMovieName.setText("");
                    SearchBar = editTextSearchMovieName.getText().toString();

                }else if (text.equals("July")){
                    filterMonth = "07";
                    currentPage=0;
                    totalPage=1;
                    movieAdapter.clear();
                    editTextSearchMovieName.setText("");
                    SearchBar = editTextSearchMovieName.getText().toString();

                }else if (text.equals("August")){
                    filterMonth = "08";
                    currentPage=0;
                    totalPage=1;
                    movieAdapter.clear();
                    editTextSearchMovieName.setText("");
                    SearchBar = editTextSearchMovieName.getText().toString();

                }else if (text.equals("September")){
                    filterMonth = "09";
                    currentPage=0;
                    totalPage=1;
                    movieAdapter.clear();
                    editTextSearchMovieName.setText("");
                    SearchBar = editTextSearchMovieName.getText().toString();

                }else if (text.equals("October")){
                    filterMonth = "10";
                    currentPage=0;
                    totalPage=1;
                    movieAdapter.clear();
                    editTextSearchMovieName.setText("");
                    SearchBar = editTextSearchMovieName.getText().toString();

                }else if (text.equals("November")){
                    filterMonth = "11";
                    currentPage=0;
                    totalPage=1;
                    movieAdapter.clear();
                    editTextSearchMovieName.setText("");
                    SearchBar = editTextSearchMovieName.getText().toString();

                }else if (text.equals("December")){
                    filterMonth = "12";
                    currentPage=0;
                    totalPage=1;
                    movieAdapter.clear();
                    editTextSearchMovieName.setText("");
                    SearchBar = editTextSearchMovieName.getText().toString();

                }
                ((TextView) parent.getChildAt(0)).setTextSize(20);
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterMonth = "";

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieListObject movie = (MovieListObject) parent.getAdapter().getItem(position);
                Intent intent = new Intent(Movies.this, MoviesDetailActivity.class);
                intent.putExtra("title",movie.getOriginalTitle());
                intent.putExtra("vote",movie.getVoteAverage());
                intent.putExtra("ID", movie.getNumberID());
                intent.putExtra("img", movie.getImgURL());
                intent.putExtra("adult", movie.isAdult());
                intent.putExtra("releaseDate", movie.getReleaseDate());
                intent.putExtra("overview", movie.getOverview());
                intent.putExtra("language", movie.getOriginalLanguage());
                startActivity(intent);

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
                            //Toast.makeText(Movies.this, String.valueOf(currentPage),Toast.LENGTH_SHORT).show();
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
                if (curMonth>=1&&curMonth<=9 && filterMonth.equals("")){
                    movie_json_string = HttpHandler.getUpcomingMovies("http://api.themoviedb.org/3/discover/movie?api_key=6660760b6f822ad32b1f7ceeb01b906b&page=" + currentPage + "&primary_release_date.gte=" + curYear + "-" + "0" + curMonth + "-" + "01");
                }else if (curMonth>=10&&curMonth<=12 && filterMonth.equals("")) {
                    movie_json_string = HttpHandler.getUpcomingMovies("http://api.themoviedb.org/3/discover/movie?api_key=6660760b6f822ad32b1f7ceeb01b906b&page=" + currentPage + "&primary_release_date.gte=" + curYear + "-" + curMonth + "-" + "01");
                }else{
                    if (filterMonth.equals("01")||filterMonth.equals("03")||filterMonth.equals("05")||filterMonth.equals("07")||filterMonth.equals("08")||filterMonth.equals("10")||filterMonth.equals("12")){
                        movie_json_string = HttpHandler.getUpcomingMovies("http://api.themoviedb.org/3/discover/movie?api_key=6660760b6f822ad32b1f7ceeb01b906b&page=" + currentPage + "&primary_release_date.gte="+curYear+"-"+filterMonth+"-01"+"&primary_release_date.lte="+curYear+"-"+filterMonth+"-31");
                    }
                    else if (filterMonth.equals("04")||filterMonth.equals("06")||filterMonth.equals("09")||filterMonth.equals("11")){
                        movie_json_string = HttpHandler.getUpcomingMovies("http://api.themoviedb.org/3/discover/movie?api_key=6660760b6f822ad32b1f7ceeb01b906b&page=" + currentPage + "&primary_release_date.gte="+curYear+"-"+filterMonth+"-01"+"&primary_release_date.lte="+curYear+"-"+filterMonth+"-30");
                    }
                    else if (filterMonth.equals("02")){
                        movie_json_string = HttpHandler.getUpcomingMovies("http://api.themoviedb.org/3/discover/movie?api_key=6660760b6f822ad32b1f7ceeb01b906b&page=" + currentPage + "&primary_release_date.gte="+curYear+"-"+filterMonth+"-01"+"&primary_release_date.lte="+curYear+"-03-01");

                    }

                }
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
                    String overview = movieObject.getString("overview");
                    double voteAverage = movieObject.getDouble("vote_average");
                    String originalLanguage = movieObject.getString("original_language");
                    int numberID = movieObject.getInt("id");
                    boolean isAdult = movieObject.getBoolean("adult");
                    movieListObject = new MovieListObject(imgURL,originalTitle,releaseDate,numberID,isAdult,originalLanguage,voteAverage,overview);

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
                Intent intent4 = new Intent(this, TvShowActivity.class);
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