package demo.swfactory.collector

import demo.swfactory.common.KafkaConsts
import demo.swfactory.common.StreamsRunner
import demo.swfactory.model.*
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.state.Stores
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class CollectorStream extends StreamsRunner {
    private static Logger LOG = LoggerFactory.getLogger(CollectorStream.class)
    private final String importStore
    private final String importStateStore
    private final String collectedBuildTopic
    private final int forwardInterval
    private final CollectorService collectorService

    CollectorStream(String servers,
                    String appId,
                    String importStateStore,
                    String importStore,
                    String collectedBuildTopic,
                    int forwardInterval,
                    CollectorService collectorService
    ) {
        super(servers, appId)
        this.collectedBuildTopic = collectedBuildTopic
        this.importStateStore = importStateStore
        this.importStore = importStore
        this.forwardInterval = forwardInterval
        this.collectorService = collectorService
    }

    @Override
    StreamsBuilder createTopologyBuilder() {
        def builder = new StreamsBuilder()
        def fragmentSerde = KafkaConsts.createJsonSerde(Fragment)
        def flightSerde = KafkaConsts.createJsonSerde(CollectedBuild)

        def results = builder.stream(importStore, Consumed.with(Serdes.String(), fragmentSerde))

        LOG.info("createTopologyBuild")

        builder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(importStateStore),
                        Serdes.String(),
                        fragmentSerde))

        results.filter { key, value -> value != null }
                .transform({ new CollectorTransformer(importStateStore, forwardInterval, collectorService) }, importStateStore)
                .to(collectedBuildTopic, Produced.with(Serdes.String(), flightSerde))

        return builder
    }
}
