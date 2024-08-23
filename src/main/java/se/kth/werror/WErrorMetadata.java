package se.kth.werror;

import lombok.Getter;
import lombok.Setter;
import se.kth.log_Analyzer.MavenErrorLog;

import java.io.File;
import java.util.List;

/**
 * This class is used to store the metadata of the Werror.
 * It contains information about the error line, warning list, whether there is a client configuration problem, and a list of Werror files.
 *
 * @author frankreyesgarcia
 */
@Getter
@Setter
public class WErrorMetadata {

    /**
     * The error line in the Maven log.
     */
    private MavenErrorLog errorLine;

    /**
     * The list of warning lines in the Maven log.
     */
    private MavenErrorLog warningList;

    /**
     * The list of files where Werror is detected.
     */
    private List<File> wErrorFiles;

    /**
     * A flag indicating whether there is a client configuration problem.
     */
    private Boolean isClientConfigProblem;

    private String client;

    /**
     * Constructs a new WErrorMetadata object with the specified error line, warning list, client configuration problem flag, and list of Werror files.
     *
     * @param errorLine             The error line in the Maven log.
     * @param warningList           The list of warning lines in the Maven log.
     * @param isClientConfigProblem A flag indicating whether there is a client configuration problem.
     * @param wErrorFiles           The list of files where Werror is detected.
     */
    public WErrorMetadata(MavenErrorLog errorLine, MavenErrorLog warningList, Boolean isClientConfigProblem, List<File> wErrorFiles, String client) {
        this.errorLine = errorLine;
        this.warningList = warningList;
        this.isClientConfigProblem = isClientConfigProblem;
        this.wErrorFiles = wErrorFiles;
        this.client = client;
    }
}
