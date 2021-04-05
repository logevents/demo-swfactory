package demo.swfactory.model

class CollectedBuild {
    final String _type = "CollectedBuild"
    String trackingId
    Build.State state
    String project
    Date started
    Date finished
    Set<Result> results


    @Override
    public String toString() {
        return "CollectedBuild{" +
                "_type='" + _type + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", state='" + state + '\'' +
                ", started=" + started +
                ", finished=" + finished +
                ", results=" + results +
                '}';
    }
}
