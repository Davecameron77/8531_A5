package ca.tinyweb.a8531_a5;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;

import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskRunner {

    /**
     * Default constructor
     */
    public TaskRunner(MainActivity activityToUpdate) {
        executor = Executors.newFixedThreadPool(NUM_THREADS);
        tasks = new PriorityQueue<>();
        mainActivity = activityToUpdate;
    }

    private boolean isRunning;
    private final int NUM_THREADS = 4;
    private final MainActivity mainActivity;
    private final String TAG = "TaskRunner";
    private int tasksInProgress;

    private final ExecutorService executor;
    private final PriorityQueue<MainActivity.Task> tasks;

    /**
     * Adds a task to the priority queue
     * @param taskToAdd The task to add
     */
    protected void addTask(MainActivity.Task taskToAdd) {
        tasks.add(taskToAdd);
        Log.d(TAG, "Queued a task with priority " + taskToAdd.priority);
        Log.d(TAG, "Tasks in queue: " + tasks.size());
    }

    /**
     * Starts the infinite loop that dispatches tasks to the ThreadPool
     */
    protected void start() {
        isRunning = true;
        manageThreadPool();
    }

    /**
     * Infinite loop that hands tasks to the ThreadPool when their is space
     * Evaluates once every ITERATION_DELAY
     */
    @SuppressLint("SetTextI18n")
    private void manageThreadPool() {
        new Thread(() -> {
            try {
                while (isRunning) {
                    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) this.executor;
                    tasksInProgress = threadPoolExecutor.getActiveCount();
                    updateGui();

                    // Process tasks
                    if (tasksInProgress < NUM_THREADS && !tasks.isEmpty()) {
                        MainActivity.Task taskToExecute = tasks.poll();
                        Log.d(TAG, "Tasks: " + tasksInProgress + ", Queue: " + tasks.size());
                        assert taskToExecute != null;
                        executor.execute(taskToExecute);
                    }
                }
            } catch (Exception ex) {
                Log.d(TAG, "Error dispatching tasks");
                ex.printStackTrace();
            }
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void updateGui() {
        long lowPriority = tasks.stream()
                .filter(task -> task.priority.getPriorityValue() == MainActivity.TaskPriority.LOW.getPriorityValue())
                .count();
        mainActivity.runOnUiThread(new Thread(() -> ((TextView) mainActivity.findViewById(R.id.tv_low_priority)).setText(Long.toString(lowPriority))));
        long medPriority = tasks.stream()
                .filter(task -> task.priority.getPriorityValue() == MainActivity.TaskPriority.MED.getPriorityValue())
                .count();
        mainActivity.runOnUiThread(new Thread(() -> ((TextView) mainActivity.findViewById(R.id.tv_med_priority)).setText(Long.toString(medPriority))));
        long highPriority = tasks.stream()
                .filter(task -> task.priority.getPriorityValue() == MainActivity.TaskPriority.HIGH.getPriorityValue())
                .count();
        mainActivity.runOnUiThread(new Thread(() -> ((TextView) mainActivity.findViewById(R.id.tv_high_priority)).setText(Long.toString(highPriority))));
        mainActivity.runOnUiThread(new Thread(() -> ((TextView) mainActivity.findViewById(R.id.tv_tasks_in_progress)).setText(Integer.toString(tasksInProgress))));
    }

    /**
     * Returns true if the task dispatcher is running
     * @return true if running, else false
     */
    protected boolean checkIsRunning() {
        return !isRunning;
    }
}
