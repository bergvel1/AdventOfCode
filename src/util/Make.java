package util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Make {

    private static void makeDay(int year, int day) {
        String className = "Day" + day;
        String file = "src/Advent" + year + "/" + className + ".java";
        if (!Files.exists(Paths.get(file))) {
            List<String> template = FileIO.getFileAsList("advent_template.txt");
            template = template.stream()
                    .map(x -> x.replace("Template", className))
                    .collect(Collectors.toList());
            FileIO.writeListToFile(template, file);
            System.out.println("Writing file: " + file);
        } else {
            System.out.println("File: " + file + " already exists.");
        }
    }

    protected static void makeAll(int year) {
        for (int i = 0; i < 26; i++) {
            makeDay(year, i);
        }
    }
}