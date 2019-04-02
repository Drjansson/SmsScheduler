package drj.smsscheduler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 2016-06-05.
 */

public class myArrayAdapter extends ArrayAdapter<ListItem> {

    private LayoutInflater mInflater;
    private ArrayList<ListItem> itemsInList = new ArrayList<ListItem>();

    public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }

    public myArrayAdapter(Context context, ArrayList<ListItem> items) {
        super(context, 0, items);
        itemsInList.addAll(items);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;

    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView);
    }

    /*private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    public View getView(int position, View convertView, ViewGroup parent)  {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);
        View View;
        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.task_details_row, null);
                    holder.View=getItem(position).getView(mInflater, convertView);
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.task_detail_header, null);
                    holder.View=getItem(position).getView(mInflater, convertView);
                    break;
            }
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView; } public static class ViewHolder {
        public  View View; }

        }*/
}
