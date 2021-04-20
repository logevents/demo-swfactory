package demo.swfactory.common

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import demo.swfactory.common.JsonDeserializer
import demo.swfactory.common.JsonSerializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes

class KafkaConsts {
    static final JSON = createMapper()
    static final JSON_PRETTY = createPrettyMapper()

    private static ObjectMapper createMapper() {
        def mapper = new ObjectMapper()
        mapper.setTimeZone(TimeZone.getTimeZone('UTC'))
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        mapper
    }

    private static ObjectMapper createPrettyMapper() {
        def mapper = createMapper()
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true)
        return mapper
    }

    static final JSON_SE = new JsonSerializer()

    static <T> Serde<T> createJsonSerde(Class<T> c) {
        Serdes.serdeFrom(JSON_SE, new JsonDeserializer<T>(c))
    }
}
