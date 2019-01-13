package util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.PatternSyntaxException;

/**
 * FileIO.java is a set of static text file reading/writing methods
 * for maximum simplicity. They are all for one-line use.
 * @author /u/Philboyd_Studge on 12/5/2015.
 */
public class FileIO {

    public static String SESSION_ID = System.getenv("SESSION_ID");

    /**
     * Load file into one String - assumes no line feeds
     * i.e. 2015 Day 1, 2015 Day 3
     * @param filename file in current working directory or full pathname
     * @return String
     */
    public static String getFileAsString(final String filename) {
        String test = "";
        try  {
            test = new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return test;

    }

    /**
     * Write byte array to given filename
     * @param input byte array
     * @param outFile valid filename
     */
    public static void writeBytesToFile(final byte[] input, String outFile) {
        Path path = Paths.get(outFile);
        try {
            Files.write(path, input);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Write List of strings to file, adding line separators after each line
     * except for the last.
     * @param input List of Strings
     * @param outfile valid filename/path
     */
    public static void writeListToFile(List<String> input, String outfile) {
        Path path = Paths.get(outfile);
        try (BufferedWriter bw = Files.newBufferedWriter(path)){
            for (int i = 0; i < input.size(); i++) {
                bw.write(input.get(i));
                if (i < input.size() - 1) {
                    bw.newLine();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Load list of strings from advent of code input for given day/year
     * writes text file to working directory named adventXXXX_dayXX.txt
     * You must supply session ID information by logging in to a valid
     * <a href>http://adventofcode.com</a> account and copying
     * the ENTIRE information string listed in the GET header under Cookie
     * Cannot download the information until midnight EST of that day
     *
     * if file has already been created, read list from that file instead.
     *
     * @param year integer year, 2015, 2016 or 2017
     * @param day 1 - 25
     * @param sessionID Session cookie information
     * @return
     */
    public static List<String> getAOCInputForDay(int year, int day, String sessionID) {
        if (year < 2015 || year > 2018) {
            throw new IllegalArgumentException("Year out of range.");
        }
        if (day < 1 || day > 25) {
            throw new IllegalArgumentException("Day out of range");
        }
        String url = "https://adventofcode.com/" + year +
                "/day/" + day + "/input";
        String filename = "data/Advent" + year + "/advent" + year + "_day" + day + ".txt";
        Path path = Paths.get(filename);
        if (Files.exists(path)) {
            return getFileAsList(filename);
        } else {
            List<String> input = getFromUrl(url, sessionID);
            writeListToFile(input, filename);
            return input;
        }
    }

    /**
     * Performs given Function on file, one line at a time, and summing the results
     * @param filename file in current working directory or full pathname
     * @param func Function that takes a String as parameter and returns an int
     * @return int summed result
     */
    public static int performIntActionOnLine(final String filename, Function<String, Integer> func) {
        int result = 0;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) {
            String input;
            while ((input = br.readLine()) != null) {
                result += func.apply(input);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return result;

    }

    /**
     * Loads entire file, one line at a time, into List
     * @param filename file in current working directory or full pathname
     * @return ArrayList of strings
     */
    public static List<String> getFileAsList(final String filename) {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) {
            String input;
            while ((input = br.readLine()) != null) {
                list.add(input);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return list;
    }

    /**
     * Read data from a URL into a List of Strings
     * Note -> does not work with Advent of Code puzzle input
     * @param url
     * @return
     */
    public static List<String> getFromUrl(final String url) {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            String input;
            while ((input = br.readLine()) != null) {
                list.add(input);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return list;
    }

    /**
     * Load input data from URL using session cookie information.
     * url will look like <code>http://adventofcode.com/2015/day/18/input</code>
     * You must have copied the entire session cookie string (not just session ID
     * but the two other strings tht start with "_ga" and "_gid"
     * You will not be able to access days that have not been posted yet
     * @param url correct adventofcode.com url
     * @param sessionID session cookie info
     * @return List of strings from data file
     */
    private static List<String> getFromUrl(final String url, String sessionID) {
        List<String> list = new ArrayList<>();
        String location = url;
        String cookie = sessionID;
        HttpURLConnection con = null;

        while(location != null) {
            try{
                con = (HttpURLConnection) new URL(location).openConnection();

                // We'll do redirects ourselves
                con.setInstanceFollowRedirects(false);

                // If we got a cookie last time round, then add it to our request
                if(cookie != null) con.setRequestProperty("Cookie", cookie);
                con.connect();

                // Get the response code, and the location to jump to (in case of a redirect)
                int responseCode = con.getResponseCode();
                location = con.getHeaderField( "Location" );

                // Try and get a cookie the site will set, we will pass this next time round
                cookie = con.getHeaderField( "Set-Cookie" );

                if(location == null) list = parseResponse(con);
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return list;

        /*try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", sessionID);
            connection.setInstanceFollowRedirects(true);
            connection.connect();

            String response = connection.getResponseMessage();
            if (!response.equals("OK")) {
                System.out.println(response.toString());
                throw new InvalidParameterException("Unable to establish connection.");
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()))) {
                String input;
                while (( input = br.readLine()) != null) {
                    list.add(input);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return list;*/

    }

    private static List<String> parseResponse(HttpURLConnection con) {
        List<String> list = new ArrayList<>();

        try {
            String response = con.getResponseMessage();
            if (!response.equals("OK")) {
                System.out.println(response.toString());
                throw new InvalidParameterException("Unable to establish connection.");
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    con.getInputStream()))) {
                String input;
                while (( input = br.readLine()) != null) {
                    list.add(input);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Return an ArrayList of String Arrays, split using the given delimiter
     * @param filename file in current working directory or full pathname
     * @param delimiter REGEX string delimiter. Catches PatternSyntaxException.
     * @return List of String Arrays
     */
    public static List<String[]> getFileLinesSplit(final String filename, String delimiter) {
        List<String[]> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) {
            String input;
            while ((input = br.readLine()) != null) {
                try {
                    // input = input.trim();
                    String[] s = input.split(delimiter);
                    list.add(s);
                } catch (PatternSyntaxException pse) {
                    System.out.println("Bad regex syntax. Delimiter \"" + delimiter + " \"");
                    return null;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return list;

    }

    /**
     * Parse a String array into an int array
     * if parsing error occurs, inserts a value of -1
     * into array at that index
     * @param input String array
     * @return array of primitive integers
     */
    public static int[] StringArrayToInt(final String[] input) {
        return StringArrayToInt(input, -1);
    }

    /**
     * Parse a String array into int array
     * Catches conversion errors and puts given defaultValue at that index
     * @param input String array
     * @param defaultValue value to use when error is caught
     * @return array of primitive integers
     */
    public static int[] StringArrayToInt(final String[] input, final int defaultValue) {
        int[] output = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            try {
                output[i] = Integer.parseInt(input[i]);
            } catch (NumberFormatException nfe) {
                System.err.println("Not a valid integer at index: " + i);
                System.err.println("Replacing with: " + defaultValue);
                output[i] = defaultValue;
            }
        }
        return output;
    }

    /**
     * Parse a String array into an Integer array
     * if parsing error occurs, inserts a value of -1
     * into array at that index
     * @param input String array
     * @return array of Integer objects
     */
    public static Integer[] StringArrayToInteger(final String[] input) {
        return StringArrayToInteger(input, -1);
    }

    /**
     * Parse a String array into Integer array
     * Catches conversion errors and puts given defaultValue at that index
     * @param input String array
     * @param defaultValue value to use when error is caught
     * @return array of Integer objects
     */
    public static Integer[] StringArrayToInteger(final String[] input, final int defaultValue) {
        Integer[] output = new Integer[input.length];
        for (int i = 0; i < input.length; i++) {
            try {
                output[i] = Integer.parseInt(input[i]);
            } catch (NumberFormatException nfe) {
                System.err.println("Not a valid integer at index: " + i);
                System.err.println("Replacing with: " + defaultValue);
                output[i] = defaultValue;
            }
        }
        return output;
    }
}