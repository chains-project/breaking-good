package se.kth.breaking_changes;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Download {

    private static final Map<String, String> repositoryUrls = Map.of(
            "mavenCentral", "https://repo1.maven.org/maven2/",
            "jenkins", "https://repo.jenkins-ci.org/artifactory/releases/",
            "jboss", "https://repository.jboss.org/nexus/content/repositories/thirdparty-releases/"
    );

    private Download() {
    }

    private static String getIndexPageOfRepository(String artifactUrl) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request =
                HttpRequest.newBuilder().uri(URI.create(artifactUrl)).build();
        HttpResponse<String> result = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (result.statusCode() != 200) {
            return null;
        }
        return result.body();
    }

    private static String getArtifactUrl(String groupId, String artifactId, String version, String repositoryUrl) {
        groupId = groupId.replace('.', '/');
        return repositoryUrl + groupId + "/" + artifactId + "/" + version + "/";
    }

    private static String getUrlOfRequestedJar(String indexPageContent, String indexPageUrl) {
        List<String> candidates = Jsoup.parse(indexPageContent).select("a").stream()
                .map(e -> e.attr("href"))
                .collect(Collectors.toList());

        Optional<String> artifactJar = candidates.stream()
                .filter(c -> c.endsWith(".jar"))
                .filter(c -> !c.contains("sources"))
                .filter(c -> !c.contains("javadoc"))
                .filter(c -> !c.contains("tests"))
                .filter(c -> !c.contains("test"))
                .filter(c -> !c.contains("config"))
                .findFirst();

        if (artifactJar.isPresent()) {
            String artifactJarName = artifactJar.get();
            // java.net.URI has the worst APIs ever
            if (artifactJarName.startsWith("https://") || artifactJarName.startsWith("http://")) {
                return artifactJarName;
            }
            return indexPageUrl + artifactJarName;
        } else {
            System.err.println("Could not find jar for " + indexPageUrl);
            return null;
        }
    }

    public static File getJarFile(String groupId, String artifactId, String version, Path directory)
            throws IOException, InterruptedException {
        for (String repositoryUrl : repositoryUrls.values()) {
            String url = getArtifactUrl(groupId, artifactId, version, repositoryUrl);
            String indexPageContent = getIndexPageOfRepository(url);
            if (indexPageContent != null) {
                String jarUrl = getUrlOfRequestedJar(indexPageContent, url);
                if (jarUrl != null) {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request =
                            HttpRequest.newBuilder().uri(URI.create(jarUrl)).build();

                    final var resolve = directory.resolve(String.format("%s-%s", artifactId, version) + ".jar");
                    HttpResponse<Path> result = client.send(request, HttpResponse.BodyHandlers.ofFile(resolve));
                    return result.body().toFile();
                }
            }
        }
        return null;
    }
}
