package Advent2017;

import util.AdventOfCode;
import util.FileIO;

import java.util.*;
import java.util.stream.Collectors;

public class Day22 extends AdventOfCode {
    private boolean[][] grid;
    private State[][] grid_2;
    private int x_size;
    private int y_size;
    private Carrier carrier;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public enum State {
        CLEAN, WEAKENED, INFECTED, FLAGGED
    }

    class Carrier{
        int x_loc;
        int y_loc;
        Direction dir;

        Carrier(int x_start, int y_start){
            this.x_loc = x_start;
            this.y_loc = y_start;
            dir = Direction.UP;
        }
    }


    public Day22(List<String> input) {
        super(input);
        part1Description = "Number of bursts causing a node to become infected: ";
        part2Description = "Number of bursts causing a node to become infected (pt. 2): ";
    }

    private int tick() {
        int curr_x = carrier.x_loc;
        int curr_y = carrier.y_loc;
        int ret = 0;

        // if infected, turn right. If healthy, turn left
        switch (carrier.dir) {
            case UP:
                carrier.dir = (grid[curr_y][curr_x] == true) ? Direction.RIGHT : Direction.LEFT;
                break;
            case RIGHT:
                carrier.dir = (grid[curr_y][curr_x] == true) ? Direction.DOWN : Direction.UP;
                break;
            case DOWN:
                carrier.dir = (grid[curr_y][curr_x] == true) ? Direction.LEFT : Direction.RIGHT;
                break;
            case LEFT:
                carrier.dir = (grid[curr_y][curr_x] == true) ? Direction.UP : Direction.DOWN;
                break;
        }

        // clean or infect current node
        if(grid[curr_y][curr_x] == true){
            grid[curr_y][curr_x] = false;
        }
        else{
            grid[curr_y][curr_x] = true;
            ret = 1;
        }

        // determine next spot
        int next_x = curr_x;
        int next_y = curr_y;
        switch (carrier.dir) {
            case UP:
                next_y--;
                break;
            case RIGHT:
                next_x++;
                break;
            case DOWN:
                next_y++;
                break;
            case LEFT:
                next_x--;
                break;
        }

        // resize and shift, if necessary
        if(next_x < 0){
            // add col on left
            boolean[][] new_grid = new boolean[y_size][x_size+1];
            for(int i = 0; i < y_size; i++)
            {
                boolean[] gridRow = grid[i];
                System.arraycopy(gridRow, 0, new_grid[i], 1, x_size);
            }
            x_size = x_size+1;
            carrier.x_loc = 0;
            grid = new boolean[y_size][];
            for (int i = 0; i < y_size; i++) {
                grid[i] = new_grid[i].clone();
            }
        }
        else if(next_x >= x_size){
            // add col on right
            boolean[][] new_grid = new boolean[y_size][x_size+1];
            for(int i = 0; i < y_size; i++)
            {
                boolean[] gridRow = grid[i];
                System.arraycopy(gridRow, 0, new_grid[i], 0, x_size);
            }
            x_size = x_size+1;
            carrier.x_loc = x_size-1;
            grid = new boolean[y_size][];
            for (int i = 0; i < y_size; i++) {
                grid[i] = new_grid[i].clone();
            }
        }
        else if(next_y < 0){
            // add row on top
            boolean[][] new_grid = new boolean[y_size+1][x_size];
            for(int i = 0; i < y_size; i++)
            {
                boolean[] gridRow = grid[i];
                System.arraycopy(gridRow, 0, new_grid[i+1], 0, x_size);
            }
            y_size = y_size+1;
            carrier.y_loc = 0;
            grid = new boolean[y_size][];
            for (int i = 0; i < y_size; i++) {
                grid[i] = new_grid[i].clone();
            }
        }
        else if(next_y >= y_size){
            // add row on bottom
            boolean[][] new_grid = new boolean[y_size+1][x_size];
            for(int i = 0; i < y_size; i++)
            {
                boolean[] gridRow = grid[i];
                System.arraycopy(gridRow, 0, new_grid[i], 0, x_size);
            }
            y_size = y_size+1;
            carrier.y_loc = y_size-1;
            grid = new boolean[y_size][];
            for (int i = 0; i < y_size; i++) {
                grid[i] = new_grid[i].clone();
            }
        }
        else{
            //System.out.println("bang");
            // only need to update carrier loc when there is no resize
            carrier.x_loc = next_x;
            carrier.y_loc = next_y;
        }

        return ret;
    }

