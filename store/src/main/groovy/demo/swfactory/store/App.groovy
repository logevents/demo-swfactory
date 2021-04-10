package demo.swfactory.store

import demo.swfactory.model.BaseEntity
import groovyx.net.http.ContentType
import spark.Request
import spark.Response
import spark.Spark

class App {
    static void main(String[] args) {
        ProducerClient<BaseEntity> client = new ProducerClient<>('localhost:9092', 'pusher', KafkaConsts.JSON_SE)
        client.init()

        def resultWorkflow = new ResultWorkflow()
        def feedbackChannel = new FeedbackChannel()
        def storeStream = new StoreStream(resultWorkflow, feedbackChannel,
                'localhost:9092', 'store1',
                'latest', 'result-source')

        storeStream.start()

        Spark.port(8080)
        Spark.post('/import', { Request request, Response response ->
            response.header('Content-Type', ContentType.TEXT.toString())
            def msg = StoreStream.createInstance(request.body())

            //TODO: maybe some validation before processing

            def feedback = feedbackChannel.register(msg)
            client.send('result-source', msg.trackingId, msg)
            feedback.waitUntilFinished()

            if (feedback.e) {
                response.status(500)
                return 'error'
            } else {
                return 'success'
            }
        })
    }
}
