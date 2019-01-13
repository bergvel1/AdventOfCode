package util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DayLoader {

    private DayLoader() {}

    public static AdventOfCode getClassForDay(int year, int day) {
        if (year < 2015 || year > 2018 || day < 1 || day > 25) {
            throw new IllegalArgumentException("Invalid date for Advent of Code.");
        }
        try {
            String name = "Advent" + year + ".Day" + day;
            Constructor constructor = Class.forName(name).getConstructor(List.class);
            return (AdventOfCode)constructor.newInstance(FileIO.getAOCInputForDay(year, day, FileIO.SESSION_ID));

        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | InvocationTargetException |
                NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }

    }
}