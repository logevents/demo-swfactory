package demo.swfactory.store

import demo.swfactory.model.BaseEntity

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class FeedbackChannel {
    class Feedback {
        private final CountDownLatch latch = new CountDownLatch(1)

        Exception e

        void waitUntilFinished() {
            latch.await(30, TimeUnit.SECONDS)
        }
    }

    private final Map<String, Feedback> feedbackMap = Collections.synchronizedMap([:])

    Feedback register(BaseEntity msg) {
        msg.requestId = UUID.randomUUID().toString()
        def feedback = new Feedback()
        feedbackMap.put(msg.requestId, feedback)

        return feedback
    }

    void finish(String requestId, Exception e) {
        def feedback = feedbackMap.remove(requestId)
        if (!feedback) {
            println "tried to send feedback but no entry found for requestId: $requestId"
        } else {
            feedback.e = e
            feedback.latch.countDown()
        }
    }
}
