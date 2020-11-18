package com.example.androidassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Weatherforecast extends AppCompatActivity {
    private final String ACTIVITY_NAME = "Weatherforecast";
    ProgressBar Progress_bar;
    ImageView imageview;
    TextView currentTemp2,minTemp2,maxTemp2;
    Spinner sp;
    List<String> cityList;
    //TextView city;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weatherforecast);

        imageview=findViewById(R.id.weatherimage);
        currentTemp2=findViewById(R.id.current_temp);
        minTemp2=findViewById(R.id.min_temp);
        maxTemp2=findViewById(R.id.max_temp);


        Progress_bar = findViewById(R.id.progress_bar);
        Progress_bar.setVisibility(View.VISIBLE);
        sp=(Spinner)findViewById(R.id.Spin);
        getcity();

    }
    public void getcity(){
        cityList = Arrays.asList(getResources().getStringArray(R.array.cities));
        ArrayAdapter ad=new ArrayAdapter(this,android.R.layout.simple_spinner_item,cityList);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sp.setAdapter(ad);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView <?> adapterView, View view, int i, long l) {
            new Forecastquery(cityList.get(i)).execute("this will go to background");
            //city.setText(cityList.get(i) + " Weather");
        }
            @Override
            public void onNothingSelected(AdapterView <?> adapterView) {

            }
        });


    }

    private class Forecastquery extends AsyncTask<String, Integer, String> {
        private String currentTemp;
        private String minTemp;
        private String maxTemp;
        private Bitmap picture;
        protected String city;

        Forecastquery(String city) {
            this.city = city;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.i("incoming param", strings[0] + "---------------------------");
            try {
//String s="http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=79cecf493cb6e52d25bb7b7050ff723c&mode=xml&units=metric";
                //Log.e("forecast",s);
//URL url=new URL(s);
             URL url = new URL("https://api.openweathermap.org/data/2.5/weather?" +
                "q=" + this.city + "," + "ca&" +
                        "APPID=79cecf493cb6e52d25bb7b7050ff723c&" +
                        "mode=xml&" +
                        "units=metric");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setReadTimeout(10000);
                httpsURLConnection.setConnectTimeout(15000);
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.connect();
                InputStream in = httpsURLConnection.getInputStream();


                try {
                    InputStream is=(InputStream) new URL("https://api.openweathermap.org/data/2.5/weather?" +
                            "q=" + this.city + "," + "ca&" +
                            "APPID=79cecf493cb6e52d25bb7b7050ff723c&" +
                            "mode=xml&" +
                            "units=metric").getContent();
                    Drawable d=Drawable.createFromStream(is,"src name");

                    imageview.setImageDrawable(d);
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(in, null);
                    int type;

                    while ((type = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {

                        if (parser.getEventType() == XmlPullParser.START_TAG) {
                            if (parser.getName().equals("temperature")) {
                                currentTemp = parser.getAttributeValue(null, "value");
                                publishProgress(25);
                                minTemp = parser.getAttributeValue(null, "min");
                                publishProgress(50);
                                maxTemp = parser.getAttributeValue(null, "max");
                                publishProgress(75);
                                Toast.makeText(getApplicationContext(),currentTemp.toString(),Toast.LENGTH_LONG).show();
                            } else if (parser.getName().equals("weather")) {
                                String iconName = parser.getAttributeValue(null, "icon");
                                String fileName = iconName + ".png";

                                Log.i(ACTIVITY_NAME, "Looking for file: " + fileName);
                                if (fileExistance(fileName)) {
                                    FileInputStream fis = null;
                                    try {
                                        fis = openFileInput(fileName);

                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    Log.i(ACTIVITY_NAME, "Found the file locally");
                                    picture = BitmapFactory.decodeStream(fis);
                                } else {
                                    String iconUrl = "https://openweathermap.org/img/w/" + fileName;
                                    picture = getImage(new URL(iconUrl));

                                    FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                                    picture.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                    Log.i(ACTIVITY_NAME, "Downloaded the file from the Internet");
                                    outputStream.flush();
                                    outputStream.close();
                                }
                                publishProgress(100);
                            }
                        }

                        parser.next();
                    }
                } finally {
                    httpsURLConnection.disconnect();
                    in.close();
                }
            } catch (Exception ex) {
//                Toast.makeText(Weatherforecast.this, ex+"", Toast.LENGTH_SHORT).show(); //ex.printStackTrace();
            }

            return " do background ended";

        }

        public boolean fileExistance(String fname) {
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

        public Bitmap getImage(URL url) {
            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // {
                    return BitmapFactory.decodeStream(connection.getInputStream());
                } else
                    return null;
            } catch (Exception e) {
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String a) {
            Log.i("post", a.toString() + "------------------");
            Progress_bar.setVisibility(View.INVISIBLE);

            imageview.setImageBitmap(picture);



            currentTemp2.setText(currentTemp + "C\u00b0");
            minTemp2.setText(minTemp + "C\u00b0");
            maxTemp2.setText(maxTemp + "C\u00b0");

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i("vlaues", values[0] + "-------------------------------");
            Progress_bar.setProgress(values[0]);
        }
    }
}








