package pl.coderslab;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TaskManager {

    static final String FILE = "tasks.csv";
    static final String[] OPTIONS = {"add", "remove", "list", "exit"};
    static String[][] tasks;

    public static void main(String[] args) {
        tasks = loadData(FILE);
        printList(OPTIONS);

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = scanner.nextLine().trim().toLowerCase();

                switch (input) {
                    case "add":
                        addTask(scanner);
                        break;

                    case "remove":
                        removeTask(tasks, scanner);
                        break;

                    case "list":
                        printTab(tasks);
                        break;

                    case "exit":
                        saveTab(FILE, tasks);
                        System.out.println(ConsoleColors.RED + "Bye, bye.");
                        return;

                    default:
                        System.out.println("Please select a correct option.");
                        break;
                }

                printList(OPTIONS);
            }
        }
    }

    public static String[][] loadData(String fileName) {
        Path path = Paths.get(fileName);

        if (!Files.exists(path)) {
            System.out.println("File does not exist: " + fileName);
            System.exit(1);
        }

        try {
            List<String> lines = Files.readAllLines(path);

            if (lines.isEmpty()) {
                System.out.println("File is empty.");
                return new String[0][0];
            }

            int columns = lines.get(0).split(",").length;
            String[][] data = new String[lines.size()][columns];

            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",", -1); // -1 to keep empty fields
                System.arraycopy(parts, 0, data[i], 0, columns);
            }

            return data;

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return new String[0][0];
        }
    }

    public static void printList(String[] options) {
        StringBuilder output = new StringBuilder();
        output.append(ConsoleColors.BLUE)
                .append("Please select an option: ")
                .append(ConsoleColors.RESET)
                .append(System.lineSeparator());

        for (String option : options) {
            output.append(option).append(System.lineSeparator());
        }

        System.out.print(output);
    }

    public static void printTab(String[][] tab) {
        for (int i = 0; i < tab.length; i++) {
            System.out.print(i + " : ");
            for (int j = 0; j < tab[i].length; j++) {
                System.out.print(tab[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void addTask(Scanner scanner) {
        System.out.println("Please add task description:");
        String description = scanner.nextLine().trim();

        System.out.println("Please add task due date:");
        String dueDate = scanner.nextLine().trim();

        String isImportant;
        while (true) {
            System.out.println("Is your task important? (true/false):");
            isImportant = scanner.nextLine().trim().toLowerCase();
            if (isImportant.equals("true") || isImportant.equals("false")) {
                break;
            }
            System.out.println("Invalid input. Please enter 'true' or 'false'.");
        }

        String[] newTask = { description, dueDate, isImportant };
        tasks = Arrays.copyOf(tasks, tasks.length + 1);
        tasks[tasks.length - 1] = newTask;

        System.out.println("Task added successfully.");
    }

    public static boolean isZero(String input) {
        if (NumberUtils.isParsable(input)) {
            return Integer.parseInt(input) >= 0;
        }
        return false;
    }

    public static void removeTask(String[][] tab, Scanner scanner) {
        System.out.println("Please select number to remove:");

        String input = scanner.nextLine();
        while (!isZero(input)) {
            System.out.println("Incorrect argument passed. Please give number greater or equal 0.");
            input = scanner.nextLine();
        }

        int index = Integer.parseInt(input);

        if (index >= tab.length) {
            System.out.println("Element does not exist in the list.");
            return;
        }

        try {
            tasks = ArrayUtils.remove(tab, index);
            System.out.println("Value was successfully deleted.");
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Error while removing: Index out of bounds.");
        }
    }

    public static void saveTab(String fileName, String[][] tab) {
        Path path = Paths.get(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (int i = 0; i < tab.length; i++) {
                String line = String.join("\t", tab[i]);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("An error occurred while saving the file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}