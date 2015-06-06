package lab.chabingba.eventorganizer.Visual.CustomExpandableListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Database.EventOfCategory;
import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;
import lab.chabingba.eventorganizer.Helpers.ValidatorHelpers;
import lab.chabingba.eventorganizer.R;

/**
 * Created by Tsvetan on 2015-05-27.
 */
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<EventOfCategory> listOfEvents;
    private Category category;
    private DBHandler database;

    public CustomExpandableListAdapter(Context context, ArrayList<EventOfCategory> listOfEvents, Category category) {
        this.listOfEvents = listOfEvents;
        this.context = context;
        this.category = category;

        this.database = new DBHandler(context, DatabaseConstants.DATABASE_NAME, null);
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

        MyEvent currentEvent = this.listOfEvents.get(groupPosition).getEvent();

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
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, final View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);

        View customView = layoutInflater.inflate(R.layout.custom_expandable_child, parent, false);

        final MyEvent currentEvent = this.listOfEvents.get(groupPosition).getEvent();

        ImageButton imageButtonEditEvent = (ImageButton) customView.findViewById(R.id.imageButtonEditEvent);
        ImageButton imageButtonDeleteEvent = (ImageButton) customView.findViewById(R.id.imageButtonDeleteEvent);
        ImageButton imageButtonViewEvent = (ImageButton) customView.findViewById(R.id.ibViewEvent);
        ImageButton imageButtonMoveEvent = (ImageButton) customView.findViewById(R.id.ibMoveEventInChild);

        if (currentEvent.getIsOld() == true) {
            imageButtonEditEvent.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(45, 45);
            params.setMargins(5, 0, 0, 0);
            imageButtonViewEvent.setLayoutParams(params);
            imageButtonViewEvent.invalidate();
        }

        final Category currentEventsCategory = CustomExpandableListAdapter.this.listOfEvents.get(groupPosition).getCategory();

        //region set onClickListeners of image buttons
        imageButtonEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralHelpers.editEvent(CustomExpandableListAdapter.this.context, currentEvent, currentEventsCategory);
            }
        });

        imageButtonDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralHelpers.deleteEvent(CustomExpandableListAdapter.this.context, database, currentEvent, currentEventsCategory);
            }
        });

        imageButtonViewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralHelpers.viewEvent(CustomExpandableListAdapter.this.context, groupPosition, currentEventsCategory, listOfEvents);
            }
        });

        imageButtonMoveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralHelpers.moveEvent(CustomExpandableListAdapter.this.context, database, currentEvent, category);
            }
        });
        //endregion

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

    public ArrayList<EventOfCategory> getListOfEvents() {
        return this.listOfEvents;
    }
}
