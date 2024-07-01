package util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class MavenCommand {

    public static String[] execCommand(String cmd, File directory) {
        try {
            Process process = Runtime.getRuntime().exec(cmd, null, directory);
            process.waitFor();
            String output = new String(process.getInputStream().readAllBytes());
            String error = new String(process.getErrorStream().readAllBytes());
            return new String[]{output, error};
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{"", e.getMessage()};
        }

    }

    /**
     * @param command the command to execute
     * @param output  the output file
     * @return the exit code of the process
     * @throws IOException          if the process cannot be started
     * @throws InterruptedException if the process is interrupted
     */
    public static int executeCommand(String command, Path output, Path directory) throws IOException, InterruptedException {


        ProcessBuilder processBuilder = new ProcessBuilder("zsh", "-c", command);
        processBuilder.directory(directory.toFile());
        processBuilder.redirectOutput(output.toFile());
        processBuilder.redirectError(output.toFile());

        Process process = processBuilder.start();
        InputStream inStream = process.getInputStream();
        InputStream errStream = process.getErrorStream();
        process.waitFor();
        inStream.close();

        try {
            inStream.close();
            errStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // wait until process is done
        return process.waitFor();
    }


}
