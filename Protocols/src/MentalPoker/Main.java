package MentalPoker;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final String pathPrefix = "./Protocols/protocolWork/";

    public static List<String> read(String file) {
        List<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(pathPrefix + file))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void write(String file, String out) {
        try {
            PrintWriter writer = new PrintWriter(pathPrefix + file, "UTF-8");
            writer.println(out);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new Protocol().init();
    }
}
