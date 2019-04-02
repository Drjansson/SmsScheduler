package drj.smsscheduler;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by David on 2016-06-05.
 */

public interface Item {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
}
