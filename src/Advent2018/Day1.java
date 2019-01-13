package Advent2018;

import util.AdventOfCode;

import java.util.HashMap;
import java.util.List;

public class Day1 extends AdventOfCode {
    public Day1(List<String> input) {
        super(input);
        part1Description = "Final frequency: ";
        part2Description = "First frequency reached twice: ";
    }

    @Override
    public Object part1() {
        int frequency = 0;

        for (String each : input) {
            frequency += Integer.parseInt(each);
        }
        return frequency;
    }

    @Override
    public Object part2() {
        Integer frequency = 0;
        int iterations = 0;
        HashMap<Integer, Integer> freqMap = new HashMap<>(); // tracks visited frequencies

        while(true) {
            for (String each : input) {
                Integer timesSeen = freqMap.get(frequency);
                if (timesSeen == null) {
                    freqMap.put(frequency, new Integer(1));
                } else {
                    return frequency;
                }

                frequency += Integer.parseInt(each);
            }
            System.err.println("No duplicate found on interation " + (++iterations));
        }
    }

    @Override
    public void parse() {

    }
}
