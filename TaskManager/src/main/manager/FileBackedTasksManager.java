package main.manager;

import main.exceptions.ManagerSaveException;
import main.task.*;
import main.additional.StaticMethods;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public final static String FILE_BACKED_NAME = "src\\main\\resources\\save.txt";

    public void save() throws ManagerSaveException {
        StringBuilder sb = new StringBuilder();
        List<Task> taskList = new ArrayList<>(getAllTaskList());
        for (Task task : taskList) {
            sb.append(taskToString(task));
            sb.append("\n");
        }
        sb.append("\n");
        sb.append(historyToString(getHistoryManager()));
        try (FileWriter file = new FileWriter(FILE_BACKED_NAME)) {
            file.write(String.valueOf(sb));
            file.flush();
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось перезаписать файл: " + FILE_BACKED_NAME + ".");
        }
    }

    public static String historyToString(HistoryManager manager) {
        List<Task> historyList = manager.getHistory();
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < historyList.size(); i++) {
                sb.append(historyList.get(i).getTaskId());
                sb.append(",");
            }
            String result = String.valueOf(sb);
            return (result == null || result.length() == 0) ? null : result.substring(0, result.length() - 1);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> historyList = new ArrayList<>();
        try {
            String[] valueContents = value.split(",");
            for (int i = 0; i < valueContents.length; i++) {
                if (!StaticMethods.checkInt(valueContents[i])) {
                    System.out.println("данный элемент строки не может быть преобразован в taskId для списка истории: " + valueContents[i]);
                    return null;
                }
                historyList.add(Integer.parseInt(valueContents[i]));
            }
            return historyList;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static TaskManager restoreTaskManagerFromLists(List<Task> taskList, List<Integer> historyList) {
        TaskManager fileBackedTasksManager = Managers.getFileBackedTasksManager();
        try {
            for (int i = 0; i < taskList.size(); i++) {
                fileBackedTasksManager.createTask(taskList.get(i));
            }
            for (int i = 0; i < historyList.size(); i++) {
                fileBackedTasksManager.getById(historyList.get(historyList.size() - i - 1));
            }
            return fileBackedTasksManager;
        } catch (NullPointerException e) {
            return fileBackedTasksManager;
        }
    }

    public static TaskManager loadFromDefaultFile() {
        return loadFromFile(FILE_BACKED_NAME);
    }

    public static TaskManager loadFromFile(String fileName) {
        if (fileName == null || fileName.equals("")) fileName = FILE_BACKED_NAME;
        String fileContents = "";
        try {
            fileContents = Files.readString(Path.of(fileName));
        } catch (IOException e) {
            System.out.println("Возможно, файл не находится в нужной директории. Проверяемый путь: " + fileName);
            return null;
        }
        String[] valueContents = fileContents.split("\n\n");
        List<Task> taskList = tasksFromString(valueContents[0]);
        List<Integer> historyList = historyFromString(valueContents[1]);
        return restoreTaskManagerFromLists(taskList, historyList);
    }

    public static String taskToString(Task task) {
//        id,type,name,status,description,duration,startTime,epic
        StringBuilder sb = new StringBuilder();
        sb.append(task.getTaskId());
        sb.append(",");
        sb.append(task.getType());
        sb.append(",");
        sb.append(task.getTitle());
        sb.append(",");
        sb.append(task.getStatus());
        sb.append(",");
        sb.append(task.getDescription());
        sb.append(",");
        sb.append(task.getDuration());
        sb.append(",");
        sb.append(task.getStartTime());
        sb.append(",");
        if (task.getType().equals(TaskType.SUB_TASK)) {
            sb.append(((SubTask) task).getEpicId());
        }
        return String.valueOf(sb);
    }

    public static List<Task> tasksFromString(String tasksInString) {
        String[] valueContents = tasksInString.split("\n");
        String[] subTaskMovingToEnd = new String[valueContents.length];
        int subTaskCount = 0;
        int nextNotSubTaskIndex = 0;
        for (int i = 0; i < valueContents.length; i++) {
            int first = valueContents[i].indexOf(",");
            int second = valueContents[i].indexOf(",", valueContents[i].indexOf(",") + 1);
            String taskType = valueContents[i].substring(first + 1, second).toUpperCase();
            if (taskType.equals("SUB_TASK")) {
                subTaskCount++;
                subTaskMovingToEnd[valueContents.length - subTaskCount] = valueContents[i];
                continue;
            }
            subTaskMovingToEnd[nextNotSubTaskIndex] = valueContents[i];
            nextNotSubTaskIndex++;
        }
        List<Task> taskList = new ArrayList<>();
        for (int i = 0; i < subTaskMovingToEnd.length; i++) {
            taskList.add(taskFromString(subTaskMovingToEnd[i]));
        }
        return taskList;
    }

    public static Task taskFromString(String value) {
        try {
            String[] valueContents = value.split(",");
            if (!(valueContents.length == 7 || valueContents.length == 8)) {
                System.out.println("в строке недостаточно данных для создания Task");
                return null;
            }

            if (!StaticMethods.checkInt(valueContents[0])) {
                System.out.println("taskId должен быть целым положительным числом, считано " + valueContents[0]);
                return null;
            }
            int taskId = Integer.parseInt(valueContents[0]);

            TaskType type = TaskType.TASK;
            switch (valueContents[1].toUpperCase()) {
                case "TASK":
                    break;
                case "SUB_TASK":
                    type = TaskType.SUB_TASK;
                    break;
                case "EPIC":
                    type = TaskType.EPIC;
                    break;
                default:
                    System.out.println("не могу распознать тип Task");
                    return null;
            }

            String title = valueContents[2];

            TaskStatus status = TaskStatus.NEW;
            switch (valueContents[3].toUpperCase()) {
                case "NEW":
                    break;
                case "IN_PROGRESS":
                    status = TaskStatus.IN_PROGRESS;
                    break;
                case "DONE":
                    status = TaskStatus.DONE;
                    break;
                default:
                    System.out.println("не могу распознать статус Task");
                    return null;
            }

            String description = valueContents[4];

            if (!StaticMethods.checkInt(valueContents[5])) {
                System.out.println("duration должен быть целым положительным числом, считано " + valueContents[5]);
                return null;
            }
            int duration = Integer.parseInt(valueContents[5]);

            if (valueContents[6].length() < 16) {
                System.out.println("startTime должен быть вида 1986-02-01T23:30, допустимо добавить секунды, считать " +
                        "время со строки \"" + valueContents[6] + "\" не удалось");
                return null;
            }
            if (!StaticMethods.checkInt(valueContents[6].substring(0, 4))) {
                System.out.println("year должен быть целым положительным числом, считано " + valueContents[6].substring(0, 4));
                return null;
            }
            if (!StaticMethods.checkInt(valueContents[6].substring(5, 7))) {
                System.out.println("month должен быть целым положительным числом, считано " + valueContents[6].substring(5, 7));
                return null;
            }
            if (!StaticMethods.checkInt(valueContents[6].substring(8, 10))) {
                System.out.println("day должен быть целым положительным числом, считано " + valueContents[6].substring(8, 10));
                return null;
            }
            if (!StaticMethods.checkInt(valueContents[6].substring(11, 13))) {
                System.out.println("hour должен быть целым положительным числом, считано " + valueContents[6].substring(11, 13));
                return null;
            }
            if (!StaticMethods.checkInt(valueContents[6].substring(14, 16))) {
                System.out.println("minute должен быть целым положительным числом, считано " + valueContents[6].substring(14, 16));
                return null;
            }
            if (valueContents[6].length() > 16) {
                if (!StaticMethods.checkInt(valueContents[6].substring(17, valueContents[6].length()))) {
                    System.out.println("sec должен быть целым положительным числом, считано " + valueContents[6].substring(17, valueContents[6].length()));
                    return null;
                }
            }
            int year = Integer.parseInt(valueContents[6].substring(0, 4));
            int month = Integer.parseInt(valueContents[6].substring(5, 7));
            int day = Integer.parseInt(valueContents[6].substring(8, 10));
            int hour = Integer.parseInt(valueContents[6].substring(11, 13));
            int minute = Integer.parseInt(valueContents[6].substring(14, 16));
            int sec = (valueContents[6].length() > 16) ?
                    Integer.parseInt(valueContents[6].substring(17, valueContents[6].length())) : 0;

            LocalDateTime startTime = (sec == 0) ?
                    LocalDateTime.of(year, month, day, hour, minute) : LocalDateTime.of(year, month, day, hour, minute, sec);

            if (valueContents.length == 8) {
                if (!type.equals(TaskType.SUB_TASK)) {
                    System.out.println("передано данных эквивалентно данным SUB_TASK, при этом тип распознан как " + type);
                    return null;
                }
                if (!StaticMethods.checkInt(valueContents[7])) {
                    System.out.println("epicId должен быть целым положительным числом, считано " + valueContents[7]);
                    return null;
                }
                int epicId = Integer.parseInt(valueContents[7]);
                if (!Task.allTask.containsKey(epicId) || !Task.allTask.get(epicId).getType().equals(TaskType.EPIC)) {
                    System.out.println("до создания SUB_TASK нужен EPIC, EPIC по данному id не найден");
                    return null;
                }
                Task subTask = new SubTask(title, description, taskId, status, type, duration, startTime, epicId);
                return subTask;
            }

            if (type.equals(TaskType.EPIC)) {
                Task epic = new Epic(title, description, taskId, status, type, duration, startTime);
                return epic;
            }

            if (type.equals(TaskType.TASK)) {
                Task task = new Task(title, description, taskId, status, type, duration, startTime);
                return task;
            } else
                System.out.println("этот сценарий не должен был случаться, но ты как-то сюда попал, а вот Task не создан...");
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public Map<Integer, Task> getTaskMap() {
        return super.getTaskMap();
    }

    @Override
    public Map<Integer, Task> getSubTaskMap() {
        return super.getSubTaskMap();
    }

    @Override
    public Map<Integer, Task> getEpicMap() {
        return super.getEpicMap();
    }

    @Override
    public Collection<Task> getAllTaskList() {
        return super.getAllTaskList();
    }

    @Override
    public List<Task> getTaskList() {
        return super.getTaskList();
    }

    @Override
    public List<Task> getSubTaskList() {
        return super.getSubTaskList();
    }

    @Override
    public List<Task> getEpicList() {
        return super.getEpicList();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return super.getHistoryManager();
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public Task getById(int taskId) {
        Task task = super.getById(taskId);
        save();
        return task;
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = super.getTaskById(taskId);
        save();
        return task;
    }

    @Override
    public Task getSubTaskById(int taskId) {
        Task task = super.getSubTaskById(taskId);
        save();
        return task;
    }

    @Override
    public Task getEpicById(int taskId) {
        Task task = super.getEpicById(taskId);
        save();
        return task;
    }

    @Override
    public boolean createTask(Task task) {
        boolean result = super.createTask(task);
        save();
        return result;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public boolean deleteAnyTaskById(int taskId) {
        boolean result = super.deleteAnyTaskById(taskId);
        save();
        return result;
    }

    @Override
    public List<Task> getSubTaskListByEpic(Epic epic) {
        return super.getSubTaskListByEpic(epic);
    }
}