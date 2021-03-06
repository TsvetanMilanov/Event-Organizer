package lab.chabingba.eventorganizer.Helpers.Visual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.AbstractList;

import lab.chabingba.eventorganizer.R;

/**
 * Created by Tsvetan on 2015-05-27.
 */
public class CustomSpinnerItem extends ArrayAdapter<String> {
    private AbstractList<String> items;

    public CustomSpinnerItem(Context context, AbstractList<String> items) {
        super(context, R.layout.custom_spinner, items);
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

        View mySpinner = inflater.inflate(R.layout.custom_spinner, parent, false);

        TextView tvSpinnerItem = (TextView) mySpinner.findViewById(R.id.tvSpinnerItem);

        tvSpinnerItem.setText(this.items.get(position));

        return mySpinner;
    }
}
