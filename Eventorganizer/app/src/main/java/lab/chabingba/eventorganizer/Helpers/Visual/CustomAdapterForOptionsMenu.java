package lab.chabingba.eventorganizer.Helpers.Visual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import lab.chabingba.eventorganizer.R;

/**
 * Created by Tsvetan on 2015-05-27.
 */
public class CustomAdapterForOptionsMenu extends ArrayAdapter<String> {
    public CustomAdapterForOptionsMenu(Context context, String[] listOfOptions) {
        super(context, R.layout.custom_row_for_options_list, listOfOptions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        View customRow = layoutInflater.inflate(R.layout.custom_row_for_options_list, parent, false);

        TextView tvOption = (TextView) customRow.findViewById(R.id.tvOptionsMenuItem);

        tvOption.setText(getItem(position));

        return customRow;
    }
}
