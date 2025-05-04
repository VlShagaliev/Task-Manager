package model;

import fileBackedTaskManager.FileBackedTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    protected int id;
    protected final String name;
    protected final String description;
    protected Progress progress;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description, Progress progress, Duration duration, LocalDateTime now) {
        this.name = name;
        this.description = description;
        this.progress = progress;
        this.duration = duration;
        this.startTime = now;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Task(String name, String description, Progress progress) {
        this.name = name;
        this.description = description;
        this.progress = progress;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void print() {
        StringBuilder outputText = new StringBuilder(id + ". Задача: " + name + "|\t Описание: " + description + "|\t Статус: " + progress);
        if (startTime != null) {
            outputText.append("|\t Время начала: ").append(startTime.format(FileBackedTaskManager.dateTimeFormatter));
        }
        if (duration != null)
            outputText.append("|\t Время окончания: ").append(getEndTime().format(FileBackedTaskManager.dateTimeFormatter))
                    .append("|\t Длительность задачи: ").append(duration.toHoursPart()).append(":").append(duration.toMinutesPart());
        System.out.println(outputText);
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Progress getProgress() {
        return progress;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    @Override
    public int compareTo(Task o) {
        if (this.startTime.isAfter(o.startTime)) {
            return 1;
        } else if (this.startTime.isBefore(o.startTime)) {
            return -1;
        }
        return 0;
    }
}
