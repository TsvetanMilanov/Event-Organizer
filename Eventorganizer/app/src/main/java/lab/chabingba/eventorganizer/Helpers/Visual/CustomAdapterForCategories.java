package lab.chabingba.eventorganizer.Helpers.Visual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.R;

/**
 * Created by Tsvetan on 2015-05-26.
 */
public class CustomAdapterForCategories extends ArrayAdapter<Category> {

    public CustomAdapterForCategories(Context context, ArrayList<Category> listOfCategories) {
        super(context, R.layout.custom_category_row, listOfCategories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        View customRow = layoutInflater.inflate(R.layout.custom_category_row, parent, false);

        Category category = getItem(position);

        ImageView imageView = (ImageView) customRow.findViewById(R.id.ivCategoryCustomRow);
        TextView textView = (TextView) customRow.findViewById(R.id.tvCategoryName);

        imageView.setImageResource(R.drawable.category_icon);
        textView.setText(category.getName());
        return customRow;
    }
}
