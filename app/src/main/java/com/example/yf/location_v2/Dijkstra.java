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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;

public class Dijkstra extends ActionBarActivity implements BeaconConsumer {

    private final int MAP = 2;
    final int V = 31;
    int NodeTotal;
    private BeaconManager beaconManager;
    String UUID, major, minor, classid, classname, Dist;
    Collection<Beacon> max;
    TextView out, jsonout, tmajor, tminor, testbeacon;
    private String android_id;
    ShortestPath SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dijkstra);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);//get andorid device id!!!


        tmajor = (TextView) findViewById(R.id.showText1);
        tminor = (TextView) findViewById(R.id.showText2);
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
//        ShortestPath SP = new ShortestPath(V);

//        setMap(SP.graph);

//        SP.initialMatrix(false);
//
//        SP.floydWarshell.calculateDistance();
//        SP.floydWarshell.output();

        SP.dijkstra.calculateDistance(source);
        SP.dijkstra.output(option, destination);

        return SP.TextOut;
    }

    //    public void setMap(ShortestPath.vertex[] graph) {
    public void setMap(int[][] mapData) {

        Log.w("mydebug222", String.valueOf(mapData));
        int node, neighbor, cost;
//        SP = new ShortestPath(NodeTotal);
        SP = new ShortestPath(31);

        for (int i = 0; i < mapData.length; i++) {
            node = mapData[i][0];
            neighbor = mapData[i][1];
            cost = mapData[i][2];

            SP.graph[node].adjacentEdge(neighbor, cost);
        }

        SP.initialMatrix(false);
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

    public void postData() {
        // Create a new HttpClient and Post Header
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpPost httppost = new HttpPost("http://120.114.104.122:8081/compare.php");
//
////        Log.w("mydebug1", UUID);
////        Log.w("mydebug2",major);
////        Log.w("mydebug3",minor);
//
//        try {
//            // Add your data
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            nameValuePairs.add(new BasicNameValuePair("uuid", UUID));
//            nameValuePairs.add(new BasicNameValuePair("major", major));
//            nameValuePairs.add(new BasicNameValuePair("minor", minor));
//            nameValuePairs.add(new BasicNameValuePair("android_id",android_id));
//            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
//                    HTTP.UTF_8));
//
//            HttpResponse response = httpclient.execute(httppost);
//
//            if (response.getStatusLine().getStatusCode() == 200) {
//                String strResult = EntityUtils.toString(response.getEntity());
//                json(strResult);
//                Log.w("mydebug", strResult);
//
//            }
//
//        } catch (IOException e) {
//
//        }
    }

    //-------------------------------------------------------------------------------
    public String http() {
        String total = "";
        try {

            String urlParameters = "uuid=" + URLEncoder.encode(UUID, "UTF-8") + "&major="
                    + URLEncoder.encode(major, "UTF-8") + "&android_id" + URLEncoder.encode(android_id, "UTF-8");
            URL url;
            HttpURLConnection connection = null;
            try {
                url = new URL("http://120.114.104.122:8081/test/get_map.php");

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                connection.setDoInput(true);
                connection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
                Log.w("mydebug111", urlParameters);

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = rd.readLine()) != null) {
                    total = total + line;
//                    Log.w("mydebug222",total);
                    jsondata(total);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

//-------------------------------------------------------------------------------

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

    public Object jsonParse(int action, String jsonString) {
        switch (action) {
            case MAP:
                try {
                    int road;
                    int count = 0;
                    int[][] temp;
                    JSONObject obj = new JSONObject(jsonString);
                    for (int j = 0; j < obj.length(); j++) {
                        JSONObject jsondata = obj.getJSONObject(String.valueOf(j));
                        JSONArray neighbor = jsondata.getJSONArray("neighbor");
                        JSONArray distance = jsondata.getJSONArray("distance");
                        int now = jsondata.getInt(String.valueOf(j));
                        for (int i = 0; i < neighbor.length(); i++) {

                        }


                    }

                    return null;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    public void jsondata(String test_1) {
        try {
            int count = 0;
            int[][] temp;
            Log.w("debug5432", test_1);
            temp = new int[64][3];
            JSONArray array = new JSONArray(test_1);
//            Log.w("mydebug51", "123123");
            for (int i = 0; i < 31; i++) {
//                Log.w("mydebug61", "123123");
                JSONObject jsonobj = array.getJSONObject(i);
                JSONArray jsonarray = jsonobj.getJSONArray("neighbor");
                JSONArray jsonarray_2 = jsonobj.getJSONArray("distance");
//                Log.w("mydebug81", "12365");
                int now = jsonobj.getInt("this");
//                Log.w("mydebug100",String.valueOf(now));

                for (int j = 0; j < jsonarray_2.length(); j++) {
//                    Log.w("mydebug71", "123123");
                    temp[count][0] = now;
                    temp[count][1] = jsonarray.getInt(j);
                    temp[count][2] = jsonarray_2.getInt(j);
                    count++;
                    setMap(temp);
//                    Log.w("mydebug921",String.valueOf(count));
                }
            }

        } catch (Exception e) {

        }

    }


    class LoadingDataAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... param) {
            // getData();
//            postData();
            http();

            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            // showData();
//            jsonout.setText(classid + "  " + classname);
            tmajor.setText(String.valueOf(major));
            tminor.setText(String.valueOf(minor));


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



//        graph[0].adjacentEdge(1, 4);
//
//        graph[1].adjacentEdge(0, 4);
//        graph[1].adjacentEdge(2, 6);
//        graph[1].adjacentEdge(5, 1);
//
//        graph[2].adjacentEdge(1, 6);
//        graph[2].adjacentEdge(3, 5);
//        graph[2].adjacentEdge(6, 5);
//
//        graph[3].adjacentEdge(2, 5);
//        graph[3].adjacentEdge(4, 5);
//
//        graph[4].adjacentEdge(3, 5);
//        graph[4].adjacentEdge(7, 5);
//        graph[4].adjacentEdge(9, 15);
//
//        graph[5].adjacentEdge(1, 1);
//        graph[5].adjacentEdge(8, 8);
//
//        graph[6].adjacentEdge(2, 5);
//
//        graph[7].adjacentEdge(4, 5);
//
//        graph[8].adjacentEdge(5, 8);
//        graph[8].adjacentEdge(10, 5);
//
//        graph[9].adjacentEdge(4, 15);
//        graph[9].adjacentEdge(11, 8);
//
//        graph[10].adjacentEdge(8, 5);
//        graph[10].adjacentEdge(12, 10);
//
//        graph[11].adjacentEdge(9, 8);
//        graph[11].adjacentEdge(14, 11);
//
//        graph[12].adjacentEdge(10, 10);
//        graph[12].adjacentEdge(13, 16);
//
//        graph[13].adjacentEdge(12, 16);
//        graph[13].adjacentEdge(14, 11);
//
//        graph[14].adjacentEdge(11, 11);
//        graph[14].adjacentEdge(13, 11);
//        graph[14].adjacentEdge(20, 11);
//
//        graph[15].adjacentEdge(16, 2);
//
//        graph[16].adjacentEdge(15, 2);
//        graph[16].adjacentEdge(17, 2);
//
//        graph[17].adjacentEdge(16, 2);
//        graph[17].adjacentEdge(18, 6);
//        graph[17].adjacentEdge(21, 7);
//
//        graph[18].adjacentEdge(17, 6);
//        graph[18].adjacentEdge(19, 6);
//
//        graph[19].adjacentEdge(18, 6);
//        graph[19].adjacentEdge(20, 3);
//
//        graph[20].adjacentEdge(14, 11);
//        graph[20].adjacentEdge(19, 3);
//        graph[20].adjacentEdge(22, 8);
//
//        graph[21].adjacentEdge(17, 7);
//        graph[21].adjacentEdge(23, 8);
//
//        graph[22].adjacentEdge(20, 8);
//        graph[22].adjacentEdge(24, 12);
//
//        graph[23].adjacentEdge(21, 8);
//        graph[23].adjacentEdge(25, 13);
//
//        graph[24].adjacentEdge(22, 12);
//        graph[24].adjacentEdge(26, 3);
//
//        graph[25].adjacentEdge(23, 13);
//        graph[25].adjacentEdge(26, 4);
//
//        graph[26].adjacentEdge(24, 3);
//        graph[26].adjacentEdge(25, 4);
//        graph[26].adjacentEdge(30, 23);
//
//        graph[27].adjacentEdge(28, 2);
//
//        graph[28].adjacentEdge(27, 2);
//        graph[28].adjacentEdge(29, 6);
//
//        graph[29].adjacentEdge(28, 6);
//        graph[29].adjacentEdge(30, 3);
//
//        graph[30].adjacentEdge(26, 23);
//        graph[30].adjacentEdge(29, 3);