package demo.swfactory.pusher

import com.fasterxml.jackson.databind.ObjectMapper
import demo.swfactory.model.CollectedBuild
import demo.swfactory.pusher.generation.CollectedBuildGeneration
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.HttpEntity
import org.apache.hc.core5.http.io.entity.StringEntity

class Pusher {
    private String target
    private ObjectMapper mapper = new ObjectMapper()

    static void main(String[] args) {
        new Pusher("localhost:9092").push()
    }

    Pusher(String target) {
        this.target = target
    }

    void push() {
        CollectedBuild collectedBuild = CollectedBuildGeneration.generateEasyBuild()
        send(collectedBuild)
    }

    private void send(Object objectToSend) {
        CloseableHttpClient client = HttpClients.createDefault()
        try {
            HttpPost request = new HttpPost(target);
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
