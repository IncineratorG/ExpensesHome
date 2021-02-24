package com.costs.newcosts;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class AdapterSmsReaderRecyclerView extends RecyclerView.Adapter<AdapterSmsReaderRecyclerView.SmsReaderViewHolder> {
    private static final String TAG = "tag";

    private OnItemClickListener clickListener;
    private Cursor cursor;
    private List<DataUnitSms> dataList;
    private Calendar calendar;
    private long currentTimeInMillis = -1;


    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }


    public AdapterSmsReaderRecyclerView(List<DataUnitSms> data) {
//        Log.d(TAG, "AdapterSmsReaderRecyclerView->DATA: " + data.size());

        dataList = data;
        calendar = new GregorianCalendar();
        currentTimeInMillis = calendar.getTimeInMillis();
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public SmsReaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_sms_expense_reader_single_item, parent, false);
        return new SmsReaderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SmsReaderViewHolder holder, int position) {
        calendar.setTimeInMillis(currentTimeInMillis);
        if (position > 0
                && dataList.get(position - 1).getSmsDay() == dataList.get(position).getSmsDay()
                && dataList.get(position - 1).getSmsMonth() == dataList.get(position).getSmsMonth()
                && dataList.get(position - 1).getSmsYear() == dataList.get(position).getSmsYear())
        {
            holder.smsDateLayout.setVisibility(View.GONE);
        }
        else
        {
            holder.smsDateLayout.setVisibility(View.VISIBLE);

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            if (day == dataList.get(position).getSmsDay()
                    && month == dataList.get(position).getSmsMonth()
                    && year == dataList.get(position).getSmsYear())
            {
                holder.smsDateTextView.setText("Сегодня");
            }
            else
            {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);

                if (day == dataList.get(position).getSmsDay()
                        && month == dataList.get(position).getSmsMonth()
                        && year == dataList.get(position).getSmsYear())
                {
                    holder.smsDateTextView.setText("Вчера");
                }
                else
                {
                    holder.smsDateTextView.setText(dataList.get(position).getSmsDay() + " "
                                                    + Constants.DECLENSION_MONTH_NAMES[dataList.get(position).getSmsMonth()] + " "
                                                    + dataList.get(position).getSmsYear());
                }
            }
        }

        holder.smsBodyTextView.setText(dataList.get(position).getSmsBody());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }



    // ===================================== View Holder ===========================================
    public class SmsReaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView smsDateTextView;
        private TextView smsBodyTextView;
        private LinearLayout smsDateLayout;


        public SmsReaderViewHolder(View itemView) {
            super(itemView);

            smsDateTextView = (TextView) itemView.findViewById(R.id.sms_expense_reader_single_item_date_textview);
            smsBodyTextView = (TextView) itemView.findViewById(R.id.sms_expense_reader_single_item_sms_body_textview);
            smsDateLayout = (LinearLayout) itemView.findViewById(R.id.sms_expense_reader_single_item_date_layout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null)
                clickListener.onItemClick(v, getAdapterPosition());
        }

    }
}
