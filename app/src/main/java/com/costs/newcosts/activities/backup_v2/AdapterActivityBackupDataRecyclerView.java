package com.costs.newcosts.activities.backup_v2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.costs.newcosts.Constants;
import com.costs.newcosts.R;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class AdapterActivityBackupDataRecyclerView extends RecyclerView.Adapter<AdapterActivityBackupDataRecyclerView.ViewHolder> {

    private List<DataUnitBackupFolder> dataList;
    private int selectedItemPosition = -1;
    private Calendar calendar;
    private Context context;
    private OnItemClickListener clickListener;


    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }



    public AdapterActivityBackupDataRecyclerView(Context context, List<DataUnitBackupFolder> dataList) {
        this.context = context;
        calendar = new GregorianCalendar();

        this.dataList = dataList;
    }


    public void swapData(List<DataUnitBackupFolder> newDataList) {
        dataList = newDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_backup_data_single_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int positionFinal = position;
        final DataUnitBackupFolder backupDescription = dataList.get(position);
        calendar.setTimeInMillis(backupDescription.getMilliseconds());
        String minuteString = "";
        if (calendar.get(Calendar.MINUTE) < 10)
            minuteString = "0" + calendar.get(Calendar.MINUTE);
        else
            minuteString = String.valueOf(calendar.get(Calendar.MINUTE));

        // Устанавливаем дату создания резервной копии
        holder.backupDateTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                backupDescription.getDay() + " " +
                Constants.DECLENSION_MONTH_NAMES[backupDescription.getMonth()] + " " +
                backupDescription.getYear() + ", " +
                calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                minuteString);

        // Устанавливаем устройство, в котором была создана данная резервная копия
        holder.deviceDescriptionTextView.setText(backupDescription.getDeviceManufacturer() + " " +
                backupDescription.getDeviceModel());
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }



    // ===================================== View Holder ===========================================
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView backupDateTextView;
        private TextView deviceDescriptionTextView;


        public ViewHolder(View itemView) {
            super(itemView);

            backupDateTextView = (TextView) itemView.findViewById(R.id.backup_data_single_item_backup_date_textview);
            deviceDescriptionTextView = (TextView) itemView.findViewById(R.id.backup_data_single_item_device_description_textview);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null)
                clickListener.onItemClick(v, getAdapterPosition());
        }

    }
}
