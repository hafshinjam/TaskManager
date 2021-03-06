package com.example.taskmanager.control.fragment;


import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.taskmanager.R;
import com.example.taskmanager.model.State;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.IRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.example.taskmanager.control.fragment.StartManagerFragment.NAME;
import static com.example.taskmanager.control.fragment.StartManagerFragment.NUMBER_OF_TASKS;


public class TaskListFragment extends Fragment {
    private RecyclerView mRecyclerViewTask;
    private FloatingActionButton mButtonFloating;
    private IRepository<Task> mTaskRepository;
    private int mNumberOfTasks;
    private TaskAdapter mAdapter;
    private String mName;

    public TaskListFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TaskListFragment newInstance(String name, int numberOfTasks) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt(NUMBER_OF_TASKS, numberOfTasks);
        args.putString(NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(NAME);
            mNumberOfTasks = getArguments().getInt(NUMBER_OF_TASKS);
        }
        mTaskRepository = TaskRepository.getInstance(mName, mNumberOfTasks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        findViews(view);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            mRecyclerViewTask.setLayoutManager(new LinearLayoutManager(getActivity()));
        else
            mRecyclerViewTask.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        setClickListener();
        updateUI();
        return view;
    }

    private void setClickListener() {
        mButtonFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTaskRepository.getList().size() == 0)
                    mTaskRepository = TaskRepository.getInstance(mName, mNumberOfTasks);
                else {
                    Task task = new Task(mName + " " + (mTaskRepository.getList().size()+1), State.TODO);
                    mTaskRepository.insert(task);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void findViews(View view) {
        mButtonFloating = view.findViewById(R.id.floatingAddButton);
        mRecyclerViewTask = view.findViewById(R.id.recycler_view_task_list);
    }

    private void updateUI() {
        if (mAdapter == null) {
            List<Task> tasks = mTaskRepository.getList();
            mAdapter = new TaskAdapter(tasks);
            mRecyclerViewTask.setAdapter(mAdapter);
        }
    }

    private class TaskHolder extends RecyclerView.ViewHolder {
        private Task mTask;
        private TextView mTextViewTaskName;
        private TextView mTextViewTaskStatus;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewTaskName = itemView.findViewById(R.id.name_row);
            mTextViewTaskStatus = itemView.findViewById(R.id.status_row);

        }

        public void bindTask(Task task) {
            mTask = task;
            mTextViewTaskName.setText(task.getTaskName());
            mTextViewTaskStatus.setText(task.getTaskState().toString());
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        private List<Task> mTasks;

        public List<Task> getTasks() {
            return mTasks;
        }

        public void setTasks(List<Task> tasks) {
            mTasks = tasks;
        }

        public TaskAdapter(List<Task> tasks) {
            mTasks = tasks;
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }

        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.task_list_row, parent, false);
            TaskHolder taskHolder = new TaskHolder(view);
            return taskHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
            if (position % 2 == 0)
                holder.itemView.setBackgroundColor(Color.YELLOW);
            else
                holder.itemView.setBackgroundColor(Color.WHITE);
            Task task = mTasks.get(position);
            holder.bindTask(task);
        }
    }

}