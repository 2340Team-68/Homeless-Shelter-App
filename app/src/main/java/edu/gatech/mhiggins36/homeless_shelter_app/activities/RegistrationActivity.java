package edu.gatech.mhiggins36.homeless_shelter_app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.gatech.mhiggins36.homeless_shelter_app.Controller;
import edu.gatech.mhiggins36.homeless_shelter_app.R;
import edu.gatech.mhiggins36.homeless_shelter_app.VolleySingleton;
import edu.gatech.mhiggins36.homeless_shelter_app.models.User;

public class RegistrationActivity extends AppCompatActivity {

    EditText emailField;
    EditText nameField;
    EditText passField;
    EditText passField2;
    TextView errorMessageReg;
    Spinner userTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        nameField = findViewById(R.id.nameFieldReg);
        emailField = findViewById(R.id.emailFieldReg);
        passField = findViewById(R.id.passwordFieldReg);
        passField2 = findViewById(R.id.passwordFieldReg2);
        errorMessageReg = findViewById(R.id.errorMessageReg);

        //stuff needed for the spinner found online
        //https://developer.android.com/guide/topics/ui/controls/spinner.html
        userTypeSpinner = findViewById(R.id.userTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.userTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

    }

    public void register(View view) {
        //if the passwords do not match then reveal red text saying so
        //TODO talk about when we want to trim and when not

        if (nameField.getText().toString().trim().equals("")) {
            errorMessageReg.setText("Name Is Empty");
            errorMessageReg.setVisibility(View.VISIBLE);
            return;
        }
        if (emailField.getText().toString().trim().equals("")) {
            errorMessageReg.setText("Email Is Empty");
            errorMessageReg.setVisibility(View.VISIBLE);
            return;
        }
        //checks if the email is in an email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailField.getText().toString().trim()).matches()) {
            errorMessageReg.setText("Email format is not valid");
            errorMessageReg.setVisibility(View.VISIBLE);
            return;
        }

        //todo check if the username is already taken
//        if (Controller.userMap.containsKey(emailField.getText().toString().trim())) {
//            errorMessageReg.setText("Email Entered Already In Use");
//            errorMessageReg.setVisibility(View.VISIBLE);
//            return;
//        }
        if (passField.getText().toString().equals("")) {
            errorMessageReg.setText("No Password Entered");
            errorMessageReg.setVisibility(View.VISIBLE);
            return;
        }
        if (passField2.getText().toString().equals("")) {
            errorMessageReg.setText("Must Re-Enter Password");
            errorMessageReg.setVisibility(View.VISIBLE);
            return;
        }
        if (!(passField.getText().toString().equals(passField2.getText().toString()))) {
            errorMessageReg.setVisibility(View.VISIBLE);
            return;
        }
        /*
        check that email is a valid email form
        check that all fields are filled in
        */

//        User newUser = new User(nameField.getText().toString(), emailField.getText().toString(),
//                passField.getText().toString(), userTypeSpinner.getSelectedItem().toString());
//
//        Controller.addUser(newUser);

        //ToDo get the specific path for this call
        String url = "http://shelter.lmc.gatech.edu/user";

        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Register", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int userId = obj.getInt("id");
                            String token = obj.getString("token");
                            User currentUser =
                                    new User(nameField.getText().toString(),
                                            emailField.getText().toString(),
                                            userTypeSpinner.getSelectedItem().toString(),
                                            userId, token);
                            /*
                            All this is to put currentUser in a SharedPreference. Using a static
                            field in Controller won't work since it gets wiped when the app is
                            closed. A SharedPreference saves it on the device and lets you access
                            it in any activity.
                             */
                            SharedPreferences pref = getSharedPreferences("myPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = pref.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(currentUser);
                            prefsEditor.putString("currentUser", json);
                            prefsEditor.commit();

                            Log.d("Register", ""+currentUser.getUserId());
                            Log.d("Register", currentUser.getJwt());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Result handling
                        // todo: discuss this later
                        Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
                        searchIntent.putExtra("Sender", "RegistrationActivity");
                        searchIntent.putExtra("userType", emailField.getText().toString());
                        // enables access to type of account
                        startActivity(searchIntent);



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                errorMessageReg.setText("Email Entered Already In Use");
                errorMessageReg.setVisibility(View.VISIBLE);
                System.out.println("Something went wrong!");
                error.printStackTrace();

            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", nameField.getText().toString().trim());
                params.put("email", emailField.getText().toString().trim());
                params.put("password", passField.getText().toString());
                return params;
            }
        };
        // Add the request to the queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    public void cancelRegistration(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }
}
