package rmit.ad.mediaalert;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

public class GameDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_details);

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

        TextView header = findViewById(R.id.header);
        TextView gameName = findViewById(R.id.game_name);
        TextView gameDate = findViewById(R.id.game_date);
        TextView gameCompany = findViewById(R.id.game_company);
        TextView gamePlatform = findViewById(R.id.game_platform);
        TextView gameDes = findViewById(R.id.game_des);
        ImageView gameImg = findViewById(R.id.game_image);

        //Get data and display
        Intent details = getIntent();
        String name = (String) details.getExtras().get("name");
        String date = (String) details.getExtras().get("date");
        String company = (String) details.getExtras().get("company");
        String des = (String) details.getExtras().get("des");
        String platform = (String) details.getExtras().get("platform");
        String imageURL = (String) details.getExtras().get("imageURL");

        header.setText(name);
        Picasso.with(GameDetails.this).load(imageURL).into(gameImg);
        gameName.setText(name);
        gameDate.setText("Release Date: "+"\n"+date);
        gameCompany.setText("Produced by: "+"\n"+company);
        gamePlatform.setText("Available on: "+"\n"+platform);
        gameDes.setText(des);

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