package hk.hku.chong.aaron.weatherforecast.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import hk.hku.chong.aaron.weatherforecast.R;

import static android.content.Context.MODE_PRIVATE;


public class ThreeFragment extends Fragment{

    private EditText edit_text;
    private Button submit_btn;
    private String target_location;

    public ThreeFragment() {
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
        return inflater.inflate(R.layout.fragment_three, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences preferences = getContext().getSharedPreferences("PREF_LOCATION", MODE_PRIVATE);
        target_location = preferences.getString("location", "");

        edit_text = (EditText) getView().findViewById(R.id.edit_text);
        submit_btn = (Button) getView().findViewById(R.id.submit_btn);
        edit_text.setText(target_location);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edit_text.getText().toString())){
                    edit_text.setError("Target location cannot be empty.");
                }else {
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("PREF_LOCATION", MODE_PRIVATE).edit();
                    editor.putString("location", edit_text.getText().toString());
                    editor.commit();
                    InputMethodManager keyboard = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
                    edit_text.clearFocus();
                    Toast.makeText(getContext(), "Location changed to " + edit_text.getText().toString() + ".", Toast.LENGTH_SHORT).show();
                    Fragment frg = null;
                    frg = getActivity().getSupportFragmentManager().getFragments().get(1);
                    final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setAllowOptimization(false);
                    transaction.detach(frg).attach(frg).commitAllowingStateLoss();
                }
            }
        });
    }
}
