package tdtu.mobile_dev_final.todo.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

import tdtu.mobile_dev_final.todo.R;
import tdtu.mobile_dev_final.todo.SubTask;

public class SubtaskAdapter extends RecyclerView.Adapter<SubtaskAdapter.SubtaskAdapterViewHolder> {

    private List<SubTask> subtasks;
    private Context context;

    public SubtaskAdapter(Context context, List<SubTask> subtasks) {
        this.subtasks = subtasks;
        this.context = context;
    }

    @NonNull
    @Override
    public SubtaskAdapter.SubtaskAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.subtask_item, parent, false);
        SubtaskAdapter.SubtaskAdapterViewHolder holder = new SubtaskAdapter.SubtaskAdapterViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SubtaskAdapter.SubtaskAdapterViewHolder holder, int position) {

        SubTask subtask = subtasks.get(position);

        if (subtask == null) {
            return;
        }

        holder.tvTaskTitle.setText(subtask.getName());
        holder.tvTaskTitle.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.textColor)));
        holder.tvTaskDescription.setText(subtask.getDescription());
        holder.tvTaskDescription.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.textColor)));

        holder.lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subtask.isStatus()) {
                    holder.lottieAnimationView.setSpeed(-1);
                    holder.lottieAnimationView.playAnimation();
                    subtask.status = false;
                } else {
                    holder.lottieAnimationView.setSpeed(1);
                    holder.lottieAnimationView.playAnimation();
                    subtask.status = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (subtasks != null) {
            return subtasks.size();
        }
        return 0;
    }

    public static class SubtaskAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle, tvTaskDescription;
//        CheckBox cbTaskStatus;
        LottieAnimationView lottieAnimationView;

       public SubtaskAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            lottieAnimationView = itemView.findViewById(R.id.cbSubtask);
        }
    }

}
