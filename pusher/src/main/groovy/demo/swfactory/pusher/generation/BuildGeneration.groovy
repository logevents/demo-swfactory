package demo.swfactory.pusher.generation

import demo.swfactory.model.Build
import demo.swfactory.model.BuildFinished
import demo.swfactory.model.CollectedBuild
import demo.swfactory.model.Result

class BuildGeneration {

    static Build generateBuild(String trackingId) {


        new Build(trackingId: trackingId,
                project: "EasyBuild",
                started: new Date(2000))
    }

    static BuildFinished generateBuildFinished(String trackingId) {
        return new BuildFinished(trackingId: trackingId, finished: new Date())
    }

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
