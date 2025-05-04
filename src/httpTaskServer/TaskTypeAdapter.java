package httpTaskServer;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fileBackedTaskManager.FileBackedTaskManager;
import model.Progress;
import model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskTypeAdapter extends TypeAdapter<Task> {

    private static final DateTimeFormatter formatter = FileBackedTaskManager.dateTimeFormatter;

    @Override
    public void write(JsonWriter out, Task task) throws IOException {
        out.beginObject();

        out.name("id").value(task.getId());
        out.name("name").value(task.getName());
        out.name("description").value(task.getDescription());

        // progress - enum, сериализуем как строку
        if (task.getProgress() != null) {
            out.name("progress").value(task.getProgress().name().toUpperCase());
        } else {
            out.name("progress").nullValue();
        }

        // startTime - LocalDateTime в строку
        if (task.getStartTime() != null) {
            out.name("startTime").value(task.getStartTime().format(formatter));
        } else {
            out.name("startTime").nullValue();
        }

        // duration - Duration в минуты (long)
        if (task.getDuration() != null) {
            out.name("duration").value(String.format("%02d:%02d",task.getDuration().toHoursPart(), task.getDuration().toMinutesPart()));
        } else {
            out.name("duration").nullValue();
        }

        out.endObject();
    }

    @Override
    public Task read(JsonReader in) throws IOException {
        int id = 0;
        String name = null;
        String description = null;
        Progress progress = null;
        LocalDateTime startTime = null;
        Duration duration = null;

        in.beginObject();

        while (in.hasNext()) {
            String fieldName = in.nextName();

            switch (fieldName) {
                case "id":
                    id = in.nextInt();
                    break;
                case "name":
                    name = in.nextString();
                    break;
                case "description":
                    description = in.nextString();
                    break;
                case "progress":
                    if (in.peek() != com.google.gson.stream.JsonToken.NULL) {
                        String progressStr = in.nextString().toUpperCase();
                        try {
                            progress = Progress.valueOf(progressStr);
                        } catch (IllegalArgumentException e) {
                            progress = null;
                        }
                    } else {
                        in.nextNull();
                    }
                    break;
                case "startTime":
                    if (in.peek() != com.google.gson.stream.JsonToken.NULL) {
                        String startStr = in.nextString();
                        startTime = LocalDateTime.parse(startStr, formatter);
                    } else {
                        in.nextNull();
                    }
                    break;
                case "duration":
                    if (in.peek() != com.google.gson.stream.JsonToken.NULL) {
                        String[] durationString = in.nextString().split(":");
                        duration = Duration.ofHours(Integer.parseInt(durationString[0])).plusMinutes(Integer.parseInt(durationString[1]));
                    } else {
                        in.nextNull();
                    }
                    break;
                default:
                    in.skipValue();
            }
        }

        in.endObject();

        // Создаем объект Task. В вашем классе нет конструктора с id, поэтому создаем через конструктор и потом устанавливаем id.

        Task task;

        if (duration != null && startTime != null && progress != null && name != null && description != null) {
            task = new Task(name, description, progress, duration, startTime);
        } else if (progress != null && name != null && description != null) {
            // fallback на конструктор без времени и длительности
            task = new Task(name, description, progress);
            task.setStartTime(startTime);
            task.setDuration(duration);
        } else {
            throw new IOException("Недостаточно данных для создания объекта Task");
        }

        task.setId(id);

        return task;
    }
}