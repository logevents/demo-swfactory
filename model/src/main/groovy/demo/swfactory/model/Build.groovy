package demo.swfactory.model

class Build {
    enum State {STARTED, FINISHED}
    final String _type = "Build"
    String trackingId
    String project
    Date started


    @Override
    public String toString() {
        return "Build{" +
                "_type='" + _type + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", project='" + project + '\'' +
                ", started=" + started +
                '}';
    }
}
