package demo.swfactory.pusher

import demo.swfactory.model.Build
import demo.swfactory.model.Result
import demo.swfactory.pusher.generation.BuildGeneration
import demo.swfactory.pusher.generation.ResultGeneration
import spock.lang.Specification

class PusherSpec extends Specification {

    def "import build, result and buildFinished"() {
        Pusher p = new Pusher("http://localhost:8080/import")

        String trackingId = UUID.randomUUID().toString()
        when:
        p.send(BuildGeneration.generateBuild(trackingId))
        def results = ResultGeneration.generateResults(trackingId, 10)
        for (Result result : results) {
            p.send(result)
        }
        p.send(BuildGeneration.generateBuildFinished(trackingId))

        then:
        true
    }

    def "import build with validation exception"() {
        Pusher p = new Pusher("http://localhost:8080/import")

        String trackingId = UUID.randomUUID().toString()
        Build build = BuildGeneration.generateBuild(trackingId)
        build.project = "INVALID"
        when:
        p.send(build)

        then:
        true
    }
}
