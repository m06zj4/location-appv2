package com.example.yf.location_v2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class iBeaconAccept extends ActionBarActivity implements BeaconConsumer {

    private BeaconManager beaconManager;
    TextView out, jsonout;
    String UUID, major, minor, classid, classname, Dist;
    Collection<Beacon> max;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_beacon_accept);
        out = (TextView) findViewById(R.id.textView2);
        jsonout = (TextView) findViewById(R.id.textView3);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));     //0215為讀取iBeacon  beac為altBeacon
        beaconManager.bind(this);


    }


    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {


            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, org.altbeacon.beacon.Region region) {

            }

            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, android.graphics.Region region) {






                if (beacons.size() > 0) {
                    UUID = beacons.iterator().next().getId1().toString();
                    major = beacons.iterator().next().getId2().toString();
                    minor = beacons.iterator().next().getId3().toString();
                    Dist = String.valueOf(beacons.iterator().next().getDistance());

                    if (max == null) {
                        max = beacons;
                    } else {
                        if (max.iterator().next().getDistance() > beacons.iterator().next().getDistance()) {
                            max = beacons;
                        }
                    }


                    new LoadingDataAsyncTask().execute();

//                    String RSSI = "RSSI:" + String.valueOf(beacons.iterator().next().getRssi()) + "\n";
//                    String Dist = "Distance:" + beacons.iterator().next().getDistance() + "\n";
//                    String android = "address" + beacons.iterator().next().getBluetoothAddress() + "\n";


//                    Message msg = new Message();
//                    msg.what = 1;
//                    msg.obj = UUID + major + minor + RSSI + Dist + android;
//                    handler.sendMessage(msg);
                }

            }

        });
        try {
            beaconManager.startRangingBeaconsInRegion(new org.altbeacon.beacon.Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }

    }

//    Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//
//            if (msg.what == 1) {
//                String print = (String) msg.obj;
//                out.setText(print);
//                new LoadingDataAsyncTask().execute();
//            }
//        }


    //    };
    public void postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://192.168.7.25/compare.php");

        Log.w("mydebug1", UUID);
        Log.w("mydebug2",major);
        Log.w("mydebug3",minor);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("uuid", UUID));
            nameValuePairs.add(new BasicNameValuePair("major", major));
            nameValuePairs.add(new BasicNameValuePair("minor", minor));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
                    HTTP.UTF_8));

            HttpResponse response = httpclient.execute(httppost);

            if (response.getStatusLine().getStatusCode() == 200) {
                String strResult = EntityUtils.toString(response.getEntity());
                json(strResult);
//                Log.w("mydebug", strResult);

            }

        } catch (IOException e) {

        }
    }

    private void json(String test) {
//        Log.w("mydebug", test);
        try {
            JSONArray jsonArray = new JSONArray(test);
            JSONObject jsondata = jsonArray.getJSONObject(0);
            classid = jsondata.getString("class_id");
            classname = jsondata.getString("class_name");
//            jsonout.setText(classid + classname);
        } catch (Exception e) {
        }
    }


    class LoadingDataAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... param) {
            // getData();
            postData();
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            // showData();
            jsonout.setText(classid + "  " + classname);


        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


    }
}


//Project下之libs新增aar檔