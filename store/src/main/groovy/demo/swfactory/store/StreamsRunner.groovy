package demo.swfactory.store


import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig

abstract class StreamsRunner {
    private props

    StreamsRunner(String servers, String appId) {
        props = new Properties()
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appId)
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, servers)
    }

    def start() {
        def streams = new KafkaStreams(createTopologyBuilder().build(), props)
        streams.start()
        Runtime.getRuntime().addShutdownHook({streams.close()})
    }

    abstract StreamsBuilder createTopologyBuilder()
}
