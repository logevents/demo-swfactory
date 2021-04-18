package demo.swfactory.collector

import demo.swfactory.model.CollectedBuild
import demo.swfactory.model.Fragment
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.processor.PunctuationType
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.time.temporal.ChronoUnit

class CollectorTransformer implements Transformer<String, Fragment, KeyValue<String, CollectedBuild>> {
    private static Logger LOG = LoggerFactory.getLogger(CollectorStream.class)
    private final String storeName
    private final int forwardInterval
    private KeyValueStore<String, Fragment> store
    private ProcessorContext context
    private final CollectorService collectorService

    CollectorTransformer(String storeName,
                         int forwardInterval,
                         CollectorService collectorService) {
        this.collectorService = collectorService
        this.forwardInterval = forwardInterval
        this.storeName = storeName
    }

    @Override
    void init(ProcessorContext context) {
        store = (KeyValueStore) context.getStateStore(storeName)
        this.context = context

        LOG.info("init CollectorTransformer")

        context.schedule(Duration.of(forwardInterval, ChronoUnit.SECONDS), PunctuationType.WALL_CLOCK_TIME, {
            collectorService.collectAndForward(store, context)
        })
    }

    @Override
    KeyValue<String, CollectedBuild> transform(String key, Fragment value) {
        collectorService.putFragment(store, key, value)
        return null
    }

    @Override
    void close() {

    }
}
