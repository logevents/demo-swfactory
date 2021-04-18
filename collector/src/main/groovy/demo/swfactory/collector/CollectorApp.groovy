package demo.swfactory.collector

import demo.swfactory.common.KafkaConsts
import demo.swfactory.common.ProducerClient
import demo.swfactory.model.BaseEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CollectorApp {
    private static Logger LOG = LoggerFactory.getLogger(CollectorApp.class)

    static void main(String[] args) {
        ProducerClient<BaseEntity> client = new ProducerClient<>('localhost:9092', 'pusher', KafkaConsts.JSON_SE)
        client.init()

        CollectorService collectorService = new CollectorService()
        def storeStream = new CollectorStream('localhost:9092', 'collector1',
                'collector-flights-cache', 'store1-result-cache-changelog', 'collector-collected-builds',5, collectorService)

        storeStream.start()
//
//        Spark.port(8080)
//        Spark.post('/import', { Request request, Response response ->
//            response.header('Content-Type', ContentType.TEXT.toString())
//            def msg = StoreStream.createInstance(request.body())
//
//            //TODO: maybe some validation before processing
//
//            LOG.info("Receive msg {}", msg.getClass().getName())
//
//            FeedbackChannel.Feedback feedback = feedbackChannel.register(msg)
//            client.send('result-source', msg.trackingId, msg)
//            feedback.waitUntilFinished()
//
//            if (feedback.e) {
//                LOG.info("Feedback error: {}",feedback)
//                if(feedback.e instanceof ValidationException){
//                    response.status(400)
//                    return 'ValidationError'
//                }else{
//                    response.status(500)
//                    return 'error'
//                }
//
//            } else {
//                LOG.info("Feedback success {}",feedback)
//                return 'success'
//            }
//        })
    }
}
