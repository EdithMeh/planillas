package com.example.planillas;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText et;
    String etre = "";
    String cargo = "";
    double total = 0, haber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void consultar(View v) {
        total = 0; haber = 0;
        et = findViewById(R.id.editText);
        etre = et.getText().toString();
        new ProcesoAsyncTask().execute("LP");
        new ProcesoAsyncTask().execute("LS");
        new ProcesoAsyncTask().execute("LB");
    }

    public void finalizar(View vista) {
        finish();
    }

    private class ProcesoAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... code) {
            return BajarDatos(code[0]);
        }

        @Override
        protected void onPostExecute(String resultado) {
            String code = resultado.split(";")[0];
            switch (code) {
                case "LP":
                    resultado = resultado.replace("<br />", "");
                    TextView tv1 = findViewById(R.id.textView1);
                    tv1.setText(resultado.split(";")[2]);
                    TextView tv2 = findViewById(R.id.textView2);
                    tv2.setText(resultado.split(";")[3]);
                    TextView tv3 = findViewById(R.id.textView3);
                    tv3.setText(resultado.split(";")[4]);
                    break;
                case "LS":
                    resultado = resultado.replace("<br />", "");
                    cargo = resultado.split(";")[3];
                    new ProcesoAsyncTask().execute("AUX");
                    break;
                case "AUX":
                    resultado = resultado.replace("<br />", "");
                    TextView tv4 = findViewById(R.id.textView4);
                    tv4.setText(resultado.split(";")[2]);
                    TextView tv5 = findViewById(R.id.textView5);
                    tv5.setText(resultado.split(";")[3]);
                    haber = Double.parseDouble(resultado.split(";")[3]);
                    break;
                case "LB":
                    String cadena[] =  resultado.split("<br />");
                    String re = "";
                    double x;
                    for (int i = 0 ; i<cadena.length ; i++){
                        if (i == 0){
                            re = cadena[i].split(";")[3];
                        } else {
                            re = cadena[i].split(";")[2];
                        }
                        x = Double.parseDouble(re);
                        total = total + x;
                    }
                    TextView tv6 = findViewById(R.id.textView6);
                    tv6.setText(total+"");
                    new ProcesoAsyncTask().execute("LD");
                    break;
                case "LD":
                    String desc[] =  resultado.split("<br />");
                    String res = "";
                    double decu= 0, y;
                    for (int i = 0 ; i<desc.length ; i++){
                        if (i == 0){
                            res = desc[i].split(";")[3];
                        } else {
                            res = desc[i].split(";")[2];
                        }
                        y = Double.parseDouble(res);
                        decu = decu + y;
                    }
                    TextView tv7 = findViewById(R.id.textView7);
                    tv7.setText(decu+"");
                    double r = haber + total - decu;
                    TextView tv8 = findViewById(R.id.textView8);
                    tv8.setText(r+"");
                    break;
            }
        }
    }

    public String BajarDatos(String code) {
        String url = "";
        switch (code) {
            case "LP":
                url = "http://clasespersonales.com/planillas/listapersonal.php?qci=" + etre;
                break;
            case "LS":
                url = "http://clasespersonales.com/giros/listasueldos.php?qci=" + etre;
                break;
            case "AUX":
                url = "http://clasespersonales.com/giros/listacargos.php?qid=" + cargo;
                break;
            case "LB" :
                url = "http://clasespersonales.com/planillas/listabonos.php?qci=" + etre;
                break;
            case "LD" :
                url = "http://clasespersonales.com/giros/listadescuentos.php?qci=" + etre;
                break;
        }
        InputStream puntero;
        String resultado = "";
        try {
            URL pageUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) pageUrl.openConnection();
            puntero = urlConnection.getInputStream();
            if (puntero != null) {
                resultado = convierteString(puntero);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code + ";" + resultado;
    }

    private static String convierteString(InputStream bloque) throws IOException {
        BufferedReader pt = new BufferedReader(new InputStreamReader(bloque));
        String line = "", res = "";
        while ((line = pt.readLine()) != null) {
            res = res + line;
        }
        pt.close();
        return res;
    }
}
