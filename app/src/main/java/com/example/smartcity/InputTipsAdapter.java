package com.example.smartcity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.help.Tip;

import java.util.List;

/**
 * 显示每一条提示的内容
*/
public class InputTipsAdapter extends BaseAdapter {
    private Context context;
    private List<Tip> currentTips;

    InputTipsAdapter(Context context, List<Tip> tips) {
        this.context = context;
        this.currentTips = tips;
    }

    class Holder {
        TextView name;
        TextView address;
    }

    @Override
    public int getCount() {
        if(currentTips != null) {
            return currentTips.size();
        }
        return 0;
    }

    @Override
    public Tip getItem(int i) {
        if(currentTips != null) {
            return currentTips.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewgroup) {
        Holder holder;
        if(view == null) {
            holder = new Holder();
            view = LayoutInflater.from(context).inflate(R.layout.inputtips_adapter, null);
            holder.name = view.findViewById(R.id.name);
            holder.address = view.findViewById(R.id.address);
            view.setTag(holder);
        } else {
            holder = (Holder)view.getTag();
        }

        if(currentTips == null) {
            return view;
        }

        holder.name.setText(currentTips.get(i).getName());
        String addr = currentTips.get(i).getAddress();
        if(addr == null || addr.equals("")) {
            holder.address.setVisibility(View.GONE);
        } else {
            holder.address.setVisibility(View.VISIBLE);
            holder.address.setText(addr);
        }

        return view;
    }
}
