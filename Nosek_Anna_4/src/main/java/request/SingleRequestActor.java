package request;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import satellite.SatelliteAPI;

import java.sql.Time;
import java.util.concurrent.*;

public class SingleRequestActor extends AbstractBehavior<SingleRequestActor.SingleRequest> {

    private RequestActor.ResponseType responseType;
    private ActorRef<RequestActor.Message> parent;
    private Integer satelliteId;
    private SatelliteAPI.Status satelliteStatus;

    public static final class SingleRequest {
        public final int satelliteId;
        public final int timeout;

        public SingleRequest(int satelliteId, int timeout) {
            this.satelliteId = satelliteId;
            this.timeout = timeout;
        }
    }

    public SingleRequestActor(ActorContext<SingleRequest> context, ActorRef<RequestActor.Message> parent) {
        super(context);
        this.parent = parent;
    }

    public static Behavior<SingleRequestActor.SingleRequest> create(ActorRef<RequestActor.Message> parent){
        return Behaviors.setup( context -> new SingleRequestActor(context, parent));
    }

    @Override
    public Receive<SingleRequest> createReceive() {
        return newReceiveBuilder()
                .onMessage(SingleRequest.class, this::onSingleRequest)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    public Behavior<SingleRequest> onSingleRequest(SingleRequest singleRequest){

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<SatelliteAPI.Status> future = executor.submit(() -> SatelliteAPI.getStatus(singleRequest.satelliteId));

        satelliteId = singleRequest.satelliteId;

        try {
            satelliteStatus = future.get(singleRequest.timeout, TimeUnit.MILLISECONDS);

            responseType = satelliteStatus == SatelliteAPI.Status.OK ? RequestActor.ResponseType.OK : RequestActor.ResponseType.ERROR;

        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            responseType = RequestActor.ResponseType.TIMEOUT;
        }
        return Behaviors.stopped();
    }

    private Behavior<SingleRequest> onPostStop(){

        if(responseType == null) responseType = RequestActor.ResponseType.ACTOR_ERROR;

        parent.tell(new RequestActor.Response(satelliteId, satelliteStatus,  responseType));
        return this;
    }


}
