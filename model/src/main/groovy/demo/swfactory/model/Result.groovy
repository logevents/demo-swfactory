package demo.swfactory.model

class Result {
    final String _type = "Result"
    String trackingId
    String component
    String result
    Date started
    Date finished
    Set<ResultProperty> properties


    @Override
    public String toString() {
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
