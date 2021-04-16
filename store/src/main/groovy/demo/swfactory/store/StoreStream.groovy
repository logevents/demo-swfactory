package demo.swfactory.store

import demo.swfactory.common.KafkaConsts
import demo.swfactory.common.StreamsRunner
import demo.swfactory.model.*
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.ValueMapper
import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.Stores

class ResultStore implements Processor<String, BaseEntity> {
    private ProcessorContext context
    private KeyValueStore<String, Fragment> resultCache
    private final ResultWorkflow resultWorkflow
    private final FeedbackChannel channel

    ResultStore(ResultWorkflow resultWorkflow, FeedbackChannel channel) {
        this.channel = channel
        this.resultWorkflow = resultWorkflow
    }

    @Override
    void init(ProcessorContext context) {
        this.context = context
        this.resultCache = (KeyValueStore) context.getStateStore('result-cache')
    }

    @Override
    void process(String key, BaseEntity msg) {
        try {
            resultWorkflow.process(msg, resultCache)
            channel.finish(msg.requestId, null)
        } catch (Exception e) {
            println e
            channel.finish(msg.requestId, e)
        }
    }

    @Override
    void close() {

    }
}

class StoreStream extends StreamsRunner {
    private final String resultSourceTopic
    private final ResultWorkflow resultWorkflow
    private final FeedbackChannel feedbackChannel

    StoreStream(ResultWorkflow resultWorkflow,
                FeedbackChannel feedbackChannel,
                String servers, String appId,
                String resultSourceTopic) {
        super(servers, appId)
        this.feedbackChannel = feedbackChannel
        this.resultWorkflow = resultWorkflow
        this.resultSourceTopic = resultSourceTopic
    }

    @Override
    StreamsBuilder createTopologyBuilder() {
        def builder = new StreamsBuilder()

        def results = builder.stream(resultSourceTopic, Consumed.with(Serdes.String(), Serdes.String()))

        builder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore('result-cache'),
                        Serdes.String(),
                        KafkaConsts.createJsonSerde(Fragment)))

        results.mapValues({ rawJson -> createInstance(rawJson) } as ValueMapper<String, BaseEntity>)
                .filter { key, value -> value != null }
                .process({ new ResultStore(resultWorkflow, feedbackChannel) }, 'result-cache')

        return builder
    }

    static BaseEntity createInstance(String rawJson) {
        def generic = KafkaConsts.JSON.readValue(rawJson, Map)
        if (generic._type == 'Build') {
            return KafkaConsts.JSON.readValue(rawJson, Build)
        } else if (generic._type == 'BuildFinished') {
            return KafkaConsts.JSON.readValue(rawJson, BuildFinished)
        } else if (generic._type == 'Result') {
            return KafkaConsts.JSON.readValue(rawJson, Result)
        } else {
            // maybe we should log or throw an exception here
            return null
        }
    }
}
