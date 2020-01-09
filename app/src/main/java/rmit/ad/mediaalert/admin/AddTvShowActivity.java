package rmit.ad.mediaalert.admin;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import rmit.ad.mediaalert.R;
import rmit.ad.mediaalert.tvShows.TvShowItem;

public class AddTvShowActivity extends AppCompatActivity {
    private static final String TAG = "AddTvShow";
    private final int PICK_IMAGE_REQUEST = 71;
    EditText tvShowName, type, description;
    CalendarView releaseDate;
    String releaseDateStr;
    //Firebase storage
    FirebaseStorage storage;
    StorageReference storageReference;

    private Uri filePath;

    private Button btnChoose, btnUpload, btnSave, btnCancel;
    private ImageView imageView;

    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tv_show_activity);

        tvShowName = findViewById(R.id.tvShowName);
        type = findViewById(R.id.type);
        description = findViewById(R.id.description);

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
                try {
                    TvShowItem tvShowItem = new TvShowItem();
                    tvShowItem.setDescription(description.getText().toString());
                    tvShowItem.setName(tvShowName.getText().toString());
                    tvShowItem.setReleaseDate(releaseDateStr);
                    tvShowItem.setTvShowType(type.getText().toString());
                    tvShowItem.setImageUrl("https://firebasestorage.googleapis.com/v0/b/mediaalert-aaa7c.appspot.com/o/you.jpeg?alt=media&token=a963287b-df79-4c91-8e46-26b07da030b2");
                    firebaseDatabase.getReference().child("TvShows").child(UUID.randomUUID().toString()).setValue(tvShowItem);
                    Toast.makeText(AddTvShowActivity.this, "TV show addded successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddTvShowActivity.this, AdminHomePage.class);
                    startActivity(intent);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(AddTvShowActivity.this, AdminHomePage.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        Toast.makeText(AddTvShowActivity.this, "Successfully uploaded the image", Toast.LENGTH_SHORT).show();
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
