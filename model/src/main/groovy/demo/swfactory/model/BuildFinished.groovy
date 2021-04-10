package demo.swfactory.model

class BuildFinished extends BaseEntity {
    final String _type = "BuildFinished"
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
