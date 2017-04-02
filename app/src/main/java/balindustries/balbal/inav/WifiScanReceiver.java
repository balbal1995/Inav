package balindustries.balbal.inav;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lenovo on 4/5/2016.
 */
public class WifiScanReceiver extends BroadcastReceiver {
    public int[] rssi = {0,0,0,0,0,0};
    int[]   sum0 = {0,0,0,0,0,0},
            sum1 = {0,0,0,0,0,0},
            sum2 = {0,0,0,0,0,0},
            sum3 = {0,0,0,0,0,0},
            sum4 = {0,0,0,0,0,0};
    int k;
    public double sums, F =1, B =0, H =1;
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
    public static double[] jarak = {0,0,0,0,0,0};
    public String ssid, bssid[], rssiSignal;
    public static String tj[] = {"0","0","0","0","0","0"};
    public String ifiw[];
    ListView lv;
    WifiManager wifi;
    List<ScanResult> wifiScanList;
    public Context cc;
    Thread loo;



    public WifiScanReceiver(WifiManager wifi){
        this.wifi = wifi;
        ifiw = SplashScreen.wifisss;
        loo= new Thread(run);
    }

    public void onReceive(final Context c, Intent intent) {
        cc=c;
        loo.run();
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    return (lhs.level < rhs.level ? 1 : (lhs.level == rhs.level ? 0 : -1));
                }
            };

            wifiScanList = wifi.getScanResults();
            Collections.sort(wifiScanList, comparator);


            bssid = new String[wifiScanList.size()];
            k = 0;

            for (int i = 0; i < wifiScanList.size(); i++) {
                bssid[i] = (wifiScanList.get(i).BSSID);
                for (int j = 0; j < Drawww.nodee.size(); j++) {
                    if (bssid[i].equals(Drawww.nodee.get(j).SSID)) {
                        ssid = (wifiScanList.get(i).SSID);

                        try {
                            rssi[j] = (wifiScanList.get(i).level);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }


                        rssiSignal = String.valueOf(rssi);

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
                        Drawww.nodee.get(j).jarak = jarak[j];

                        tj[j] = String.format("%.2f", jarak[j]);

                        Log.d("dist = ", tj[j]);

                        sum4[j] = sum3[j];
                        sum3[j] = sum2[j];
                        sum2[j] = sum1[j];
                        sum1[j] = sum0[j];

                        k++;

                    }
                }

            }
        }

    };

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

    public void sstop(){
        try {
            loo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}




