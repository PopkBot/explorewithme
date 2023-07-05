package main.event;

public enum State {
    PUBLISHED,
    CONFIRMED,
    PENDING,
    WAITING,
    PUBLISH_EVENT,
    CANCELED,
    CANCEL_REVIEW,
    REJECTED,
    REJECT_EVENT,
    SEND_TO_REVIEW
}
