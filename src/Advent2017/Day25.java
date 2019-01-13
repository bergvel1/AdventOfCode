package Advent2017;

import util.AdventOfCode;

import java.util.HashMap;
import java.util.List;

public class Day25 extends AdventOfCode {
    private HashMap<Integer,Integer> tape;
    private Integer cursor_pos;
    private State state;
    private int curr_step;
    private int check_step;

    public enum State {
        A, B, C, D, E, F
    }

    public Day25(List<String> input) {
        super(input);
        part1Description = "Diagnostic checksum: ";
        part2Description = "???: ";
    }

    int checksum(){
        return tape.size();
    }

    void execute(){
        tape.clear();
        cursor_pos = 0;
        state = State.A;
        curr_step = 0;

        while(curr_step < check_step){
            //System.out.println(curr_step + ": " + checksum() + " (" + cursor_pos + ")");
            Integer curr_val = 0;
            if(tape.get(cursor_pos) == null) {}//tape.put(cursor_pos,0);
            else curr_val = tape.get(cursor_pos);

            if(state == State.A){
                if(curr_val == 0){
                    tape.put(cursor_pos,1);
                    cursor_pos++;
                    state = State.B;
                }
                else if(curr_val == 1){
                    //tape.put(cursor_pos,0);
                    tape.remove(cursor_pos);
                    cursor_pos--;
                    state = State.C;
                }
            }
            else if(state == State.B){
                if(curr_val == 0){
                    tape.put(cursor_pos,1);
                    cursor_pos--;
                    state = State.A;
                }
                else if(curr_val == 1){
                    tape.put(cursor_pos,1);
                    cursor_pos++;
                    state = State.C;
                }
            }
            else if(state == State.C){
                if(curr_val == 0){
                    tape.put(cursor_pos,1);
                    cursor_pos++;
                    state = State.A;
                }
                else if(curr_val == 1){
                    //tape.put(cursor_pos,0);
                    tape.remove(cursor_pos);
                    cursor_pos--;
                    state = State.D;
                }
            }
            else if(state == State.D){
                if(curr_val == 0){
                    tape.put(cursor_pos,1);
                    cursor_pos--;
                    state = State.E;
                }
                else if(curr_val == 1){
                    tape.put(cursor_pos,1);
                    cursor_pos--;
                    state = State.C;
                }
            }
            else if(state == State.E){
                if(curr_val == 0){
                    tape.put(cursor_pos,1);
                    cursor_pos++;
                    state = State.F;
                }
                else if(curr_val == 1){
                    tape.put(cursor_pos,1);
                    cursor_pos++;
                    state = State.A;
                }
            }
            else if(state == State.F){
                if(curr_val == 0){
                    tape.put(cursor_pos,1);
                    cursor_pos++;
                    state = State.A;
                }
                else if(curr_val == 1){
                    tape.put(cursor_pos,1);
                    cursor_pos++;
                    state = State.E;
                }
            }

            curr_step++;
        }
    }

    @Override
    public Object part1() {
        execute();
        return checksum();
    }
    @Override
    public Object part2() {
        return 0;
    }

    @Override
    public void parse() {
        tape = new HashMap<>();
        check_step = 12261543;
    }
}
