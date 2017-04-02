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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lenovo on 3/6/2016.
 */

public class jarak extends AppCompatActivity {

    public String[] nbssid;
    public String[] nwifis;
    private List<String> bs;

    WifiManager wifi;
    WifiScanReceiver wifiReciever;
    ListView tv2;

    Thread loop;
    boolean set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jarak);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        bs = getIntent().getStringArrayListExtra("bs");
        nwifis = getIntent().getStringArrayExtra("nwifi");
        nbssid = getIntent().getExtras().getStringArray("nbssid");
        tv2 = (ListView) findViewById(R.id.jrak);


    }

    public class WifiScanReceiver extends BroadcastReceiver {
        public int[] rssi = {0,0,0,0,0,0};
        int[]   sum0 = {0,0,0,0,0,0},
                sum1 = {0,0,0,0,0,0},
                sum2 = {0,0,0,0,0,0},
                sum3 = {0,0,0,0,0,0},
                sum4 = {0,0,0,0,0,0};
        int k;
        public double sums, FSL, F =1, B =0, H =1;
        public double[] x ={0,0,0,0,0,0},
                cov ={1,1,1,1,1,1},
                x2 = {0,0,0,0,0,0},
                Z2 = {0,0,0,0,0,0},
                Q = {0,0,0,0,0,0},
                R = {1,1,1,1,1,1},
                predX = {0,0,0,0,0,0},
                predCov = {0,0,0,0,0,0},
                K = {0,0,0,0,0,0},
                xbar = {0,0,0,0,0,0},
                Zbar = {0,0,0,0,0,0};
        public double[] jarak = {0,0,0,0,0,0};
        public String wifis[], ssid, bssid[], rssiSignal;
        public String  tj[] = {"0","0","0","0","0","0"};
        ListView lv;
        WifiManager wifi;
        List<ScanResult> wifiScanList;
        public Context cc;
        public ArrayAdapter<String> adapter;


        public WifiScanReceiver(WifiManager wifi, ListView lv){
            this.wifi = wifi;
            this.lv = lv;
        }

        public void onReceive(final Context c, Intent intent) {
            cc=c;

            Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    return (lhs.level < rhs.level ? 1 : (lhs.level == rhs.level ? 0 : -1));
                }
            };

            wifiScanList = wifi.getScanResults();
            Collections.sort(wifiScanList, comparator);


            bssid = new String[wifiScanList.size()];
        wifis = new String[bs.size()];
            k = 0;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < wifiScanList.size(); i++) {
                        bssid[i] = (wifiScanList.get(i).BSSID);
                        for (int j = 0; j < nbssid.length; j++) {
                            if (bssid[i].equals(nbssid[j])) {
                                ssid = (wifiScanList.get(i).SSID);

                                try {
                                    rssi[j] = (wifiScanList.get(i).level);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }


                                rssiSignal = String.valueOf(rssi[j]);
//                                Log.d("RSSI = ", rssiSignal);
                                sum0[j] = rssi[j];
                                if (sum0[j] < 0) {
                                    sum0[j] *= -1;
                                }

                                if (sum1[j] == 0) {
                                    sums = sum0[j];
                                } else if (sum2[j] == 0) {
                                    sums = (sum0[j] + sum1[j]) / 2;
                                } else if (sum3[j] == 0) {
                                    sums = (sum0[j] + sum1[j] + sum2[j]) / 3;
                                } else if (sum4[j] == 0) {
                                    sums = (sum0[j] + sum1[j] + sum2[j] + sum3[j]) / 4;
                                } else {
                                    sums = (sum0[j] + sum1[j] + sum2[j] + sum3[j] + sum4[j]) / 5;
                                }

                                sums = skalmanf(sums, j);

                                jarak[j] = Math.pow(10, ((sums - (20 * Math.log10(2400)) + 27.55) / 20));

                                tj[j] = String.format("%.2f", jarak[j]);

                                try {
                                    wifis[j] = ssid + "\n"
                                            + rssiSignal + " dBm " + sums + " dBm"+ "\n"
                                            + "distance2 = " + tj[j] + " m" + "\n";
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                sum4[j] = sum3[j];
                                sum3[j] = sum2[j];
                                sum2[j] = sum1[j];
                                sum1[j] = sum0[j];

                            }
                        }

                    }
                }
            });

            adapter = new ArrayAdapter<>(c,android.R.layout.simple_list_item_1, wifis);
            lv.setAdapter(adapter);

        }

        double skalmanf(double Z, int k){

            predX[k] = F*x[k] + B;
            predCov[k] = ((F*cov[k])*F)+Q[k];

            K[k] = (predCov[k] *H)/((H*predCov[k]*H)+R[k]);

            x[k] = predX[k]+K[k]*(Z-(H*predX[k]));
            cov[k] = predCov[k]-(K[k]*H*predCov[k]);

            xbar[k] = (x[k] + x2[k])/2;
            Q[k] = ((Math.pow((x[k] - xbar[k]),2) + Math.pow((x2[k] - xbar[k]),2))/2)+0.0000000001;

            Zbar[k] = (Z + Z2[k])/2;
            R[k] = (Math.pow((Z - Zbar[k]),2) + Math.pow((Z2[k] - Zbar[k]),2))/2;

            x2[k] = x[k];
            Z2[k] = Z;
            return x[k];
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        set = false;

        try {
            loop.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        unregisterReceiver(wifiReciever);

    }

    @Override
    protected void onResume() {
        super.onResume();
        wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver(wifi, tv2);
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        set = true;
        loop = new Thread(new Runnable() {
            @Override
            public void run() {
                while (set) {

                    if(!wifi.isWifiEnabled())
                    {
                        wifi.setWifiEnabled(true);
                    }

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
        });

        loop.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
