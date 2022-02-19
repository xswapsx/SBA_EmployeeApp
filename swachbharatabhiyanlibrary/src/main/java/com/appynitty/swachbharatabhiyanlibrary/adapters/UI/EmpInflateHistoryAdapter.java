package com.appynitty.swachbharatabhiyanlibrary.adapters.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.appynitty.swachbharatabhiyanlibrary.R;
import com.appynitty.swachbharatabhiyanlibrary.pojos.TableDataCountPojo;
import com.appynitty.swachbharatabhiyanlibrary.utils.AUtils;

import java.util.List;

/**
 * Created by Ayan Dey on 25/10/18.
 */

public class EmpInflateHistoryAdapter extends ArrayAdapter<TableDataCountPojo.WorkHistory> {

    private final List<TableDataCountPojo.WorkHistory> historyPojoList;
    private final Context context;
    private View view;
    private ViewHolder holder;

    public EmpInflateHistoryAdapter(@NonNull Context context, @NonNull List<TableDataCountPojo.WorkHistory> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.context = context;
        this.historyPojoList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_history_card, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.date = view.findViewById(R.id.history_date_txt);
            viewHolder.month = view.findViewById(R.id.history_month_txt);
            viewHolder.houseCollection = view.findViewById(R.id.house_collection);
//            viewHolder.gpCollection = view.findViewById(R.id.gp_collection);
            viewHolder.lwCollection = view.findViewById(R.id.lwc_collection);
            viewHolder.ssCollection = view.findViewById(R.id.ss_collection);
            viewHolder.dyCollection = view.findViewById(R.id.dy_collection);
            view.setTag(viewHolder);

        } else {
            view = convertView;
        }
        holder = (ViewHolder) view.getTag();

        if (!AUtils.isNull(historyPojoList) && !historyPojoList.isEmpty()) {
            TableDataCountPojo.WorkHistory workHistoryPojo = historyPojoList.get(position);
            holder.date.setText(AUtils.extractDateEmp(workHistoryPojo.getDate()));
            holder.month.setText(AUtils.extractMonthEmp(workHistoryPojo.getDate()));
            holder.houseCollection.setText(workHistoryPojo.getHouseCollection());
//            holder.gpCollection.setText(workHistoryPojo.getPointCollection());
            holder.lwCollection.setText(workHistoryPojo.getLiquidCollection());
            holder.ssCollection.setText(workHistoryPojo.getStreetCollection());
            holder.dyCollection.setText(workHistoryPojo.getDumpYardCollection());
        }

        return view;
    }

    private class ViewHolder {

        private TextView date;
        private TextView month;
        private TextView houseCollection;
        //        private TextView gpCollection;
        private TextView dyCollection;
        private TextView lwCollection;
        private TextView ssCollection;
    }


}
