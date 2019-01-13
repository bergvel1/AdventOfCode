package util;

public class Runner {

    public static void runAll(int year, int upToDay) {
        for (int i = 1; i <= upToDay; i++) {
            run(year, i);
        }
    }

    public static void run(int year, int day) {
        AdventOfCode challenge = DayLoader.getClassForDay(year, day);
        if (challenge == null) throw new RuntimeException("Unable to load class/input");
        //Timer.startTimer();
        System.out.println("<===== Advent of Code challenge output for Year: " + year + " - Day " + day + " =====>");
        challenge.run();
        //System.out.println(timer.endTimer());
    }

    public static void main(String[] args) {
        runAll(2018, 1);
    }
}