package Advent2018;

import util.AdventOfCode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day7 extends AdventOfCode {
    private final int NUM_WORKERS = 5;
    private final int BASE_STEP_DURATION = 60;
    // global variables go here
    private HashMap<Character, Step> stepMap;
    private boolean[][] adjMatrix;
    private StringBuilder stepSequence;

    public Day7(List<String> input) {
        super(input);
        part1Description = "Step ordering: ";
        part2Description = "Seconds needed to complete steps: ";
    }

    @Override
    public Object part1() {
        parse();
        PriorityQueue<Integer> stepQueue = new PriorityQueue<>();
        stepSequence = new StringBuilder();

        // find first steps
        for (int i = 0; i < adjMatrix.length; i++) {
            if (checkStep(i)) {
                stepQueue.add(i);
            }
        }

        // process queue
        while (stepQueue.peek() != null) {
            int i = stepQueue.remove();
            Step s = stepMap.get((char) (i + 65));
            if (s.done) continue;

            s.done = true;
            stepSequence.append(s.label);

            for (int post = 0; post < adjMatrix.length; post++) {
                if (checkStep(post)) {
                    stepQueue.add(post);
                }
            }

        }

        return stepSequence.toString();
    }

    @Override
    public Object part2() {
        parse();
        PriorityQueue<Integer> stepQueue = new PriorityQueue<>();
        stepSequence = new StringBuilder();
        ArrayList<Worker> workers = new ArrayList<>();

        for (int i = 0; i < NUM_WORKERS; i++) {
            workers.add(new Worker(i));
        }

        // find first steps
        for (int i = 0; i < adjMatrix.length; i++) {
            if (checkStep(i)) {
                stepQueue.add(i);
            }
        }

        // process queue
        int tick = 0;

        while (stepSequence.toString().length() < adjMatrix.length) {
            // assign jobs
            for (Worker w : workers) {
                if ((w.job != null) || (stepQueue.peek() == null)) continue;

                int i = stepQueue.remove();
                Step s = stepMap.get((char) (i + 65));
                if (s.done || s.active) continue;

                w.job = s;
                s.active = true;
                w.startTime = tick;
                w.endTime = tick + BASE_STEP_DURATION + i + 1;
            }

            int numFree = workersFree(workers, tick, stepQueue);

            // if either no workers are free, or free workers are waiting on other jobs to finish, let time pass
            while ((numFree == 0) || ((numFree < NUM_WORKERS) && (stepQueue.peek() == null))) {
                tick++;
                numFree = workersFree(workers, tick, stepQueue);
            }

            //System.err.println(tick);
        }

        return tick;
    }

    @Override
    public void parse() {
        // parse input into global variables
        stepMap = new HashMap<>();

        Pattern pattern = Pattern.compile("Step (\\w+) must be finished before step (\\w+) can begin.");

        for (String each : input) {
            Matcher m = pattern.matcher(each);

            // parse string if properly formatted
            if (m.matches()) {
                Character c1 = m.group(1).charAt(0);
                Character c2 = m.group(2).charAt(0);
                Step s1, s2;

                if (stepMap.containsKey(c1)) s1 = stepMap.get(c1);
                else {
                    s1 = new Step(c1 - 65, c1);
                    stepMap.put(c1, s1);
                }

                if (stepMap.containsKey(c2)) s2 = stepMap.get(c2);
                else {
                    s2 = new Step(c2 - 65, c2);
                    stepMap.put(c2, s2);
                }

                s1.post.add(s2);
                s2.pre.add(s1);
            } else {
                System.err.println("Malformed region string: " + each);
            }
        }

        initAdjMatrix();
    }

    private void initAdjMatrix() {
        adjMatrix = new boolean[stepMap.size()][stepMap.size()];

        for (Step s : stepMap.values()) {
            for (Step pre : s.pre) {
                adjMatrix[pre.id][s.id] = true;
            }
            for (Step post : s.post) {
                adjMatrix[s.id][post.id] = true;
            }
        }
    }

    // returns true iff a given step is unfinished and its prereqs are satisfied
    private boolean checkStep(int i) {
        Step s = stepMap.get((char) (i + 65));
        if (s.done) return false;

        for (int j = 0; j < adjMatrix.length; j++) {
            if (adjMatrix[j][i]) {
                Step sPre = stepMap.get((char) (j + 65));

                if (!sPre.done) {
                    return false;
                }
            }
        }

        return true;
    }

    private int workersFree(ArrayList<Worker> workers, int tick, PriorityQueue<Integer> stepQueue) {
        int ret = 0;

        for (Worker w : workers) {
            if (w.job == null) {
                ret++;
            } else if (tick >= w.endTime) {
                w.job.done = true;
                w.job.active = false;
                stepSequence.append(w.job.label);

                for (int post = 0; post < adjMatrix.length; post++) {
                    if (checkStep(post)) {
                        stepQueue.add(post);
                    }
                }

                w.job = null;
                w.startTime = -1;
                w.endTime = Integer.MAX_VALUE;
                ret++;
            }
        }

        return ret;
    }

    private class Step {
        int id;
        char label;
        boolean active;
        boolean done;
        Set<Step> pre;
        Set<Step> post;

        Step(int id, char label) {
            this.id = id;
            this.label = label;
            this.active = false;
            this.done = false;
            pre = new HashSet<>();
            post = new HashSet<>();
        }
    }

    private class Worker {
        int id;
        Step job;
        int startTime;
        int endTime;

        Worker(int id) {
            this.id = id;
            this.job = null;
            this.startTime = -1;
            this.endTime = Integer.MAX_VALUE;
        }
    }
}