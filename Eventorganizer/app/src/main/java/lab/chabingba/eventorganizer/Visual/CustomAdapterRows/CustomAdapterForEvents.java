package lab.chabingba.eventorganizer.Visual.CustomAdapterRows;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.R;

/**
 * Created by Tsvetan on 2015-05-26.
 */
public class CustomAdapterForEvents extends ArrayAdapter<MyEvent> {

    public CustomAdapterForEvents(Context context, ArrayList<MyEvent> listOfEvents) {
        super(context, R.layout.custom_event_row, listOfEvents);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        View customRow = layoutInflater.inflate(R.layout.custom_event_row, parent, false);

        MyEvent event = getItem(position);

        TextView menuItemType = (TextView) customRow.findViewById(R.id.tvMenuItemType);
        TextView menuItemDate = (TextView) customRow.findViewById(R.id.tvMenuItemDate);
        TextView menuItemDay = (TextView) customRow.findViewById(R.id.tvMenuItemDay);
        TextView menuItemHour = (TextView) customRow.findViewById(R.id.tvMenuItemHour);

        menuItemType.setText(event.getType());
        menuItemDate.setText(event.getEventDateAsString());
        menuItemDay.setText(event.getEventDayOfWeekAsString());
        menuItemHour.setText(event.getEventHourAsString() + " h");

        ImageView ivIcon = (ImageView) customRow.findViewById(R.id.imageView);

        int resID = R.drawable.icon;

        ivIcon.setImageResource(resID);

        return customRow;
    }
}
