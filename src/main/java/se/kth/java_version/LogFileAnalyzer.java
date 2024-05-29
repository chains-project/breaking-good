package se.kth.java_version;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static se.kth.java_version.JavaIncompatibilityAnalyzer.extractVersionErrors;

public class LogFileAnalyzer {

    public static void main(String[] args) throws IOException {
        String logFilePath = "/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/11be71ab8713fe987785e9e25e4f3e410e709ab9/camunda-platform-7-mockito/11be71ab8713fe987785e9e25e4f3e410e709ab9.log";

        JavaIncompatibilityAnalyzer analyzer = new JavaIncompatibilityAnalyzer();
       Set<String> errorList = analyzer.parseErrors(logFilePath);

        for (String error : errorList) {
            System.out.println("Error:");
            System.out.println(error);
            System.out.println("----");
        }

        Set<String> errorsSet = new HashSet<>();
        Map<JavaVersionIncompatibility, Set<String>> versionErrors = extractVersionErrors(errorList);
        for (Map.Entry<JavaVersionIncompatibility, Set<String>> entry : versionErrors.entrySet()) {
            System.out.println("Java version incompatibility:");
            System.out.println(entry.getKey());
            System.out.println("Errors:");
            for (String error : entry.getValue()) {
                System.out.println(error);
            }
            System.out.println("----");
        }

//        extracted(logFilePath);
//
//        Set<String> errors = extractErrors(logFilePath);
//        System.out.println(errors.size() + " errors found in the log file.");

//        Map<String, List<Integer>> javaVersions =   VersionFinder.findJavaVersions("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/11be71ab8713fe987785e9e25e4f3e410e709ab9/camunda-platform-7-mockito");
//
//        for (Map.Entry<String, List<Integer>> entry : javaVersions.entrySet()) {
//            System.out.println("File: " + entry.getKey());
//            System.out.println("Java versions: " + entry.getValue());
//        }

    }


    public static Set<String> extractErrors(String logFilePath) {
        Set<String> errorMessages = new HashSet<>();
        StringBuilder logContent = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                logContent.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Expresión regular para capturar bloques de error completos
        Pattern errorPattern = Pattern.compile("(?s)(\\[ERROR\\].*?)(?=(\\[ERROR\\]|\\z))");
        Matcher matcher = errorPattern.matcher(logContent.toString());

        while (matcher.find()) {
            errorMessages.add(matcher.group(1).trim());
        }

        return errorMessages;
    }


    private static void extracted(String logFilePath) {
        String errorPattern = "class file has wrong version (\\d+\\.\\d+), should be (\\d+\\.\\d+)";
        Pattern pattern = Pattern.compile(errorPattern);

        // Patrón para identificar el inicio de una nueva entrada de log
        String newEntryPattern = "^\\[ERROR\\]";  // Ajusta este patrón según el formato de tu archivo de log
        Pattern newEntry = Pattern.compile(newEntryPattern);

        Set<String> errors = new HashSet<>();

        // Leer el archivo de log y buscar el patrón
        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            boolean errorFound = false;
            StringBuilder errorInfo = new StringBuilder();
            Queue<String> errorLines = new LinkedList<>();

            while ((line = br.readLine()) != null) {
                if (newEntry.matcher(line).find()) {
                    // Si encontramos una nueva entrada de error, reiniciamos la cola
                    errorLines.clear();
                    if (errorFound) {
                        // Si ya se encontró un error y se encuentra una nueva entrada, se guarda el error y se reinicia el proceso
                        errors.add(errorInfo.toString());
                        errorInfo.setLength(0); // Limpiar errorInfo para el próximo error
                        errorFound = false;
                    }
                }

                if (!errorFound) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        errorFound = true;
                        // Extraer los números de las versiones
                        String wrongVersion = matcher.group(1);
                        String expectedVersion = matcher.group(2);
                        System.out.println("La línea de error se encontró en el archivo de log.");
                        System.out.println("Versión incorrecta: " + wrongVersion);
                        System.out.println("Versión esperada: " + expectedVersion);
                        // Agregar las líneas anteriores al errorInfo
                        for (String errorLine : errorLines) {
                            errorInfo.append(errorLine).append(System.lineSeparator());
                        }
                        errorInfo.append(line).append(System.lineSeparator());
                    } else {
                        // Agregar la línea actual a la cola
                        errorLines.add(line);
                    }
                } else {
                    errorInfo.append(line).append(System.lineSeparator());
                }
            }

            // Agregar el último error encontrado si existe
            if (errorFound) {
                errors.add(errorInfo.toString());
            }

            if (!errors.isEmpty()) {
                System.out.println("Se encontraron las siguientes incompatibilidades de versión de Java en el archivo de log:");
                for (String error : errors) {
                    System.out.println("Información completa del error:");
                    System.out.println(error);
                }
            } else {
                System.out.println("No se encontraron incompatibilidades de versión de Java en el archivo de log.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

