package demo.swfactory.pusher

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.entity.StringEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Pusher {
    private static Logger LOG = LoggerFactory.getLogger(Pusher.class)
    private String target
    private ObjectMapper mapper = new ObjectMapper()

    Pusher(String target) {
        this.target = target
    }

    void send(Object objectToSend) {
        CloseableHttpClient client = HttpClients.createDefault()
        try {
            LOG.info("Send {} to {}", objectToSend.getClass().getName(), target)
            HttpPost request = new HttpPost(target)
            request.setEntity(new StringEntity(toJson(objectToSend)))
            CloseableHttpResponse response = client.execute(request)

            if (response.getCode() == 200) {
                LOG.info("Success")
            } else {
                LOG.info("Error {}", response.getCode())
            }
        } finally {
            client.close()
        }
    }

    String toJson(Object obj) {
        return mapper.writeValueAsString(obj)
    }
}
