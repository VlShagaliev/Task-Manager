package httpTaskServer;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fileBackedTaskManager.FileBackedTaskManager;
import model.Progress;
import model.Subtask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubtaskTypeAdapter extends TypeAdapter<Subtask> {

    private static final DateTimeFormatter formatter = FileBackedTaskManager.dateTimeFormatter;

    @Override
    public void write(JsonWriter out, Subtask subtask) throws IOException {
        out.beginObject();

        // Сериализуем поля родительского класса Task
        out.name("id").value(subtask.getId());
        out.name("name").value(subtask.getName());
        out.name("description").value(subtask.getDescription());

        // progress - enum, сериализуем как строку
        if (subtask.getProgress() != null) {
            out.name("progress").value(subtask.getProgress().name());
        } else {
            out.name("progress").nullValue();
        }

        // startTime - LocalDateTime в строку
        if (subtask.getStartTime() != null) {
            out.name("startTime").value(subtask.getStartTime().format(formatter));
        } else {
            out.name("startTime").nullValue();
        }

        // duration - Duration в минуты (long)
        if (subtask.getDuration() != null) {
            out.name("duration").value(String.format("%02d:%02d",subtask.getDuration().toHoursPart(), subtask.getDuration().toMinutesPart()));
        } else {
            out.name("duration").nullValue();
        }

        // idEpic - поле специфичное для Subtask
        out.name("idEpic").value(subtask.getIdEpic());

        out.endObject();
    }

    @Override
    public Subtask read(JsonReader in) throws IOException {
        int id = 0;
        String name = null;
        String description = null;
        Progress progress = null;
        LocalDateTime startTime = null;
        Duration duration = null;
        int idEpic = 0;

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
                        String progressStr = in.nextString();
                        try {
                            progress = Progress.valueOf(progressStr);
                        } catch (IllegalArgumentException e) {
                            progress = null; // или выбросить исключение
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
                case "idEpic":
                    idEpic = in.nextInt();
                    break;
                default:
                    in.skipValue();
            }
        }

        in.endObject();

        // Создаем объект Subtask. В вашем классе нет конструктора с id, поэтому создаем через конструктор и потом устанавливаем id.

        Subtask subtask;

        if (duration != null && startTime != null && progress != null && name != null && description != null) {
            subtask = new Subtask(name, description, idEpic, progress);
            subtask.setStartTime(startTime);
            subtask.setDuration(duration);
            subtask.setId(id); // Устанавливаем id после создания объекта
        } else if (progress != null && name != null && description != null) {
            // fallback на конструктор без времени и длительности
            subtask = new Subtask(name, description, idEpic, progress);
            subtask.setStartTime(startTime);
            subtask.setDuration(duration);
            subtask.setId(id); // Устанавливаем id после создания объекта
        } else {
            throw new IOException("Недостаточно данных для создания объекта Subtask");
        }

        return subtask;
    }
}