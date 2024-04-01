package se.kth.breaking_changes;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@lombok.Setter
public class ApiMetadata {

    private static final Logger log = LoggerFactory.getLogger(ApiMetadata.class);

    String name;

    Path file;

    private static final Map<String, String> repositoryUrls = Map.of(
            "mavenCentral", "https://repo1.maven.org/maven2/",
            "jenkins", "https://repo.jenkins-ci.org/artifactory/releases/"
    );


    public ApiMetadata(String name, Path file) {
        this.name = name;
        this.file = file;

    }

    public String getName() {
        return name.contains(".jar") ? name.substring(0, name.indexOf(".jar")) : name;
    }

    /**
     * Downloads the version of the dependency from the maven repository
     *
     * @param artifactId the name of the dependency
     * @param groupId    the group id of the dependency
     * @param version    the version of the dependency
     * @return true if the download was successful, false otherwise
     */
    public static boolean download(String groupId, String artifactId, String version, Path directory) {
        /*
         * Maven repository link for the previous version of the dependency
         */
        final OkHttpClient httpConnector
                = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();

        String jarName = artifactId + "-" + version + ".jar";

        for (String repository : repositoryUrls.values()) {
            String artifactUrl = getArtifactUrl(groupId, artifactId, version, repository);
            try (Response response = httpConnector.newCall(new Request.Builder().url(artifactUrl).build()).execute()) {
                if (response.code() == 200) {
                    downloadAndConvert(response, directory.toString() + File.separator + jarName);
                    return true;
                }
            } catch (IOException e) {
                log.error("Maven source links could not be found for the updated dependency {}.", artifactId, e);
                return false;
            }
        }

        return false;
    }

    private static String getArtifactUrl(String groupId, String artifactId, String version, String repositoryUrl) {
        groupId = groupId.replace('.', '/');
        return repositoryUrl + groupId + "/" + artifactId + "/" + version + "/";
    }

    private static void downloadAndConvert(Response response, String fileName) throws IOException {
        assert response.body() != null;
        byte[] bytes = response.body().bytes();
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(bytes);
        }
    }

}

