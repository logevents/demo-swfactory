package demo.swfactory.store

import demo.swfactory.common.KafkaConsts
import demo.swfactory.model.Build
import demo.swfactory.model.BuildFinished
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.processor.StateStore
import org.apache.kafka.streams.state.KeyValueIterator
import org.apache.kafka.streams.state.KeyValueStore
import spock.lang.Specification

class DummyStore<V> implements KeyValueStore<String, V> {
    private final String id = UUID.randomUUID().toString()
    final Map<String, V> store = [:]

    @Override
    void put(String key, V value) {
        store.put(key, (V) copyValue(value))
    }

    @Override
    V putIfAbsent(String key, V value) {
        return store.putIfAbsent(key, copyValue(value))
    }

    @Override
    void putAll(List<KeyValue<String, V>> entries) {
        store.putAll(entries.collect { new MapEntry(it.key, copyValue(it.value)) })
    }

    private V copyValue(V value) {
        KafkaConsts.JSON.readValue(KafkaConsts.JSON.writeValueAsString(value), value.getClass() as Class<V>)
    }

    @Override
    V delete(String key) {
        return store.remove(key)
    }

    @Override
    String name() {
        return 'dummyStore-' + id
    }

    @Override
    void init(ProcessorContext context, StateStore root) {

    }

    @Override
    void flush() {

    }

    @Override
    void close() {

    }

    @Override
    boolean persistent() {
        return false
    }

    @Override
    boolean isOpen() {
        return false
    }

    @Override
    V get(String key) {
        return store.get(key)
    }

    @Override
    KeyValueIterator<String, V> range(String from, String to) {
        return null
    }

    @Override
    KeyValueIterator<String, V> all() {
        return null
    }

    @Override
    long approximateNumEntries() {
        return store.keySet().size()
    }
}

class ResultWorkflowSpec extends Specification {
    def 'test'() {
        def dummyStore = new DummyStore()
        def workflow = new ResultWorkflow()

        when:
        workflow.process(new Build(trackingId: '1'), dummyStore)

        then:
        false
    }
}
