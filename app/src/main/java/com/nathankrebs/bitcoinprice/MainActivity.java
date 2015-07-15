package com.nathankrebs.bitcoinprice;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends Activity {

    private static final String URL = "https://api.bitfinex.com/v1/pubticker/btcusd";
    private static final String testString = "This is a string to test git!";

    TextView tvPrice, tvTitle, tvVolume, tvTime;
    HttpClient client;
    JSONObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toast.makeText(MainActivity.this, "Touch \"Bitfinex\" to refresh!", Toast.LENGTH_LONG).show();

        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvVolume = (TextView) findViewById(R.id.tvVolume);
        tvTime = (TextView) findViewById(R.id.tvTime);
        client = new DefaultHttpClient();

        new ReadPrice().execute("mid");
        new ReadVolume().execute("volume");

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Updating...", Toast.LENGTH_SHORT).show();
                new ReadPrice().execute("mid");
                new ReadVolume().execute("volume");
            }
        });


    }

    public JSONObject getPrice() throws ClientProtocolException, IOException, JSONException {
        StringBuilder url = new StringBuilder(URL);

        HttpGet get = new HttpGet(url.toString());
        HttpResponse r = client.execute(get);
        int status = r.getStatusLine().getStatusCode();
        if(status == 200) {
            HttpEntity e = r.getEntity();
            String data = EntityUtils.toString(e);
            JSONObject obj = new JSONObject(data);
            return obj;
        } else {
            Toast.makeText(MainActivity.this, "error!", Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    public class ReadPrice extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                json = getPrice();
                String data = "";
                String price = "";
                String volume = "";
                price = json.getString(params[0]);
                //volume = json.getString(params[0]);
                //data = price + "*" + volume;
                data = price;
                return data;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
//            char c = s.charAt('*');
//            String price = s.substring(0, c);
//            String volume = s.substring(c, s.length());
            double raw = Double.parseDouble(s);
            raw = raw * 100;
            raw = Math.round(raw);
            raw = raw / 100;
            String result = String.valueOf(raw);

            tvPrice.setText(result);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String strDate = sdf.format(c.getTime());
            tvTime.setText(strDate);

        }
    }

    public class ReadVolume extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                json = getPrice();
                String str = "";
                str = json.getString(params[0]);
                return str;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            double raw = Double.parseDouble(s);
            raw = Math.round(raw);
            int num = (int) raw;
            String result = String.valueOf(num);

            tvVolume.setText(result);

        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        } else if(id == R.id.history) {
            Intent i = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(i);
            return true;
        } else if(id == R.id.graphs) {
            Intent i = new Intent(MainActivity.this, GraphActivity.class);
            startActivity(i);
            return true;
        } else if(id == R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        Toast.makeText(MainActivity.this, "Touch \"Bitfinex\" to refresh!", Toast.LENGTH_LONG).show();
        new ReadPrice().execute("mid");
        new ReadVolume().execute("volume");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }
}
