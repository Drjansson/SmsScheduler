package drj.smsscheduler;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Comparator;

/**
 * Created by David on 2016-06-05.
 */

public class ListItem implements Item, Comparable<ListItem> {
    private String name;
    private String number;
    private int color;
    private boolean selected = false;
    private View view;

    public ListItem(String name, String number) {
        this.name = name;
        this.number = number;
    }

    @Override
    public int getViewType() {
        return myArrayAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {

        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.contact_list_item, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        TextView text1 = (TextView) view.findViewById(R.id.listItem1);
        TextView text2 = (TextView) view.findViewById(R.id.listItem2);
        text1.setText(name);
        text2.setText(number);


        if(selected)
            view.setBackgroundResource(R.color.listItemBackgroundSelected);
        else
            view.setBackgroundResource(R.color.listItemBackground);

        return view;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected(){
        return selected;
    }

    public int compareTo(ListItem l)
    {
        return name.compareToIgnoreCase(l.getName());
    }

    public boolean equals(ListItem l){
        if(name.equalsIgnoreCase(l.getName()) && number.equalsIgnoreCase(l.getNumber()))
            return true;
        else
            return false;
    }

    public void setBackgroundColor(int color){
        if(view != null) {
            view.setBackgroundColor(color);
        }else
            this.color = color;
    }

}
