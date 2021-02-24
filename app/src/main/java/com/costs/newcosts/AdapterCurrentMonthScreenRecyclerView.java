package com.costs.newcosts;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class AdapterCurrentMonthScreenRecyclerView extends RecyclerView.Adapter<AdapterCurrentMonthScreenRecyclerView.FragmentCurrentMonthScreenViewHolder> {

    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private List<DataUnitExpenses> data;
    private Context context;
    private Calendar calendar;
    private Fragment targetFragment;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int position);
    }



    public AdapterCurrentMonthScreenRecyclerView(List<DataUnitExpenses> data, Context context, Fragment targetFragment) {
        this.data = data;
        this.context = context;
        this.targetFragment = targetFragment;
        calendar = Calendar.getInstance();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public FragmentCurrentMonthScreenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_current_month_screen_single_item, parent, false);
        return new FragmentCurrentMonthScreenViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FragmentCurrentMonthScreenViewHolder holder, int position) {
        holder.categoryNameTextView.setText(data.get(position).getExpenseName());

        final int finalPosition = position;
        // При нажатии на значок редактирования категории расходов
        // показываем соответсвующее дилоговое окно
        holder.editCategoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentEditExpenseName editExpenseNameDialogFragment = DialogFragmentEditExpenseName.newInstance(data.get(finalPosition));
                editExpenseNameDialogFragment.setTargetFragment(targetFragment, Constants.EDIT_EXPENSE_NAME_REQUEST_CODE);
                editExpenseNameDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), Constants.EDIT_DIALOG_TAG);
            }
        });

        // При обработке пункта "Добавить новую категорию" необходимо
        // скрыть ненужные элементы
        if (data.get(position).getExpenseId_N() == Integer.MIN_VALUE) {
            holder.editCategoryImageView.setVisibility(View.INVISIBLE);
            holder.categoryValueTextView.setVisibility(View.INVISIBLE);
            holder.inCurrentMonthTextView.setVisibility(View.INVISIBLE);
            holder.arrowRight.setVisibility(View.INVISIBLE);
        } else {
            holder.inCurrentMonthTextView.setVisibility(View.VISIBLE);
            holder.editCategoryImageView.setVisibility(View.VISIBLE);
            holder.categoryValueTextView.setVisibility(View.VISIBLE);
            holder.arrowRight.setVisibility(View.VISIBLE);
            holder.categoryValueTextView.setText(data.get(position).getExpenseValueString() + " " +
                                                    context.getResources().getString(R.string.rur_string) +
                                                    context.getResources().getString(R.string.dot_sign_string));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void setClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }
    public void setLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }


    // ===================================== View Holder ===========================================
    public class FragmentCurrentMonthScreenViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        private ImageView editCategoryImageView;
        private ImageView arrowRight;
        private TextView categoryNameTextView;
        private TextView categoryValueTextView;
        private TextView inCurrentMonthTextView;
        private LinearLayout topLayout;


        public FragmentCurrentMonthScreenViewHolder(View itemView) {
            super(itemView);

            editCategoryImageView = (ImageView) itemView.findViewById(R.id.current_month_single_item_edit_category_imageview);

            categoryNameTextView = (TextView) itemView.findViewById(R.id.current_month_single_item_category_textview);
            categoryValueTextView = (TextView) itemView.findViewById(R.id.current_month_single_item_value_textview);
            inCurrentMonthTextView = (TextView) itemView.findViewById(R.id.current_month_single_item_in_current_month_textview);
            arrowRight = (ImageView) itemView.findViewById(R.id.current_month_single_item_arrow_right_imageview);
            topLayout = (LinearLayout) itemView.findViewById(R.id.current_month_single_item_top_layout);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null)
                clickListener.onItemClick(v, getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            if (longClickListener != null)
                longClickListener.onItemLongClick(v, getAdapterPosition());
            return true;
        }

    }
}
