import fileBackedTaskManager.FileBackedTaskManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class InMemoryTaskManagerTest {

    @Test
    void clearTask() throws FileBackedTaskManager.ManagerSaveException {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("1", "1", Progress.NEW);
        task.setId(1);
        taskManager.addTask(task);
        taskManager.clearTask();
        Assertions.assertEquals(taskManager.getTasks(), new ArrayList<>());
    }

    @Test
    void clearEpic() throws FileBackedTaskManager.ManagerSaveException {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("1", "1");
        epic.setId(1);
        taskManager.addEpic(epic);
        taskManager.clearEpic();
        Assertions.assertEquals(taskManager.getEpics(), new ArrayList<>());
    }

    @Test
    void clearSubtask() throws FileBackedTaskManager.ManagerSaveException {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("1", "1");
        epic.setId(1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("2", "2", 1, Progress.NEW);
        subtask.setId(2);
        taskManager.addSubtask(subtask);
        taskManager.clearSubtask();
        Assertions.assertEquals(taskManager.getSubtasks(), new ArrayList<>());
    }

    @Test
    void updateEpicStatusInProgress() throws FileBackedTaskManager.ManagerSaveException {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("1", "1");
        epic.setId(1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("2", "2", 1, Progress.NEW);
        subtask.setId(2);
        taskManager.addSubtask(subtask);
        Subtask subtask1 = (Subtask) taskManager.getSubtasks().get(0);
        subtask1.setProgress(Progress.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        Assertions.assertEquals(taskManager.getEpics().get(0).getProgress(), Progress.IN_PROGRESS);
    }

    @Test
    void updateEpicStatusDone() throws FileBackedTaskManager.ManagerSaveException {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("1", "1");
        epic.setId(1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("2", "2", 1, Progress.NEW);
        subtask.setId(2);
        taskManager.addSubtask(subtask);
        Subtask subtask1 = (Subtask) taskManager.getSubtasks().get(0);
        subtask1.setProgress(Progress.DONE);
        taskManager.updateSubtask(subtask1);
        Assertions.assertEquals(taskManager.getEpics().get(0).getProgress(), Progress.DONE);
    }

    @Test
    void updateEpicStatusAfterClearSubtask() throws FileBackedTaskManager.ManagerSaveException {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("1", "1");
        epic.setId(1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("2", "2", 1, Progress.NEW);
        subtask.setId(2);
        taskManager.addSubtask(subtask);
        Subtask subtask1 = (Subtask) taskManager.getSubtasks().get(0);
        subtask1.setProgress(Progress.DONE);
        taskManager.clearSubtask();
        Assertions.assertEquals(taskManager.getEpics().get(0).getProgress(), Progress.NEW);
    }

    @Test
    void updateTask() throws FileBackedTaskManager.ManagerSaveException {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("1", "1", Progress.NEW);
        task.setId(1);
        taskManager.addTask(task);
        Task task1 = taskManager.getTasks().get(0);
        task1.setProgress(Progress.DONE);
        Assertions.assertEquals(taskManager.getTasks().get(0).getProgress(), Progress.DONE);
    }

    @Test
    void updateSubtask() throws FileBackedTaskManager.ManagerSaveException {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("1", "1");
        epic.setId(1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("2", "2", 1, Progress.NEW);
        subtask.setId(2);
        taskManager.addSubtask(subtask);
        Subtask subtask1 = (Subtask) taskManager.getSubtasks().get(0);
        subtask1.setProgress(Progress.DONE);
        taskManager.updateSubtask(subtask1);
        Assertions.assertEquals(taskManager.getSubtasks().get(0).getProgress(), Progress.DONE);
    }

    @Test
    void testInitializationTaskManager() {
        Managers managers = new Managers();
        Assertions.assertNotNull(managers.getDefault());
    }

    @Test
    void testInitializationHistoryManager() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    void testFindTaskById() throws FileBackedTaskManager.ManagerSaveException {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("1", "1", Progress.NEW);
        task.setId(1);
        taskManager.addTask(task);
        Assertions.assertNotNull(taskManager.getTasks().get(0));
    }

    @Test
    void testHistorySavePreviousVersionTask() throws FileBackedTaskManager.ManagerSaveException {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("1", "1", Progress.NEW);
        task.setId(1);
        taskManager.addTask(task);
        taskManager.printById(1);
        task.setProgress(Progress.DONE);
        Task taskBefore = taskManager.getHistory().get(0);
        boolean checkDifferent = (taskBefore.getName().equals(task.getName())) || (taskBefore.getDescription()
                .equals(task.getDescription())) || (taskBefore.getProgress().equals(task.getProgress()));
        Assertions.assertTrue(checkDifferent);
    }

    @Test
    void testHistoryReplaceTaskChecked() throws FileBackedTaskManager.ManagerSaveException {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("1", "1", Progress.NEW);
        task.setId(1);
        taskManager.addTask(task);
        taskManager.printById(1);
        task = new Task("2", "2", Progress.NEW);
        task.setId(2);
        taskManager.addTask(task);
        taskManager.printById(2);
        List<Task> previousHistory = new ArrayList<>(taskManager.getHistory());
        taskManager.printById(1);
        List<Task> newHistory = new ArrayList<>(taskManager.getHistory());
        Assertions.assertNotEquals(previousHistory,newHistory);
    }

    @Test
    void testTaskHaveDateTime(){
        Task task = new Task("1", "1", Progress.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(90));
        task.setId(1);
        Assertions.assertTrue(task.getStartTime() != null);
    }

    @Test
    void testHistoryRemovedTaskById() throws FileBackedTaskManager.ManagerSaveException {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("1", "1", Progress.NEW);
        task.setId(1);
        taskManager.addTask(task);
        taskManager.printById(1);
        task = new Task("2", "2", Progress.NEW);
        task.setId(2);
        taskManager.addTask(task);
        taskManager.printById(2);
        List<Task> previousHistory = new ArrayList<>(taskManager.getHistory());
        taskManager.getHistoryManager().remove(1);
        List<Task> newHistory = new ArrayList<>(taskManager.getHistory());
        Assertions.assertNotEquals(previousHistory,newHistory);
    }

    @Test
    void testEpicStatusIsDoneWithTwoAndMoreDoneSubtask(){
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("1", "1");
        epic.setId(1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("2", "2", 1, Progress.NEW);
        subtask.setId(2);
        taskManager.addSubtask(subtask);
        subtask = new Subtask("3", "3", 1, Progress.NEW);
        subtask.setId(3);
        taskManager.addSubtask(subtask);
        subtask = (Subtask) taskManager.getSubtasks().get(0);
        subtask.setProgress(Progress.DONE);
        taskManager.updateSubtask(subtask);
        subtask = (Subtask) taskManager.getSubtasks().get(1);
        subtask.setProgress(Progress.DONE);
        taskManager.updateSubtask(subtask);
        Assertions.assertEquals(Progress.DONE, epic.getProgress());
    }

    @Test
    void testEpicStatusIsNewWithTwoAndMoreNewSubtask(){
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("1", "1");
        epic.setId(1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("2", "2", 1, Progress.NEW);
        subtask.setId(2);
        taskManager.addSubtask(subtask);
        subtask = new Subtask("3", "3", 1, Progress.NEW);
        subtask.setId(3);
        taskManager.addSubtask(subtask);
        Assertions.assertEquals(Progress.NEW, epic.getProgress());
    }

    @Test
    void testEpicStatusIsInProgressWithTwoAndMoreDifferentSubtask(){
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("1", "1");
        epic.setId(1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("2", "2", 1, Progress.NEW);
        subtask.setId(2);
        taskManager.addSubtask(subtask);
        subtask = new Subtask("3", "3", 1, Progress.NEW);
        subtask.setId(3);
        taskManager.addSubtask(subtask);
        subtask = (Subtask) taskManager.getSubtasks().get(0);
        subtask.setProgress(Progress.DONE);
        taskManager.updateSubtask(subtask);
        subtask = (Subtask) taskManager.getSubtasks().get(1);
        subtask.setProgress(Progress.NEW);
        taskManager.updateSubtask(subtask);
        Assertions.assertEquals(Progress.IN_PROGRESS, epic.getProgress());
    }

    @Test
    void testEpicStatusIsInProgressWithTwoAndMoreInProgressSubtask(){
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("1", "1");
        epic.setId(1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("2", "2", 1, Progress.NEW);
        subtask.setId(2);
        taskManager.addSubtask(subtask);
        subtask = new Subtask("3", "3", 1, Progress.NEW);
        subtask.setId(3);
        taskManager.addSubtask(subtask);
        subtask = (Subtask) taskManager.getSubtasks().get(0);
        subtask.setProgress(Progress.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        subtask = (Subtask) taskManager.getSubtasks().get(1);
        subtask.setProgress(Progress.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        Assertions.assertEquals(Progress.IN_PROGRESS, epic.getProgress());
    }

    @Test
    void testEpicIdEqualSubtaskIdEpic(){
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("1", "1");
        epic.setId(1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("2", "2", 1, Progress.NEW);
        subtask.setId(2);
        taskManager.addSubtask(subtask);
        subtask = epic.getSubtaskHashMap().get(2);
        Assertions.assertEquals(epic.getId(),subtask.getIdEpic());
    }

    @Test
    void testIntersectionDateTimeWithDuration(){
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("1","1",Progress.IN_PROGRESS);
        LocalDateTime localDateTime = LocalDateTime.now();
        task.setStartTime(localDateTime);
        task.setDuration(Duration.ofMinutes(20));
        taskManager.addTask(task);
        task = new Task("2","2", Progress.IN_PROGRESS);
        task.setStartTime(localDateTime.plusMinutes(10));
        task.setDuration(Duration.ofMinutes(10));
        Task finalTask = task;
        Assertions.assertThrows(InMemoryTaskManager.TaskValidException.class,()-> taskManager.addTask(finalTask));
    }

    @Test
    void testNotIntersectionDateTimeWithDuration(){
        TaskManager taskManager = new InMemoryTaskManager();
        LocalDateTime localDateTime = LocalDateTime.now();
        Task task = new Task("1","1",Progress.IN_PROGRESS, Duration.ofMinutes(20), localDateTime);
        taskManager.addTask(task);
        task.setStartTime(localDateTime.plusMinutes(30));
        task = new Task("2","2", Progress.IN_PROGRESS,Duration.ofMinutes(10), localDateTime );
        Task finalTask = task;
        Assertions.assertDoesNotThrow(()-> taskManager.addTask(finalTask));
    }
}