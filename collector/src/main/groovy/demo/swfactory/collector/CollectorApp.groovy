package demo.swfactory.collector

import demo.swfactory.common.KafkaConsts
import demo.swfactory.common.ProducerClient
import demo.swfactory.model.BaseEntity
import groovyx.net.http.ContentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import spark.Spark

class CollectorApp {
    private static Logger LOG = LoggerFactory.getLogger(CollectorApp.class)

    static void main(String[] args) {
        ProducerClient<BaseEntity> client = new ProducerClient<>('localhost:9092', 'pusher', KafkaConsts.JSON_SE)
        client.init()

        CollectorService collectorService = new CollectorService()
        def storeStream = new CollectorStream('localhost:9092', 'collector1',
                'collector-flights-cache', 'store1-result-cache-changelog', 'builds', 5, collectorService)
        CollectorStateQuery query = new CollectorStateQuery('collector-flights-cache', storeStream)
        storeStream.init()
        query.init()
        storeStream.start()


        Spark.port(8082)
        Spark.get('/query/fragment', { Request request, Response response ->
            response.header('Content-Type', ContentType.TEXT.toString())

            def trackingId = request.queryParams("trackingId")

            return KafkaConsts.JSON_PRETTY.writeValueAsString(query.fetchFragment(trackingId))
        })
    }
}
