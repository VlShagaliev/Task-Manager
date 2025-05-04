package httpTaskServer;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fileBackedTaskManager.FileBackedTaskManager;
import model.Epic;
import model.Progress;
import model.Subtask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class EpicTypeAdapter extends TypeAdapter<Epic> {

    Gson gson = BaseHttpHandler.gson;
    private static final DateTimeFormatter formatter = FileBackedTaskManager.dateTimeFormatter;

    @Override
    public void write(JsonWriter out, Epic epic) throws IOException {
        out.beginObject();

        // Сериализуем поля родительского класса Task
        out.name("id").value(epic.getId());
        out.name("name").value(epic.getName());
        out.name("description").value(epic.getDescription());

        // progress - enum, сериализуем как строку
        if (epic.getProgress() != null) {
            out.name("progress").value(epic.getProgress().name());
        } else {
            out.name("progress").nullValue();
        }

        // startTime - LocalDateTime в строку
        if (epic.getStartTime() != null) {
            out.name("startTime").value(epic.getStartTime().format(formatter));
        } else {
            out.name("startTime").nullValue();
        }

        // duration - Duration в минуты (long)
        if (epic.getDuration() != null) {
            out.name("duration").value(String.format("%02d:%02d",epic.getDuration().toHoursPart(), epic.getDuration().toMinutesPart()));
        } else {
            out.name("duration").nullValue();
        }

        // endTime - LocalDateTime в строку
        if (epic.getEndTime() != null) {
            out.name("endTime").value(epic.getEndTime().format(formatter));
        } else {
            out.name("endTime").nullValue();
        }

        // subtaskHashMap - сериализуем как объект
        out.name("subtasks");
        out.beginArray();
        for (Subtask subtask : epic.getSubtaskHashMap().values()) {
            out.value(subtask.getId()); // или можно сериализовать весь объект Subtask
            // Для полной сериализации можно использовать отдельный адаптер для Subtask
            // String jsonSubtask = gson.toJson(subtask);
            // out.jsonValue(jsonSubtask);
        }
        out.endArray();

        out.endObject();
    }

    @Override
    public Epic read(JsonReader in) throws IOException {
        int id = 0;
        String name = null;
        String description = null;
        Progress progress = null;
        LocalDateTime startTime = null;
        Duration duration = null;
        LocalDateTime endTime = null;

        HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();

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
                case "endTime":
                    if (in.peek() != com.google.gson.stream.JsonToken.NULL) {
                        String endStr = in.nextString();
                        endTime = LocalDateTime.parse(endStr, formatter);
                    } else {
                        in.nextNull();
                    }
                    break;
                case "subtasks":
                    in.beginArray();
                    while (in.hasNext()) {
                        String requestBody = in.nextString();
                        Subtask subtask = gson.fromJson(requestBody, Subtask.class);
                        subtaskHashMap.put(subtask.getId(), subtask);
                    }
                    in.endArray();
                    break;
                default:
                    in.skipValue();
            }
        }

        in.endObject();

        Epic epic;

        if(name != null && description != null){
            epic = new Epic(name, description);
            epic.setId(id);  // Устанавливаем id после создания объекта
            epic.setStartTime(startTime);
            epic.setDuration(duration);
            epic.setEndTime(endTime);
            epic.getSubtaskHashMap().putAll(subtaskHashMap);  // Добавляем все подзадачи

        }else{
            throw new IOException("Недостаточно данных для создания объекта Epic");
        }

        return epic;
    }
}