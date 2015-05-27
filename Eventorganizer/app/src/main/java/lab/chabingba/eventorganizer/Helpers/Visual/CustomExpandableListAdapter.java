package lab.chabingba.eventorganizer.Helpers.Visual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;
import lab.chabingba.eventorganizer.R;

/**
 * Created by Tsvetan on 2015-05-27.
 */
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<MyEvent> listOfEvents;
    private String[] parentList;
    private String[] childList;

    public CustomExpandableListAdapter(Context context, ArrayList<MyEvent> listOfEvents) {
        this.listOfEvents = listOfEvents;
        this.context = context;
        this.parentList = GeneralHelpers.createStringArrayWithEventTypesAndDates(this.listOfEvents);
    }

    @Override
    public int getGroupCount() {
        return listOfEvents.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);

        View customView = layoutInflater.inflate(R.layout.custom_parent_expandable, parent, false);

        MyEvent currentEvent = this.listOfEvents.get(groupPosition);

        TextView tvEventType = (TextView) customView.findViewById(R.id.tvEventType);
        tvEventType.setPadding(60, 0, 0, 0);
        tvEventType.setText(currentEvent.getType());

        TextView tvEventDate = (TextView) customView.findViewById(R.id.tvDateOfEvent);
        tvEventDate.setText(currentEvent.getEventDateAsString());

        return customView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);

        View customView = layoutInflater.inflate(R.layout.custom_expandable_child, parent, false);

        MyEvent currentEvent = this.listOfEvents.get(groupPosition);

        TextView tvDayOfWeek = (TextView) customView.findViewById(R.id.tvDayOfWeek);
        tvDayOfWeek.setText(currentEvent.getEventDayOfWeekAsString());

        TextView tvHour = (TextView) customView.findViewById(R.id.tvHour);
        tvHour.setText(currentEvent.getEventHourAsString() + " h");

        TextView tvDescription = (TextView) customView.findViewById(R.id.tvDescription);
        tvDescription.setText(currentEvent.getDescription());

        CheckBox checkBoxIsFinished = (CheckBox) customView.findViewById(R.id.cbIsFinished);

        checkBoxIsFinished.setClickable(false);

        if (currentEvent.getIsFinished()) {
            checkBoxIsFinished.setChecked(true);
        } else {
            checkBoxIsFinished.setChecked(false);
        }

        TextView tvLocation = (TextView) customView.findViewById(R.id.tvLocationExpandable);

        tvLocation.append(currentEvent.getLocation());

        return customView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
