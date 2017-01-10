package com.example.q.plzshow_res;

/**
 * Created by q on 2016-12-28.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.example.q.plzshow_res.Adapter.fullScreenAdapter;
import com.example.q.plzshow_res.Helper.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class fullScreenActivity extends Activity{

    private Utils utils;
    private fullScreenAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        viewPager = (ViewPager) findViewById(R.id.pager);
        utils = new Utils(getApplicationContext());
        Intent i = getIntent();
        String photolist = i.getExtras().getString("photoArray");
        JSONArray photoArray = null;
        try {
            photoArray = new JSONArray(photolist);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int position = i.getIntExtra("position", 0);
        adapter = new fullScreenAdapter(fullScreenActivity.this,
                photoArray);
        viewPager.setAdapter(adapter);
        // displaying selected image first
        viewPager.setCurrentItem(position);
    }
}
