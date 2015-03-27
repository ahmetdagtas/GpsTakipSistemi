package com.soysalmustafa.gpstakipsistemi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.annotation.SuppressLint;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;

public class MainActivity extends Activity implements OnClickListener {
     EditText k_ad,sifre;
     Button btnGiris,btnKayit,btnNerdeyim;
    Bundle bdn;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {   StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        bdn=new Bundle();
        setContentView(R.layout.activity_main);
        k_ad=(EditText)findViewById(R.id.etKullanici);
        sifre=(EditText)findViewById(R.id.etSifre);
        btnGiris=(Button)findViewById(R.id.btnGiris);
        btnKayit=(Button)findViewById(R.id.btnKayit);
        btnNerdeyim=(Button)findViewById(R.id.btnNerdeyim);
        btnGiris.setOnClickListener(this);
        btnKayit.setOnClickListener(this);
        btnNerdeyim.setOnClickListener(this);
    }

    public void girisKontrol()
    {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            String query1 = URLEncoder.encode(k_ad.getText().toString(), "utf-8");// utf-8 yaparak get verisinin boşluklarını%20 olmasını ve programın hata vermesini engelledik
            String query2 = URLEncoder.encode(sifre.getText().toString(), "utf-8");
            request.setURI(new URI("http://soysalmustafa.com/islem.php?type=enter&k_ad="+query1+"&sifre="+query2)); //Get isteğimizi URL yoluyla belirliyoruz.
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

            String s = "";
            int id=0;
            JSONArray jArray = new JSONArray(result);
            for(int i=0; i<jArray.length();i++){
                JSONObject json = jArray.getJSONObject(i);
                s = s + json.getString("imei");
                id=id+json.getInt("id");
            }
            final Intent i=new Intent(getApplicationContext(),Giris.class);
            if(s.equals(""))
            {
                String m="Hata";
                Toast.makeText(getApplicationContext(), m, Toast.LENGTH_LONG).show();//toast mesajı olarakta göster
            }
            else
            {
                String aktarilacak=k_ad.getText().toString();
                bdn.putString("veri",aktarilacak);
                i.putExtras(bdn);
                startActivity(i);
            }


        }
        catch(Exception ex){

        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btnGiris:
                girisKontrol();
                break;
            case R.id.btnKayit:
                startActivity(new Intent("android.intent.action.Kayit"));
                break;
            case R.id.btnNerdeyim:
                startActivity(new Intent("android.intent.action.fragment"));
                break;
            default:
                break;
        }
    }


}
