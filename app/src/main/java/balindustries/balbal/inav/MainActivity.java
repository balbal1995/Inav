package balindustries.balbal.inav;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by lenovo on 1/25/2016.
 */

public class MainActivity extends AppCompatActivity {


    Drawww vi;
    Thread loop;
    public static boolean display_map = false;
    public static String[] wifisss;

    Button start, target, startnav;

    ListView lv;
    ArrayAdapter adapter;
    String[] daftar;
    Point setar;

    AStar a;
    AStar.Cell pilihan;

    WifiManager wifi;
    WifiScanReceiver wifiReciever;
    boolean set, nav;
    int k=1;

    String s;
    long starttime = 0L;
    long timeInMilliseconds = 0L;
    long updatedtime = 0L;
    int secs = 0;
    int mins = 0;
    int milliseconds = 0;
    int tim=1000;
    int t =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        daftar = getResources().getStringArray(R.array.daftar_ruang);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wifisss = getResources().getStringArray(R.array.wifiss);

        for (int i=0;i<Drawww.nodee.size();i++) {
            Drawww.nodee.get(i).SSID = wifisss[i];
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                vi = (Drawww) findViewById(R.id.mapss);
            }
        });

        start = (Button) findViewById(R.id.setstart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (startnav.isEnabled()) {
//                    setar = Drawww.setstart();
//                    Drawww.setstra = true;
//                    startnav.setEnabled(false);
//                    target.setEnabled(false);
////                start.setText("Selesai");
//                    final Toast toast = Toast.makeText(MainActivity.this,"Set Titik Start",Toast.LENGTH_SHORT);
//                    toast.show();
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            toast.cancel();
//                        }
//                    }, 1000);
//                } else {
//                    setar = Drawww.setstart();
//                    Drawww.setstra = false;
//                    startnav.setEnabled(true);
//                    target.setEnabled(true);
////                start.setText("set start");
//
//                }
                t++;
                if (t==1) {
                    tim =1000;
                } else if (t==2) {
                    tim =2000;
                } else if (t==3) {
                    tim =5000;
                } else if (t==4) {
                    tim =10000;
                    t=0;
                }

                final Toast toast = Toast.makeText(MainActivity.this,"Set waktu " + tim,Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 1000);

            }
        });
        startnav = (Button) findViewById(R.id.nav);
        startnav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!nav) {
                            nav = true;
                            Drawww.nav = true;
                        } else {
                            nav = false;
                            Drawww.nav = false;
                            vi.refresh();
                        }
                    }
                });
            }
        });
        target = (Button) findViewById(R.id.settarget);
        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lv.isEnabled()) {
//                target.setText("set target");
                    lv.setVisibility(View.INVISIBLE);
                    lv.setEnabled(false);
                    startnav.setEnabled(true);
                    start.setEnabled(true);

                } else {
//                target.setText("Selesai");
                    startnav.setEnabled(false);
                    start.setEnabled(false);
                    Drawww.nav = false;
                    lv.setEnabled(true);
                    lv.setVisibility(View.VISIBLE);
                    final Toast toast = Toast.makeText(MainActivity.this,"Set Titik Target",Toast.LENGTH_SHORT);
                    toast.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);
                }
            }
        });
        lv = (ListView) findViewById(R.id.lists);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice,daftar);
        lv.setChoiceMode(1);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(pilih);

        lv.setVisibility(View.INVISIBLE);
        lv.setEnabled(false);

        wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!wifi.isWifiEnabled())
                {
                    wifi.setWifiEnabled(true);
                }
            }
        });

    }

    public void trilat(){
        Drawww.get3lowestnode();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                start_timer();
                Trilateration tl = new Trilateration(
                        Drawww.trinodel.get(0).x,
                        Drawww.trinodel.get(0).y,
                        Drawww.trinodel.get(0).neighbors.get(0).x,
                        Drawww.trinodel.get(0).neighbors.get(0).y,
                        Drawww.trinodel.get(0).neighbors.get(1).x,
                        Drawww.trinodel.get(0).neighbors.get(1).y,
                        Drawww.trinodel.get(0).jarak,
                        Drawww.trinodel.get(0).neighbors.get(0).jarak,
                        Drawww.trinodel.get(0).neighbors.get(1).jarak);
                tl.itung();
                int[] nxy = Normalisasi((int)tl.getX(),(int)tl.getY());
                Drawww.x = nxy[0];
                Drawww.y = nxy[1];
                vi.refresh();
                if (nav) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                a = new AStar(vi.getMeasuredWidth(),vi.getMeasuredHeight(),Drawww.x, Drawww.y, pilihan.x, pilihan.y);
                                a.search();
                                Drawww.route = a.route;
                                vi.refresh();
                            }
                        });
                    } catch (Exception e) {
                        final Toast toast = Toast.makeText(MainActivity.this,"pilih titik target",Toast.LENGTH_SHORT);
                        toast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 700);
                    }
//                    end_timer();
                }
                if (k==1){
                    timeInMilliseconds = SystemClock.uptimeMillis();
                    starttime = timeInMilliseconds;
                    secs = (int) (timeInMilliseconds / 1000);
                    mins = secs / 60;
                    secs = secs % 60;
                    int hours = mins/60;
                    mins = mins % 60;
                    milliseconds = (int) (timeInMilliseconds % 1000);
                    s = String.format("" + hours + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs) + ":"
                            + String.format("%03d /n", milliseconds));
                    note(MainActivity.this,"trilat",s);
                    k++;
                }

