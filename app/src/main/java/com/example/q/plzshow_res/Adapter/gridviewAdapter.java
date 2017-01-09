package com.example.q.plzshow_res.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;


/**
 * Created by q on 2017-01-10.
 */

public class gridviewAdapter extends BaseAdapter{

    private Activity activity;
    private JSONArray photoArray;

    public gridviewAdapter(Activity activity, JSONArray photoArray){
        this.activity = activity;
        this.photoArray = photoArray;
    }

    @Override
    public int getCount() {
        return this.photoArray.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return this.photoArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        try {
            ImageView imageView;
            if (convertView == null){
                imageView = new ImageView(activity);
            } else{
                imageView = (ImageView) convertView;
            }
            String url = photoArray.getJSONObject(position).getString("photo");
            Bitmap img = new loadPhoto().execute(url).get();

            imageView.setImageBitmap(img);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(50, 50));

            return imageView;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class loadPhoto extends AsyncTask<String, Void, Bitmap> {

        public loadPhoto() {
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

    }

}
