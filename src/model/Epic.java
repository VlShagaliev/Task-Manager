package model;

import fileBackedTaskManager.FileBackedTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    private LocalDateTime endTime;

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Epic(String name, String description) {
        super(name, description, Progress.NEW);
    }

    public HashMap<Integer, Subtask> getSubtaskHashMap() {
        return subtaskHashMap;
    }

    public void addSubtask(Subtask newSubtask) {
        for (Integer key : subtaskHashMap.keySet()) {
            if (subtaskHashMap.get(key).equals(newSubtask)) {
                System.out.println("Такая подзадача уже существует");
                return;
            }
        }
        subtaskHashMap.put(newSubtask.getId(), newSubtask);
    }

    public void setDuration() {
        for (Integer key : subtaskHashMap.keySet()) {
            Duration subtaskDuration = subtaskHashMap.get(key).getDuration();
            if (subtaskDuration != null) {
                if (this.duration == null) {
                    this.duration = subtaskDuration;
                } else {
                    this.duration.plus(subtaskDuration);
                }
            }
        }
    }

    public void print() {
        StringBuilder outputText = new StringBuilder(id + ". Эпик: " + name + "|\t Описание: " + description + "|\t Статус: " + progress);
        if (startTime != null) {
            outputText.append("|\t Время начала: ").append(startTime.format(FileBackedTaskManager.dateTimeFormatter));
        }
        if (duration != null)
            outputText.append("|\t Время окончания: ").append(getEndTime().format(FileBackedTaskManager.dateTimeFormatter));
        System.out.println(outputText);
    }
}
