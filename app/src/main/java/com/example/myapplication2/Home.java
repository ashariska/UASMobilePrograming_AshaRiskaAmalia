package com.example.myapplication2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.api.ApiConfig;
import com.example.myapplication2.helper.DatabaseHelper;
import com.example.myapplication2.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {
    ListView listView;
    private ImageView imageView;
    private static final int ACTIVITY_REQUEST_CODE = 1000;
    private static final int PERMISSION_REQUEST_CODE = 2000;
    ArrayAdapter<String> arrayAdapter;
    List<String> userNamesList;
    DatabaseHelper db;
    List<String> taskList;
    ArrayAdapter<String> taskAdapter;
    EditText newTask;
    Button addTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = new DatabaseHelper(this);
        taskList = db.getAllTasks();

        userNamesList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNamesList);

        listView = findViewById(R.id.list);
        newTask = findViewById(R.id.newTask);
        addTaskButton = findViewById(R.id.addTaskButton);

        taskAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);
        listView.setAdapter(taskAdapter);

        String username = getIntent().getStringExtra("username");
        if (username != null) {
            TextView usernameTextView = findViewById(R.id.tekshome);
            usernameTextView.setText("Selamat Datang " + username);
        }

        ApiConfig.getRetrofitClient().getAllUser().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (User user : response.body()) {
                        userNamesList.add(user.getEmail());
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
                // Handle failure
            }
        });

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = newTask.getText().toString();
                if (!task.isEmpty()) {
                    db.addTask(task);
                    taskList.clear();
                    taskList.addAll(db.getAllTasks());
                    taskAdapter.notifyDataSetChanged();
                    newTask.setText("");
                    Toast.makeText(Home.this, "Task added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Home.this, "Please enter a task", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String task = taskList.get(position);
            showEditTaskDialog(task, position);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            String task = taskList.get(position);
            db.deleteTask(task);
            taskList.remove(position);
            taskAdapter.notifyDataSetChanged();
            Toast.makeText(Home.this, "Task deleted", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void showEditTaskDialog(String oldTask, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        final EditText input = new EditText(this);
        input.setText(oldTask);
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTask = input.getText().toString();
                if (!newTask.isEmpty()) {
                    db.updateTask(oldTask, newTask);
                    taskList.set(position, newTask);
                    taskAdapter.notifyDataSetChanged();
                    Toast.makeText(Home.this, "Task updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Home.this, "Task cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
