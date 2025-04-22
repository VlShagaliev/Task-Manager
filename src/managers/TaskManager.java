package managers;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {


    void printAllTask();

    void printAllEpic();

    void printAllSubtask();

    void printById(int id);

    void printSubtaskByIdEpic(int idEpic);

    void clearTask();

    void clearEpic();

    void clearSubtask();

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void deleteById(int id);

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    int addTask(Task newTask);

    int addEpic(Epic newEpic);

    int addSubtask(Subtask newSubtask);

    void updateEpicStatus(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Task> getTasks();

    List<Task> getEpics();

    List<Task> getSubtasks();

    boolean checkIdInEpic(int id);

    boolean checkIdInTask(int id);

    boolean checkIdSubtask(int id);

    List<Task> getHistory();

    HistoryManager getHistoryManager();

    void addToTreeSet(Task task);

    List<Task> getPrioritizedTasks();

}
