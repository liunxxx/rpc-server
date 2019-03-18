package com.liunx.rpc.publisher;

public class ServicePublisherBuilder {

    public static ServicePublisherBuilder INSTANCE = new ServicePublisherBuilder();

    private int port;

    private volatile ServicePublisher publisher = null;
    private final Object lock = new Object();

    private ServicePublisherBuilder() {

    }

    public ServicePublisherBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public ServicePublisher build() {
        if (port <= 0) {
            throw new IllegalStateException("you must invoke setPort method to set the listen port");
        }
        if (publisher == null) {
            synchronized (lock) {
                if (publisher == null) {
                    publisher = new ServicePublisher(port);
                }
            }
        }
        return publisher;
    }


}
