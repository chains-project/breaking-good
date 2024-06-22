package util;

import java.io.File;

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


}
