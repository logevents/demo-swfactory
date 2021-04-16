package demo.swfactory.model

class Result extends BaseEntity {
    final String _type = "Result"
    String component
    String result
    Date started
    Date finished
    Set<ResultProperty> properties

    String key() {
        trackingId + '-' + component
    }

    String buildKey() {
        trackingId
    }

    @Override
    String label() {
        return "result-${key()}"
    }

    @Override
    String toString() {
        return "Result{" +
                "_type='" + _type + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", component='" + component + '\'' +
                ", result='" + result + '\'' +
                ", started=" + started +
                ", finished=" + finished +
                ", properties=" + properties +
                '}';
    }
}
