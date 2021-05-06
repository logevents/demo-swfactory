package demo.swfactory.collector

import demo.swfactory.common.StreamsRunner
import demo.swfactory.model.Fragment
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CollectorStateQuery {
    private static Logger LOG = LoggerFactory.getLogger(CollectorStateQuery.class)
    private final List<ReadOnlyKeyValueStore<String, Fragment>> flightStores = []
    private final String stateStore
    private final StreamsRunner stream

    CollectorStateQuery(String stateStore,
                        StreamsRunner stream) {
        this.stream = stream
        this.stateStore = stateStore
    }

    void init() {
        stream.setListener({
            streams ->
                flightStores.add(streams.store(stateStore, QueryableStoreTypes.keyValueStore()))
        })
        LOG.info("Got {} flightStores", flightStores.size())
    }

    List<ReadOnlyKeyValueStore<String, Fragment>> getFlightStores() {
        flightStores
    }

    Fragment fetchFragment(String key) {
        LOG.info("Searching within {} flightStores", flightStores.size())
        flightStores
                .collect { it.get(key) }
                .find { it }
    }

}
