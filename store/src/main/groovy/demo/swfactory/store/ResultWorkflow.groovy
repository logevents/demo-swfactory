package demo.swfactory.store

import demo.swfactory.model.Build
import demo.swfactory.model.BuildFinished
import demo.swfactory.model.Fragment
import demo.swfactory.model.Result
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ResultWorkflow {
    private static Logger LOG = LoggerFactory.getLogger(App.class)

    void process(Object msg, KeyValueStore<String, Fragment> importCache) {
        if (msg instanceof Build) {
            processBuild(msg, importCache)
        } else if (msg instanceof Result) {
            processResult(msg, importCache)
        } else if (msg instanceof BuildFinished) {
            processBuildFinished(msg, importCache)
        } else {
            LOG.error("invalid msg type {}", msg.getClass().getName())
        }
    }

    void processBuild(Build build, KeyValueStore<String, Fragment> resultCache) {
        validateCreateBuild(resultCache, build)

        def fragment = new Fragment(build: build, resultKeys: [], state: Build.State.STARTED)
        resultCache.put(fragment.key(), fragment)
    }

    void processBuildFinished(BuildFinished buildFinished, KeyValueStore<String, Fragment> resultCache) {
        validateBuildExists(buildFinished.trackingId, resultCache)
        def fragment = resultCache.get(buildFinished.key())

        fragment.state = Build.State.FINISHED

        resultCache.put(fragment.key(), fragment)
    }

    void validateBuildExists(String trackingId, KeyValueStore<String, Fragment> resultCache) {
        def fragment = resultCache.get(trackingId)

        if (!fragment) {
            throw new ValidationException("no build for trackingId $trackingId")
        }
    }

    private void validateCreateBuild(KeyValueStore<String, Fragment> resultCache, Build build) {
        if (build.project == "INVALID") {
            throw new ValidationException("error during adding new build, build project name is invalid")
        }
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
