package de.smartsquare.ddd.sonarqube.sensor;

import com.google.common.collect.ImmutableList;
import com.sonar.sslr.api.typed.ActionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.java.SonarComponents;
import org.sonar.java.ast.JavaAstScanner;
import org.sonar.java.ast.parser.JavaParser;
import org.sonar.java.model.VisitorsBridge;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.JavaVersion;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.squidbridge.api.CodeVisitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to instantiate check classes and to run a code scanner
 * to execute them on a given set of files.
 * Dependencies to checks can be injected by subclasses.
 */
abstract class ScannerRun<T extends JavaCheck> {

    private static final Logger LOG = LoggerFactory.getLogger(ScannerRun.class);

    private final SonarComponents sonarComponents;
    private final JavaVersion javaVersion;
    private Iterable<T> javaChecks;
    private List<File> javaClasspath;

    ScannerRun(SonarComponents sonarComponents, List<File> classpath, JavaVersion javaVersion) {
        this.sonarComponents = sonarComponents;
        this.javaClasspath = classpath;
        this.javaVersion = javaVersion;
    }

    void registerChecks(List<Class<? extends T>> classes) {
        javaChecks = instantiateChecks(classes);
    }

    void registerCheckInstances(List<? extends T> checks) {
        for (T check : checks) {
            inject(check);
        }
        javaChecks = ImmutableList.copyOf(checks);
    }

    private Iterable<T> instantiateChecks(List<Class<? extends T>> classes) {
        ArrayList<T> checks = new ArrayList<>();
        for (Class<? extends T> c : classes) {
            try {
                T check = c.newInstance();
                inject(check);
                checks.add(check);
            } catch (Exception e) {
                LOG.warn("Could not instantiate check: " + c.getSimpleName(), e);
            }
        }
        return checks;
    }

    abstract void inject(T check);

    void scan(Iterable<File> sourceFiles) {
        JavaAstScanner scanner = createAstScanner(javaVersion, javaChecks);
        scanner.scan(sourceFiles);
    }

    private JavaAstScanner createAstScanner(JavaVersion javaVersion, Iterable<? extends CodeVisitor> visitors) {
        ActionParser<Tree> parser = JavaParser.createParser();
        JavaAstScanner astScanner = new JavaAstScanner(parser, sonarComponents);
        VisitorsBridge visitorsBridge = new VisitorsBridge(visitors,
                javaClasspath, sonarComponents, false);
        visitorsBridge.setJavaVersion(javaVersion);
        astScanner.setVisitorBridge(visitorsBridge);
        return astScanner;
    }
}