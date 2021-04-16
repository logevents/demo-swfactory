package demo.swfactory.model

class BuildFinished extends BaseEntity {
    final String _type = "BuildFinished"
    Date finished

    String key() {
        trackingId
    }

    @Override
    String label() {
        return "build-finished-${key()}"
    }

    @Override
    String toString() {
        return "BuildFinished{" +
                "_type='" + _type + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", finished=" + finished +
                '}';
    }
}
