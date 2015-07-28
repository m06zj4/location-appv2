package com.example.yf.location_v2;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
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

public class Dijkstra extends ActionBarActivity implements BeaconConsumer {


    final int V = 31;

    private BeaconManager beaconManager;
    String UUID, major, minor, classid, classname, Dist;
    Collection<Beacon> max;
    TextView out, jsonout,tmajor,tminor,testbeacon;
    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dijkstra);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);//get andorid device id!!!


        tmajor=(TextView)findViewById(R.id.showText1);
        tminor=(TextView)findViewById(R.id.showText2);
        tminor.setSelected(true);
        Button bt = (Button) findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sour, opti, dest;
                String out;

//                EditText et1 = (EditText) findViewById(R.id.editText1);
//                EditText et2 = (EditText) findViewById(R.id.editText2);
                EditText et3 = (EditText) findViewById(R.id.editText3);


                try {
                    sour = Integer.parseInt(minor.toString());
                } catch (Exception e) {
                    sour = 0;
                    e.printStackTrace();
                }
//
//                try {
//                    opti = Integer.parseInt(et2.getText().toString());
//                } catch (Exception e) {
//                    opti = 0;
//                    e.printStackTrace();
//                }

                try {
                    dest = Integer.parseInt(et3.getText().toString());
                } catch (Exception e) {
                    dest = 0;
                    e.printStackTrace();
                }

                out = calculateShortestPath(sour, dest, 2);

                TextView tv = (TextView) findViewById(R.id.textView);
                tv.setText(out);


            }

        });
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));     //0215為讀取iBeacon  beac為altBeacon
        beaconManager.bind(this);
    }

    public String calculateShortestPath(int source, int destination, int option) {
        ShortestPath SP = new ShortestPath(V);

        setMap(SP.graph);

        SP.initialMatrix(false);
//
//        SP.floydWarshell.calculateDistance();
//        SP.floydWarshell.output();

        SP.dijkstra.calculateDistance(source);
        SP.dijkstra.output(option, destination);

        return SP.TextOut;
    }

    public void setMap(ShortestPath.vertex[] graph) {

        graph[0].adjacentEdge(1, 4);

        graph[1].adjacentEdge(0, 4);
        graph[1].adjacentEdge(2, 6);
        graph[1].adjacentEdge(5, 1);

        graph[2].adjacentEdge(1, 6);
        graph[2].adjacentEdge(3, 5);
        graph[2].adjacentEdge(6, 5);

        graph[3].adjacentEdge(2, 5);
        graph[3].adjacentEdge(4, 5);

        graph[4].adjacentEdge(3, 5);
        graph[4].adjacentEdge(7, 5);
        graph[4].adjacentEdge(9, 15);

        graph[5].adjacentEdge(1, 1);
        graph[5].adjacentEdge(8, 8);

        graph[6].adjacentEdge(2, 5);

        graph[7].adjacentEdge(4, 5);

        graph[8].adjacentEdge(5, 8);
        graph[8].adjacentEdge(10, 5);

        graph[9].adjacentEdge(4, 15);
        graph[9].adjacentEdge(11, 8);

        graph[10].adjacentEdge(8, 5);
        graph[10].adjacentEdge(12, 10);

        graph[11].adjacentEdge(9, 8);
        graph[11].adjacentEdge(14, 11);

        graph[12].adjacentEdge(10, 10);
        graph[12].adjacentEdge(13, 16);

        graph[13].adjacentEdge(12, 16);
        graph[13].adjacentEdge(14, 11);

        graph[14].adjacentEdge(11, 11);
        graph[14].adjacentEdge(13, 11);
        graph[14].adjacentEdge(20, 11);

        graph[15].adjacentEdge(16, 2);

        graph[16].adjacentEdge(15, 2);
        graph[16].adjacentEdge(17, 2);

        graph[17].adjacentEdge(16, 2);
        graph[17].adjacentEdge(18, 6);
        graph[17].adjacentEdge(21, 7);

        graph[18].adjacentEdge(17, 6);
        graph[18].adjacentEdge(19, 6);

        graph[19].adjacentEdge(18, 6);
        graph[19].adjacentEdge(20, 3);

        graph[20].adjacentEdge(14, 11);
        graph[20].adjacentEdge(19, 3);
        graph[20].adjacentEdge(22, 8);

        graph[21].adjacentEdge(17, 7);
        graph[21].adjacentEdge(23, 8);

        graph[22].adjacentEdge(20, 8);
        graph[22].adjacentEdge(24, 12);

        graph[23].adjacentEdge(21, 8);
        graph[23].adjacentEdge(25, 13);

        graph[24].adjacentEdge(22, 12);
        graph[24].adjacentEdge(26, 3);

        graph[25].adjacentEdge(23, 13);
        graph[25].adjacentEdge(26, 4);

        graph[26].adjacentEdge(24, 3);
        graph[26].adjacentEdge(25, 4);
        graph[26].adjacentEdge(30, 23);

        graph[27].adjacentEdge(28, 2);

        graph[28].adjacentEdge(27, 2);
        graph[28].adjacentEdge(29, 6);

        graph[29].adjacentEdge(28, 6);
        graph[29].adjacentEdge(30, 3);

        graph[30].adjacentEdge(26, 23);
        graph[30].adjacentEdge(29, 3);
    }


    //--------------------------------------Beacon---------------------------------------------------

    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {


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
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
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
            nameValuePairs.add(new BasicNameValuePair("android_id",android_id));
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
//            jsonout.setText(classid + "  " + classname);
                tmajor.setText(String.valueOf(major));
                tminor.setText(String.valueOf(classid+classname));


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


