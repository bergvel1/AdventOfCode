package util;

import java.io.OutputStream;
import java.io.PrintStream;

public class Runner {

    public static void runAll(int year, int upToDay) {
        for (int i = 1; i <= upToDay; i++) {
            run(year, i);
        }
    }

    private static void run(int year, int day) {
        AdventOfCode challenge = DayLoader.getClassForDay(year, day);
        if (challenge == null) throw new RuntimeException("Unable to load class/input");
        //Timer.startTimer();
        System.out.println("<===== Advent of Code challenge output for Year: " + year + " - Day " + day + " =====>");
        challenge.run();
        //System.out.println(timer.endTimer());
    }

    public static void main(String[] args) {
        if (System.getenv("AOC_VERBOSE").equals("false")) {
            // disable stderr console output
            System.setErr(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) {
                }
            }));
        }

        run(2018, 6);
        //runAll(2018, 2);
    }
}