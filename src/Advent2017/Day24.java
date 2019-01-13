package Advent2017;

import util.AdventOfCode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day24 extends AdventOfCode {

    // regex to extract the components from each input line
    private static final Pattern pattern =
            Pattern.compile("(\\d+)[/](\\d+)");

    private List<Component> components;
    private List<Bridge> bridges;
    private Bridge strongest;
    private Bridge strongest_longest;

    private void print_bridges(){
        for (Bridge each : bridges){
            each.print_bridge();
        }
    }

    private void print_components(){
        for (Component each : components){
            each.print_component();
        }
    }

    class Component {
        private int left;
        private int right;
        private boolean used_l;
        private boolean used_r;

        Component(int left, int right) {
            this.left = left;
            this.right = right;
            this.used_l = false;
            this.used_r = false;
        }

        Component(Component c){
            this.left = c.left;
            this.right = c.right;
            this.used_l = c.used_l;
            this.used_r = c.used_r;
        }

        int strength(){
            return ((this.left)+(this.right));
        }

        // determines whether the given componet may be attached to another component
        boolean matches(int connector){
            return (((this.left == connector) /*&& (this.used_l == false)*/) || ((this.right == connector) /*&& (this.used_r == false)*/));
        }

        void print_component(){
            System.out.println(this.left + "/" + this.right);
        }

    }

    class Bridge {
        List<Component> br;
        List<Component> available;
        int next_connector;

        Bridge(List<Component> available){
            this.br = new ArrayList<>();
            this.available = new ArrayList<>();
            this.available.addAll(available);
            this.next_connector = 0;
        }

        Bridge(Bridge b){
            this.br = new ArrayList<>();
            this.br.addAll(b.br);
            this.available = new ArrayList<>();
            this.available.addAll(b.available);
            this.next_connector = b.next_connector;
        }

        void print_bridge(){
            for(Component each : br){
                System.out.print(each.left + "/" + each.right + " ");
            }
            System.out.print("--- " + this.strength() + " (");
            for(Component each : available){
                System.out.print(each.left + "/" + each.right + " ");
            }
            System.out.println(")");
        }

        // adds element to bridge
        void grow(int idx){
            Component c = this.available.remove(idx);
            if(c.left == this.next_connector){
                c.used_l = true;
                this.next_connector = c.right;
            }
            else{
                c.used_r = true;
                this.next_connector = c.left;
            }
            this.br.add(new Component(c));
        }

        void grow(Component c){
            this.available.remove(c);
            if(c.left == this.next_connector){
                c.used_l = true;
                this.next_connector = c.right;
            }
            else{
                c.used_r = true;
                this.next_connector = c.left;
            }
            this.br.add(new Component(c));
        }

        int strength(){
            int ret = 0;

            for(Component each : br){
                ret += each.strength();
            }

            return ret;
        }

        int length(){
            return br.size();
        }
    }

    public Day24(List<String> input) {
        super(input);
        part1Description = "Strength of strongest bridge: ";
        part2Description = "Strength of longest, strongest bridge: ";
    }

    // returns all components in available with a free connection of type connector
    private List<Component> get_next_components(int connector, List<Component> available){
        List<Component> ret = new ArrayList<>();

        for(Component each : available){
            if(each.matches(connector)){
                ret.add(each);
            }
        }

        return ret;
    }

    private void construct_bridges(){
        bridges = new ArrayList<>();

        List<Component> starters = get_next_components(0,components);
        for(Component each : starters){
            Bridge b = new Bridge(components);
            b.grow(each);
            bridges.add(b);
        }
        strongest = bridges.get(0);
        strongest_longest = strongest;

        for(int i = 0; i < bridges.size(); i++){
            Bridge curr = bridges.get(i);
            if(curr.strength() > strongest.strength()) strongest = curr;
            if(curr.length() > strongest_longest.length()) strongest_longest = curr;
            else if(curr.length() == strongest_longest.length()) strongest_longest = (curr.strength() > strongest_longest.strength()) ? curr : strongest_longest;

            List<Component> next_c_list = get_next_components(curr.next_connector,curr.available);
            for(Component each : next_c_list){
                Bridge curr_expanded = new Bridge(curr);
                curr_expanded.grow(each);
                bridges.add(curr_expanded);
            }
        }

        //print_bridges();
        /*
        for(int i = 0; i < components.size(); i++){
            int connector = 0; // the type of connection needed
            Component c = components.get(i);

            // check if current component is a valid bridge start
            if(((c.left == connector) || (c.right == connector))){
                List<Component> available = new ArrayList<>(components);
                List<Component> bridge = new ArrayList<>();

                // remove from available and add to current bridge
                Component start_c = available.remove(i); //new Component(c.left,c.right);
                if(c.left == connector) start_c.used_l = true;
                else start_c.used_r = true;
                bridge.add(start_c);

            }

        }
        */
    }


    @Override
    public Object part1() {
        construct_bridges();
        return strongest.strength();
    }
    @Override
    public Object part2() {
        return strongest_longest.strength();
    }

    @Override
    public void parse() {
        components = new ArrayList<>();
        int count = 0;
        for (String each : input) {
            Matcher m = pattern.matcher(each);
            m.find();
            components.add(new Component(Integer.parseInt(m.group(1)),Integer.parseInt(m.group(2))));
        }
    }
}
