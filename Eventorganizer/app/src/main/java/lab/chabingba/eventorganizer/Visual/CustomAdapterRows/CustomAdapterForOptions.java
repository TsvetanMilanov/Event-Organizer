package lab.chabingba.eventorganizer.Visual.CustomAdapterRows;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import lab.chabingba.eventorganizer.R;

/**
 * Created by Tsvetan on 2015-06-01.
 */
public class CustomAdapterForOptions extends ArrayAdapter<String> {
    public CustomAdapterForOptions(Context context, String[] listOfOptions) {
        super(context, R.layout.custom_row_for_options_items, listOfOptions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        View customRow = layoutInflater.inflate(R.layout.custom_row_for_options_items, parent, false);

        TextView tvOption = (TextView) customRow.findViewById(R.id.tvOptionsMenuItem);

        tvOption.setText(getItem(position));

        return customRow;
    }
}
