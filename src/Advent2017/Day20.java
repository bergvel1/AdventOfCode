package Advent2017;

import javafx.geometry.Point3D;
import util.AdventOfCode;
import util.FileIO;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day20 extends AdventOfCode {

    // regex to extract the three 3d vectors from each input line
    private static final Pattern pattern =
            Pattern.compile("<([-\\d,]+)>, [a-z]=<([-\\d,]+)>, [a-z]=<([-\\d,]+)");

    private List<Particle> particles;
    private int closest;

    class Particle {
        int num;
        Point3D position;
        Point3D velocity;
        Point3D acceleration;

        Particle(int num, Point3D position, Point3D velocity, Point3D acceleration) {
            this.num = num;
            this.position = position;
            this.velocity = velocity;
            this.acceleration = acceleration;
        }

        private Point3D getPosition() {
            return position;
        }


        // manhattan distance to point <0, 0, 0>
        int distance() {
            return (int) (Math.abs(position.getX())
                    + Math.abs(position.getY())
                    + Math.abs(position.getZ()));
        }
    }

    public Day20(List<String> input) {
        super(input);
        part1Description = "Number of particle closest to <0, 0, 0>: ";
        part2Description = "Remaining particles after collisions have been resolved: ";
    }

    private int tick() {
        return particles.stream()
                .map(this::move)
                .min(Comparator.comparing(Particle::distance))
                .map(x -> x.num)
                .orElse(0);
    }

    @Override
    public Object part1() {
        for (int i = 0; i < 500; i++) {
            closest = tick();
        }
        return closest;
    }

    @Override
    public Object part2() {
        parse();
        for (int i = 0; i < 300; i++) {
            tick();
            collisions();
        }
        return particles.size();
    }

    private Particle move(Particle p) {
        p.velocity = p.velocity.add(p.acceleration);
        p.position = p.position.add(p.velocity);
        return p;
    }


    private void collisions() {
        particles = particles.stream()
                .collect(Collectors.groupingBy(Particle::getPosition))
                .values().stream()
                .filter(x -> x.size() == 1)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void parse() {
        particles = new ArrayList<>();
        int count = 0;
        for (String each : input) {
            Matcher m = pattern.matcher(each);
            m.find();
            Point3D[] points = new Point3D[3];
            for (int i = 0; i < 3; i++) {
                int[] verts = FileIO.StringArrayToInt(m.group(i + 1).split(","));
                points[i] = new Point3D(verts[0], verts[1], verts[2]);
            }
            particles.add(new Particle(count++, points[0], points[1], points[2]));
        }
    }
}