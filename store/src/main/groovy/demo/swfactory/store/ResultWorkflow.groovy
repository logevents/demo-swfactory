package demo.swfactory.store

import demo.swfactory.model.Build
import demo.swfactory.model.Fragment
import demo.swfactory.model.Result
import org.apache.kafka.streams.state.KeyValueStore

class ResultWorkflow {
    void process(Object msg, KeyValueStore<String, Fragment> importCache) {
        if (msg instanceof Build) {
            processBuild(msg, importCache)
        } else if (msg instanceof Result) {
            processResult(msg, importCache)
        }
    }

    void processBuild(Build build, KeyValueStore<String, Fragment> resultCache) {
        validateCreateBuild(resultCache, build)

        def fragment = new Fragment(build: build, resultKeys: [])
        resultCache.put(fragment.key(), fragment)
    }

    private void validateCreateBuild(KeyValueStore<String, Fragment> resultCache, Build build) {
        def existingBuild = resultCache.get(build.key())
        if (existingBuild) {
            throw new ValidationException("error during adding new build, build with id ${build.trackingId} already exists")
        }
    }

    void processResult(Result result, KeyValueStore<String, Fragment> resultCache) {
        def build = resultCache.get(result.buildKey())
        if (!build) {
            throw new ValidationException("build with id ${result.buildKey()} does not exist")
        }

        build.resultKeys.add(result.key())
        resultCache.put(build.key(), build)

        def resultFragment = new Fragment(result: result)

        resultCache.put(resultFragment.key(), resultFragment)
    }
}
