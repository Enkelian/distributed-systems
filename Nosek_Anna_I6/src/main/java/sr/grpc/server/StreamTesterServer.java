package sr.grpc.server;

import io.grpc.ServerBuilder;
import sr.grpc.server.impl.AdvancedCalculatorImpl;
import sr.grpc.server.impl.CalculatorImpl;
import sr.grpc.server.impl.StreamTesterImpl;

import java.io.IOException;
import java.util.concurrent.Executors;

public class StreamTesterServer extends grpcServer {

    public StreamTesterServer(int port) {
        super(port);
    }

    @Override
    protected void setupServer() throws IOException {
        //You will want to employ flow-control so that the queue doesn't blow up your memory. You can cast StreamObserver to CallStreamObserver to get flow-control API

        server = ServerBuilder/*NettyServerBuilder*/.forPort(port).executor(Executors.newFixedThreadPool(16))
                .addService(new StreamTesterImpl())
                .build()
                .start();

    }
}
