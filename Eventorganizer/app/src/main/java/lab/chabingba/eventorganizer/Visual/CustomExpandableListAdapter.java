package lab.chabingba.eventorganizer.Visual;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.EditEventActivity;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;
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
    private Category category;

    public CustomExpandableListAdapter(Context context, ArrayList<MyEvent> listOfEvents, Category category) {
        this.listOfEvents = listOfEvents;
        this.context = context;
        this.parentList = GeneralHelpers.createStringArrayWithEventTypesAndDates(this.listOfEvents);
        this.category = category;
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
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, final View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);

        View customView = layoutInflater.inflate(R.layout.custom_expandable_child, parent, false);

        final MyEvent currentEvent = this.listOfEvents.get(groupPosition);

        ImageButton imageButtonEditEvent = (ImageButton) customView.findViewById(R.id.imageButtonEditEvent);
        ImageButton imageButtonDeleteEvent = (ImageButton) customView.findViewById(R.id.imageButtonDeleteEvent);

        if (currentEvent.getIsOld() == true) {
            imageButtonEditEvent.setVisibility(View.GONE);
        }

        imageButtonEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditEventActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(GlobalConstants.EVENT_WORD, currentEvent);
                bundle.putSerializable(GlobalConstants.CATEGORY_WORD, CustomExpandableListAdapter.this.category);

                intent.putExtras(bundle);

                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        imageButtonDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);

                deleteDialog.setMessage("Are you sure you want to delete this event?");

                deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int currentDatabaseVersion = GeneralHelpers.getCurrentDatabaseVersion(context);

                        DBHandler database = new DBHandler(context, DatabaseConstants.DATABASE_NAME, null, currentDatabaseVersion);

                        database.removeEvent(category.getSQLName(), currentEvent.getId());

                        Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(context, category, false);

                        context.startActivity(intent);

                        ((Activity) context).finish();
                    }
                });

                deleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = deleteDialog.create();
                alertDialog.show();
            }
        });

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
