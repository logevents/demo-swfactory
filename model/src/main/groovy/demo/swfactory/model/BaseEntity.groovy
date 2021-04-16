package demo.swfactory.model

abstract class BaseEntity {
    String trackingId
    String requestId

    abstract String key()
    abstract String label()
}
