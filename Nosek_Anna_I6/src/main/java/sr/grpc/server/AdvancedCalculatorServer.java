package sr.grpc.server;

import io.grpc.ServerBuilder;
import sr.grpc.server.impl.AdvancedCalculatorImpl;

import java.io.IOException;
import java.util.concurrent.Executors;

public class AdvancedCalculatorServer extends grpcServer {

    public AdvancedCalculatorServer(int port) {
        super(port);
    }

    @Override
    protected void setupServer() throws IOException {
        //You will want to employ flow-control so that the queue doesn't blow up your memory. You can cast StreamObserver to CallStreamObserver to get flow-control API

        server = ServerBuilder/*NettyServerBuilder*/.forPort(port).executor(Executors.newFixedThreadPool(16))
                .addService(new AdvancedCalculatorImpl())
                .build()
                .start();

    }
}
