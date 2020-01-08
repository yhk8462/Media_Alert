package rmit.ad.mediaalert.tvShows;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rmit.ad.mediaalert.R;

public class TvShowAdapter extends ArrayAdapter<TvShowItem> {

    protected ImageView tvShowImage;
    protected TextView tvShowName;
    protected TextView releaseDate;
    protected TextView days;

    public TvShowAdapter(Activity context, ArrayList<TvShowItem> tvShowItems) {
        super(context, 0, tvShowItems);
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = (d2.getTime() - d1.getTime()) + 1;
        return TimeUnit.DAYS.convert(diff , TimeUnit.MILLISECONDS);
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
        days = tvShowListView.findViewById(R.id.txtDays);

        Glide.with(getContext()).load(tvShowItem.getImageUrl()).into(tvShowImage);
        tvShowName.setText(tvShowItem.getName());
        releaseDate.setText(tvShowItem.getReleaseDate());

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date relDate = null;
        try {
            relDate = format1.parse(tvShowItem.getReleaseDate());

        } catch (Exception ex) {
            Log.d("TVshowListElement", "Exception occurred while parsing the date");
        }
        days.setText(String.valueOf(getDifferenceDays(new Date(), relDate)));

        tvShowListView.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), TvShowDetails.class);

                        intent.putExtra("name", tvShowItem.getName());
                        intent.putExtra("releaseDate", tvShowItem.getReleaseDate());
                        intent.putExtra("type", tvShowItem.getTvShowType());
                        intent.putExtra("description", tvShowItem.getDescription());
                        intent.putExtra("imageURL", tvShowItem.getImageUrl());
                        getContext().startActivity(intent);
                    }
                }
        );

        return tvShowListView;
    }
}
