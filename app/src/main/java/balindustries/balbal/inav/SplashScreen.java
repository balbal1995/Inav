package balindustries.balbal.inav;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by lenovo on 6/5/2016.
 */

public class SplashScreen extends Activity {


    public static String[] wifisss;
    WifiManager wifi;
    ImageView mimage;

public static int displayHeight, displayWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_splash);

        mimage = (ImageView) findViewById(R.id.mimage);
        mimage.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.logo, 200, 200));

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;

        new splash().execute();

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private class splash extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            wifisss = getResources().getStringArray(R.array.wifiss);

            AStar sa = new AStar();

                    for (int i=0;i<Drawww.nodee.size();i++) {
                        Drawww.nodee.get(i).SSID = wifisss[i];
                    }
            wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);

                    if(!wifi.isWifiEnabled())
                    {
                        wifi.setWifiEnabled(true);
                    }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Intent i = new Intent(SplashScreen.this, MainActivity.class);

            startActivity(i);

            finish();
        }

    }

}

