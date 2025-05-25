import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SSHCommandExecutor {
    static String user = "root";  // Server username
    static String host = "localhost";  // Server IP address

    public static void main(String[] args) {
        stats();
    }

    private static void stats() {
        String sshCommand = String.format("docker stats --no-stream");
        parseDockerStats(executeCommand(sshCommand));
    }

    private static List<String> executeCommand(String sshCommand) {
        List<String> outputLines = new ArrayList<>();
        try {
            String[] split = sshCommand.split(" ");
            Process process = new ProcessBuilder(split).start();
            BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Collect standard output
            String line;
            while ((line = stdOutput.readLine()) != null) {
                outputLines.add(line);
            }

            // Print error output
            while ((line = errorOutput.readLine()) != null) {
                System.err.println(line);
            }

            // Wait for command to complete
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return outputLines;
    }

    private static void parseDockerStats(List<String> statsOutput) {
        if (statsOutput.isEmpty()) {
            System.out.println("No output from docker stats command.");
            return;
        }

        Map<String, Map<String, String>> statsMap = new LinkedHashMap<>();

        // The first line contains column headers
        String headerLine = statsOutput.get(0);
        String[] headers = headerLine.split("\\s{2,}"); // Split by 2 or more spaces

        // Parse each subsequent line
        for (int i = 1; i < statsOutput.size(); i++) {
            String line = statsOutput.get(i);
            String[] values = line.split("\\s{2,}");

            if (values.length != headers.length) {
                System.err.println("Mismatch between headers and values: " + line);
                continue;
            }

            // The first value is the container name
            String containerName = values[0];
            Map<String, String> containerStats = new LinkedHashMap<>();

            // Map remaining columns to their values
            for (int j = 1; j < headers.length; j++) {
                containerStats.put(headers[j], values[j]);
            }

            statsMap.put(containerName, containerStats);
        }

        // Print parsed stats
        System.out.println("Parsed Docker Stats:");
        for (Map.Entry<String, Map<String, String>> entry : statsMap.entrySet()) {
            System.out.println("Container: " + entry.getKey());
            for (Map.Entry<String, String> statEntry : entry.getValue().entrySet()) {
                System.out.println("  " + statEntry.getKey() + ": " + statEntry.getValue());
            }
        }
    }
}
