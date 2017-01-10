package com.example.q.plzshow_res.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.plzshow_res.App;
import com.example.q.plzshow_res.R;
import com.example.q.plzshow_res.sendToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class reservationAdapter extends RecyclerView.Adapter<reservationAdapter.ViewHolder> {
    private static final int COLOR_GREY = Color.parseColor("#8c8c8c");
    private static final int COLOR_ORANGE = Color.parseColor("#d8d841");
    private static final int COLOR_RED = Color.parseColor("#d84141");
    private static final int COLOR_GREEN = Color.parseColor("#41d88c");
    private static final int COLOR_BLACK = Color.parseColor("#000000");

    private final Activity activity;
    private JSONArray resArray;

    String _status_msg;
    String _status_res;
    String _reserv_id;
    String _user_name;
    String _rest_phone;
    String _reserv_time;
    String _send_time;
    String _checked_time;
    String _respond_time;
    String _reserv_fee;
    String _request;
    String _people;
    String _rest_id;

    ViewGroup parent;
    int viewType;

    boolean accepted = false;

    public reservationAdapter(Activity activity, JSONArray resArray) {
        assert activity != null;
        this.activity = activity;
        this.resArray = resArray;
    }

    @Override
    public int getItemCount() {
        return resArray.length();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item, parent, false);
        this.parent = parent;
        this.viewType = viewType;
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ViewHolder holder_ = holder;
        holder.position = position;
        final JSONObject resobj = resArray.optJSONObject(position);
        try {
            _status_msg = resobj.getString("status_msg");
            _status_res = resobj.getString("status_res");
            _reserv_id= resobj.getString("id");
            _user_name = resobj.getString("user_name");
            _reserv_time = resobj.getString("reserv_time");
            _send_time = resobj.getString("send_time");
            _checked_time = resobj.getString("checked_time");
            _respond_time = resobj.getString("respond_time");
            _reserv_fee = resobj.getString("reserv_fee");
            _request = resobj.getString("request");
            _people = resobj.getString("people");

            if (_status_msg.equals("NOT_READ_YET")) {
                holder.status_color.setBackgroundColor(COLOR_GREY);
                holder.status_color.invalidate();
                holder.reserv_status.setText("읽지 않음");
            } else if (_status_msg.equals("CHECKED")) {
                holder.status_color.setBackgroundColor(COLOR_ORANGE);
                holder.status_color.invalidate();
                holder.reserv_status.setText("확인함");
            } else if (_status_msg.equals("ACCEPTED")) {
                if (_status_res.equals("UNKNOWN_YET")) {
                    holder.status_color.setBackgroundColor(COLOR_GREEN);
                    holder.reserv_status.setText("수락함");
                } else if (_status_res.equals("SHOWED")) {
                    holder.status_color.setBackgroundColor(COLOR_BLACK);
                    holder.reserv_status.setText("성사");
                } else if (_status_res.equals("NOT_SHOWED")) {
                    holder.status_color.setBackgroundColor(COLOR_BLACK);
                    holder.reserv_status.setText("부도");
                }
                holder.status_color.invalidate();
            } else if (_status_msg.equals("DECLINED")) {
                holder.status_color.setBackgroundColor(COLOR_RED);
                holder.status_color.invalidate();
                holder.reserv_status.setText("거절함");
            }

            holder.user_name.setText(_user_name);
            holder.reserv_time.setText(formatDateTimeString(_reserv_time) + " ");
            holder.reserv_num_people.setText("(" + _people + "명)");
            holder.reserv_elapsed.setText(passingTime(_send_time) + " 받음  /");

            holder.user_name.setTypeface(App.NanumBarunGothicBold);
            holder.reserv_time.setTypeface(App.NanumBarunGothicLight);
            holder.reserv_num_people.setTypeface(App.NanumBarunGothicLight);
            holder.reserv_elapsed.setTypeface(App.NanumBarunGothicLight);
            holder.reserv_status.setTypeface(App.NanumBarunGothicLight);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    Intent reservIntent = new Intent(activity, FullReservation.class);
                    reservIntent.putExtra("res_json", resobj.toString());
                    activity.startActivity(reservIntent);
                    */

                    final AlertDialog.Builder alertDlg = new AlertDialog.Builder(v.getContext());
                    alertDlg.setView(R.layout.reservation_detail);
                    final AlertDialog dialog = alertDlg.show();
                    dialog.setCanceledOnTouchOutside(true);

                    TextView user_name = (TextView) dialog.findViewById(R.id.reserv_detail_user_name);
                    TextView date = (TextView) dialog.findViewById(R.id.reserv_detail_date);
                    TextView time = (TextView) dialog.findViewById(R.id.reserv_detail_time);
                    TextView fee = (TextView) dialog.findViewById(R.id.reserv_detail_fee);
                    TextView people = (TextView) dialog.findViewById(R.id.reserv_detail_people);
                    TextView request = (TextView) dialog.findViewById(R.id.reserv_detail_request);
                    final Button accept = (Button) dialog.findViewById(R.id.reserv_detail_accept);
                    final Button decline = (Button) dialog.findViewById(R.id.reserv_detail_decline);

                    try {
                        _status_msg = resArray.optJSONObject(position).getString("status_msg");
                        _status_res = resArray.optJSONObject(position).getString("status_res");
                        _rest_phone = resArray.optJSONObject(position).getString("rest_phone");
                        _reserv_id = resArray.optJSONObject(position).getString("id");
                        Log.e("REST_PHONE", _rest_phone);

                        user_name.setText(resArray.optJSONObject(position).getString("user_name"));
                        date.setText(formatDateString(resArray.optJSONObject(position).getString("reserv_time")));
                        time.setText(formatTimeString(resArray.optJSONObject(position).getString("reserv_time")));
                        fee.setText(resArray.optJSONObject(position).getString("reserv_fee") + "원");
                        people.setText(resArray.optJSONObject(position).getString("people") + "명");
                        request.setText(resArray.optJSONObject(position).getString("request"));

                        if (_status_msg.equals("NOT_READ_YET")) {
                            try {
                                SharedPreferences pref = activity.getSharedPreferences("pref", Context.MODE_PRIVATE);
                                _rest_id = pref.getString("rest_id", "");

                                // GENERATE REQUEST
                                JSONObject req = new JSONObject();
                                req.put("type", "CHECK_RESERV");
                                req.put("rest_id", _rest_id);
                                req.put("reserv_id", _reserv_id);

                                // GET RESPONSE
                                JSONObject res = new sendToServer.sendJSON("http://52.78.200.87:3000", req.toString(), "application/json").execute().get();
                                if (res == null) {
                                    Log.e("null response", "res = null");
                                } else if (!res.has("result") || res.get("result").equals("failed")) {
                                    Log.e("failed", res.toString());
                                }
                                onCreateViewHolder(parent, viewType);
                                notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (_status_msg.equals("CHECKED")) {
                        } else if (_status_msg.equals("ACCEPTED")) {
                            accept.setText("왔어요");
                            decline.setText("안왔어요");
                            if (_status_res.equals("UNKNOWN_YET")) {
                                accept.setClickable(true);
                                accept.setEnabled(true);
                                decline.setClickable(true);
                                decline.setEnabled(true);
                            } else if (_status_res.equals("SHOWED")) {
                                accept.setClickable(false);
                                accept.setEnabled(false);
                                decline.setClickable(false);
                                decline.setEnabled(false);
                            } else if (_status_res.equals("NOT_SHOWED")) {
                                accept.setClickable(false);
                                accept.setEnabled(false);
                                decline.setClickable(false);
                                decline.setEnabled(false);
                            }
                        } else if (_status_msg.equals("DECLINED")) {
                            accept.setClickable(false);
                            accept.setEnabled(false);
                            decline.setClickable(false);
                            decline.setEnabled(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    accept.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        try {
                            SharedPreferences pref = activity.getSharedPreferences("pref", Context.MODE_PRIVATE);
                            _rest_id = pref.getString("rest_id", "");
                            Log.e("STATUS_MSG", _status_msg);
                            Log.e("STATUS_RES", _status_res);
                            if (_status_msg.equals("ACCEPTED") && _status_res.equals("UNKNOWN_YET")) {
                                // GENERATE REQUEST
                                JSONObject req = new JSONObject();
                                req.put("type", "RESULT_SHOWED");
                                req.put("rest_id", _rest_id);
                                req.put("reserv_id", _reserv_id);

                                // GET RESPONSE
                                JSONObject res = new sendToServer.sendJSON("http://52.78.200.87:3000", req.toString(), "application/json").execute().get();
                                if (res == null) {
                                    Log.e("null response", "res = null");
                                } else if (!res.has("result") || res.get("result").equals("failed")) {
                                    Log.e("failed", res.toString());
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(activity.getApplicationContext(), res.getString("description"), Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                    // REFRESH
                                }
                            } else {
                                // GENERATE REQUEST
                                JSONObject req = new JSONObject();
                                req.put("type", "ACCEPT_RESERV");
                                req.put("rest_id", _rest_id);
                                req.put("reserv_id", _reserv_id);

                                // GET RESPONSE
                                JSONObject res = new sendToServer.sendJSON("http://52.78.200.87:3000", req.toString(), "application/json").execute().get();
                                if (res == null) {
                                    Log.e("null response", "res = null");
                                } else if (!res.has("result") || res.get("result").equals("failed")) {
                                    Log.e("failed", res.toString());
                                } else {
                                    dialog.dismiss();
                                    resArray.remove(position);
                                    Toast.makeText(activity.getApplicationContext(), res.getString("description"), Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                    accept.setEnabled(false);
                                    accept.setClickable(false);
                                    // REFRESH
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        notifyDataSetChanged();
                        }
                    });

                    decline.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                SharedPreferences pref = activity.getSharedPreferences("pref", Context.MODE_PRIVATE);
                                _rest_id = pref.getString("rest_id", "");
                                Log.e("STATUS_MSG", _status_msg);
                                Log.e("STATUS_RES", _status_res);
                                if (_status_msg.equals("ACCEPTED") && _status_res.equals("UNKNOWN_YET")) {
                                    // GENERATE REQUEST
                                    JSONObject req = new JSONObject();
                                    req.put("type", "RESULT_NOT_SHOWED");
                                    req.put("rest_id", _rest_id);
                                    req.put("reserv_id", _reserv_id);

                                    // GET RESPONSE
                                    JSONObject res = new sendToServer.sendJSON("http://52.78.200.87:3000", req.toString(), "application/json").execute().get();
                                    if (res == null) {
                                        Log.e("null response", "res = null");
                                    } else if (!res.has("result") || res.get("result").equals("failed")) {
                                        Log.e("failed", res.toString());
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(activity.getApplicationContext(), res.getString("description"), Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                        onCreateViewHolder(parent, viewType);
                                        decline.setEnabled(false);
                                        decline.setClickable(false);
                                        // REFRESH
                                    }
                                } else {
                                    // GENERATE REQUEST
                                    JSONObject req = new JSONObject();
                                    req.put("type", "DECLINE_RESERV");
                                    req.put("rest_id", _rest_id);
                                    req.put("reserv_id", _reserv_id);

                                    // GET RESPONSE
                                    JSONObject res = new sendToServer.sendJSON("http://52.78.200.87:3000", req.toString(), "application/json").execute().get();
                                    if (res == null) {
                                        Log.e("null response", "res = null");
                                    } else if (!res.has("result") || res.get("result").equals("failed")) {
                                        Log.e("failed", res.toString());
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(activity.getApplicationContext(), res.getString("description"), Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                        onCreateViewHolder(parent, viewType);
                                        // REFRESH
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            notifyDataSetChanged();
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private int position;
        private CardView cardView;
        private TextView user_name;
        private TextView reserv_time;
        private TextView reserv_num_people;
        private TextView reserv_elapsed;
        private TextView reserv_status;
        private LinearLayout status_color;

        public ViewHolder(View view) {
            super(view);
            status_color = (LinearLayout) view.findViewById(R.id.status_color_indicator);
            cardView = (CardView) view.findViewById(R.id.reservation_cardView);
            user_name = (TextView) view.findViewById(R.id.txtView_reserv_user_name);
            reserv_time = (TextView) view.findViewById(R.id.txtView_reserv_time);
            reserv_num_people = (TextView) view.findViewById(R.id.txtView_reserv_num_people);
            reserv_elapsed = (TextView) view.findViewById(R.id.txtView_reserv_elapsed_time);
            reserv_status = (TextView) view.findViewById(R.id.txtView_reserv_status);
        }
    }

    private String trimZero (String number) {
        return String.valueOf(Integer.parseInt(number));
    }

    private String formatDateString (String datetime) {
        String yyyy = datetime.substring(0,4);
        String mm = datetime.substring(5,7);
        String dd = datetime.substring(8,10);
        String formatted = trimZero(yyyy) + "년 " + trimZero(mm) + "월 " + trimZero(dd) + "일";
        return formatted;
    }

    private String formatTimeString (String datetime) {
        String AMPM = "";
        String hh = datetime.substring(11, 13);
        String mm = datetime.substring(14, 16);
        int hour = Integer.parseInt(hh);
        if (hour < 12) {
            AMPM = "오전 ";
        } else if (hour == 12) {
            AMPM = "오후 ";
        } else if (hour > 12) {
            AMPM = "오후 ";
            hour -= 12;
        }
        hh = String.valueOf(hour);

        String formatted = AMPM + trimZero(hh) + "시 " + trimZero(mm) + "분";
        return formatted;
    }

    private String formatDateTimeString (String datetime) {
        String yyyy = datetime.substring(0,4);
        String mm = datetime.substring(5,7);
        String dd = datetime.substring(8,10);
        String hh = datetime.substring(11, 13);
        String mm_ = datetime.substring(14, 16);
        String formatted = trimZero(yyyy) + "년 " + trimZero(mm) + "월 " + trimZero(dd) + "일 " + trimZero(hh) + "시 " + trimZero(mm_) + "분" ;
        return formatted;
    }

    private String passingTime (String datetime) {
        String passed = "";
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        try {
            Date date = dateFormat.parse(datetime);
            long passedSec = (now.getTime() - date.getTime()) / 1000;
            if (passedSec < 60) {
                passed = "방금";
            } else if (passedSec < 60 * 60) {
                passed = String.valueOf((int) Math.floor(passedSec / 60)) + "분 전";
            } else if (passedSec < 60 * 60 * 24) {
                passed = String.valueOf((int) Math.floor(passedSec / (60 * 60))) + "시간 전";
            } else if (passedSec > 60 * 60 * 24) {
                passed = String.valueOf((int) Math.floor(passedSec / (60 * 60 * 24))) + "일 전";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passed;
    }
}