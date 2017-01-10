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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
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
    String rest_id;
    ImageView image;

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
        rest_id = pref.getString("rest_id", "");

        //initiate
        image = (ImageView) rootView.findViewById(R.id.image);
        final EditText name = (EditText) rootView.findViewById(R.id.name);
        final EditText phone = (EditText) rootView.findViewById(R.id.phone);
        final EditText address = (EditText) rootView.findViewById(R.id.address);
        final EditText simple_address = (EditText) rootView.findViewById(R.id.simple_address);
        final EditText rest_type = (EditText) rootView.findViewById(R.id.rest_type);
        final EditText descript = (EditText) rootView.findViewById(R.id.descrip);
        final EditText oper_time = (EditText) rootView.findViewById(R.id.oper_time);
        final EditText rest_time = (EditText) rootView.findViewById(R.id.rest_time);
        final EditText holiday = (EditText) rootView.findViewById(R.id.holiday);
        final EditText price = (EditText) rootView.findViewById(R.id.price);
        final EditText reserv_price = (EditText) rootView.findViewById(R.id.reserv_price);
        final TextView coin = (TextView) rootView.findViewById(R.id.coin);


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

                if (!res.getString("rest_type").equals("")){
                    Log.d("CHECKPOINT", res.getString("rest_type"));
                    String merged_rest_type = res.getString("rest_type");
                    int index = merged_rest_type.indexOf("·");
                    if (index > 0){
                        simple_address.setText(merged_rest_type.substring(0, index));
                        rest_type.setText(merged_rest_type.substring(index+1));
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
                    reserv_price.setText(res.getString("reserv_price")+"원");

                coin.setText(res.getString("coin")+"원");
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
                Intent intent = new Intent(getActivity(), restaurant_gallery.class);
                startActivity(intent);
            }
        });

        LinearLayout footer = (LinearLayout) rootView.findViewById(R.id.footer);
        footer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //send to server
                JSONObject resObj = new JSONObject();
                try {
                    resObj.put("type", "UPDATE_REST_INFO");
                    resObj.put("rest_id", rest_id);
                    resObj.put("name", name.getText());
                    resObj.put("phone", phone.getText());
                    resObj.put("location", address.getText());
                    resObj.put("rest_type", simple_address.getText() + "·" + rest_type.getText());
                    resObj.put("description", descript.getText());
                    resObj.put("oper_time", oper_time.getText());
                    resObj.put("rest_time", rest_time.getText());
                    resObj.put("holiday", holiday.getText());
                    resObj.put("price", price.getText());
                    resObj.put("reserv_price", reserv_price.getText());
                    JSONObject res = new sendToServer.sendJSON(getString(R.string.server_ip), resObj.toString(), "application/json").execute().get();
                    Log.d("UPDATE_REST_INFO", res+"");
                    Toast.makeText(getContext(), "성공적으로 저장되었습니다", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
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

    @Override
    public void onResume() {
        super.onResume();
        try {
            JSONObject rest_json = new JSONObject();
            rest_json.put("type", "GET_REST");
            rest_json.put("rest_id", rest_id);

            JSONObject res = null;
            res = new sendToServer.sendJSON(getString(R.string.server_ip), rest_json.toString(), "application/json").execute().get();

            if (!res.getString("pic").equals("")){
                Bitmap img = new loadPhoto().execute(res.getString("pic")).get();
                image.setImageBitmap(img);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

}
