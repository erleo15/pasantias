package amef.queue;

public class QueueSize {

    public long ready;

    public long inflight;

    public QueueSize(long ready, long inflight) {
        this.ready = ready;
        this.inflight = inflight;
    }
}
