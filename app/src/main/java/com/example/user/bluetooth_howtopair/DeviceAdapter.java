package com.example.user.bluetooth_howtopair;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.example.user.bluetooth_howtopair.BleDevice;

public class DeviceAdapter extends BaseAdapter {
    private Context context;
    private List<BleDevice> devices;
    private LayoutInflater inflater;

    class ViewHolder {
        TextView content;
        ImageView image;
        TextView title;

        ViewHolder() {
        }
    }

    public DeviceAdapter(Context context, List<BleDevice> devices) {
        this.devices = devices;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    public int getCount() {
        return this.devices.size();
    }

    public Object getItem(int position) {
        return this.devices.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int i;
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.layout_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.textView1);
            holder.content = (TextView) convertView.findViewById(R.id.textView2);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.imageid);
        }
        BleDevice device = (BleDevice) this.devices.get(position);
        ImageView imageView = holder.image;
        if (device.isConnect()) {
            i = R.drawable.bluetooth_checkbox_checked3x;
        } else {
            i = R.drawable.bluetooth_checkbox_normal3x;
        }
        imageView.setImageResource(i);
        holder.title.setText(device.getName());
        holder.content.setText(device.getUuid());
        convertView.setTag(device);
        convertView.setTag(R.id.imageid, holder);
        return convertView;
    }
}