    private int tick_2() {
        int curr_x = carrier.x_loc;
        int curr_y = carrier.y_loc;
        int ret = 0;

        // turn
        if(grid_2[curr_y][curr_x] == State.CLEAN){
            switch (carrier.dir) {
                case UP:
                    carrier.dir = Direction.LEFT;
                    break;
                case RIGHT:
                    carrier.dir = Direction.UP;
                    break;
                case DOWN:
                    carrier.dir = Direction.RIGHT;
                    break;
                case LEFT:
                    carrier.dir = Direction.DOWN;
                    break;
            }
        }
        else if(grid_2[curr_y][curr_x] == State.INFECTED){
            switch (carrier.dir) {
                case UP:
                    carrier.dir = Direction.RIGHT;
                    break;
                case RIGHT:
                    carrier.dir = Direction.DOWN;
                    break;
                case DOWN:
                    carrier.dir = Direction.LEFT;
                    break;
                case LEFT:
                    carrier.dir = Direction.UP;
                    break;
            }
        }
        else if(grid_2[curr_y][curr_x] == State.FLAGGED){
            switch (carrier.dir) {
                case UP:
                    carrier.dir = Direction.DOWN;
                    break;
                case RIGHT:
                    carrier.dir = Direction.LEFT;
                    break;
                case DOWN:
                    carrier.dir = Direction.UP;
                    break;
                case LEFT:
                    carrier.dir = Direction.RIGHT;
                    break;
            }
        }

        // update node state
        if(grid_2[curr_y][curr_x] == State.CLEAN){
            grid_2[curr_y][curr_x] = State.WEAKENED;
        }
        else if(grid_2[curr_y][curr_x] == State.WEAKENED){
            grid_2[curr_y][curr_x] = State.INFECTED;
            ret = 1;
        }
        else if(grid_2[curr_y][curr_x] == State.INFECTED){
            grid_2[curr_y][curr_x] = State.FLAGGED;
        }
        else if(grid_2[curr_y][curr_x] == State.FLAGGED){
            grid_2[curr_y][curr_x] = State.CLEAN;
        }

        // determine next spot
        int next_x = curr_x;
        int next_y = curr_y;
        switch (carrier.dir) {
            case UP:
                next_y--;
                break;
            case RIGHT:
                next_x++;
                break;
            case DOWN:
                next_y++;
                break;
            case LEFT:
                next_x--;
                break;
        }

        // resize and shift, if necessary
        if(next_x < 0){
            // add col on left
            State[][] new_grid = new State[y_size][x_size+1];
            for(int i = 0; i < y_size; i++)
            {
                State[] gridRow = grid_2[i];
                System.arraycopy(gridRow, 0, new_grid[i], 1, x_size);
                new_grid[i][0] = State.CLEAN;
            }
            x_size = x_size+1;
            carrier.x_loc = 0;
            grid_2 = new State[y_size][];
            for (int i = 0; i < y_size; i++) {
                grid_2[i] = new_grid[i].clone();
            }
        }
        else if(next_x >= x_size){
            // add col on right
            State[][] new_grid = new State[y_size][x_size+1];
            for(int i = 0; i < y_size; i++)
            {
                State[] gridRow = grid_2[i];
                System.arraycopy(gridRow, 0, new_grid[i], 0, x_size);
                new_grid[i][x_size] = State.CLEAN;
            }
            x_size = x_size+1;
            carrier.x_loc = x_size-1;
            grid_2 = new State[y_size][];
            for (int i = 0; i < y_size; i++) {
                grid_2[i] = new_grid[i].clone();
            }
        }
        else if(next_y < 0){
            // add row on top
            State[][] new_grid = new State[y_size+1][x_size];
            for(int i = 0; i < y_size; i++)
            {
                State[] gridRow = grid_2[i];
                System.arraycopy(gridRow, 0, new_grid[i+1], 0, x_size);
            }
            Arrays.fill(new_grid[0],State.CLEAN);
            y_size = y_size+1;
            carrier.y_loc = 0;
            grid_2 = new State[y_size][];
            for (int i = 0; i < y_size; i++) {
                grid_2[i] = new_grid[i].clone();
            }
        }
        else if(next_y >= y_size){
            // add row on bottom
            State[][] new_grid = new State[y_size+1][x_size];
            for(int i = 0; i < y_size; i++)
            {
                State[] gridRow = grid_2[i];
                System.arraycopy(gridRow, 0, new_grid[i], 0, x_size);
            }
            Arrays.fill(new_grid[y_size],State.CLEAN);
            y_size = y_size+1;
            carrier.y_loc = y_size-1;
            grid_2 = new State[y_size][];
            for (int i = 0; i < y_size; i++) {
                grid_2[i] = new_grid[i].clone();
            }
        }
        else{
            //System.out.println("bang");
            // only need to update carrier loc when there is no resize
            carrier.x_loc = next_x;
            carrier.y_loc = next_y;
        }

        return ret;
    }

