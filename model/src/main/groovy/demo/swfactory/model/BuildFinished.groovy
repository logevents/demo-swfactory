package demo.swfactory.model

class BuildFinished {
    final String _type = "BuildFinished"
    String trackingId
    Date finished

    @Override
    public String toString() {
        return "BuildFinished{" +
                "_type='" + _type + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", finished=" + finished +
                '}';
    }
}
