package demo.swfactory.store

import demo.swfactory.model.Build
import demo.swfactory.model.Result
import demo.swfactory.pusher.generation.ResultGeneration
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import spock.lang.Specification

class StreamSpike extends Specification {
    def 'import route'() {
        def client = new RESTClient('http://localhost:8080/import')
        client.post(body: 'test', requestContentType: ContentType.TEXT)

        expect: true
    }

    def 'send build and results'() {
        ProducerClient<Object> client = new ProducerClient<>('localhost:9092', 'pusher', KafkaConsts.JSON_SE)
        client.init()

        def trackingId = UUID.randomUUID().toString()

        Build build = new Build(requestId: UUID.randomUUID().toString(),
                trackingId: trackingId, started: new Date())

        client.send('result-source', trackingId, build).get()

        Set<Result> results = ResultGeneration.generateResults(trackingId, 10)
        results.each {
            it.requestId = UUID.randomUUID().toString()
            client.send('result-source', trackingId, it).get()
        }

        expect: true
    }

    def 'send build and results to rest'() {
        def client = new RESTClient('http://localhost:8080/import')

        def trackingId = UUID.randomUUID().toString()

        Build build = new Build(requestId: UUID.randomUUID().toString(),
                trackingId: trackingId, started: new Date())

        send(client, build)


        Set<Result> results = ResultGeneration.generateResults(trackingId, 10)
        results.each {
            it.requestId = UUID.randomUUID().toString()
            send(client, it)
        }

        expect: true
    }

    private void send(RESTClient client, Object msg) {
        def response = client.post(body: KafkaConsts.JSON.writeValueAsString(msg), requestContentType: ContentType.TEXT)
        println response.data.text
    }

    def 'send some json to topic'() {
        ProducerClient<Object> client = new ProducerClient<>('localhost:9092', 'pusher', KafkaConsts.JSON_SE)
        client.init()

        def trackingId = UUID.randomUUID().toString()

        def build = new Build()
        build.trackingId = trackingId

        client.send('result-source', trackingId, build).get()

        expect: true
    }

    def 'send result without build to topic'() {
        ProducerClient<Object> client = new ProducerClient<>('localhost:9092', 'pusher', KafkaConsts.JSON_SE)
        client.init()

        def trackingId = UUID.randomUUID().toString()

        def result = new Result()
        result.trackingId = trackingId
        result.component = 'c1'

        def send = client.send('result-source', trackingId, result)
        send.get()

        expect: true
    }

    def 'aggregate'() {
        def resultWorkflow = new ResultWorkflow()
        def feedbackChannel = new FeedbackChannel()

        def storeStream = new StoreStream(resultWorkflow, feedbackChannel,
                'localhost:9092', 'store1',
                'latest', 'result-source')

        storeStream.start()

        sleep(10000000)

        expect: true
    }

}
