package com.soysalmustafa.gpstakipsistemi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.widget.Toast;



/**
 * Created by Mustafa on 25.3.2015.
 */
public class Giris extends Activity implements LocationListener {

    TextView goster;
    private TextView enlemDegeri;
    private TextView boylamDegeri;
    private LocationManager locationManager;
    private String provider;
    private Handler mHandler = new Handler();

    private void startTime() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 10000);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            // buraya ne yapmak istiyorsan o kodu yaz.. Kodun sonlandıktan sonra 1 saniye sonra tekrar çalışacak şekilde handler tekrar çalışacak.
            final TextView tview = (TextView) findViewById(R.id.textView1);
            tview.setText("Mustafa Soysal");
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();


            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                String query1 = URLEncoder.encode(enlemDegeri.getText().toString(), "utf-8");// utf-8 yaparak get verisinin boşluklarını%20 olmasını ve programın hata vermesini engelledik
                String query2 = URLEncoder.encode(boylamDegeri.getText().toString(), "utf-8");
                String query3=URLEncoder.encode(imei,"utf-8");
                String query4 = URLEncoder.encode(goster.getText().toString(), "utf-8");
                request.setURI(new URI("http://soysalmustafa.com/islem.php?type=addkordinaat&latitude="+query1+"&longitude="+query2+"&imei="+query3+"&k_adi="+query4)); //Get isteğimizi URL yoluyla belirliyoruz.
                HttpResponse response = httpclient.execute(request); //İsteğimizi gerçekleştiriyoruz.
                HttpEntity entity = response.getEntity(); //Gelen cevabı işliyoruz.
                String result = null;
                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8); //utf-8 burda önemli
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result=sb.toString(); //Artık result stringi php tarafından ekrana print yada echo komutlarıyla yazdırılmış veriyi içeriyor.
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();//toast mesajı olarakta gösterdik
                //Harita İşlemleri

            }
            catch(IllegalStateException e)
            {

                tview.setText("Hata: "+e.toString());
            }
            catch(Exception ex){
                tview.setText("Hata: "+ex.toString());
            }

            mHandler.postDelayed(this, 10000);
        }
    };
    public void onCreate(Bundle savedInstanceState)
    {   StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giris);
        goster=(TextView)findViewById(R.id.txtGoster);
        Bundle alinan=getIntent().getExtras();
        String alinmis=alinan.getString("veri");
        goster.setText(alinmis);
        enlemDegeri=(TextView)findViewById(R.id.txtEnlem2);
        boylamDegeri=(TextView)findViewById(R.id.txtBoylam2);
        final TextView tview = (TextView) findViewById(R.id.textView1);
        tview.setText("Mustafa Soysal");
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            gpsErisilemiyorUyarisi();
        }
        provider=LocationManager.GPS_PROVIDER;
        Location location=locationManager.getLastKnownLocation(provider);
        if(location!=null)
        {
            enlemDegeri.setText(String.valueOf(location.getLatitude()));
            boylamDegeri.setText(String.valueOf(location.getLongitude()));
            startTime();

        }
        else
        {
            enlemDegeri.setText("Konum bilgisi yok");
            boylamDegeri.setText("Konum bilgisi yok");
        }

    }

    private void gpsErisilemiyorUyarisi()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Gps Kapali Açmak İster misiniz ?")
                .setCancelable(false)
                .setPositiveButton("GPS aktifleştir", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        gpsSecenekleriGoster();
                    }
                });
        builder.setNegativeButton("Hayır etkinleştirme", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }

    private void gpsSecenekleriGoster()
    {
        Intent gpsOptionsIntent=new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        locationManager.requestLocationUpdates(provider,40,1,this);
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        enlemDegeri.setText(String.valueOf(location.getLatitude()));
        boylamDegeri.setText(String.valueOf(location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Aktif konum bilgisi kaynağı:"+provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Pasif konum bilgisi kaynağı:"+provider, Toast.LENGTH_SHORT).show();
    }
}
