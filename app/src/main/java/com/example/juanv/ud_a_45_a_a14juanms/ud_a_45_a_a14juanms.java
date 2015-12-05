package com.example.juanv.ud_a_45_a_a14juanms;

import android.app.Activity;
import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ud_a_45_a_a14juanms extends Activity {

    Button btnDescargar;
    TextView txtRutas;
    File ruta;
    private final String arquivo = "http://manuais.iessanclemente.net/images/2/20/Platega_pdm_rutas.xml";
    private File rutaArquivo;
    private Thread thread;
    private ArrayList<Ruta> listadoRutas = new ArrayList<Ruta>();

    public static enum TIPOREDE {MOBIL, ETHERNET, WIFI, SENREDE}

    ;
    private TIPOREDE conexion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ud_a_45_a_a14juanms);

        ruta = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/RUTAS/");
        if (!ruta.exists()) {
            ruta.mkdirs();
            Log.i("Cartafol", "Creado" + ruta.toString());
        } else {
            Log.i("Cartafol", "Non creado,xa estaba" + ruta.toString());
        }
        conexion = comprobarRede();
        if (conexion == TIPOREDE.SENREDE) {
            Toast.makeText(this, "NON SE PODE FACER ESTA PRACTICA SEN CONEXION A INTERNET", Toast.LENGTH_LONG).show();
            finish();
        }

        xestionarEventos();

    }


    private TIPOREDE comprobarRede() {
        NetworkInfo networkInfo = null;

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    return TIPOREDE.MOBIL;
                case ConnectivityManager.TYPE_ETHERNET:
                    // ATENCION API LEVEL 13 PARA ESTA CONSTANTE
                    return TIPOREDE.ETHERNET;
                case ConnectivityManager.TYPE_WIFI:
                    // NON ESTEAS MOITO TEMPO CO WIFI POSTO
                    // MAIS INFORMACION EN http://www.avaate.org/
                    return TIPOREDE.WIFI;
            }
        }
        return TIPOREDE.SENREDE;
    }//METODO QUE COMPROBA O TIPO DE REDE


    private void descargarArquivo() {
        URL url = null;
        try {
            url = new URL(arquivo);
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }

        HttpURLConnection conn = null;
        String nomeArquivo = Uri.parse(arquivo).getLastPathSegment();
        rutaArquivo = new File(Environment.getExternalStorageDirectory() + "/RUTAS/" + nomeArquivo);
        try {

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000); /* milliseconds */
            conn.setConnectTimeout(15000); /* milliseconds */
            conn.setRequestMethod("POST");
            conn.setDoInput(true); /* Indicamos que a conexión vai recibir datos */

            conn.connect();

            int response = conn.getResponseCode();
            if (response != HttpURLConnection.HTTP_OK) {
                return;
            }
            OutputStream os = new FileOutputStream(rutaArquivo);
            InputStream in = conn.getInputStream();
            byte data[] = new byte[1024];// Buffer a utilizar
            int count;
            while ((count = in.read(data)) != -1) {
                os.write(data, 0, count);
            }
            os.flush();
            os.close();
            in.close();
            conn.disconnect();
            Log.i("COMUNICACION", "ACABO");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e("COMUNICACION", e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("COMUNICACION", e.getMessage());
        }

    }

    private void xestionarEventos() {


        btnDescargar = (Button) findViewById(R.id.btnDescargar);
        txtRutas = (TextView) findViewById(R.id.txtRutas);
        btnDescargar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtRutas.setText("");
                // TODO Auto-generated method stub
                thread = new Thread() {

                    @Override
                    public void run() {
                        descargarArquivo();

                    }
                };
                thread.start();
                try {
                    lerArquivo();

                    for (Ruta r : listadoRutas) {

                        txtRutas.setText("\nNOVA RUTA:\n" + r.getNome() + "\n"+r.getDescricion() + "\n");
                        //txtRutas.append(r.getNome() + "\n");
                        //txtRutas.append(r.getDescricion() + "\n");

                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "ERRO:" + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "ERRO:" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });




    }


    public void lerArquivo() throws IOException, XmlPullParserException {
        File arch = new File(ruta, Uri.parse(arquivo).getLastPathSegment());
        InputStream is = new FileInputStream(arch);

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "UTF-8");

        int evento = parser.nextTag();
        Ruta r = null;

        while (evento != XmlPullParser.END_DOCUMENT) {
            if (evento == XmlPullParser.START_TAG) {
                if (parser.getName().equals("ruta")) {
                    r = new Ruta();
                    evento = parser.nextTag();
                    r.setNome(parser.nextText());
                    evento = parser.nextTag();
                    r.setDescricion(parser.nextText());

                }
            }
            if (evento == XmlPullParser.END_TAG) {
                if (parser.getName().equals("ruta")) {
                    listadoRutas.add(r);
                }
            }

            evento = parser.next();
        }

        is.close();
    }

}
