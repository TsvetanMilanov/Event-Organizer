package lab.chabingba.eventorganizer.Visual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.AbstractList;

import lab.chabingba.eventorganizer.R;

/**
 * Created by Tsvetan on 2015-06-06.
 */
public class CustomFeedbackCategorySpinnerItem extends ArrayAdapter<String> {
    private AbstractList<String> items;

    public CustomFeedbackCategorySpinnerItem(Context context, AbstractList<String> items) {
        super(context, R.layout.custom_feedback_category_spinner, items);
        this.items = items;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View mySpinner = inflater.inflate(R.layout.custom_feedback_category_spinner, parent, false);

        TextView tvSpinnerItem = (TextView) mySpinner.findViewById(R.id.tvFBCategorySpinnerItem);

        tvSpinnerItem.setText(this.items.get(position));

        return mySpinner;
    }
}
