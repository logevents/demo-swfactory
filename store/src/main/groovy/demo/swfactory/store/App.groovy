package demo.swfactory.store

import demo.swfactory.model.BaseEntity
import groovyx.net.http.ContentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import spark.Spark

class App {
    private static Logger LOG = LoggerFactory.getLogger(App.class)

    static void main(String[] args) {
        ProducerClient<BaseEntity> client = new ProducerClient<>('localhost:9092', 'pusher', KafkaConsts.JSON_SE)
        client.init()

        def resultWorkflow = new ResultWorkflow()
        def feedbackChannel = new FeedbackChannel()
        def storeStream = new StoreStream(resultWorkflow, feedbackChannel,
                'localhost:9092', 'store1', 'result-source')

        storeStream.start()

        Spark.port(8080)
        Spark.post('/import', { Request request, Response response ->
            response.header('Content-Type', ContentType.TEXT.toString())
            def msg = StoreStream.createInstance(request.body())

            //TODO: maybe some validation before processing

            LOG.info("received msg {}", msg.label())

            FeedbackChannel.Feedback feedback = feedbackChannel.register(msg)
            client.send('result-source', msg.trackingId, msg)
            feedback.waitUntilFinished()

            if (feedback.e) {
                LOG.info("import error: {}", msg.label(), feedback.e)
                if (feedback.e instanceof ValidationException) {
                    response.status(400)
                    return 'validation error: ' + feedback.e.message
                } else {
                    response.status(500)
                    return 'unknown error: ' + feedback.e.message
                }

            } else {
                LOG.info("import successful {}", msg.label())
                return 'success'
            }
        })
    }
}
