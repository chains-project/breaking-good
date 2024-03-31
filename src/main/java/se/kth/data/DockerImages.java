package se.kth.data;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class DockerImages {


    private static final String PRECEDING_COMMIT_CONTAINER_TAG = "-pre";

    private static final String BREAKING_UPDATE_CONTAINER_TAG = "-breaking";
    private static final String REGISTRY = "ghcr.io/chains-project/breaking-updates";
    private static final Logger log = LoggerFactory.getLogger(DockerImages.class);
    private static DockerClient dockerClient;
    private static final Short EXIT_CODE_OK = 0;
    private static final List<String> containers = new ArrayList<>();


    public void getProject(BreakingUpdateMetadata breakingUpdates) {

        createDockerClient();

        if (breakingUpdates != null) {
            //read each breaking update json file
            String breakingCommit = breakingUpdates.breakingCommit();
            String project = breakingUpdates.project();

            log.info("Processing breaking update {}", breakingCommit);

            String breakingImage = REGISTRY + ":" + breakingCommit + BREAKING_UPDATE_CONTAINER_TAG;

            Path dir = Path.of("projects/");
            Path breaking = dir.resolve(breakingCommit);


            if (Files.exists(breaking)) {
                return;
            }

            Path projectDir_breaking;
            try {
                projectDir_breaking = Files.createDirectories(breaking);
            } catch (IOException e) {
                log.error("Could not create the project directory {}", project, e);
                return;
            }


            Path projectDir = copyProject(Objects.requireNonNull(startContainer(breakingImage, project)).getKey(), project, projectDir_breaking);
            if (projectDir == null) {
                deleteContainers(breakingImage);
                return;
            }

            deleteContainers(breakingImage);

            log.info("=====================================================================================================");
            log.info("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ");
            log.info("=====================================================================================================");
            log.info("");


        }


    }


    /**
     * Start a container with the given image and project.
     *
     * @param image   the image to start the container from.
     * @param project the project name.
     * @return a map entry with the container id as key and a boolean indicating if the container was started as value.
     */
    public Map.Entry<String, Boolean> startContainer(String image, String project) {
        try {
            dockerClient.inspectImageCmd(image).exec();
        } catch (NotFoundException e) {
            try {
                log.info("Pulling image for {}", image);
                dockerClient.pullImageCmd(image)
                        .exec(new PullImageResultCallback())
                        .awaitCompletion();
            } catch (Exception ex) {
                log.error("Image not found for {}", image, ex);
                return null;
            }
        }
        // Create container
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image)
                .withWorkingDir("/" + project)
                .withCmd("sh", "-c", "echo 'Hello World'");
        CreateContainerResponse container = containerCmd.exec();
        String containerId = container.getId();
        // Start container
        dockerClient.startContainerCmd(containerId).exec();
        log.info("Created container for " + image);

        return Map.entry(containerId, true);
    }

    /**
     * Copy the project from the container to the local file system.
     *
     * @param containerId the container id
     * @param project     the project name
     * @param dir         the directory where the project should be copied to
     * @return the path to the project
     */
    public Path copyProject(String containerId, String project, Path dir) {
        containers.add(containerId);

        try (InputStream dependencyStream = dockerClient.copyArchiveFromContainerCmd(containerId, "/" + project)
                .exec()) {
            try (TarArchiveInputStream tarStream = new TarArchiveInputStream(dependencyStream)) {
                TarArchiveEntry entry;
                while ((entry = tarStream.getNextTarEntry()) != null) {
                    if (!entry.isDirectory()) {
                        Path filePath = dir.resolve(entry.getName());

                        if (!Files.exists(filePath)) {
                            Files.createDirectories(filePath.getParent());
                            Files.createFile(filePath);

                            byte[] fileContent = tarStream.readAllBytes();
                            Files.write(filePath, fileContent, StandardOpenOption.WRITE);
                        }
                    }
                }
            }
            log.info("Successfully copied the project {}.", project);
            return dir;
        } catch (Exception e) {
            log.error("Could not copy the project {}", project, e);
            return null;
        }
    }

    public void deleteContainers(String breakingImage) {


        containers.forEach(container -> {
            try {
                dockerClient.listContainersCmd().withShowAll(true).exec().forEach(container1 -> {
                    if (container1.getId().equals(container)) {

                        dockerClient.removeContainerCmd(container).withForce(true).exec();
                    }
                });
                dockerClient.removeContainerCmd(container).withForce(true).exec();
            } catch (Exception e) {
                log.error("Container {} could not be deleted.", container);
            }
        });

        try {

            if (breakingImage != null)
                dockerClient.removeImageCmd(breakingImage).withForce(true).exec();

        } catch (Exception e) {
            log.error("Container {} could not be deleted.", breakingImage);
        }
        containers.clear();
        log.info("removed images and containers.");
    }

    private void createDockerClient() {
        DockerClientConfig clientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withRegistryUrl("https://hub.docker.com")
                .build();
        DockerHttpClient httpClient = new OkDockerHttpClient.Builder()
                .dockerHost(clientConfig.getDockerHost())
                .sslConfig(clientConfig.getSSLConfig())
                .connectTimeout(30)
                .build();
        dockerClient = DockerClientImpl.getInstance(clientConfig, httpClient);
    }

    public DockerImages() {
        createDockerClient();
    }


}
