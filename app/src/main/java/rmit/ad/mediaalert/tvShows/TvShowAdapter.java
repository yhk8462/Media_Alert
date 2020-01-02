package rmit.ad.mediaalert.tvShows;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import rmit.ad.mediaalert.R;

public class TvShowAdapter extends ArrayAdapter<TvShowItem> {

    protected ImageView tvShowImage;
    protected TextView tvShowName;
    protected TextView releaseDate;

    public TvShowAdapter(Activity context, ArrayList<TvShowItem> tvShowItems) {
        super(context, 0, tvShowItems);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View tvShowListView = convertView;
        if (tvShowListView == null) {
            tvShowListView = LayoutInflater.from(getContext()).inflate(
                    R.layout.tv_show_list_element, parent, false);
        }

        final TvShowItem tvShowItem = getItem(position);

        tvShowName = tvShowListView.findViewById(R.id.txtTvShowName);
        releaseDate = tvShowListView.findViewById(R.id.releaseDate);
        tvShowImage = tvShowListView.findViewById(R.id.img);

        Glide.with(getContext()).load(tvShowItem.getImageUrl()).into(tvShowImage);
        tvShowName.setText(tvShowItem.getName());
        releaseDate.setText(tvShowItem.getReleaseDate());

        return tvShowListView;
    }
}
