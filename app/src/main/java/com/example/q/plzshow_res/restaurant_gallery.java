package com.example.q.plzshow_res;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.plzshow_res.Adapter.gridviewAdapter;
import com.example.q.plzshow_res.Helper.AppConstant;
import com.example.q.plzshow_res.Helper.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by q on 2017-01-10.
 */

public class restaurant_gallery extends AppCompatActivity {

    int PICK_IMAGE_MULTIPLE = 1;
    int PICK_MAIN_IMAGE = 2;
    String rest_id;
    JSONArray array;
    gridviewAdapter madapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_gallery);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        rest_id = pref.getString("rest_id", "");

        JSONObject getObj = new JSONObject();
        try {
            getObj.put("type", "GET_REST_PHOTOS");
            getObj.put("rest_id", rest_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // GET photos of restaurant
        JSONObject res = null;
        try {
            res = new sendToServer.sendJSON(getString(R.string.server_ip), getObj.toString(), "application/json").execute().get();
            Log.e("cs496_photos", res + "");
            array = res.getJSONArray("photos");


            GridView gridview = (GridView) findViewById(R.id.gridview);

            Resources r = getResources();
            float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    AppConstant.GRID_PADDING, r.getDisplayMetrics());
            Utils utils = new Utils(this);
            int columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);
            gridview.setNumColumns(AppConstant.NUM_OF_COLUMNS);
            gridview.setColumnWidth(columnWidth);
            gridview.setStretchMode(GridView.NO_STRETCH);
            gridview.setPadding((int) padding, (int) padding, (int) padding,
                    230);
            gridview.setHorizontalSpacing((int) padding);
            gridview.setVerticalSpacing((int) padding);

            madapter = new gridviewAdapter(this, array, columnWidth);
            gridview.setAdapter(madapter);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        TextView upload = (TextView) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        TextView delete = (TextView) findViewById(R.id.main_pic);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_MAIN_IMAGE);
            }
        });
    }

    public void uploadImage(String img) throws JSONException, ExecutionException, InterruptedException {
        JSONObject rest_img = new JSONObject();
        rest_img.put("type", "UPLOAD_REST_PIC");
        rest_img.put("rest_id", rest_id);
        rest_img.put("img", img);

        JSONObject res = new sendToServer.sendJSON(getString(R.string.server_ip), rest_img.toString(), "application/json").execute().get();
        Log.d("UPLOAD MAIN IMAGE", res+"");
        finish();
    }

    public void uploadImage(JSONArray img) throws JSONException, ExecutionException, InterruptedException {
        JSONObject rest_gallery = new JSONObject();
        rest_gallery.put("type", "UPLOAD_REST_GALLERY");
        rest_gallery.put("rest_id", rest_id);
        rest_gallery.put("img", img);

        JSONObject res = new sendToServer.sendJSON(getString(R.string.server_ip), rest_gallery.toString(), "application/json").execute().get();
        Log.e("cs496_photos", res + "");
        JSONArray addedArray = res.getJSONArray("photos");
        for (int i = 0; i < addedArray.length(); i++)
            array.put(addedArray.getJSONObject(i));
        madapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            String image64Encoded;
            if (requestCode == PICK_MAIN_IMAGE && resultCode == RESULT_OK
                    && null != data){
                if (data.getData() != null) {
                    Uri mImageUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                    image64Encoded = base64Encode(bitmap);
                    uploadImage(image64Encoded);
                }
            }
            else if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                JSONArray imagesEncodedList= new JSONArray();
                if (data.getClipData() != null) {

                    ClipData mClipData = data.getClipData();
                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        // Get the cursor
                        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String imageEncoded  = cursor.getString(columnIndex);

                        image64Encoded = base64Encode(bitmap);
                        imagesEncodedList.put(image64Encoded);
                    }
                    Log.d("No of image", imagesEncodedList.length()+"");

                }
                uploadImage(imagesEncodedList);
            } else {
                Toast.makeText(getApplicationContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
                super.onActivityResult(requestCode, resultCode, data);
        } catch (IOException | JSONException | InterruptedException | ExecutionException e1) {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            e1.printStackTrace();
        }
    }

    private String base64Encode(Bitmap bmp) {
        ByteArrayOutputStream ByteStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, ByteStream);
        byte[] b = ByteStream.toByteArray();
        String encoded = Base64.encodeToString(b, Base64.NO_WRAP);
        encoded = "data:image/jpeg;base64," + encoded;
        return encoded;
    }

}
