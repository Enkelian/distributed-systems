package request;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import dispatcher.DispatcherActor;
import query.QueryActor;
import satellite.SatelliteAPI;
import station.MonitorStationActor;
import util.RequestDetails;

import java.util.HashMap;
import java.util.Map;

public class RequestActor extends AbstractBehavior<RequestActor.Message> {

    private final Map<Integer, SatelliteAPI.Status> statusMap;
    private int successfulResponseCount;
    private int noResponseCount;
    private int okResponseCount;
    private int expectedResponseCount;
    private ActorRef<MonitorStationActor.Command> replyTo;
    private ActorRef<QueryActor.DatabaseQuery> queryActor;
    private long queryId;

    interface Message {
    }

    enum ResponseType{
        TIMEOUT,
        ERROR,
        OK,
        ACTOR_ERROR
    }


    public static final class Response implements Message {
        public final int satelliteId;
        public final SatelliteAPI.Status status;
        public final ResponseType type;

        public Response(Integer satelliteId,
                        SatelliteAPI.Status status,
                        ResponseType type) {
            this.satelliteId = satelliteId;
            this.status = status;
            this.type = type;
        }
    }

    public static final class Request implements Message, DispatcherActor.DispatcherCommand {
        public final RequestDetails details;
        public final ActorRef<MonitorStationActor.Command> replyTo;
        public final ActorRef<QueryActor.DatabaseQuery> queryActor;

        public Request(RequestDetails details, ActorRef<MonitorStationActor.Command> replyTo, ActorRef<QueryActor.DatabaseQuery> queryActor) {
            this.details = details;
            this.replyTo = replyTo;
            this.queryActor = queryActor;
        }
    }

    private RequestActor(ActorContext<Message> context) {
        super(context);
        this.statusMap = new HashMap<>();
        this.okResponseCount = 0;
        this.successfulResponseCount = 0;
        this.noResponseCount = 0;
    }

    public static Behavior<RequestActor.Message> create() {
        return Behaviors.setup(RequestActor::new);
    }


    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(Request.class, this::onRequest)
                .onMessage(Response.class, this::onResponse)
                .build();
    }


    private Behavior<Message> onRequest(Request request) {

        this.expectedResponseCount = request.details.range;
        this.replyTo = request.replyTo;
        this.queryId = request.details.queryId;
        this.queryActor = request.queryActor;

        for (int i = 0; i < request.details.range; i++) {
            getContext().spawn(
                    Behaviors.supervise(SingleRequestActor.create(getContext().getSelf()))
                            .onFailure(Exception.class, SupervisorStrategy.stop()),
                    "SingleRequestActor." + queryId + "." + i,
                    DispatcherSelector.fromConfig("my-dispatcher")
            ).tell(new SingleRequestActor.SingleRequest(
                    request.details.firstSatId + i,
                    request.details.timeout
            ));
        }

        return Behaviors.same();
    }

    private void sendReply() {
        replyTo.tell(new MonitorStationActor.Reply(queryId, statusMap, (float) (successfulResponseCount + okResponseCount) / expectedResponseCount));
    }

    private void sendUpdate() {
        for (int satelliteId: statusMap.keySet())
            queryActor.tell(new QueryActor.UpdateQuery(satelliteId, 1));
    }


    private Behavior<Message> onResponse(Response response) {
        switch (response.type){
            case TIMEOUT, ACTOR_ERROR -> noResponseCount++;
            case OK -> okResponseCount++;
            case ERROR -> statusMap.put(response.satelliteId, response.status);
        }

        if (statusMap.size() + okResponseCount + noResponseCount == expectedResponseCount) {
            sendReply();
            sendUpdate();
            return Behaviors.stopped();
        }
        return Behaviors.same();
    }

}
