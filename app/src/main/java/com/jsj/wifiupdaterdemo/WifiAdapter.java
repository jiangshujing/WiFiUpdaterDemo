package com.jsj.wifiupdaterdemo;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jsj on 16/4/15.
 */
public class WifiAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<ScanResult> list;
    private OnClickDeleteWifiListener listener;

    public WifiAdapter(Context context, ArrayList<ScanResult> list,OnClickDeleteWifiListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ScanResult scanResult = list.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_wifi_list, null);
            holder.tv_sSid = (TextView) convertView.findViewById(R.id.tv_sSid);
            holder.iv_nothing = (ImageView) convertView.findViewById(R.id.iv_nothing);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_sSid.setText(scanResult.SSID);
        holder.tv_sSid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.connectWifi(v);
            }
        });
        holder.iv_nothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteWifiConfig();
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView tv_sSid;
        ImageView iv_nothing;
    }

    interface OnClickDeleteWifiListener {
        void deleteWifiConfig();
        void connectWifi(View view);
    }
}
