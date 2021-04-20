package demo.swfactory.common


import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig

import java.util.function.Consumer

abstract class StreamsRunner {
    private props
    KafkaStreams stream

    StreamsRunner(String servers, String appId) {
        props = new Properties()
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appId)
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, servers)
    }

    def init(){
        stream = new KafkaStreams(createTopologyBuilder().build(), props)
    }

    def start() {
        stream.start()
        Runtime.getRuntime().addShutdownHook({ stream.close() })
    }


    void setListener(Consumer<KafkaStreams> c) {
        stream.setStateListener({ newState, oldState ->
            if (newState.name() == "RUNNING") {
                c.accept(stream)
            }
        })
    }

    abstract StreamsBuilder createTopologyBuilder()
}
