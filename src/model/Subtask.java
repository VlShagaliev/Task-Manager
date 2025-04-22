package model;

import fileBackedTaskManager.FileBackedTaskManager;

public class Subtask extends Task {
    private final int idEpic;

    public Subtask(String name, String description, int idEpic, Progress progress) {
        super(name, description, progress);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public void print() {
        StringBuilder outputText = new StringBuilder(id + ". Номер Эпика: " + idEpic + ". Подзадача: " + name +
                "|\t Описание: " + description + "|\t Статус: " + progress);
        if (startTime != null) {
            outputText.append("|\t Время начала: ").append(startTime.format(FileBackedTaskManager.dateTimeFormatter));
        }
        if (duration != null)
            outputText.append("|\t Время окончания: ").append(getEndTime().format(FileBackedTaskManager.dateTimeFormatter))
                    .append("|\t Длительность задачи: ").append(duration.toHoursPart()).append(":").append(duration.toMinutesPart());
        System.out.println(outputText);
    }
}
