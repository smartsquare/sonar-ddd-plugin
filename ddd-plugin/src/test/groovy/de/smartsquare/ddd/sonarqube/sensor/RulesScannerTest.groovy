package de.smartsquare.ddd.sonarqube.sensor

import com.google.common.graph.GraphBuilder
import com.google.common.graph.ImmutableGraph
import de.smartsquare.ddd.sonarqube.collect.ModelCollection
import de.smartsquare.ddd.sonarqube.rules.RulesList
import org.sonar.api.config.Settings
import org.sonar.java.SonarComponents
import org.sonar.java.checks.verifier.JavaCheckVerifier
import org.sonar.plugins.java.api.JavaVersion
import spock.lang.Specification

import java.nio.file.Paths

class RulesScannerTest extends Specification {

    def "should collect model classes"() {
        given:
        def model = Mock(ModelCollection)
        def settings = Mock(Settings)
        def components = Mock(SonarComponents)
        def graph = GraphBuilder.directed().build()
        def run = new RulesScanner(components,
                JavaCheckVerifier.getFilesRecursively(Paths.get("target/test-jars"), ["jar"] as String[]),
                Mock(JavaVersion),
                model,
                settings, ImmutableGraph.copyOf(graph))

        when:
        run.registerChecks(RulesList.checkClasses())
        run.scan([new File("src/test/files/ImmutabilityCheck_sample.java")])

        then:
        (1.._) * components.reportIssue(_)
        components.fileContent(_) >> ""
        model.hasValueObject("obj1") >> true
        components.symbolizableFor(_) >> new DDDSonarComponents.MockSymbolTable()
    }
}