//                timeInMilliseconds = SystemClock.uptimeMillis() - starttime;
//                updatedtime = timeInMilliseconds;
//
//                secs = (int) (updatedtime / 1000);
//                mins = secs / 60;
//                secs = secs % 60;
//                milliseconds = (int) (updatedtime % 1000);
//                s = String.format("" + mins + ":" + String.format("%02d", secs) + ":"
//                        + String.format("%03d", milliseconds));
                s = String.format("x = " + nxy[0] + "y = " + nxy[1]);
                note(MainActivity.this,"trilat",s);
                Log.d("tri",s);

            }
        });
    }

    AdapterView.OnItemClickListener pilih =  new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getItemAtPosition(position).toString().equals("Tangga")){
                pilihan  = AStar.node.get(16);
            } else if (parent.getItemAtPosition(position).toString().equals("Toilet")){
                pilihan  = AStar.node.get(20);
            } else {
                pilihan  = AStar.node.get(position);
            }

        }
    };

    private int[] Normalisasi(int x, int y){
        if (x<325 && y >160){//125x
            if (x<325 && x>235 && y < 270){
                y = 185;
            } else  if (y > 650){
                y = 640;
                x = 123;
            } else {
                x = 123;
            }
        } else if (x>325 && y >160){//125x
            if (x<430&& x>325 && y < 270){
                y = 185;
            } else  if (y > 650){
                y = 640;
                x = 535;
            } else {
                x = 535;
            }
        } else if (y<160){//125x
            if (x > 120 && x<325) {
                y = 185;
            } else if (x<120){
                x=123;
                y=185;
            } else if (x > 325 && x<530){
                y = 185;
            } else if (x>530){
                x=535;
                y=185;
            }
        }
        int[] nxy = new int[]{x, y};
        return nxy;
    }

    private void start_timer(){
        starttime = SystemClock.uptimeMillis();
    }

    private void end_timer(){
        timeInMilliseconds = SystemClock.uptimeMillis() - starttime;
        updatedtime = timeInMilliseconds;
        secs = (int) (updatedtime / 1000);
        mins = secs / 60;
        secs = secs % 60;
        milliseconds = (int) (updatedtime % 1000);
        String s = String.format("" + mins + ":" + String.format("%02d", secs) + ":"
                + String.format("%03d \n", milliseconds));
        if (k==1){
            note(this,"timer", "Timer");
            k++;
        }
        note(this,"timer",s);
        Log.d("Timer ", s);

        starttime = 0L;
        timeInMilliseconds = 0L;
        updatedtime = 0L;
        secs = 0;
        mins = 0;
        milliseconds = 0;
    }

    public void note(Context context, String sFileName, String sBody) {
        try {
//            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            File root = new File(Environment.getExternalStorageDirectory()+File.separator+"TA_Folder", "Report Files");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile,true);
            writer.append(sBody+"\n\n");
            writer.flush();
            writer.close();
//            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!wifi.isWifiEnabled())
        {
            wifi.setWifiEnabled(true);
        }

        wifiReciever = new WifiScanReceiver(wifi);
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        set = true;
        loop = new Thread(new Runnable() {
            @Override
            public void run() {
                while (set) {
                    start_timer();
                    if(!wifi.isWifiEnabled())
                    {
                        wifi.setWifiEnabled(true);
                    }

                    try {
                        Thread.sleep(tim);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                wifi.startScan();
                                trilat();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("jancuk", "jancuk");
                            }
                        }
                    });
                    end_timer();
                }
            }
        });

        loop.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        set = false;

        wifiReciever.sstop();

        try {
            loop.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        unregisterReceiver(wifiReciever);

        if(wifi.isWifiEnabled())
        {
            wifi.setWifiEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (display_map) {
            MenuItem itemm = menu.findItem(R.id.disp_map);
            itemm.setVisible(true);

            MenuItem itemn = menu.findItem(R.id.node);
            itemn.setVisible(false);

            MenuItem itema = menu.findItem(R.id.disp_AP);
            itema.setVisible(false);
        } else {
            MenuItem item = menu.findItem(R.id.disp_map);
            item.setVisible(false);

            MenuItem itemn = menu.findItem(R.id.node);
            itemn.setVisible(true);

            MenuItem itema = menu.findItem(R.id.disp_AP);
            itema.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.node){
            if (!Drawww.viewnode) {
                Drawww.viewnode = true;
                item.setTitle(getResources().getString(R.string.rnode));
                vi.refresh();
            } else {
                Drawww.viewnode = false;
                item.setTitle(getResources().getString(R.string.node));
                vi.refresh();
            }
        } else if (id == R.id.disp_wifi){
            Intent intent = new Intent(getBaseContext(),DisplayListWifi.class);

            display_map = true;
            invalidateOptionsMenu();
            startActivity(intent);
        } else if (id == R.id.disp_map){
            Intent intent = new Intent(getBaseContext(),MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.disp_AP){
            if (!Drawww.view_AP) {
                Drawww.view_AP = true;
                item.setTitle(getResources().getString(R.string.rem_AP));
                vi.refresh();
            } else {
                Drawww.view_AP = false;
                item.setTitle(getResources().getString(R.string.disp_AP));
                vi.refresh();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
