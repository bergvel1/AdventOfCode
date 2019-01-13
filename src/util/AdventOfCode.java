package util;

import java.util.List;

public abstract class AdventOfCode {

    protected List<String> input;

    public AdventOfCode(List<String> input) {
        this.input = input;
        parse();
    }

    public abstract Object part1();
    public abstract Object part2();
    public abstract void parse();

    public String part1Description;
    public String part2Description;

    public void run() {
        System.out.println(part1Description + part1().toString());
        System.out.println(part2Description + part2().toString());
    }
}