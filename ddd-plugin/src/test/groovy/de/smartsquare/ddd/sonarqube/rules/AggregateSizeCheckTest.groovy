package de.smartsquare.ddd.sonarqube.rules

import com.google.common.graph.GraphBuilder
import com.google.common.graph.ImmutableGraph
import de.smartsquare.ddd.sonarqube.collect.ModelCollection
import org.sonar.java.checks.verifier.JavaCheckVerifier
import spock.lang.Specification

class AggregateSizeCheckTest extends Specification {

    def "should detect aggregates with high depth"() {
        given:
        def check = new AggregateSizeCheck()
        def collection = Mock(ModelCollection)
        def graph = GraphBuilder.directed().build()

        graph.putEdge("Root1", "Entity1")
        graph.putEdge("Entity1", "Entity2")
        def immutableGraph = ImmutableGraph.copyOf(graph)

        check.setAggregateGraph(immutableGraph)
        check.setModelCollection(collection)

        when:
        JavaCheckVerifier.verify("src/test/files/AggregateSizeCheck_sample_depth.java", check)

        then:
        collection.hasEntity("Entity1") >> true
        collection.hasEntity("Entity2") >> true
        collection.hasAggregateRoot("Root1") >> true
    }

    def "should detect aggregates with too many entities"() {
        given:
        def check = new AggregateSizeCheck()
        def collection = Mock(ModelCollection)
        def graph = GraphBuilder.directed().build()

        graph.putEdge("Root", "Entity1")
        graph.putEdge("Root", "Entity2")
        graph.putEdge("Root", "Entity3")
        def immutableGraph = ImmutableGraph.copyOf(graph)

        check.setAggregateGraph(immutableGraph)
        check.setModelCollection(collection)

        when:
        JavaCheckVerifier.verify("src/test/files/AggregateSizeCheck_sample_size.java", check)

        then:
        collection.hasEntity("Entity1") >> true
        collection.hasEntity("Entity2") >> true
        collection.hasEntity("Entity3") >> true
        collection.hasEntity("Root") >> true
        collection.hasAggregateRoot("Root") >> true
    }
}