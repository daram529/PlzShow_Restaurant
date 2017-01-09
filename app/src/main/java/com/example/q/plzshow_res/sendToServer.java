package com.example.q.plzshow_res;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by q on 2017-01-07.
 */

public class sendToServer {

    public sendToServer() {
        super();
    }

    // AsyncTask to communicate with Facebook or our MongoDB
    public static class sendJSON extends AsyncTask<Void, Void, JSONObject> {
        String urlstr;
        String data;
        String contentType;

        public sendJSON(String url, String data, String contentType) {
            this.urlstr = url;
            this.data = data;
            this.contentType = contentType;
        }

        @Override
        public JSONObject doInBackground(Void... params) {
            HttpURLConnection conn;
            OutputStream os;
            InputStream is;
            BufferedReader reader;
            JSONObject json;

            try {
                URL url = new URL(urlstr);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);

                // If sending to our DB
                if (urlstr.contains("52.78.200.87")) {
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", contentType);
                    conn.setRequestProperty("Accept-Charset", "UTF-8");
                    conn.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length));

                    os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(data.getBytes());
                    os.flush();
                    os.close();
                }

                int statusCode = conn.getResponseCode();
                is = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                    response.append("\n");
                }
                reader.close();
                json = new JSONObject(response.toString());
                conn.disconnect();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                return null;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }
    }

    public void send(final JSONObject obj){
        new Thread() {
            public void run(){
                try {
                    URL url = new URL("http://52.78.200.87:3000");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept-Charset", "UTF-8");
                    connection.setRequestProperty("Content-Length", Integer.toString(obj.toString().getBytes().length));

                    connection.setDoInput(true);
                    connection.setDoOutput(true);


                    Log.d("cs496_post_server", obj+"");
                    Log.d("cs496_post_server2", obj.toString());
                    Log.d("cs496_post_server3", obj.toString().getBytes()+"");


                    OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
                    outputStream.write(obj.toString().getBytes());
                    outputStream.flush();
                    outputStream.close();

                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    JSONObject json = new JSONObject();
                    StringBuffer response = new StringBuffer();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                        response.append("\n");
                    }
                    reader.close();
                    json = new JSONObject(response.toString());
                    Log.d("Response", json+"");

                    connection.disconnect();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Thread.currentThread().interrupt();
            }
        }.start();
    }
}
