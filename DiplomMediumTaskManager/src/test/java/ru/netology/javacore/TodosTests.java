package ru.netology.javacore;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.Assert.*;

public class TodosTests {
    private static long suiteStartTime;
    private long testStartTime;

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running All Tests");
        suiteStartTime = System.nanoTime();
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("All Tests completed in time: " + (System.nanoTime() - suiteStartTime));
    }

    @BeforeEach
    public void initTest() {
        System.out.println("Starting new test");
        testStartTime = System.nanoTime();
    }

    @AfterEach
    public void finalizeTest() {
        System.out.println("Test complete in time: " + (System.nanoTime() - testStartTime));
    }

    @org.junit.jupiter.api.Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"101", "102", "103"})
    public void addTaskTest(String argument) {
        Todos todos = new Todos();
        todos.addTask(argument);
        assertTrue(todos.getTaskList().get(0).contains("1"));
    }

    @Test
    public void removeTaskTest() {
        Todos todos = new Todos();
        todos.addTask("Very hard task!");
        todos.addTask("Very easy task!");
        todos.addTask("Medium difficulty task!");
        todos.removeTask("Very easy task!");
        assertFalse(todos.getTaskList().contains("Very easy task!"));
        todos.removeTask("Very hard task!");
        assertEquals(1, todos.getTaskList().size());
        todos.removeTask("Medium difficulty task!");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> todos.getTaskList().get(2));
    }

    @ParameterizedTest
    @MethodSource("methodSource")
    public void getAllTasksTest(String a, String b, String c, String expectedResult) {
        Todos todos = new Todos();
        todos.addTask(a);
        todos.addTask(b);
        todos.addTask(c);
        String result = todos.getAllTasks();
        Assertions.assertEquals(expectedResult, result);
    }

    public static Stream<Arguments> methodSource() {
        return Stream.of(
                Arguments.of("2", "1", "3", "1 2 3"),
                Arguments.of("Bella", "Boris", "Alena", "Alena Bella Boris")
        );
    }
}