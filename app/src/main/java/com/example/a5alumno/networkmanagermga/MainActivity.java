package com.example.a5alumno.networkmanagermga;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_read_feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_read_feed = (Button) findViewById(R.id.btnReadFeed);
        btn_read_feed.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnReadFeed) {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

            if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
                Toast.makeText(this, "Ole mi arma!!!", Toast.LENGTH_LONG).show();
                new MyAsyncTask().execute("https://www.theguardian.com/international/rss");
            } else {
                Toast.makeText(this, "Merda pa ti", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class MyAsyncTask extends AsyncTask<String, String, String> {

        private Context mContext;

        @Override
        protected
        @NonNull
        String doInBackground(String[] params) {

            URL mUrl = null;
            try {
                mUrl = new URL(params[0]);
                HttpURLConnection mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
                mHttpURLConnection.setRequestMethod("GET");
                mHttpURLConnection.setDoInput(true);

                mHttpURLConnection.connect();
                int respCode = mHttpURLConnection.getResponseCode();
                Log.i("MainActivity", "The response is: " + respCode);

                if (mHttpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    Log.i("MainActivity", "FUCK!");
                    InputStream mInputStream = mHttpURLConnection.getInputStream();

                    XmlPullParser mXmlPullParser = Xml.newPullParser();
                    mXmlPullParser.setInput(mInputStream, null);

                    StringBuilder mStringBuilder = new StringBuilder("");
                    int event = mXmlPullParser.nextTag();
                    //Done while the end document is not reached
                    while (mXmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                        switch (event) {
                            case XmlPullParser.START_TAG:
                                if (mXmlPullParser.getName().equals("item")) {
                                    mXmlPullParser.nextTag();
                                    mXmlPullParser.next();
                                    mStringBuilder.append(mXmlPullParser.getText().toString()).append("\n");
                                }
                                break;
                        }
                        event = mXmlPullParser.next();
                    }
                    mInputStream.close();
                    Log.i("MainActivity", mStringBuilder.toString());

                    return mStringBuilder.toString();
                }

            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
            return "Cajo na puta";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "merda cona", Toast.LENGTH_LONG).show();
            }
        }
    }
}
