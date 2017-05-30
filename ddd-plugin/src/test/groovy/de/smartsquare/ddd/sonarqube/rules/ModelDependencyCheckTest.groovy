package de.smartsquare.ddd.sonarqube.rules

import de.smartsquare.ddd.sonarqube.collect.ModelCollection
import org.sonar.api.config.MapSettings
import org.sonar.java.checks.verifier.JavaCheckVerifier
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
class ModelDependencyCheckTest extends Specification {

    def "test"() {
        given:
        def check = new ModelDependencyCheck()
        def collection = Mock(ModelCollection)
        def settings = Mock(MapSettings)
        check.setModelCollection(collection)
        check.setSettings(settings)

        when:
        JavaCheckVerifier.verify("src/test/files/dependencyTest/model/TestEntity.java",
                check,
                [new File("src/test/files/dependencyTest/ui/SampleController.java")])

        then:
        collection.contains("dependencyTest.model.TestEntity") >> true
        collection.contains(_) >> false
        collection.findModelPackages() >> ["dependencyTest.model"]
        settings.getString("sonar.ddd.applicationPackage") >> "dependencyTest"
    }
}
