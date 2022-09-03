package ru.netology.javacore;

import java.util.*;
import java.util.stream.Collectors;

public class Todos {
    private List<String> taskList = new ArrayList<>();

    public void addTask(String task) {
        taskList.add(task);
        System.out.println("Задача `" + task + "` успешно добавлена!");
    }

    public void removeTask(String task) {
        if (!taskList.contains(task)) {
            System.out.println("Менеджер задач не содержит задачу  `" + task + "`, удаление не произведено!");
        } else {
            taskList.remove(task);
            System.out.println("Задача `" + task + "` успешно удалена!");
        }
    }

    public String getAllTasks() {
        List<String> sortedTaskList = taskList.stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < sortedTaskList.size(); i++) {
//            sb.append(sortedTaskList.get(i) + " ");
//        }
//        return String.valueOf(sb);
        return String.join(" ", sortedTaskList);
    }

    public List<String> getTaskList() {
        return taskList;
    }
}