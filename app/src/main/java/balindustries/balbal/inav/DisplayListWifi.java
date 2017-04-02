package balindustries.balbal.inav;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by lenovo on 2/25/2016.
 */

public class DisplayListWifi extends AppCompatActivity {

    private ListView lv;
    private WifiManager wifi;
    private WifiScanReceiver wifiReciever;
    private Thread loopp;
    static boolean set;
    static boolean selection;
    public int total = 0, totalselected =0;
    public ArrayList<Integer> selected;
    private ArrayList<String> bs;
    int pos;
    String[] name, nbssid;
    Intent intent;
    boolean[] bool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list_wifi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refresh Wifi", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                wifi.startScan();
            }
        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bs = new ArrayList<String>();
        lv = (ListView)findViewById(R.id.lv);
        selected = new ArrayList<>();
        bool = new boolean[10];

        name = new String[10];
        nbssid = new String[10];

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
                if (selection) {
                    pos = position;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selected.add(total, pos);

                            name[total] = (String) parent.getItemAtPosition(pos);
                            String[] lines = name[total].split("\n");
                            nbssid[total] = lines[2];
                            bs.add(total,lines[2]);
                            total += 1;
                            Toast.makeText(DisplayListWifi.this, "Selected Wifi : " + total, Toast.LENGTH_SHORT).show();
                            Snackbar.make(view, "Click action to check distance for " + total + " wifi", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("action", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            intent = new Intent(DisplayListWifi.this, jarak.class);
                                            intent.putExtra("nwifi", name);
                                            intent.putExtra("nbssid", nbssid);
                                            intent.putExtra("bs", bs);
                                            startActivity(intent);
                                        }
                                    }).show();

                            totalselected = total;
                        }
                    });
                }

            }
        });



    }

    Runnable runwifi = new Runnable() {
        @Override
        public void run() {
            while (set) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            wifi.startScan();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("jancuk", "jancuk");
                        }
                    }
                });
            }
        }
    };

    public class WifiScanReceiver extends BroadcastReceiver {
        private int[] rssi;
        private String wifis[],ssid[], bssid[], rssiSignal[];
        private ListView lv;
        private WifiManager wifi;
        private List<ScanResult> wifiScanList;
        private ArrayAdapter adapter;

        public WifiScanReceiver(WifiManager wifi, ListView lv){
            this.wifi = wifi;
            this.lv = lv;
        }

        public void onReceive(Context c, Intent intent) {

            Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    return (lhs.level <rhs.level ? 1 : (lhs.level==rhs.level ? 0 : -1));
                }
            };

            wifiScanList = wifi.getScanResults();
            if (!selection) {
                Collections.sort(wifiScanList, comparator);
            }

            wifis = new String[wifiScanList.size()];
            ssid = new String[wifiScanList.size()];
            bssid = new String[wifiScanList.size()];
            rssi = new int[wifiScanList.size()];
            rssiSignal = new String[wifiScanList.size()];

            for(int i = 0; i < wifiScanList.size(); i++){
                ssid[i] = (wifiScanList.get(i).SSID);
                bssid[i] = (wifiScanList.get(i).BSSID);
                for (int j = 0; j < SplashScreen.wifisss.length; j++) {
                    if (bssid[i].equals(SplashScreen.wifisss[j]))
                    {
                        ssid[i] = "AP" + Integer.toString(j+1);
                    }
                }
                rssi[i] = (wifiScanList.get(i)).level;
                rssiSignal[i] = String.valueOf(rssi[i]);
                wifis[i] = ssid[i]  + "\n" + rssiSignal[i] + " dBm" + "\n" + bssid[i];
            }

            if (selection) {
                total = 0;
                adapter = new ArrayAdapter<>(c, android.R.layout.simple_list_item_multiple_choice,wifis);
                lv.setChoiceMode(2);
                lv.setAdapter(adapter);
                set = false;
                for (int i = 0 ; i < totalselected ; i++) {
                    lv.setItemChecked(selected.get(i), true);
                }
            } else {
                adapter = new ArrayAdapter<>(c,android.R.layout.simple_list_item_1,wifis);
                lv.setAdapter(adapter);
            }

        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem mItem=menu.findItem(R.id.action_send);
        mItem.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        selection = true;

                        return false;
                    }
                });

        if (MainActivity.display_map) {
            MenuItem item = menu.findItem(R.id.disp_map);
            item.setVisible(true);

            MenuItem itemn = menu.findItem(R.id.node);
            itemn.setVisible(false);

            MenuItem itema = menu.findItem(R.id.disp_AP);
            itema.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.disp_map){
            Intent intent = new Intent(getBaseContext(),MainActivity.class);

            MainActivity.display_map = false;
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {

        set = false;
        selection = false;
        super.onPause();
        try {
            loopp.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        unregisterReceiver(wifiReciever);
    }

    @Override
    protected void onResume() {
        super.onResume();
        set = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                wifiReciever = new WifiScanReceiver(wifi, lv);
                registerReceiver(wifiReciever, new IntentFilter(
                        WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                if (!wifi.isWifiEnabled()) {
                    wifi.setWifiEnabled(true);
                }
            }
        });

        loopp = new Thread(runwifi);
        loopp.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
