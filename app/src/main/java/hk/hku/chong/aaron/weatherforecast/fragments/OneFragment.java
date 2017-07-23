package hk.hku.chong.aaron.weatherforecast.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import hk.hku.chong.aaron.weatherforecast.R;

import static android.content.Context.MODE_PRIVATE;


public class OneFragment extends Fragment{

    private APITask apiTask;
    private TextView detail_high_textview;
    private TextView detail_low_textview;
    private TextView detail_forecast_textview;
    private TextView detail_forecast_detail_textview;
    private TextView detail_humidity_textview;
    private TextView detail_date_textview;
    private String target_location;

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences preferences = getContext().getSharedPreferences("PREF_LOCATION", MODE_PRIVATE);
        target_location = preferences.getString("location", "");

        Calendar c = Calendar.getInstance();
        Integer yyyy = c.get(Calendar.YEAR);
        Integer mm = c.get(Calendar.MONTH);
        Integer dd = c.get(Calendar.DAY_OF_MONTH);
        String current_date = String.format("%02d", dd) + "/" + String.format("%02d", mm+1) + "/" + String.format("%04d", yyyy);

        detail_high_textview = (TextView)getView().findViewById(R.id.detail_high_textview);
        detail_low_textview = (TextView)getView().findViewById(R.id.detail_low_textview);
        detail_forecast_textview = (TextView)getView().findViewById(R.id.detail_forecast_textview);
        detail_humidity_textview = (TextView)getView().findViewById(R.id.detail_humidity_textview);
        detail_forecast_detail_textview = (TextView)getView().findViewById(R.id.detail_forecast_detail_textview);
        detail_date_textview = (TextView)getView().findViewById(R.id.detail_date_textview);
        detail_date_textview.setText(current_date);
        apiTask = new APITask();
        apiTask.execute();
    }

    public String RoundCelsius(String input){
        return String.format("%.1f", Double.parseDouble(input));
    }

    public class APITask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            // TODO Auto-generated method stub
            if (s != null) {
                Log.i("JSON Response: ", s);
                try {
                    JSONObject temp = new JSONObject(s);
                    JSONObject obj = temp.getJSONArray("list").getJSONObject(0);
                    if (obj.has("temp")) {
                        JSONObject tempObj = obj.getJSONObject("temp");
                        detail_high_textview.setText(RoundCelsius(tempObj.optString("max")) + " \u2103");
                        detail_low_textview.setText(RoundCelsius(tempObj.optString("min")) + " \u2103");
                    }
                    if (obj.has("humidity")) {
                        detail_humidity_textview.setText("Humidity: " + obj.optString("humidity") + "%");
                    }
                    if (obj.has("weather")) {
                        JSONObject weatherObj = obj.getJSONArray("weather").getJSONObject(0);
                        detail_forecast_textview.setText(weatherObj.optString("main"));
                        detail_forecast_detail_textview.setText(weatherObj.optString("description"));
                    }
                } catch (Throwable t) {
                    Log.e("API Task ERR:", "cannot parse malformed JSON");
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;

            String target_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=" + target_location + "&cnt=1&units=metric&appid=ba7d5d53599254ae48141b652c3fd61f";

            Log.i("GET URL : ", target_URL);

            try {
                URL url = new URL(target_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null || isCancelled()) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null && isCancelled() == false) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                jsonStr = buffer.toString();
                return jsonStr;

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }
    }
}
