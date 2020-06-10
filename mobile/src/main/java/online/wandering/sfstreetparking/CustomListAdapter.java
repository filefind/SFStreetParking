package online.wandering.sfstreetparking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {
    private ArrayList<SharelistItem> listData;
    private LayoutInflater layoutInflater;

    public CustomListAdapter(Context aContext, ArrayList<SharelistItem> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.sharelist_row,null);
            holder = new ViewHolder();
            holder.headlineView = convertView.findViewById(R.id.name);
            holder.reportedStatusView = convertView.findViewById(R.id.status);
            holder.reportedLatView = convertView.findViewById(R.id.lat);
            holder.reportedLngView = convertView.findViewById(R.id.lng);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.headlineView.setText(listData.get(position).getHeadline());
        holder.reportedStatusView.setText(listData.get(position).getStatus());
        holder.reportedLatView.setText(listData.get(position).getLat());
        holder.reportedLngView.setText(listData.get(position).getLng());
        return convertView;
    }

    static class ViewHolder {
        TextView headlineView;
        TextView reportedStatusView;
        TextView reportedLatView;
        TextView reportedLngView;
    }
}
