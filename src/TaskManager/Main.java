package TaskManager;

import fileBackedTaskManager.FileBackedTaskManager;
import managers.HistoryManager;
import managers.Managers;
import model.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    static Managers managers = new Managers();
    static TaskManager taskManager = managers.getDefault();

    public static void main(String[] args) throws FileBackedTaskManager.ManagerSaveException {
        Scanner scanner = new Scanner(System.in);
        File file = new File("Backup.csv");
        taskManager = FileBackedTaskManager.loadFromFile(file);
        HistoryManager historyManager = taskManager.getHistoryManager();
        int command;
        while (true) {
            printMenu();
            String name;
            String description;
            int id;
            int numberOfProgress;
            Progress progress;
            Duration newDuration;
            LocalDateTime localDateTime;
            Task task;
            command = Integer.parseInt(scanner.nextLine());
            switch (command) {
                case 1:
                    additionEpic(taskManager);
                    /*name = printName(scanner,TypeTask.EPIC);
                    description = printDescription(scanner);
                    model.Epic newEpic = new model.Epic(name, description);
                    if (taskManager.addEpic(newEpic) > 0) {
                        System.out.println("Добавлена задача под номером: " + newEpic.getId());
                    }*/
                    break;
                case 2:
                    additionTask(taskManager);
                    /*name = printName(scanner, TypeTask.TASK);
                    description = printDescription(scanner);
                    newDuration = printDuration(scanner);
                    localDateTime = printDateTime(scanner);
                    printMenuStatus();
                    numberOfProgress = Integer.parseInt(scanner.nextLine());
                    progress = setProgress(numberOfProgress);
                    if (progress != null) {
                        task = new Task(name, description, progress);
                        task.setStartTime(localDateTime);
                        task.setDuration(newDuration);
                        if (taskManager.addTask(task) > 0) {
                            System.out.println("Добавлена задача под номером: " + task.getId());
                        } else {
                            System.out.println("Такая задача уже существует!");
                        }
                    }*/
                    break;
                case 3:
                    additionSubtask(taskManager);
                    /*System.out.print("Введите ID Эпика которому хотите добавить подзадачу: ");
                    int idEpic = Integer.parseInt(scanner.nextLine());
                    if (taskManager.checkIdInEpic(idEpic)) {
                        name = printName(scanner,TypeTask.SUBTASK);
                        description = printDescription(scanner);
                        newDuration = printDuration(scanner);
                        localDateTime = printDateTime(scanner);
                        printMenuStatus();
                        numberOfProgress = Integer.parseInt(scanner.nextLine());
                        progress = setProgress(numberOfProgress);
                        if (progress != null) {
                            model.Subtask newSubtask = new model.Subtask(name, description, idEpic, progress);
                            newSubtask.setDuration(newDuration);
                            newSubtask.setStartTime(localDateTime);
                            if (taskManager.addSubtask(newSubtask) > 0) {
                                System.out.println("Добавлена задача под номером: " + newSubtask.getId());
                            } else {
                                System.out.println("Такая подзадача уже существует!");
                            }
                        }
                    } else {
                        System.out.println("Такого Эпика нет!");
                    }*/
                    break;
                case 4:
                    taskManager.printAllTask();
                    break;
                case 5:
                    taskManager.printAllEpic();
                    break;
                case 6:
                    taskManager.printAllSubtask();
                    break;
                case 7:
                    System.out.print("Введите ID задачи/Эпика/подзадачи которую хотите вывести: ");
                    id = Integer.parseInt(scanner.nextLine());
                    if (taskManager.checkIdSubtask(id) || taskManager.checkIdInEpic(id) || taskManager.checkIdInTask(id)) {
                        taskManager.printById(id);
                    } else {
                        System.out.println("Такой задачи/Эпика/подзадачи нет!");
                    }
                    break;
                case 8:
                    System.out.print("Введите ID Эпика подзадачи которого хотите вывести: ");
                    id = Integer.parseInt(scanner.nextLine());
                    if (taskManager.checkIdInEpic(id)) {
                        taskManager.printSubtaskByIdEpic(id);
                    } else {
                        System.out.println("Такого Эпика нет!");
                    }
                    break;
                case 9:
                    taskManager.clearTask();
                    break;
                case 10:
                    taskManager.clearEpic();
                    break;
                case 11:
                    taskManager.clearSubtask();
                    break;
                case 12:
                    System.out.print("Введите ID: ");
                    id = Integer.parseInt(scanner.nextLine());
                    taskManager.deleteById(id);
                    break;
                case 13:
                    System.out.print("Введите номер задачи статус которой хотите обновить: ");
                    id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Вы хотите обновить имя, описание задачи и продолжительность задачи?: ");
                    System.out.println("1.Да \n2.Нет");
                    int replace = Integer.parseInt(scanner.nextLine());
                    if (replace == 1) {
                        if (taskManager.checkIdInTask(id)) {
                            name = printName(scanner, TypeTask.TASK);
                            description = printDescription(scanner);
                            newDuration = printDuration(scanner);
                            printMenuStatus();
                            numberOfProgress = Integer.parseInt(scanner.nextLine());
                            progress = setProgress(numberOfProgress);
                            if (progress != null) {
                                task = new Task(name, description, progress);
                                task.setId(id);
                                task.setDuration(newDuration);
                                taskManager.updateTask(task);
                            }
                        } else if (taskManager.checkIdInEpic(id)) {
                            name = printName(scanner, TypeTask.EPIC);
                            description = printDescription(scanner);
                            Epic epic = new Epic(name, description);
                            epic.setId(id);
                            taskManager.updateEpic(epic);
                        } else if (taskManager.checkIdSubtask(id)) {
                            name = printName(scanner, TypeTask.SUBTASK);
                            description = printDescription(scanner);
                            newDuration = printDuration(scanner);
                            printMenuStatus();
                            numberOfProgress = Integer.parseInt(scanner.nextLine());
                            progress = setProgress(numberOfProgress);
                            Subtask oldSubtask = (Subtask) taskManager.getSubtasks().get(id);
                            if (progress != null) {
                                Subtask subtask = new Subtask(name, description, oldSubtask.getIdEpic(), progress);
                                subtask.setId(oldSubtask.getId());
                                subtask.setDuration(newDuration);
                                taskManager.updateSubtask(subtask);
                            }
                        } else {
                            System.out.println("Такого действия нет!");
                        }
                    } else if (replace == 2) {
                        if (taskManager.checkIdInTask(id)) {
                            printMenuStatus();
                            numberOfProgress = Integer.parseInt(scanner.nextLine());
                            progress = setProgress(numberOfProgress);
                            if (progress != null) {
                                task = getTask(id, Action.SETPROGRESS);
                                if (task != null) {
                                    task.setProgress(progress);
                                    taskManager.updateTask(task);
                                }
                            }
                        } else if (taskManager.checkIdSubtask(id)) {
                            printMenuStatus();
                            numberOfProgress = Integer.parseInt(scanner.nextLine());
                            progress = setProgress(numberOfProgress);
                            if (progress != null) {
                                Subtask subtask = (Subtask) getTask(id, Action.SETPROGRESS);
                                if (subtask != null) {
                                    subtask.setProgress(progress);
                                    taskManager.updateSubtask(subtask);
                                }
                            }
                        } else {
                            System.out.println("Невозможно изменить статус у данного номера!");
                        }
                    } else {
                        System.out.println("Такого действия нет!");
                    }
                    break;
                case 14:
                    if (!taskManager.getHistory().isEmpty()) {
                        System.out.println("История просмотров: ");
                        for (Task taskFromHistory : taskManager.getHistory()) {
                            taskFromHistory.print();
                        }
                    } else {
                        System.out.println("История просмотров пуста!");
                    }
                    break;
                case 15:
                    System.out.print("Введите номер истории которую хотите удалить: ");
                    id = Integer.parseInt(scanner.nextLine());
                    historyManager.remove(id);
                    break;
                case 16:
                    System.out.print("Введите номер задачи, время начала которой хотите указать: ");
                    id = Integer.parseInt(scanner.nextLine());
                    task = getTask(id, Action.SETTIME);
                    if (task != null) {
                        localDateTime = printDateTime(scanner);
                        task.setStartTime(localDateTime);
                        if (taskManager.checkIdInTask(id)) {
                            taskManager.updateTask(task);
                        } else {
                            taskManager.updateSubtask((Subtask) task);
                        }
                        taskManager.addToTreeSet(task);
                    }
                    break;
                case 17:
                    System.out.print("Введите номер задачи, длительность которой хотите указать: ");
                    id = Integer.parseInt(scanner.nextLine());
                    task = getTask(id, Action.SETDURATUION);
                    if (task != null) {
                        newDuration = printDuration(scanner);
                        task.setDuration(newDuration);
                        if (taskManager.checkIdInTask(id)) {
                            taskManager.updateTask(task);
                        } else {
                            taskManager.updateSubtask((Subtask) task);
                        }
                    }
                    break;
                case 18:
                    for (Task taskInTree : taskManager.getPrioritizedTasks()) {
                        taskInTree.print();
                    }
                    break;
                case 19:
                    System.out.println("Пересекаются ли задачи: " + taskManager.intersectionTasks());
                    break;
                case 20:
                    return;
                default:
                    System.out.println("Такого действия нет!");
            }
        }
    }

    public static void printMenu() {
        System.out.println("--------------------");
        System.out.println("Выберите действие: ");
        System.out.println("1. Добавить Эпик");
        System.out.println("2. Добавить задачу");
        System.out.println("3. Добавить подзадачу Эпика");
        System.out.println("4. Распечатать все задачи");
        System.out.println("5. Распечатать все Эпики");
        System.out.println("6. Распечатать все подзадачи");
        System.out.println("7. Распечатать задачу/Эпик/подзадачу по номеру");
        System.out.println("8. Распечатать все подзадачи по номеру Эпика");
        System.out.println("9. Удалить все задачи");
        System.out.println("10. Удалить все Эпики");
        System.out.println("11. Удалить все подзадачи");
        System.out.println("12. Удалить задачу/Эпик/подзадачу по номеру");
        System.out.println("13. Обновить статус");
        System.out.println("14. Получить историю");
        System.out.println("15. Удалить историю по номеру");
        System.out.println("16. Назначить время начала задачи");
        System.out.println("17. Указать длительность задачи");
        System.out.println("18. Вывести список приоритетных задач");
        System.out.println("19. Проверить на пересечение задачи");
        System.out.println("20. Выход");
        System.out.println("--------------------");
    }

    public static Task getTask(int id, Action action) {
        Task task = null;
        if (taskManager.checkIdInTask(id)) {
            task = taskManager.getTasks().stream().filter(task1 -> task1.getId() == id).findFirst().orElse(null);
            /*for (Task taskFromList : taskList) {
                if (taskFromList.getId() == id) {
                    task = taskFromList;
                    break;
                }
            }*/
        } else if (taskManager.checkIdSubtask(id)) {
            task = taskManager.getSubtasks().stream().filter(task1 -> task1.getId() == id).findFirst().orElse(null);
            /*List<Task> subTaskList = taskManager.getSubtasks();
            for (Task subtaskFromList : subTaskList) {
                if (subtaskFromList.getId() == id) {
                    task = subtaskFromList;
                    break;
                }
            }*/
        }
        if (taskManager.checkIdInEpic(id)) {
            System.out.println("Невозможно изменить данные у данной задачи!");
        }
        if (action.equals(Action.SETDURATUION) && (task.getStartTime() == null)) {
            System.out.println("Необходимо сперва указать время начала задачи");
            return null;
        }
        return task;
    }

    public static void printMenuStatus() {
        System.out.println("Введите номер статуса задачи: ");
        System.out.println("1. NEW");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. DONE");
    }

    public static Duration printDuration(Scanner scanner) {
        System.out.print("Введите новую продолжительность задачи: ");
        return Duration.ofMinutes(Integer.parseInt(scanner.nextLine()));
    }

    public static String printName(Scanner scanner, TypeTask typeTask) {
        switch (typeTask) {
            case EPIC -> {
                System.out.print("Введите новое имя Эпика: ");
                return scanner.nextLine();
            }
            case SUBTASK -> {
                System.out.print("Введите новое имя подзадачи: ");
                return scanner.nextLine();
            }
            case TASK -> {
                System.out.print("Введите новое имя задачи: ");
                return scanner.nextLine();
            }
            default -> {
                return "";
            }
        }
    }

    public static String printDescription(Scanner scanner) {
        System.out.print("Введите новое описание: ");
        return scanner.nextLine();
    }

    public static LocalDateTime printDateTime(Scanner scanner) {
        System.out.print("Введите дату и время начала задачи в формате \"ДД.ММ.ГГГГ ЧЧ:мм\": ");
        String dateTime = scanner.nextLine();
        return LocalDateTime.parse(dateTime, FileBackedTaskManager.dateTimeFormatter);
    }

    public static Progress setProgress(int numberOfProgress) {
        switch (numberOfProgress) {
            case 1:
                return Progress.NEW;
            case 2:
                return Progress.IN_PROGRESS;
            case 3:
                return Progress.DONE;
            default:
                System.out.println("Вы выбрали неверный статус задачи.");
                return null;
        }
    }

    private static void additionEpic(TaskManager taskManager) throws FileBackedTaskManager.ManagerSaveException {
        Epic epic = new Epic("1длоотываа", "1dkiu5d");
        if (taskManager.addEpic(epic) > 0) {
            System.out.println("Добавлена задача под номером: " + epic.getId());
        }
        epic = new Epic("2slkdnfbjb", "2kjsbdf");
        if (taskManager.addEpic(epic) > 0) {
            System.out.println("Добавлена задача под номером: " + epic.getId());
        }
        epic = new Epic("3sdkfjbrf", "3sdkjfb");
        if (taskManager.addEpic(epic) > 0) {
            System.out.println("Добавлена задача под номером: " + epic.getId());
        }
        epic = new Epic("4dfmg5", "4kjsdkfb");
        if (taskManager.addEpic(epic) > 0) {
            System.out.println("Добавлена задача под номером: " + epic.getId());
        }
    }

    private static void additionTask(TaskManager taskManager) throws FileBackedTaskManager.ManagerSaveException {
        Task task = new Task("5sdfkjb45", "5kjsdfgjfb", Progress.NEW);
        taskManager.addTask(task);
        task = new Task("6afjhbsdf", "ksdfu4", Progress.IN_PROGRESS);
        taskManager.addTask(task);
        task = new Task("7sdkjbfbi4", "7sdfjbldsf", Progress.DONE);
        taskManager.addTask(task);
    }

    private static void additionSubtask(TaskManager taskManager) throws FileBackedTaskManager.ManagerSaveException {
        Subtask subtask = new Subtask("8sdlknfb", "8aslkfb", 2, Progress.NEW);
        taskManager.addSubtask(subtask);
        subtask = new Subtask("9sdlknfb", "9aslkfb", 3, Progress.NEW);
        taskManager.addSubtask(subtask);
        subtask = new Subtask("10sdlknfb", "10aslkfb", 3, Progress.IN_PROGRESS);
        taskManager.addSubtask(subtask);
        subtask = new Subtask("11sdlknfb", "11aslkfb", 3, Progress.DONE);
        taskManager.addSubtask(subtask);
    }
}
