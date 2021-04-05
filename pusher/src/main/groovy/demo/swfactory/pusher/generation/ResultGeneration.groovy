package demo.swfactory.pusher.generation

import demo.swfactory.model.Result

class ResultGeneration {

    static Set<Result> generateResults(String trackingId, int number) {
        Set<Result> set = [].toSet()
        for (int i = 0; i < number; i++) {
            set.add(generateResult(trackingId))
        }
        return set
    }

    static Result generateResult(String trackingId) {
        return new Result(trackingId: trackingId,
                component: RandomComponentNames.generate(3),
                result: "Verified",
                started: new Date(1000),
                finished: new Date(3000))
    }
}
