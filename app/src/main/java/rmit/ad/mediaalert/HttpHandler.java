package rmit.ad.mediaalert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpHandler {

    public static String getUpcomingMovies(String urlString){
        HttpURLConnection httpURLConnection = null;
        StringBuilder stringBuilder = new StringBuilder();
        try{
            URL url = new URL(urlString);
            httpURLConnection=(HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line = "";
            while((line=bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }
        } catch (MalformedURLException e){
            e.printStackTrace();
;        } catch (IOException e){
            e.printStackTrace();
        }
        return  stringBuilder.toString();
    }
}
