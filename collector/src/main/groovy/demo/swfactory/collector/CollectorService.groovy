package demo.swfactory.collector

import demo.swfactory.model.Build
import demo.swfactory.model.BuildFinished
import demo.swfactory.model.CollectedBuild
import demo.swfactory.model.Fragment
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.stream.Collectors

import static demo.swfactory.model.Build.State.*

class CollectorService {
    private static Logger LOG = LoggerFactory.getLogger(CollectorService.class)

    void collectAndForward(KeyValueStore<String, Fragment> store, ProcessorContext processorContext) {
        LOG.info("collectAndForward")
        Fragment updated = getUpdated(store)
        def updatedBuilds = updated.updated

        LOG.info("Having {} builds updated", updatedBuilds.size())


        updatedBuilds.stream()
                .map { trackingId -> collectBuild(store, trackingId) }
                .peek { LOG.info("collected {} and forward it now", it.trackingId) }
                .forEach {
                    processorContext.forward(it.trackingId, it)
                }

        updated.updated = [].toSet()
        store.put("updated", updated)
    }

    void putFragment(KeyValueStore<String, Fragment> store, String key, Fragment fragment) {
        LOG.info("putFragment {} {}", key, fragment)

        store.put(key, fragment)

        updateUpdatedFragment(store, fragment.trackingId())
    }

    private void updateUpdatedFragment(KeyValueStore<String, Fragment> store, String trackingId) {
        Fragment updated = getUpdated(store)
        updated.updated.add(trackingId)
        LOG.info("add to updated: {}", trackingId)
        LOG.debug("full updated: {}", updated.updated)
        store.put("updated", updated)
    }

    private Fragment getUpdated(KeyValueStore<String, Fragment> store) {
        Fragment updated = store.get('updated')
        if (updated == null) {
            updated = new Fragment()
            updated.setUpdated(new HashSet<String>())
            LOG.info("creating new updated")
        }
        updated
    }

    CollectedBuild collectBuild(KeyValueStore<String, Fragment> store, String trackingId) {
        Fragment buildFragment = store.get(trackingId)


        //TODO how to get buildFinished??
        CollectedBuild collectedBuild = createFromBuilds(buildFragment.build, null)

        collectedBuild.results = buildFragment.getResultKeys()
                .stream()
                .map { k -> store.get(k).result }
                .collect(Collectors.toSet())

        return collectedBuild
    }

    CollectedBuild createFromBuilds(Build started, BuildFinished finished) {
        def collectedBuild = new CollectedBuild()

        collectedBuild.trackingId = started.trackingId
        collectedBuild.project = started.project
        collectedBuild.started = started.started

        if (finished == null) {
            collectedBuild.state = STARTED
        } else {
            collectedBuild.state = FINISHED
            collectedBuild.finished = finished.finished
        }
        return collectedBuild
    }
}
