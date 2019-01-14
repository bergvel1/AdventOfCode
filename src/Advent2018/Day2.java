package Advent2018;

import util.AdventOfCode;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day2 extends AdventOfCode {
    public Day2(List<String> input) {
        super(input);
        part1Description = "Checksum: ";
        part2Description = "Common letters: ";
    }

    @Override
    public Object part1() {
        int letterDoubles = 0;
        int letterTriples = 0;

        // sort each string, and then check for adjacent identical characters via regex
        for (String each : input) {
            char[] ar = each.toCharArray();
            Arrays.sort(ar);
            String sorted = String.valueOf(ar);

            Pattern p2 = Pattern.compile("((\\w)(?!\\2)|^)((\\w)\\4)(?!\\4)");
            Matcher m2 = p2.matcher(sorted);

            Pattern p3 = Pattern.compile("((\\w)(?!\\2)|^)((\\w)\\4\\4)(?!\\4)");
            Matcher m3 = p3.matcher(sorted);

            if (m2.find()) {
                letterDoubles++;
                System.err.println("Double letter in " + sorted);
            }

            if (m3.find()) {
                letterTriples++;
                System.err.println("Triple letter in " + sorted);
            }
        }

        return letterDoubles * letterTriples;
    }

    @Override
    public Object part2() {
        // iteratively compare each string pair
        for (int i = 0; i < input.size(); i++) {
            for (int j = i + 1; j < input.size(); j++) {
                String str1 = input.get(i);
                String str2 = input.get(j);

                // only compare equal-length strings
                if (str1.length() != str2.length()) {
                    continue;
                }

                int diff = 0;
                int idx = 0;
                int lastDiff = -1;

                // count indices where characters differ between str1 and str2
                while ((diff <= 1) && (idx < str1.length())) {
                    if (str1.charAt(idx) != str2.charAt(idx)) {
                        diff++;
                        lastDiff = idx;
                    }

                    idx++;
                }

                if (diff <= 1) {
                    return str1.substring(0, lastDiff) + str1.substring(lastDiff + 1);
                }
            }
        }

        return "ERR: Failed to find string pair!";
    }

    @Override
    public void parse() {

    }
}
