package demo.swfactory.collector

import demo.swfactory.model.CollectedBuild
import demo.swfactory.model.Fragment
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CollectorService {
    private static Logger LOG = LoggerFactory.getLogger(CollectorService.class)

    void collectAndForward(KeyValueStore<String, Fragment> store, ProcessorContext processorContext) {
        LOG.info("collectAndForward")
        Fragment updated = getUpdated(store)
        def updatedBuilds = updated.updated

        updatedBuilds.stream()
        .map{trackingId -> collectBuild(store, trackingId)}


        LOG.info("Having {} builds updated", updatedBuilds.size())
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
//        def collectedBuild = new CollectedBuild()
//collectedBuild. = store.get(trackingId)
//
//        def results = build.getResultKeys()


    }
}
