package lab.chabingba.eventorganizer.Helpers.Visual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;
import lab.chabingba.eventorganizer.Helpers.ValidatorHelpers;
import lab.chabingba.eventorganizer.R;

/**
 * Created by Tsvetan on 2015-05-27.
 */
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<MyEvent> listOfEvents;
    private String[] parentList;

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
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);

        View customView = layoutInflater.inflate(R.layout.custom_parent_expandable, parent, false);

        MyEvent currentEvent = this.listOfEvents.get(groupPosition);

        TextView tvEventType = (TextView) customView.findViewById(R.id.tvEventType);
        tvEventType.setText(currentEvent.getType());

        TextView tvEventDate = (TextView) customView.findViewById(R.id.tvDateOfEvent);
        tvEventDate.setText(currentEvent.getEventDateAsString());

        ImageView ivIcon = (ImageView) customView.findViewById(R.id.ivExpandableListIcon);

        if (!isExpanded) {
            ivIcon.setImageResource(R.drawable.arrow_down_light_grey);
        } else {
            ivIcon.setImageResource(R.drawable.arrow_right_light_grey);
        }

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
        if (ValidatorHelpers.isNullOrEmpty(currentEvent.getDescription())) {
            tvDescription.setVisibility(View.GONE);
            View secondSeparator = customView.findViewById(R.id.secondSeparator);
            secondSeparator.setVisibility(View.GONE);
        }

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

        if (ValidatorHelpers.isNullOrEmpty(currentEvent.getLocation())) {
            tvLocation.setVisibility(View.GONE);
        }

        return customView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public ArrayList<MyEvent> getListOfEvents() {
        return this.listOfEvents;
    }
}