    private void print_grid(){
        for(int y = 0; y < y_size; y++){
            for(int x = 0; x < x_size; x++) {
                if ((carrier.x_loc == x) && (carrier.y_loc == y)){
                    //System.out.println("x = " + x + ", y = " + y);
                    if (grid[y][x] == true) System.out.print('I');
                    else System.out.print('H');
                }
                else {
                    if (grid[y][x] == true) System.out.print('#');
                    else System.out.print('.');
                }
            }
            System.out.println("");
        }

    }

    @Override
    public Object part1() {
        int infection_bursts = 0;
        //print_grid();

        for (int i = 0; i < 10000; i++) {
            //System.out.println(i);
            infection_bursts += tick();
        }
        print_grid();
        return infection_bursts;
    }

    @Override
    public Object part2() {
        parse();
        int infection_bursts = 0;

        for (int i = 0; i < 10000000; i++) {
            infection_bursts += tick_2();
        }
        return infection_bursts;
    }


    @Override
    public void parse() {
        /*
        x_size = 3;
        y_size = 3;
        carrier = new Carrier(x_size/2,y_size/2);
        grid = new boolean[y_size][x_size];
        grid_2 = new State[y_size][x_size];
        grid[0][0] = false;
        grid[0][1] = false;
        grid[0][2] = true;
        grid[1][0] = true;
        grid[1][1] = false;
        grid[1][2] = false;
        grid[2][0] = false;
        grid[2][1] = false;
        grid[2][2] = false;
        grid_2[0][0] = State.CLEAN;
        grid_2[0][1] = State.CLEAN;
        grid_2[0][2] = State.INFECTED;
        grid_2[1][0] = State.INFECTED;
        grid_2[1][1] = State.CLEAN;
        grid_2[1][2] = State.CLEAN;
        grid_2[2][0] = State.CLEAN;
        grid_2[2][1] = State.CLEAN;
        grid_2[2][2] = State.CLEAN;
        */


        int count = 0;

        x_size = (input.get(0)).length();
        y_size = input.size();
        grid = new boolean[y_size][x_size];
        grid_2 = new State[y_size][x_size];
        carrier = new Carrier(x_size/2,y_size/2);
        for (String each : input) {
            for(int i = 0; i < each.length(); i++){
                if(each.charAt(i) == '#'){
                    grid[count][i] = true;
                    grid_2[count][i] = State.INFECTED;
                }
                else{
                    grid[count][i] = false;
                    grid_2[count][i] = State.CLEAN;
                }
            }
            count++;
        }


        //print_grid();
        //System.out.println("");
    }
}