package rmit.ad.mediaalert;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class HttpUtils {

    private static final String BASE_URL = "http://localhost:8080/api/v1/push/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        StringEntity jsonEntity = null;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("token", "evRjoyFo9uQ:APA91bFMBvepRf8UAz2cepWnXCEbdatrvO96sMlbq7gHptbl8RgdwAwHTjePzTii4H0jYDK1xRcAB0oDKDTSO91FZzh7mj5ImYFscP3GV1vuPE2797FXE1oZdyOdTG9Rf04Xk4xs2eKK");
            jsonParams.put("message", "test");
            jsonParams.put("title", "firebase");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonEntity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
