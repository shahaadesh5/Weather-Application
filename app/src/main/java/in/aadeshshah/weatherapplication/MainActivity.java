package in.aadeshshah.weatherapplication;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button btnGetWeather, clearButton;
    private TextView result, humidityText, cloudsText, minMax;
    private EditText cityName;
    private String cityNameStr;
    private ImageView weatherIcon;
    private RequestQueue mQueue;
    private JsonObjectRequest stringRequest;
    private String url = "http://api.openweathermap.org/data/2.5/weather?q=halifax&appid=cccf0bec5cd6c006ba737af8aa7877ce&units=metric";
    private String temperature;
    private String icon;
    private String description, cityNameAPI, countryNameAPI, temp_min, temp_max, humidity, clouds ,mainDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        sendRequest();

        //Initiating UI components
        result = (TextView) findViewById(R.id.resultView);
        humidityText = (TextView) findViewById(R.id.humidty);
        cloudsText = (TextView) findViewById(R.id.clouds);
        minMax = (TextView) findViewById(R.id.minMax);
        cityName = (EditText) findViewById(R.id.cityName);
        btnGetWeather = (Button) findViewById(R.id.getWeather);
        clearButton = (Button) findViewById(R.id.clear);
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send request to API if string is entered in EditView
                cityNameStr = cityName.getText().toString();
                if (!cityNameStr.matches("")) {
                    url = url.replace("halifax",cityNameStr);
                    sendRequest();
                    url = url.replace(cityNameStr,"halifax");
                }
                else {
                    clearTemp();
                    // Error Handling - if nothing is entered and weather search button is pressed
                    Toast.makeText(getApplicationContext(),"Please Enter a City Name!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (result.getText()=="")
                {
                    Toast.makeText(getApplicationContext(), "Nothing to clear on screen!", Toast.LENGTH_SHORT).show();
                    cityName.getText().clear();
                }
                else {
                    clearTemp();
                    cityName.getText().clear();
                }
            }
        });
    }

    private void sendRequest() {

        mQueue = Volley.newRequestQueue(this);
        stringRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(),"Here's the weather for the city!",Toast.LENGTH_SHORT).show();
                try {
                    //Getting all relevant data in string form to display in activity
                    temperature = response.getJSONObject("main").getString("temp"); // getting temperature value
                    icon = response.getJSONArray("weather").getJSONObject(0).getString("icon"); // getting icon value
                    mainDescription = response.getJSONArray("weather").getJSONObject(0).getString("main"); // getting main description value
                    description = response.getJSONArray("weather").getJSONObject(0).getString("description"); // getting description value
                    mainDescription = mainDescription.substring(0,1).toUpperCase() + mainDescription.substring(1);
                    description = description.substring(0,1).toUpperCase() + description.substring(1);
                    cityNameAPI = response.getString("name"); // getting cityName value
                    countryNameAPI = response.getJSONObject("sys").getString("country"); // getting countryName value
                    temp_min = response.getJSONObject("main").getString("temp_min"); // getting min_temperature value
                    temp_max = response.getJSONObject("main").getString("temp_max"); // getting max_temperature value
                    humidity = response.getJSONObject("main").getString(    "humidity"); // getting humidity value
                    clouds = response.getJSONObject("clouds").getString("all"); // getting clouds value
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadImageFromUrl("http://openweathermap.org/img/w/"+icon+".png");
                result.setText(temperature + "°C\n"+mainDescription+"\n"+description+"\n"+cityNameAPI+", "+countryNameAPI);
                humidityText.setText(humidity+"%\nHumidity");
                cloudsText.setText(clouds+"%\nClouds");
                minMax.setText("↑ "+temp_max+"°C\n↓ "+temp_min+"°C");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                clearTemp();
                Toast.makeText(getApplicationContext(),"Error retrieving data! Check the city name!",Toast.LENGTH_SHORT).show();
            }
        });

        mQueue.add(stringRequest);
    }

    public void clearTemp(){
        result.setText("");
        weatherIcon.setImageResource(android.R.color.transparent);
        humidityText.setText("");
        minMax.setText("");
        cloudsText.setText("");
    }

    // Displaying the weather icon in ImageView Check library dependancy in build.gradle

    private void loadImageFromUrl(String imgUrl){
        Picasso.with(this).load(imgUrl).resize(200,0).into(weatherIcon); //https://codinginflow.com/tutorials/android/picasso/part-1-image-loading
    }
}
