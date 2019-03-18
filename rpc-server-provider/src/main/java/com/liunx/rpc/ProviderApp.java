package com.liunx.rpc;

import com.liunx.rpc.publisher.ServicePublisher;
import com.liunx.rpc.publisher.ServicePublisherBuilder;

public class ProviderApp {

    public static void main(String[] args) {
        ServicePublisher publisher =
                ServicePublisherBuilder.INSTANCE.setPort(8080).build();
        publisher.publish();
    }
}
