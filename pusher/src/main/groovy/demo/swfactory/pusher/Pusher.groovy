package demo.swfactory.pusher

import com.fasterxml.jackson.databind.ObjectMapper
import demo.swfactory.model.Result
import demo.swfactory.pusher.generation.BuildGeneration
import demo.swfactory.pusher.generation.ResultGeneration
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.entity.StringEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Pusher {
    private static Logger LOG = LoggerFactory.getLogger(Pusher.class)
    private String target
    private ObjectMapper mapper = new ObjectMapper()

    static void main(String[] args) {
        new Pusher("http://localhost:8080/import").push()
    }

    Pusher(String target) {
        this.target = target
    }

    void push() {
        String trackingId = UUID.randomUUID().toString()

        send(BuildGeneration.generateBuild(trackingId))
        def results = ResultGeneration.generateResults(trackingId, 10)
        for(Result result : results ){
            send(result)
        }
        send(BuildGeneration.generateBuildFinished(trackingId))
    }

    private void send(Object objectToSend) {
        CloseableHttpClient client = HttpClients.createDefault()
        try {
            LOG.info("Send {} to {}", objectToSend.getClass().getName(), target)
            HttpPost request = new HttpPost(target)
            request.setEntity(new StringEntity(toJson(objectToSend)))
            client.execute(request)
        } finally {
            client.close()
        }
    }

    String toJson(Object obj) {
        return mapper.writeValueAsString(obj)
    }
}
