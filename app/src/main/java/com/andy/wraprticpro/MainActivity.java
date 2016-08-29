package com.andy.wraprticpro;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Downloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ImageView noInternetIM;

    private static final String ADMOB_APP_ID = "ca-app-pub-9276418840515235~5207672303";

    private ProgressDialog pDialog;

    private static String TAG = "MainActivity";

    private ArrayList<String[]> appList = new ArrayList<>();

    private String BASE_URL = "http://waptrickpro.xyz/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.digitcreativestudio.adakajian")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.digitcreativestudio.adakajian")));
                }
            }
        });
        MobileAds.initialize(this, ADMOB_APP_ID);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);

        noInternetIM = (ImageView) findViewById(R.id.no_internet);

        if(isNetworkConnected()){
            getData();
        }else{
            showSettingsAlert();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    //cek internet connection
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    private void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setIcon(R.drawable.ic_wifi);
        alertDialog.setTitle(getResources().getString(R.string.no_internet));

        alertDialog
                .setMessage(getResources().getString(R.string.no_internet_question));

        alertDialog.setPositiveButton(getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_SETTINGS);
                        MainActivity.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton(getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        noInternetIM.setVisibility(View.VISIBLE);
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                    if(isNetworkConnected()){
                        getData();
                    }else{
                        showSettingsAlert();
                    }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getData(){
        noInternetIM.setVisibility(View.GONE);
        showpDialog();
        // url untuk mengambil data list aplikasi
        // String url = "http://waptrickpro.xyz/api/rest_apps.php";
        String url = "http://waptrickpro.xyz/api/rest_apps.php?id_proyek=1";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    appList.clear();
                    // Parsing json object response
                    JSONArray reseps = response.getJSONArray("apps");
                    for (int i=0;i<reseps.length();i++){
                        JSONObject data = (JSONObject) reseps.get(i);
                        String nama = data.getString("nama");
                        String info = data.getString("info");
                        String url_gambar = BASE_URL+data.getString("url_gambar");
                        String url_app = data.getString("url_app");
                        String[] item2 = new String[]{nama,info,url_gambar,"1",url_app};
                        appList.add(item2);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
                String[] item = new String[]{"","","","2",""};
                int number = (int) Math.ceil(Math.random() * 3);
                appList.add(number,item);
                mAdapter = new ListAdapter(MainActivity.this, appList,
                        new ListAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(String[] item) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item[4])));
                            }
                        });
                mRecyclerView.setAdapter(mAdapter);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                // hide the progress dialog
                hidepDialog();
            }
        });
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(jsonObjReq);

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
