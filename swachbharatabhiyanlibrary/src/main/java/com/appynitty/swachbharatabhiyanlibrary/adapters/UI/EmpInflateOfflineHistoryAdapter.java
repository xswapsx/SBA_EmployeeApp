package com.appynitty.swachbharatabhiyanlibrary.adapters.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appynitty.swachbharatabhiyanlibrary.R;
import com.appynitty.swachbharatabhiyanlibrary.pojos.EmpOfflineCollectionCount;
import com.appynitty.swachbharatabhiyanlibrary.utils.AUtils;

import java.util.List;

/**
 * Created by Swapnil Lanjewar on 08/01/2022.
 */

public class EmpInflateOfflineHistoryAdapter extends ArrayAdapter<EmpOfflineCollectionCount> {

    List<EmpOfflineCollectionCount> items_list;
    int custom_layout_id;

    public EmpInflateOfflineHistoryAdapter(@NonNull Context context, int resource, @NonNull List<EmpOfflineCollectionCount> objects) {
        super(context, resource, objects);
        items_list = objects;
        custom_layout_id = resource;
    }

    @Override
    public int getCount() {
        return items_list.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View v = convertView;
        if (v == null) {
            // getting reference to the main layout and
            // initializing
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(custom_layout_id, null);
        }

        // initializing the textview and
        // setting data

        TextView day = v.findViewById(R.id.history_date_txt);
        TextView month = v.findViewById(R.id.history_month_txt);

        TextView houseCollectionCount = v.findViewById(R.id.house_collection);
        TextView dyCollectionCount = v.findViewById(R.id.dy_collection);
        TextView lwcCollectionCount = v.findViewById(R.id.lwc_collection);
        TextView ssCollectionCount = v.findViewById(R.id.ss_collection);

        // get the item using the  position param
        EmpOfflineCollectionCount item = items_list.get(position);

        houseCollectionCount.setText(item.getHouseCount());
        dyCollectionCount.setText(item.getDumpYardCount());
        lwcCollectionCount.setText(item.getLiquidWasteCount());
        ssCollectionCount.setText(item.getStreetSweepCount());
        day.setText(AUtils.extractDate(item.getDate()));
        month.setText(AUtils.extractMonth(item.getDate()));

        return v;
    }
}
