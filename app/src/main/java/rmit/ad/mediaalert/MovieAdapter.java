package rmit.ad.mediaalert;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter<MovieListObject> {
    Context context;
    public MovieAdapter(Context context, ArrayList<MovieListObject> movieListObjects) {
        super(context, 0, movieListObjects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MovieListObject movieListObject = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.movie_list_single, parent, false);
        }
        ImageView img = (ImageView) convertView.findViewById(R.id.img);
        TextView txt = (TextView) convertView.findViewById(R.id.txt);
        TextView txt2 = (TextView) convertView.findViewById(R.id.txt2);
        String display = movieListObject.originalTitle;
        String display2 = movieListObject.releaseDate;
        txt.setText(display);
        txt2.setText(display2);
        Picasso.with(this.context).load("http://image.tmdb.org/t/p/w185/"+movieListObject.imgURL).into(img);

        return convertView;
    }

}
