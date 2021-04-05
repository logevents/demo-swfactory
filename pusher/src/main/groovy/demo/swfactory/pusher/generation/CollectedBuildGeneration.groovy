package demo.swfactory.pusher.generation

import demo.swfactory.model.Build
import demo.swfactory.model.CollectedBuild
import demo.swfactory.model.Result

class CollectedBuildGeneration {
    static CollectedBuild generateEasyBuild() {
        String trackingId = UUID.randomUUID().toString()

        Set<Result> results = ResultGeneration.generateResults(trackingId, 10)

        new CollectedBuild(trackingId: trackingId,
                project: "EasyBuild",
                state: Build.State.FINISHED,
                started: new Date(2000),
                finished: new Date(2000),
                results: results)
    }
}
