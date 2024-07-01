package Script;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.data.BreakingUpdateMetadata;
import se.kth.data.BuildHelp;
import se.kth.japianalysis.BreakingChange;
import se.kth.transitive_changes.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static se.kth.transitive_changes.Dependency.findUniqueDependencies;
import static se.kth.transitive_changes.MavenTree.diff;

public class TransitiveChanges {

    public static void main(String[] args) {
//        List<BreakingUpdateMetadata> transitives = BuildHelp.getBreakingCommit(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/RQ3/transitive_jsons"));
        List<BreakingUpdateMetadata> transitives = BuildHelp.getBreakingCommit(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/src/main/java/Script/example"));

        // Mapa para almacenar los resultados por breakingCommit
        Map<String, Map<String, Integer>> commitChangesMap = new HashMap<>();
        Map<String, Map<String, Integer>> transitiveModifications = new HashMap<>();

        // Cargar análisis previos desde el archivo JSON si existe
        File jsonFile = new File("breaking_changes_analysis.json");
        File transitiveFile = new File("transitive_changes_analysis.json");
//        if (jsonFile.exists()) {
//            try {
//                ObjectMapper mapper = new ObjectMapper();
//                commitChangesMap = mapper.readValue(jsonFile, HashMap.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if (transitiveFile.exists()) {
//            try {
//                ObjectMapper mapper = new ObjectMapper();
//                transitiveModifications = mapper.readValue(transitiveFile, HashMap.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        // Procesar cada breakingCommit
        for (BreakingUpdateMetadata breakingUpdate : transitives) {
            String breakingCommit = breakingUpdate.breakingCommit();

            // Verificar si el breakingCommit ya ha sido procesado antes
//            if (commitChangesMap.containsKey(breakingCommit)) {
//                System.out.println("El commit " + breakingCommit + " ya ha sido analizado. Saltando al siguiente...");
//                continue;
//            }
            if (transitiveModifications.containsKey(breakingCommit)) {
                System.out.println("El commit " + breakingCommit + " ya ha sido analizado. Saltando al siguiente...");
                continue;
            }

            Path jarsFile = Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/");

            // Construir rutas de dependencias antiguas y nuevas
            Path oldDependency = jarsFile.resolve("%s/%s-%s.jar".formatted(breakingCommit, breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().previousVersion()));
            Path newDependency = jarsFile.resolve("%s/%s-%s.jar".formatted(breakingCommit, breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().newVersion()));

            // Crear metadatos de API para las versiones nuevas y antiguas
            ApiMetadata newApiVersion = new ApiMetadata(newDependency.toFile().getName(), newDependency);
            ApiMetadata oldApiVersion = new ApiMetadata(oldDependency.toFile().getName(), oldDependency);

            // Crear dependencias para las versiones antiguas y nuevas
            Dependency oldVersion = new Dependency(breakingUpdate.updatedDependency().dependencyGroupID(),
                    breakingUpdate.updatedDependency().dependencyArtifactID(),
                    breakingUpdate.updatedDependency().previousVersion(),
                    "jar",
                    "compile"
            );
            Dependency newVersion = new Dependency(breakingUpdate.updatedDependency().dependencyGroupID(),
                    breakingUpdate.updatedDependency().dependencyArtifactID(),
                    breakingUpdate.updatedDependency().newVersion(),
                    "jar",
                    "compile");

            // Leer árboles de dependencias para las versiones antiguas y nuevas
            Set<Dependency> v1 = new HashSet<>();
            Set<Dependency> v2 = new HashSet<>();

            try {
                v1 = MavenTree.read(oldApiVersion, oldVersion);
                v2 = MavenTree.read(newApiVersion, newVersion);
            } catch (Exception e) {
                System.out.println("Error al leer los árboles de dependencias para %s y %s".formatted(oldDependency, newDependency));
                continue;
            }

            // Encontrar dependencias que han cambiado según SemVer
//            semver(v1, v2, commitChangesMap, breakingCommit, jsonFile);

//            transitiveChanges(v1, v2, transitiveModifications, breakingCommit, transitiveFile, transitiveModifications);




        }

        System.out.println("Todos los commits han sido analizados y escritos en el archivo JSON.");
    }

    private static void transitiveChanges(Set<Dependency> v1, Set<Dependency> v2, Map<String, Map<String, Integer>> transitiveModifications, String breakingCommit, File jsonFile, Map<String, Map<String, Integer>> commitChangesMap) {
        Set<PairTransitiveDependency> transitiveDependencies = diff(v1, v2);

        Set<Dependency> newDependencies = findUniqueDependencies(v1, v2);
        Set<Dependency> removedDependencies = findUniqueDependencies(v2, v1);
        int modification = transitiveModifications.size();
        int removed = removedDependencies.size();
        int add = newDependencies.size();
        Map<String, Integer> transitiveChanges = new HashMap<>();
        transitiveChanges.put("MODIFY", modification);
        transitiveChanges.put("REMOVED", removed);
        transitiveChanges.put("ADD", add);
        transitiveModifications.put(breakingCommit, transitiveChanges);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(jsonFile, commitChangesMap);
            System.out.println("Análisis para el commit " + breakingCommit + " escrito en el archivo JSON.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void semver
            (Set<Dependency> v1, Set<Dependency> v2, Map<String, Map<String, Integer>> commitChangesMap, String
                    breakingCommit, File jsonFile) {
        Set<DependencyRecord> changedDependencies = Dependency.findChangedDependenciesBySemVer(v1, v2);

        // Encontrar dependencias que han modificado solo versión y tipo
        Set<DependencyRecord> modifiedDependencies = Dependency.findModifiedDependenciesByVersionAndScope(v1, v2);

        int majorChanges = 0;
        int minorChanges = 0;
        int patchChanges = 0;
        int versionOnlyChanges = 0;
        int scopeOnlyChanges = 0;
        int breakingChanges = 0;
        int minorBreakingChanges = 0;
        int majorBreakingChanges = 0;
        int patchBreakingChanges = 0;


        // Contar y agrupar cambios
        for (DependencyRecord record : changedDependencies) {

            CompareTransitiveDependency compareTransitiveDependency = new CompareTransitiveDependency(record.newDependency(), record.oldDependency());
            List<BreakingChange> changes = null;
            try {
                changes = compareTransitiveDependency.getChangesBetweenDependencies();
            } catch (Exception e) {
                System.out.println("Error al comparar dependencias %s y %s".formatted(record.oldDependency(), record.newDependency()));
                continue;
            }
            breakingChanges += changes.size();

            switch (record.changeType()) {
                case "MAJOR":
                    majorChanges++;
                    majorBreakingChanges += changes.size();
                    break;
                case "MINOR":
                    minorChanges++;
                    minorBreakingChanges += changes.size();
                    break;
                case "PATCH":
                    patchChanges++;
                    patchBreakingChanges += changes.size();
                    break;
            }
        }

        for (DependencyRecord record : modifiedDependencies) {
            switch (record.changeType()) {
                case "VERSION_ONLY":
                    versionOnlyChanges++;
                    break;
                case "SCOPE_ONLY":
                    scopeOnlyChanges++;
                    break;
            }
        }

        // Crear un mapa para almacenar los cambios detectados en este breakingCommit
        Map<String, Integer> commitChanges = new HashMap<>();
        commitChanges.put("MAJOR", majorChanges);
        commitChanges.put("MINOR", minorChanges);
        commitChanges.put("PATCH", patchChanges);
        commitChanges.put("VERSION_ONLY", versionOnlyChanges);
        commitChanges.put("SCOPE_ONLY", scopeOnlyChanges);

        commitChanges.put("BREAKING_CHANGES_MAJOR", majorBreakingChanges);
        commitChanges.put("BREAKING_CHANGES_MINOR", minorBreakingChanges);
        commitChanges.put("BREAKING_CHANGES_PATCH", patchBreakingChanges);

        // Agregar los cambios por breakingCommit al mapa principal
        commitChangesMap.put(breakingCommit, commitChanges);

        // Escribir el análisis JSON en un archivo después de cada commit analizado
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(jsonFile, commitChangesMap);
            System.out.println("Análisis para el commit " + breakingCommit + " escrito en el archivo JSON.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Función para obtener los metadatos de commit de Breaking Updates
    private static List<BreakingUpdateMetadata> getBreakingCommit(Path path) {
        // Implementación de obtención de metadatos de commit
        return null; // Reemplazar con lógica real
    }
}
