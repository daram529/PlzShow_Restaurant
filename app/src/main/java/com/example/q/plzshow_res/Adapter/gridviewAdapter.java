package com.example.q.plzshow_res.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.q.plzshow_res.fullScreenActivity;

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
    private int imageWidth;

    public gridviewAdapter(Activity activity, JSONArray photoArray, int imageWidth){
        this.activity = activity;
        this.photoArray = photoArray;
        this.imageWidth = imageWidth;
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
            if (img.getHeight() > 1024 || img.getWidth() > 1024){
                img = img.createScaledBitmap(img, img.getWidth()/2, img.getHeight()/2, false);
            }

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
                    imageWidth));

            imageView.setImageBitmap(img);

            imageView.setOnClickListener(new OnImageClickListener(position));

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

    class OnImageClickListener implements View.OnClickListener {

        int _postion;

        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(activity, fullScreenActivity.class);
            i.putExtra("position", _postion);
            i.putExtra("photoArray", photoArray.toString());
            activity.startActivity(i);
        }

    }

    public class loadPhoto extends AsyncTask<String, Void, Bitmap> {

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
