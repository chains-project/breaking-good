package se.kth.breaking_changes;

import japicmp.cli.JApiCli;
import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChange;
import japicmp.output.OutputFilter;
import japicmp.output.semver.SemverOut;
import japicmp.output.xml.XmlOutput;
import japicmp.output.xml.XmlOutputGenerator;
import japicmp.output.xml.XmlOutputGeneratorOptions;
import japicmp.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class JApiCmpAnalyze {
    private static final Logger log = LoggerFactory.getLogger(JApiCmpAnalyze.class);


    private final Path oldJar;
    private final Path newJar;


    public JApiCmpAnalyze(Path oldJar, Path newJar) {
        this.oldJar = oldJar;
        this.newJar = newJar;
    }

    private static Options getDefaultOptions() {
        Options defaultOptions = Options.newDefault();
        defaultOptions.setAccessModifier(AccessModifier.PROTECTED);
        defaultOptions.setOutputOnlyModifications(true);
        defaultOptions.setXmlOutputFile(Optional.of("output.xml"));
        defaultOptions.setClassPathMode(JApiCli.ClassPathMode.TWO_SEPARATE_CLASSPATHS);
        defaultOptions.setIgnoreMissingClasses(true);
        defaultOptions.setReportOnlyFilename(true);
        String[] excl = {"(*.)?tests(.*)?", "(*.)?test(.*)?",
                "@org.junit.After", "@org.junit.AfterClass",
                "@org.junit.Before", "@org.junit.BeforeClass",
                "@org.junit.Ignore", "@org.junit.Test",
                "@org.junit.runner.RunWith"};

        for (String e : excl) {
            defaultOptions.addExcludeFromArgument(Optional.of(e), false);
        }

        return defaultOptions;
    }

    public Set<ApiChange> useJApiCmp() {
        Options defaultOptions = getDefaultOptions();
        JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(defaultOptions);

        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
        JApiCmpArchive newF = new JApiCmpArchive(newJar.toFile(), newJar.getFileName().toString());
        JApiCmpArchive old = new JApiCmpArchive(oldJar.toFile(), oldJar.getFileName().toString());

        List<JApiClass> jApiClasses = jarArchiveComparator.compare(old, newF);
        OutputFilter filter = new OutputFilter(defaultOptions);
        filter.filter(jApiClasses);
        Set<ApiChange> libraryChanges = new HashSet<>();

        //list of classes
        jApiClasses.forEach(jApiClass -> {

            //read incompatible changes
            jApiClass.getCompatibilityChanges().forEach(jApiCompatibilityChange -> {
                Collection<JApiCompatibilityChange> changes = jApiClass.getCompatibilityChanges();
                //go for each change
                jApiClasses.iterator().forEachRemaining(jApiClass1 -> {
                    //get methods
                    jApiClass1.getMethods().forEach(jApiMethod -> {
                        if (jApiMethod.getChangeStatus().equals(JApiChangeStatus.REMOVED)) {
                            libraryChanges.add(new ApiChange(
                                    jApiMethod.getOldMethod().isPresent() ? jApiMethod.getOldMethod().get().getName() : "null",
                                    jApiMethod.getNewMethod().isPresent() ? jApiMethod.getNewMethod().get().getName() : "null",
                                    jApiMethod.getCompatibilityChanges().toString(),
                                    jApiMethod.getName()
                            ));
                        }
                    });
                });
            });
        });
        return libraryChanges;
    }

    public void getChanges() {

        log.info("Comparing {} with {}", this.newJar.getFileName(), this.oldJar.getFileName());
        Options defaultOptions = getDefaultOptions();
        JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(defaultOptions);

        final var jApiClasses = getjApiClasses(comparatorOptions, defaultOptions);


        defaultOptions.setXmlOutputFile(Optional.of("output.xml"));

        SemverOut semverOut = new SemverOut(defaultOptions, jApiClasses);
        XmlOutputGeneratorOptions xmlOutputGeneratorOptions = new XmlOutputGeneratorOptions();
        xmlOutputGeneratorOptions.setCreateSchemaFile(true);
        xmlOutputGeneratorOptions.setSemanticVersioningInformation(semverOut.generate());
        XmlOutputGenerator xmlGenerator = new XmlOutputGenerator(jApiClasses, defaultOptions, xmlOutputGeneratorOptions);
        try (XmlOutput xmlOutput = xmlGenerator.generate()) {
            XmlOutputGenerator.writeToFiles(defaultOptions, xmlOutput);
        } catch (Exception e) {
            throw new JApiCmpException(JApiCmpException.Reason.IoException, "Could not close output streams: " + e.getMessage(), e);
        }
    }

    private List<JApiClass> getjApiClasses(JarArchiveComparatorOptions comparatorOptions, Options defaultOptions) {
        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
        JApiCmpArchive newF = null;

        newF = new JApiCmpArchive(newJar.toFile(), newJar.getFileName().toString());

        JApiCmpArchive old = new JApiCmpArchive(oldJar.toFile(), oldJar.getFileName().toString());

        List<JApiClass> jApiClasses = jarArchiveComparator.compare(old, newF);
        OutputFilter filter = new OutputFilter(defaultOptions);
        filter.filter(jApiClasses);
        return jApiClasses;
    }

    public Map<String, Set<String>> extractResult(String jApiCmpXml, Set<String> spoonedElements) {
        return readXMLFilesFromSubfolder(jApiCmpXml, spoonedElements);
    }

    private Map<String, Set<String>> readXMLFilesFromSubfolder(String subfolderPath,
                                                               Set<String> spoonedElements) {
        Map<String, Set<String>> japicmpResult = new HashMap<>();
        File subfolder = new File(subfolderPath);
        if (true) {
//            File[] xmlFiles = subfolder.listFiles((dir, name) -> name.endsWith(".xml"));
//            if (xmlFiles != null && xmlFiles.length == 1) {
            File xmlFile = new File(subfolderPath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                return japicmpResult;
            }
            Document document;
            try {
                document = builder.parse(xmlFile);
            } catch (SAXException | IOException e) {
                e.printStackTrace();
                return japicmpResult;
            }

            Element root = document.getDocumentElement();
            Set<String> uniqueCodeValues = new HashSet<>();
            for (String element : spoonedElements) {
                if (element != null) {
                    uniqueCodeValues.addAll(searchAndExtractXML(root, element));
                    japicmpResult.put(element, uniqueCodeValues);
                }
            }
        }
//        }
        return japicmpResult;

    }

    private Set<String> searchAndExtractXML(Element root, String searchWord) {
        NodeList classNodes = root.getElementsByTagName("*");
        Set<String> compatibilityChanges = new HashSet<>();
        for (int i = 0; i < classNodes.getLength(); i++) {
            Element classElement = (Element) classNodes.item(i);
            String fullyQualifiedName = classElement.getAttribute("fullyQualifiedName");
            String name = classElement.getAttribute("name");

            if (fullyQualifiedName.contains(searchWord) || name.contains(searchWord)) {
                NodeList compatibilityChangeNodes = classElement.getElementsByTagName("compatibilityChange");
                for (int j = 0; j < compatibilityChangeNodes.getLength(); j++) {
                    String compatibilityChangeValue = compatibilityChangeNodes.item(j).getTextContent();
                    compatibilityChanges.add(compatibilityChangeValue);
                }
            }
        }
        return compatibilityChanges;
    }

}
