package com.example.yf.location_v2;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
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

    private BeaconManager beaconManager;
    String UUID, major, minor, Dist, out = "waiting", m06zj4 = "0";
    Collection<Beacon> max;
    private String android_id;
    ShortestPath SP;
    private ImageView imageView;
    private Bitmap bitmap;
    int img_out = 0, sour = 0;
    TextView show;
    ProgressDialog progressDialog;
    private static final int REQUEST_ENABLE_BT = 2;
    private TouchLocation TL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img);
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);//get andorid device id!!!

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
        } else if (ni == null) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(Dijkstra.this);
            dialog.setMessage("請開啟網路連線功能")
                    .setTitle("無法使用")
                    .setCancelable(false)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish(); // exit program
                                    beaconManager.unbind(Dijkstra.this);//停止掃描
                                }
                            });
            dialog.show();
        }

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(Dijkstra.this, "device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        progressDialog = ProgressDialog.show(Dijkstra.this, "提醒", "正在取得資訊請稍候");

        imageView = (ImageView) findViewById(R.id.image_view);
//        show = (TextView) findViewById(R.id.textView5);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo);
        int H = bitmap.getHeight();
        int W = bitmap.getWidth();

        imageView.setMaxHeight(H);
        imageView.setMaxWidth(W);

        TL = new TouchLocation(H, W);
//        TL.setDotWithJson("111");

        imageView.setImageBitmap(bitmap);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.w("output222", out);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.w("123", "X:" + event.getX() + "  Y:" + event.getY());
                    Object oo = TL.analyseTouchLocation(event.getX(), event.getY());
                    if (oo != null) {
                        img_out = (int) oo;

                        try {
                            sour = Integer.parseInt(minor.toString());
                        } catch (Exception e) {
                            sour = 0;
                            e.printStackTrace();
                        }

                        m06zj4 = "User_OnClick";
//                        show.setText("");
                        new LoadingDataAsyncTask().execute();


                    }
                }
                return true;
            }
        });

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));     //0215為讀取iBeacon  beac為altBeacon
        beaconManager.bind(this);


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {//攔截beacon 不會讓他在背景執行
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Dijkstra.this.finish();

            beaconManager.unbind(Dijkstra.this);

        }
        return super.onKeyDown(keyCode, event);
    }


    public String calculateShortestPath(int source, int destination, int option) {

        SP.dijkstra.calculateDistance(source);
        SP.dijkstra.output(option, destination);

        return SP.TextOut;
    }

    public void setMap(int[][] mapData) {

//        Log.w("mydebug222", String.valueOf(mapData));
        int node, neighbor, cost;
        SP = new ShortestPath(31);

        for (int i = 0; i < mapData.length; i++) {
            node = mapData[i][0];
            neighbor = mapData[i][1];
            cost = mapData[i][2];
            Log.w("mydebugggg", String.valueOf(cost));

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

                    if (m06zj4.equals("0")) {
                        m06zj4 = "finish_download";
                        new LoadingDataAsyncTask().execute();
                    }

                }

            }

        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }


    }

    public String http() {
        String total = "";
        try {

            String urlParameters = "uuid=" + URLEncoder.encode(UUID, "UTF-8") + "&major="
                    + URLEncoder.encode(major, "UTF-8") + "&android_id" + URLEncoder.encode(android_id, "UTF-8");
            URL url;
            HttpURLConnection connection = null;
            try {
                url = new URL("http://120.114.138.143/test/get_map.php");

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
                    jsondata(total);
                }

//                try {
//
//                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");//取得時間
//
//                    String date = sDateFormat.format(new java.util.Date());
//
//
//                    File myFile = new File("/sdcard/fu/"+date+".txt");//存資訊至SD card
//                    myFile.createNewFile();
//                    FileOutputStream fOut = new FileOutputStream(myFile);
//                    OutputStreamWriter myOutWriter =
//                            new OutputStreamWriter(fOut);
//                    myOutWriter.append(total);
//                    myOutWriter.close();
//                    fOut.close();
//                } catch (Exception e) {
////
//                }


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

    public void jsondata(String test_1) {
        try {
            Log.w("mydebug_gettest", test_1);
            int road;
            int count = 0;
            int NodeTotal;
            int width, height, radius, x, y;
            int[][] temp,tou;
            int count2=0;
            JSONObject obj = new JSONObject(test_1);
            //---------------------------------------------start1
            JSONObject information = obj.getJSONObject("information");
            NodeTotal = information.getInt("node");
            Log.w("mydebug_NodeTotal", String.valueOf(NodeTotal));
            road = information.getInt("road");
            JSONObject photo = information.getJSONObject("photo");
            width = photo.getInt("width");
            height = photo.getInt("height");
            radius = photo.getInt("radius");
            Log.w("mydebug_radius", String.valueOf(radius));
            //---------------------------------------------end1
            temp = new int[road][3];
            tou = new int[road][2];

            //---------------------------------------------start2
            JSONArray algorithm = obj.getJSONArray("algorithm");
            for (int i = 0; i < NodeTotal; i++) {
                Log.w("mydebug_log", "Start for");
                JSONObject nowNode = algorithm.getJSONObject(i);

                JSONObject touch = nowNode.getJSONObject("touch");
                Log.w("mydebug_touch", String.valueOf(touch));
                x = touch.getInt("X");
                y = touch.getInt("Y");
                Log.w("mydebug_x", String.valueOf(x));
                Log.w("mydebug_y", String.valueOf(y));

                JSONArray neighbor = nowNode.getJSONArray("neighbor");
                JSONArray cost = nowNode.getJSONArray("distance");
                int now = nowNode.getInt("this");
                tou[count2][0] = x;
                tou[count2][1]=y;
                Log.w("array1", String.valueOf(tou[count2][0]));
                Log.w("array2", String.valueOf(tou[count2][1]));
                count2++;

                Log.w("mydebug_this", String.valueOf(now));
                for (int j = 0; j < neighbor.length(); j++) {
                    Log.w("mydebug_for2", "start for");
//                    nei=neighbor.get(j).toString();
//                    cos=cost.get(j).toString();
//                    abc.append(cos+",");
                    temp[count][0] = now;
                    temp[count][1] = neighbor.getInt(j);
                    temp[count][2] = cost.getInt(j);
                    Log.w("mydebug_temp", String.valueOf(now));
                    Log.w("mydebug_for2", "end for");
                    count++;
                }


            }



            setMap(temp);
            TL.setDotWithJson(tou);
            Log.w("mydebug_finishsetmap", "okok");

        } catch (Exception e) {

        }

//        try {
//            int count = 0;
//            int[][] temp;
//            Log.w("debug5432", test_1);
//            temp = new int[64][3];
//            JSONArray array = new JSONArray(test_1);
//            for (int i = 0; i < 31; i++) {
//                JSONObject jsonobj = array.getJSONObject(i);
//                JSONArray jsonarray = jsonobj.getJSONArray("neighbor");
//                JSONArray jsonarray_2 = jsonobj.getJSONArray("distance");
//                int now = jsonobj.getInt("this");
//
//                for (int j = 0; j < jsonarray.length(); j++) {
//                    temp[count][0] = now;
//                    temp[count][1] = jsonarray.getInt(j);
//                    temp[count][2] = jsonarray_2.getInt(j);
//                    count++;
//                }
//            }
//            setMap(temp);
//
//        } catch (Exception e) {
//
//        }

    }

    class LoadingDataAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... param) {

            switch (m06zj4) {
                case "Check_Beacon":

                    break;

                case "User_OnClick":
                    out = calculateShortestPath(sour, img_out, 2);
                    break;

                case "finish_download":
                    http();
                    break;

                default:
                    break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            Log.w("mydebug_m06zj4", m06zj4);

            switch (m06zj4) {
                case "Check_Beacon":
                    break;

                case "User_OnClick":
//                    show.setText(out);
                    Log.w("output", out);
                    out = "reset";
                    m06zj4 = "Check_Beacon";
                    break;

                case "finish_download":
                    progressDialog.dismiss();
                    m06zj4 = "Check_Beacon";
                    Toast.makeText(Dijkstra.this, "讀取完成", Toast.LENGTH_SHORT).show();
//                    Log.w("mydebug_finish_scan","OK!!");
                    break;

                default:
                    break;
            }

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