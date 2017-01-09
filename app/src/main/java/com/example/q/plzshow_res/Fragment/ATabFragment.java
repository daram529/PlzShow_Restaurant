package com.example.q.plzshow_res.Fragment;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.q.plzshow_res.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.example.q.plzshow_res.restaurant_gallery;
import com.example.q.plzshow_res.sendToServer;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ATabFragment extends Fragment {

    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;


    public ATabFragment() {
        // Required empty public constructor
    }

    public static ATabFragment newInstance() {
        return new ATabFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_atab, container, false);

        SharedPreferences pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String rest_id = pref.getString("rest_id", "");

        //initiate
        ImageView image = (ImageView) rootView.findViewById(R.id.image);
        EditText name = (EditText) rootView.findViewById(R.id.name);
        EditText phone = (EditText) rootView.findViewById(R.id.phone);
        EditText address = (EditText) rootView.findViewById(R.id.address);
        EditText simple_address = (EditText) rootView.findViewById(R.id.simple_address);
        EditText type = (EditText) rootView.findViewById(R.id.type);
        EditText descript = (EditText) rootView.findViewById(R.id.descrip);
        EditText oper_time = (EditText) rootView.findViewById(R.id.oper_time);
        EditText rest_time = (EditText) rootView.findViewById(R.id.rest_time);
        EditText holiday = (EditText) rootView.findViewById(R.id.holiday);
        EditText price = (EditText) rootView.findViewById(R.id.price);
        EditText reserv_price = (EditText) rootView.findViewById(R.id.reserv_price);


        //get rest information from server
        JSONObject rest_json = new JSONObject();
        try {
            rest_json.put("type", "GET_REST");
            rest_json.put("rest_id", rest_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject res = null;
        try {
            res = new sendToServer.sendJSON(getString(R.string.server_ip), rest_json.toString(), "application/json").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //fill editexts if available
        try {
            if (res.getString("result").equals("success")){
                Log.d("ResMK", res+"");
                if (!res.getString("pic").equals("")){
                    Bitmap img = new loadPhoto().execute(res.getString("pic")).get();
                    image.setImageBitmap(img);
                }
                if (!res.getString("name").equals(""))
                    name.setText(res.getString("name"));

                if (!res.getString("phone").equals(""))
                    phone.setText(res.getString("phone"));

                if (!res.getString("location").equals("")){
                    Log.d("Location", "sth wrong");
                    address.setText(res.getString("location"));
                }


                if (!res.getString("type").equals("")){
                    Log.d("CHECKPOINT", res.getString("type"));
                    String merged_type = res.getString("type");
                    int index = merged_type.indexOf("·");
                    if (index > 0){
                        simple_address.setText(merged_type.substring(0, index));
                        type.setText(merged_type.substring(index+1));
                    }

                }

                if (!res.getString("description").equals(""))
                    descript.setText(res.getString("description"));

                if (!res.getString("oper_time").equals(""))
                    oper_time.setText(res.getString("oper_time"));

                if (!res.getString("rest_time").equals(""))
                    rest_time.setText(res.getString("rest_time"));

                if (!res.getString("holiday").equals(""))
                    holiday.setText(res.getString("holiday"));

                if (!res.getString("price").equals(""))
                    price.setText(res.getString("price"));

                if (!res.getString("reserv_price").equals(""))
                    reserv_price.setText(res.getString("reserv_price"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("clicked", "worked!");
                Intent intent = new Intent(getActivity(), restaurant_gallery.class);
                startActivity(intent);
            }
        });


        return rootView;
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
