package Advent2018;

import util.AdventOfCode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day6 extends AdventOfCode {
    private final int TOTAL_DISTANCE_LIMIT = 10000; // for part 2
    // global variables go here
    private ArrayList<Region> regions;
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = 0;
    private int maxY = 0;
    private ArrayList<ArrayList<Cell>> grid;

    public Day6(List<String> input) {
        super(input);
        part1Description = "Area of largest non-infinite region: ";
        part2Description = "Number of locations with total distance less than " + TOTAL_DISTANCE_LIMIT + ": ";

        parse();
    }

    @Override
    public Object part1() {
        initGrid();
        growRegions();
        markFiniteRegions();
        Region r = getMaxFiniteRegion();
        return r.area;
    }

    @Override
    public Object part2() {
        initGrid();
        return calcGridTotalDistances();
    }

    @Override
    public void parse() {
        // parse input into global variables
        regions = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d+), (\\d+)");

        int currID = 0;

        for (String each : input) {
            Matcher m = pattern.matcher(each);

            // parse region string if properly formatted
            if (m.matches()) {
                int id = currID++;
                int x = Integer.parseInt(m.group(1));
                int y = Integer.parseInt(m.group(2));

                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;

                Region r = new Region(id, x, y);
                regions.add(r);
            } else {
                System.err.println("Malformed region string: " + each);
            }
        }
    }

    private void initGrid() {
        grid = new ArrayList<>();

        // initialize grid cells
        for (int rowIdx = 0; rowIdx <= maxY - minY; rowIdx++) {
            Cell[] newRow = new Cell[maxX - minX + 1];

            for (int colIdx = 0; colIdx <= maxX - minX; colIdx++) {
                newRow[colIdx] = new Cell(colIdx, rowIdx);
            }

            grid.add(new ArrayList<>(Arrays.asList(newRow)));
        }

        // mark region centers
        for (Region r : regions) {
            Cell c = grid.get(r.y - minY).get(r.x - minX);
            c.owner = r;
            c.ownerDistance = 0;
            c.border = false;
        }
    }

    // Mark cells belonging to each region by performing a BFS from each region center simultaneously
    private void growRegions() {
        Queue<Cell> growthQueue = new LinkedList<>();
        for (Region r : regions) {
            Cell c = grid.get(r.y - minY).get(r.x - minX);
            growthQueue.add(c);
        }

        while (growthQueue.peek() != null) {
            growStep(growthQueue);
        }
    }

    // attempts to grow cell at the head of the queue
    private void growStep(Queue<Cell> growthQueue) {
        Cell c = growthQueue.remove();
        Region owner = c.owner;

        // try to grow to adjacent cells
        Cell left = tryGrow(c.x - 1, c.y, owner);
        Cell right = tryGrow(c.x + 1, c.y, owner);
        Cell up = tryGrow(c.x, c.y - 1, owner);
        Cell down = tryGrow(c.x, c.y + 1, owner);

        if (left != null) growthQueue.add(left);
        if (right != null) growthQueue.add(right);
        if (up != null) growthQueue.add(up);
        if (down != null) growthQueue.add(down);
    }

    // if possible, claims the cell at the given coordinates for the given region
    // if cell is claimed, return it
    private Cell tryGrow(int targetX, int targetY, Region owner) {
        int actualX = targetX + minX;
        int actualY = targetY + minY;

        if (!cellInbounds(targetX, targetY)) {
            System.err.println("Tried to grow region out of bounds! id: " + owner.id + ", actual: (" + targetX + ", " + targetY + "), grid: (" + actualX + ", " + actualY + ")");
            return null;
        }

        Cell targetCell = grid.get(targetY).get(targetX);
        int distance = Math.abs(actualX - owner.x) + Math.abs(actualY - owner.y);

        if (targetCell.owner == null) {
            // free cell: claim it
            targetCell.owner = owner;
            targetCell.ownerDistance = distance;
            owner.area++;
            return targetCell;
        } else if (targetCell.owner.id != owner.id) {
            // cell belongs to other region: claim iff our region is closer to the cell
            if (targetCell.ownerDistance > distance) {
                targetCell.owner.area--;
                targetCell.owner = owner;
                targetCell.ownerDistance = distance;
                owner.area++;
                return targetCell;
            } else if (targetCell.ownerDistance == distance) {
                // mark cell as a border between regions
                if (!targetCell.border) {
                    targetCell.owner.area--;
                    targetCell.border = true;
                }
            }
        }

        return null;
    }

    private void markFiniteRegions() {
        ArrayList<Cell> firstRow = grid.get(0);
        ArrayList<Cell> lastRow = grid.get(maxY - minY);

        // first and last rows
        for (int colIdx = 0; colIdx <= maxX - minX; colIdx++) {
            Cell cFirst = firstRow.get(colIdx);
            Cell cLast = lastRow.get(colIdx);

            setCellRegionFiniteness(cFirst);
            setCellRegionFiniteness(cLast);
        }

        // first and last columns
        for (int rowIdx = 1; rowIdx < maxY - minY; rowIdx++) {
            ArrayList<Cell> row = grid.get(rowIdx);

            Cell cLeft = row.get(0);
            Cell cRight = row.get(maxX - minX);

            setCellRegionFiniteness(cLeft);
            setCellRegionFiniteness(cRight);
        }

    }

    private void setCellRegionFiniteness(Cell cFirst) {
        if (cFirst.owner != null) {
            cFirst.owner.finiteArea = false;
        }
    }

    private Region getMaxFiniteRegion() {
        int maxArea = 0;
        Region maxRegion = null;

        for (Region r : regions) {
            if (r.finiteArea) {
                if (r.area > maxArea) {
                    maxArea = r.area;
                    maxRegion = r;
                }
            }
        }

        return maxRegion;
    }

    // returns cells with total distance below limit
    private int calcGridTotalDistances() {
        int numCells = 0;

        for (int rowIdx = 0; rowIdx <= maxY - minY; rowIdx++) {
            ArrayList<Cell> row = grid.get(rowIdx);

            for (int colIdx = 0; colIdx <= maxX - minX; colIdx++) {
                Cell c = row.get(colIdx);
                if (calcCellTotalDistance(c)) numCells++;
            }
        }

        return numCells;
    }

    // returns true iff total distance for cell is under limit
    private boolean calcCellTotalDistance(Cell c) {
        int actualX = c.x + minX;
        int actualY = c.y + minY;

        c.totalDistance = 0;

        for (Region r : regions) {
            int distance = Math.abs(actualX - r.x) + Math.abs(actualY - r.y);
            c.totalDistance += distance;
        }

        return c.totalDistance < TOTAL_DISTANCE_LIMIT;
    }

    private boolean cellInbounds(int x, int y) {
        if (y < 0) return false;
        if (y >= grid.size()) return false;
        if (x < 0) return false;
        return x < grid.get(y).size();
    }

    private void printGrid(boolean part1) {
        System.out.println("minX: " + minX);
        System.out.println("maxX: " + maxX);
        System.out.println("minY: " + minY);
        System.out.println("maxY: " + maxY);
        System.out.flush();

        for (int rowIdx = 0; rowIdx <= maxY - minY; rowIdx++) {
            ArrayList<Cell> row = grid.get(rowIdx);

            for (int colIdx = 0; colIdx <= maxX - minX; colIdx++) {
                Cell c = row.get(colIdx);

                if (part1) printCell1(c);
                else printCell2(c);

                System.out.print(" ");
            }

            System.out.print('\n');
        }
    }

    // for part 1
    private void printCell1(Cell c) {
        if (c.owner == null) System.out.print('.');
        else if (c.border) System.out.print('+');
        else if (c.ownerDistance == 0) System.out.print('@'); // region center
        else {
            System.out.print((char) (c.owner.id + 65));
        }
    }

    // for part 2
    private void printCell2(Cell c) {
        if (c.totalDistance >= TOTAL_DISTANCE_LIMIT) System.out.print('.');
        else if (c.ownerDistance == 0) System.out.print((char) (c.owner.id + 65)); // region center
        else {
            System.out.print('#');
        }
    }

    private class Region {
        int id, x, y, area;
        boolean finiteArea;

        Region(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.area = 1;
            this.finiteArea = true;
        }
    }

    private class Cell {
        final int x, y;
        Region owner;
        int ownerDistance;
        int totalDistance;
        boolean border; // true iff cell is equally close to two or more regions

        Cell(Region owner, int x, int y) {
            this.owner = owner;
            this.x = x;
            this.y = y;
            this.ownerDistance = Integer.MAX_VALUE;
            this.totalDistance = -1;
            this.border = false;
        }

        Cell(int x, int y) {
            this.owner = null;
            this.x = x;
            this.y = y;
            this.ownerDistance = Integer.MAX_VALUE;
            this.totalDistance = -1;
            this.border = false;
        }
    }
}