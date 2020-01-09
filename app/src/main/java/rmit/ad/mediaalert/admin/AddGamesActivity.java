package rmit.ad.mediaalert.admin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import rmit.ad.mediaalert.GameList;
import rmit.ad.mediaalert.R;
import rmit.ad.mediaalert.tvShows.TvShowItem;

public class AddGamesActivity extends AppCompatActivity {
    private static final String TAG = "AddTvShow";
    private final int PICK_IMAGE_REQUEST = 71;
    EditText tvShowName, platform, description, company;
    CalendarView releaseDate;
    String releaseDateStr;
    //Firebase storage
    FirebaseStorage storage;
    StorageReference storageReference;

    private Uri filePath;


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
    private Button btnChoose, btnUpload, btnSave, btnCancel;
    private ImageView imageView;

    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_game_activity);

        tvShowName = findViewById(R.id.tvShowName);
        platform = findViewById(R.id.platform);
        description = findViewById(R.id.description);
        company = findViewById(R.id.company);
        releaseDate = findViewById(R.id.releaseDate);

        releaseDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                Toast.makeText(getApplicationContext(), dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                Date releaseDate = c.getTime();
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                releaseDateStr = format1.format(releaseDate);
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        imageView = findViewById(R.id.imgView);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);


        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    GameList gameList = new GameList();
                    gameList.setName(tvShowName.getText().toString());
                    gameList.setCompany(company.getText().toString());
                    gameList.setPlatform(company.getText().toString());
                    gameList.setDate(releaseDateStr);
                    gameList.setDescription(description.getText().toString());
                    gameList.setImage("https://firebasestorage.googleapis.com/v0/b/mediaalert-aaa7c.appspot.com/o/suits.jpg?alt=media&token=629a1c35-42fc-4eb6-a773-79b6c3a4a037");
                    firebaseDatabase.getReference().child("Games").child(UUID.randomUUID().toString()).setValue(gameList);
                    Toast.makeText(AddGamesActivity.this, "Game ADDED successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddGamesActivity.this, AdminHomePage.class);
                    startActivity(intent);

                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(AddGamesActivity.this, AdminHomePage.class);
                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    uploadImage();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference storageRef = storageReference.child(System.currentTimeMillis() +"." + getFileExtension(filePath));

            try{
                InputStream inputStream = this.getContentResolver().openInputStream(filePath);
               /**
                UploadTask uploadTask = storageRef.putStream(inputStream);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        progressDialog.dismiss();

                        Toast.makeText(AddTvShowActivity.this, "Failed "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        //Log.e(TAG, "Task :" + taskSnapshot.getTask());

                        //Log.e(TAG, "Class Store:" + taskSnapshot.getStorage().getDownloadUrl());
                        Log.e(TAG,"metaData :"+taskSnapshot.getMetadata().getPath());

                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });
                */
            } catch (Exception e){
                e.printStackTrace();
            }


        }
        //if there is not any file
        else {
            //you can display an error toast
        }

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select TV show cover Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}