package com.example.q.plzshow_res.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.q.plzshow_res.R;
import com.example.q.plzshow_res.Adapter.reservationAdapter;
import com.example.q.plzshow_res.sendToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class CTabFragment extends Fragment {
    private RecyclerView recyclerView;
    public static reservationAdapter reservAdapter;
    private String rest_id;

    public CTabFragment() {
        // Required empty public constructor
    }

    public static CTabFragment newInstance() {return new CTabFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        rest_id = pref.getString("rest_id", "");

        JSONObject getObj = new JSONObject();
        try {
            getObj.put("type", "GET_ACCEPTED_RESERV_LIST");
            getObj.put("rest_id", rest_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // GET RESPONSE
        sendToServer server = new sendToServer();
        JSONObject res = null;
        try {
            res = new sendToServer.sendJSON("http://52.78.200.87:3000", getObj.toString(), "application/json").execute().get();
            Log.e("REQUEST", getObj.toString());
            Log.e("RESERV_LIST", res.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        JSONArray resArray= new JSONArray();
        try {
            resArray = res.getJSONArray("reservations");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reservAdapter = new reservationAdapter(getActivity(), resArray);
        recyclerView.setAdapter(reservAdapter);
        reservAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_btab, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.reservation_recyclerView);
        this.onResume();
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            this.onResume();
        }else{
            // fragment is no longer visible
        }
    }
}
