package com.marcruble.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a whole experiment which is defined as a sequence of tasks.
 * These tasks contain a sequence of conditions for each participant.
 */
public class Experiment {

    private List<Task> tasks;

    public Experiment()
    {
        tasks = new ArrayList<>();
    }

    public void addTask(Task task)
    {
        tasks.add(task);
    }

    public Task getTask(int index)
    {
        return tasks.get(index);
    }

    public List<Task> getTasks()
    {
        return tasks;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tasks.size(); i++)
        {
            sb.append("Task " + (i+1));
            sb.append("\n");
            sb.append(tasks.get(i).toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Alternative toString which contains only parts relevant for the given participant.
     * @param participant
     * @return
     */
    public String toString(int participant)
    {
        StringBuilder sb = new StringBuilder("### Experiment for participant ");
        sb.append(participant);
        sb.append(" ###\n");

        for (int i = 0; i < tasks.size(); i++)
        {
            sb.append("Task " + (i+1));
            sb.append("\n");
            sb.append(tasks.get(i).toString(participant));
            sb.append("\n");
        }

        return sb.toString();
    }
}
