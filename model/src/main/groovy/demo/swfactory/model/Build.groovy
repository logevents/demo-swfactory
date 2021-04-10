package demo.swfactory.model

class Build extends BaseEntity {
    enum State {STARTED, FINISHED}
    final String _type = "Build"
    String project
    Date started

    String key() {
        return trackingId
    }

    @Override
    String toString() {
        return "Build{" +
                "_type='" + _type + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", project='" + project + '\'' +
                ", started=" + started +
                '}';
    }
}
