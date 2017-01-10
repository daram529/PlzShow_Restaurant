package com.example.q.plzshow_res.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.q.plzshow_res.R;
import com.example.q.plzshow_res.Helper.TouchImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by q on 2016-12-28.
 */

public class fullScreenAdapter extends PagerAdapter {

    private Activity _activity;
    private JSONArray photoArray;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;

    // constructor
    public fullScreenAdapter(Activity activity,
                             JSONArray imageArray) {
        this._activity = activity;
        this.photoArray = imageArray;
    }

    @Override
    public int getCount() {
        return this.photoArray.length();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        try {
            TouchImageView imgDisplay;
            Button btnClose;

            inflater = (LayoutInflater) _activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                    false);

            imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
            btnClose = (Button) viewLayout.findViewById(R.id.btnClose);

            String url = photoArray.getJSONObject(position).getString("photo");
            Bitmap img = null;
            img = new loadPhoto().execute(url).get();

            if (img.getHeight() > 1024 || img.getWidth() > 1024){
                img = img.createScaledBitmap(img, img.getWidth()/2, img.getHeight()/2, false);
            }


            imgDisplay.setImageBitmap(img);

            // close button click event
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _activity.finish();
                }
            });

            ((ViewPager) container).addView(viewLayout);

            return viewLayout;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

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
