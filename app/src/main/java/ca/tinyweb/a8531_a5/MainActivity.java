package ca.tinyweb.a8531_a5;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnLowPriority;
    Button btnMedPriority;
    Button btnHighPriority;

    TextView tvLowPriority;
    TextView tvMedPriority;
    TextView tvHighPriority;
    TextView tvTasksInProgress;

    int lowPriorityTasks;
    int medPriorityTasks;
    int highPriorityTasks;

    private TaskRunner taskRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskRunner = new TaskRunner(this);

        lowPriorityTasks = 0;
        medPriorityTasks = 0;
        highPriorityTasks = 0;

        btnLowPriority = findViewById(R.id.btn_low_priority);
        btnMedPriority = findViewById(R.id.btn_med_priority);
        btnHighPriority = findViewById(R.id.btn_high_priority);

        tvLowPriority = findViewById(R.id.tv_low_priority);
        tvLowPriority.setText("0");
        tvMedPriority = findViewById(R.id.tv_med_priority);
        tvMedPriority.setText("0");
        tvHighPriority = findViewById(R.id.tv_high_priority);
        tvHighPriority.setText("0");
        tvTasksInProgress = findViewById(R.id.tv_tasks_in_progress);
        tvTasksInProgress.setText("0");

        btnLowPriority.setOnClickListener(view -> {
            if (taskRunner.checkIsRunning()) {
                taskRunner.start();
            }
            taskRunner.addTask(new Task(TaskPriority.LOW));
        });

        btnMedPriority.setOnClickListener(view -> {
            if (taskRunner.checkIsRunning()) {
                taskRunner.start();
            }
            taskRunner.addTask(new Task(TaskPriority.MED));
        });

        btnHighPriority.setOnClickListener(view -> {
            if (taskRunner.checkIsRunning()) {
                taskRunner.start();
            }
            taskRunner.addTask(new Task(TaskPriority.HIGH));
        });
    }

    /**
     * Task to store/sort for the ThreadPool
     */
    static class Task implements Comparable<Task>, Runnable {
        protected TaskPriority priority;
        private final long taskLength;

        public Task(TaskPriority priority) {
            this.priority = priority;
            switch (this.priority) {
                case HIGH: taskLength = 10000; break;
                case MED: taskLength = 5000; break;
                default: taskLength = 1000; break;
            }
        }

        public int compareTo(Task task) {
            return this.priority.getPriorityValue() < task.priority.getPriorityValue() ? 1 : -1;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(taskLength);
                Log.d("Task", "Finished a " + priority.getPriorityValue() +" priority task");
            } catch (Exception ex) {
                Log.e("TASK", "Error executing task with priority " + priority.getPriorityValue());
            }
        }
    }

    /**
     * Priority for sorting tasks in PriorityQueue
     */
    enum TaskPriority {
        HIGH(10),
        MED(5),
        LOW(1);

        private final int priorityValue;

        TaskPriority(int priorityValue) {
            this.priorityValue = priorityValue;
        }

        public int getPriorityValue() {
            return this.priorityValue;
        }
    }
}

