package Advent2018;

import util.AdventOfCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 extends AdventOfCode {
    // minimum dimensions needed to accommodate all claims
    private int maxX = 0;
    private int maxY = 0;
    private HashMap<Integer, Claim> claimMap;
    private ArrayList<ArrayList<Character>> grid;

    public Day3(List<String> input) {
        super(input);
        part1Description = "Area of overlapping claims (square inches): ";
        part2Description = "ID of non-overlapping claim: ";

        parse();

        // check that claim IDs do not conflict with the value used to mark contested cells (i.e. Character.MAX_VALUE)
        if (claimMap.size() >= Character.MAX_VALUE) {
            System.err.println("Cannot process more than 65535 claims! Exiting...");
            System.exit(1);
        }
    }

    @Override
    public Object part1() {
        initGrid();
        return fillGrid();
    }

    @Override
    public Object part2() {
        initGrid();
        fillGrid();

        for (Claim c : claimMap.values()) {
            if (!c.contested) return c.id;
        }

        System.err.println("Could not find non-overlapping claim!");
        return -1;
    }

    @Override
    public void parse() {
        claimMap = new HashMap<>();
        Pattern pattern = Pattern.compile("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)");

        for (String each : input) {
            Matcher m = pattern.matcher(each);

            // parse claim if properly formatted
            if (m.matches()) {
                int id = Integer.parseInt(m.group(1));
                int x = Integer.parseInt(m.group(2));
                int y = Integer.parseInt(m.group(3));
                int width = Integer.parseInt(m.group(4));
                int height = Integer.parseInt(m.group(5));

                int boundX = x + width;
                int boundY = y + height;

                if (boundX > maxX) maxX = boundX;
                if (boundY > maxY) maxY = boundY;

                Claim c = new Claim(id, x, y, width, height);
                claimMap.put(id, c);
            } else {
                System.err.println("Malformed claim: " + each);
            }
        }
    }

    private void initGrid() {
        grid = new ArrayList<>();
        Character[] emptyRow = new Character[maxX];
        Arrays.fill(emptyRow, null);

        for (int rowIdx = 0; rowIdx < maxY; rowIdx++) {
            grid.add(new ArrayList<>(Arrays.asList(emptyRow)));
        }
    }

    // marks each claim on the grid, returning the number cells where claims overlap
    private int fillGrid() {
        int overlapCount = 0;

        for (Claim c : claimMap.values()) {
            overlapCount += markClaim(c);
        }

        return overlapCount;
    }

    // marks each grid cell occupied by a given claim and updates contested status of claims
    // returns the number of newly-contested cells
    private int markClaim(Claim c) {
        int overlapCount = 0;

        for (int y = c.y; y < c.y + c.height; y++) {
            ArrayList<Character> row = grid.get(y);

            for (int x = c.x; x < c.x + c.width; x++) {
                Character curr = row.get(x);

                if (curr == null) {
                    // free cell: mark with claim id
                    row.set(x, (char) c.id);
                } else {
                    // occupied cell: mark c as contested
                    c.contested = true;

                    // if cell was previously uncontested
                    if (curr != Character.MAX_VALUE) {
                        claimMap.get((int) curr).contested = true;

                        // contested cells marked with Character.MAX_VALUE
                        row.set(x, Character.MAX_VALUE);
                        overlapCount++;
                    }
                }
            }
        }

        return overlapCount;
    }

    private class Claim {
        int id, x, y, width, height;
        boolean contested; // true if this claim overlaps another

        Claim(int id, int x, int y, int width, int height) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.contested = false;
        }
    }
}
