package hk.hku.chong.aaron.weatherforecast.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hk.hku.chong.aaron.weatherforecast.MainActivity;
import hk.hku.chong.aaron.weatherforecast.R;

import static android.content.Context.MODE_PRIVATE;


public class TwoFragment extends Fragment{

    private APITask apiTask;
    private String target_location;

    public TwoFragment() {
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
        return inflater.inflate(R.layout.fragment_two, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences preferences = getContext().getSharedPreferences("PREF_LOCATION", MODE_PRIVATE);
        target_location = preferences.getString("location", "");
        apiTask = new APITask();
        apiTask.execute();
    }

    public String RoundCelsius(String input){
        return String.format("%.1f", Double.parseDouble(input));
    }

    public static String fixedLengthString(String string, int length) {
        return String.format("%1$"+length+ "s", string);
    }

    public class APITask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            // TODO Auto-generated method stub
            if (s != null) {
                Log.i("JSON Response: ", s);
                try {
                    JSONObject temp = new JSONObject(s);
                    JSONArray jsonArray = temp.getJSONArray("list");
                    int length = jsonArray.length();
                    List<String> listContents = new ArrayList<String>(length);
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_MONTH, -1);

                    for (int i = 0; i < length; i++)
                    {
                        String tempStr = "";
                        c.add(Calendar.DAY_OF_MONTH, 1);
                        Integer yyyy = c.get(Calendar.YEAR);
                        Integer mm = c.get(Calendar.MONTH);
                        Integer dd = c.get(Calendar.DAY_OF_MONTH);
                        tempStr += String.format("%02d", dd) + "/" + String.format("%02d", mm+1) + "/" + String.format("%04d", yyyy);
                        JSONObject obj = jsonArray.getJSONObject(i);
                        if (obj.has("weather")) {
                            JSONObject weatherObj = obj.getJSONArray("weather").getJSONObject(0);
                            tempStr += fixedLengthString(weatherObj.optString("main"),20);
                        }
                        if (obj.has("temp")) {
                            JSONObject tempObj = obj.getJSONObject("temp");
                            tempStr += fixedLengthString(RoundCelsius(tempObj.optString("min")) + " / " + RoundCelsius(tempObj.optString("max")) + " \u2103", 20);
                        }
                        listContents.add(tempStr);
                    }

                    ListView myListView = (ListView) getView().findViewById(R.id.my_list);
                    myListView.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.list_item, listContents));

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

            String target_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=" + target_location + "&cnt=7&units=metric&appid=ba7d5d53599254ae48141b652c3fd61f";

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
