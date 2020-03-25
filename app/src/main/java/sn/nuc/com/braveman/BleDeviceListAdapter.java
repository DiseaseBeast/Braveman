package sn.nuc.com.braveman;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2019/6/29.
 */

public class BleDeviceListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<BluetoothDevice> mLeDevices;
    private String TAG = "BleDeviceListAdapter";

    public BleDeviceListAdapter(Context context) {
        mLeDevices = new ArrayList<BluetoothDevice>();
        this.mInflater = LayoutInflater.from(context);
    }

    public void addDevice(BluetoothDevice device) {
        if (!mLeDevices.contains(device)) {
            this.mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        // TODO Auto-generated method stub
        return mLeDevices.get(position);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return mLeDevices.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder viewholder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_devicelist, null);
            viewholder = new ViewHolder();
            viewholder.devicename = (TextView) view
                    .findViewById(R.id.tv_devicelist_name);
            viewholder.deviceAddress = (TextView) view
                    .findViewById(R.id.tv_devicelist_address);
            view.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) view.getTag();
        }
        String name = mLeDevices.get(position).getName();
        if (name != null)
            viewholder.devicename.setText(name);
        else
            viewholder.devicename.setText("Unknow Device");
        viewholder.deviceAddress.setText("地址： "
                + mLeDevices.get(position).getAddress());
        return view;
    }

    static class ViewHolder {
        TextView devicename;
        TextView deviceAddress;
    }

    public void clear() {
        // TODO Auto-generated method stub
        mLeDevices.clear();
        this.notifyDataSetChanged();
    }
}