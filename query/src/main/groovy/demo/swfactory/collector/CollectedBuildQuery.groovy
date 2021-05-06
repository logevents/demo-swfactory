package demo.swfactory.collector

import demo.swfactory.common.KafkaConsts
import demo.swfactory.common.StreamsRunner
import demo.swfactory.model.CollectedBuild
import demo.swfactory.model.Fragment
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CollectedBuildQuery extends StreamsRunner {
    private static Logger LOG = LoggerFactory.getLogger(CollectedBuildQuery.class)
    private ReadOnlyKeyValueStore<String, CollectedBuild> buildStore
    private String buildTopic

    CollectedBuildQuery(String servers, String appId, String buildTopic) {
        super(servers, appId)
        this.buildTopic = buildTopic
    }

    def initQuery() {
        setListener({
            streams ->
                buildStore = streams.store('build-store', QueryableStoreTypes.keyValueStore())
        })
        LOG.info("Got {} flightStores", buildStore.toString())
    }

    ReadOnlyKeyValueStore<String, CollectedBuild> getBuildStore() {
        buildStore
    }

    CollectedBuild fetchBuild(String key) {
        LOG.info("Searching within build store")
        buildStore.get(key)
    }

    List<CollectedBuild> fetchBuilds() {
        LOG.info("Searching within build store")
        buildStore.all().collect{it.value}
    }

    @Override
    StreamsBuilder createTopologyBuilder() {
        def builder = new StreamsBuilder()

        builder.globalTable(buildTopic, Materialized.as('build-store')
                .withKeySerde(Serdes.String()).withValueSerde(KafkaConsts.createJsonSerde(CollectedBuild))
                as Materialized<String, CollectedBuild, KeyValueStore<Bytes, byte[]>>)

        return builder
    }
}
