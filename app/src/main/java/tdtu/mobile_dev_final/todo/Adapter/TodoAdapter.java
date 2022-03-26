package tdtu.mobile_dev_final.todo.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import tdtu.mobile_dev_final.todo.R;
import tdtu.mobile_dev_final.todo.Todo;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private List<Todo> listTodo;
    private Context mconText;
    private ArrayList<Integer> positionList;
    private OnTodoListener onTodoListener;

    public TodoAdapter(@NonNull Context context, @NonNull List<Todo> listTodo, OnTodoListener onTodoListener) {
        this.mconText = context;
        this.listTodo = listTodo;
        this.onTodoListener = onTodoListener;
    }

    @NonNull
    @Override
    public TodoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_row_item, parent, false);
        ViewHolder holder = new TodoAdapter.ViewHolder(v, this.onTodoListener);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Todo t = listTodo.get(position);
        if (t == null) {
            return;
        }

        try {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

            Date startDate = inputFormat.parse(t.getStartDate());
            Date endDate = inputFormat.parse(t.getEndDate());

            assert startDate != null;
            assert endDate != null;

            // Day Name
            SimpleDateFormat dateNameFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            String dayName = dateNameFormat.format(endDate);

            // Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
            String date = dateFormat.format(endDate);

            // Month
            SimpleDateFormat monthFormat = new SimpleDateFormat("LLL", Locale.ENGLISH);
            String month = monthFormat.format(endDate);

            holder.tvTimeEstimation.setText(calculateTime(startDate, endDate));
            holder.tvTimeEstimation.setTextColor(ColorStateList.valueOf(mconText.getResources().getColor(R.color.pastelRed)));
//            holder.tvCreatedAt.setText(" " + stringFormat.format(endDate));
            holder.tvDaysOfWeek.setText(dayName.substring(0,3));
            holder.tvDate.setText(date);
            holder.tvMonth.setText(month.substring(0,3));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tvTitle.setText(t.getName());

//        if (t.getStatus()) {
////            holder.status.setText(R.string.done);
////            holder.status.setTextColor(mconText.getResources().getColor(R.color.doneText));
//            holder.status.setBackgroundTintList(ColorStateList.valueOf(mconText.getResources().getColor(R.color.doneBackground)));
//            holder.status.setIcon(mconText.getResources().getDrawable(R.drawable.ic_check));
//            holder.status.setIconTint(ColorStateList.valueOf(mconText.getResources().getColor(R.color.doneText)));
//        } else {
////            holder.status.setText(R.string.assign);
////            holder.status.setTextColor(mconText.getResources().getColor(R.color.assignText));
//            holder.status.setBackgroundTintList(ColorStateList.valueOf(mconText.getResources().getColor(R.color.assignedBackground)));
//            holder.status.setIcon(mconText.getResources().getDrawable(R.drawable.ic_assigned));
//            holder.status.setIconTint(ColorStateList.valueOf(mconText.getResources().getColor(R.color.assignText)));
//        }

        holder.chipGroup.removeAllViews();

        for (String tag: t.getTags()) {
            holder.chipGroup.addView(makeChip(tag));
        }


    }

    @Override
    public int getItemCount() {
        if (listTodo != null) {
            return listTodo.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardView;
        TextView tvTitle, tvTaskProgress, tvCreatedAt, tvTimeEstimation, tvDaysOfWeek, tvDate, tvMonth;
        CircularProgressBar taskProgressBar;
//        MaterialButton status;
        ChipGroup chipGroup;
        OnTodoListener onTodoListener;

        public ViewHolder(@NonNull View itemView, OnTodoListener onTodoListener) {
            super(itemView);

            cardView = itemView.findViewById(R.id.todoItemCard);
            tvTitle = itemView.findViewById(R.id.tvTodoDetailTitle);
            tvTaskProgress = itemView.findViewById(R.id.tvTaskProgress);
            taskProgressBar = itemView.findViewById(R.id.taskProgressBar);
            tvDaysOfWeek = itemView.findViewById(R.id.tvDaysOfWeek);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvTimeEstimation = itemView.findViewById(R.id.timeEstimation);
//            tvCreatedAt = itemView.findViewById(R.id.tvCreatedDate);
//            status = itemView.findViewById(R.id.btnStatus);
            chipGroup = itemView.findViewById(R.id.chipGroup);
            this.onTodoListener = onTodoListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onTodoListener.onTodoClick(getAdapterPosition());
        }
    }

    public static String calculateTime(Date startDate, Date endDate) {

        long difference = endDate.getTime() - startDate.getTime();

        int seconds = (int) (difference/1000);

        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24L);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);

        StringBuilder result= new StringBuilder();
        HashMap<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("day", String.valueOf(day) + "d");
        resultMap.put("hours",  String.valueOf(hours) +"h");
        resultMap.put("minute", String.valueOf(minute) + "m");
        resultMap.put("second", String.valueOf(second) + "s");

        for(String key : resultMap.keySet()){
            if (!resultMap.get(key).contains("0d") && !resultMap.get(key).contains("0h") && !resultMap.get(key).contains("0m") && !resultMap.get(key).contains("0s") ) {
                result.append(resultMap.get(key)).append(" left");
            }
        }

        return result.toString();

    }

    public Chip makeChip(String tag) {

        Chip tagChip = new Chip(mconText);
        tagChip.setText(tag);
        tagChip.setChipBackgroundColorResource(R.color.title);
        tagChip.setTextColor(mconText.getResources().getColor(R.color.overlay));

        return tagChip;
    }

    public interface OnTodoListener {
        void onTodoClick(int position);
    }

}
