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

class QueryApp {
    private static Logger LOG = LoggerFactory.getLogger(QueryApp.class)

    static void main(String[] args) {
        CollectedBuildQuery query = new CollectedBuildQuery('localhost:9092', 'build-query',
                'builds')

        query.init()
        query.initQuery()
        query.start()

        Spark.port(8083)
        Spark.get('/query/builds', { Request request, Response response ->
            response.header('Content-Type', ContentType.TEXT.toString())

            def trackingId = request.queryParams("trackingId")

            if (trackingId) return KafkaConsts.JSON_PRETTY.writeValueAsString(query.fetchBuild(trackingId))
            else return KafkaConsts.JSON_PRETTY.writeValueAsString(query.fetchBuilds())
        })
    }
}
