package station;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import dispatcher.DispatcherActor;
import query.QueryActor;
import request.RequestActor;
import satellite.SatelliteAPI;
import util.RequestDetails;

import java.util.HashMap;
import java.util.Map;

public class MonitorStationActor extends AbstractBehavior<MonitorStationActor.Command> {

    private long lastQueryId;
    private final ActorRef<DispatcherActor.DispatcherCommand> dispatcherActor;
    private final Map<Long, Long> requestTimes;
    private final ActorRef<QueryActor.DatabaseQuery> queryActor;

    public interface Command {
    }

    public static final class Request implements Command {
        public final int firstSatId;
        public final int range;
        public final int timeout;

        public Request(int firstSatId, int range, int timeout) {
            this.firstSatId = firstSatId;
            this.range = range;
            this.timeout = timeout;
        }
    }

    public static final class Reply implements Command {

        public final long queryId;
        public final Map<Integer, SatelliteAPI.Status> statusMap;
        public final float respondedPercent;

        public Reply(long queryId, Map<Integer, SatelliteAPI.Status> statusMap, float respondedPercent) {
            this.queryId = queryId;
            this.statusMap = statusMap;
            this.respondedPercent = respondedPercent;
        }
    }

    public static final class QueryReply implements Command {

        public final int satelliteId;
        public final int errorsCount;

        public QueryReply(int satelliteId, int errorsCount) {
            this.satelliteId = satelliteId;
            this.errorsCount = errorsCount;
        }
    }

    public static final class QueryRequest implements Command {

        public final int satelliteId;


        public QueryRequest(int satelliteId) {
            this.satelliteId = satelliteId;
        }
    }

    private MonitorStationActor(ActorContext<Command> context,
                                ActorRef<DispatcherActor.DispatcherCommand> dispatcherActor,
                                ActorRef<QueryActor.DatabaseQuery> queryActor) {
        super(context);
        this.lastQueryId = 0;
        this.requestTimes = new HashMap<>();
        this.dispatcherActor = dispatcherActor;
        this.queryActor = queryActor;

    }


    public static Behavior<MonitorStationActor.Command> create(ActorRef<DispatcherActor.DispatcherCommand> dispatcherActor,
            ActorRef<QueryActor.DatabaseQuery> queryActor){
        return Behaviors.setup((context)-> new MonitorStationActor(context, dispatcherActor, queryActor));
    }


    @Override
    public Receive<MonitorStationActor.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(MonitorStationActor.Request.class, this::onRequest)
                .onMessage(MonitorStationActor.Reply.class, this::onReply)
                .onMessage(MonitorStationActor.QueryRequest.class, this::onQueryRequest)
                .onMessage(MonitorStationActor.QueryReply.class, this::onQueryReply)
                .build();
    }

    private Behavior<Command> onRequest(MonitorStationActor.Request request){

        RequestDetails requestDetails = new RequestDetails(++lastQueryId, request.firstSatId, request.range, request.timeout);

        dispatcherActor.tell(new RequestActor.Request(requestDetails, this.getContext().getSelf(), queryActor));

        requestTimes.put(lastQueryId, System.currentTimeMillis());

        return Behaviors.same();
    }

    private Behavior<Command> onReply(MonitorStationActor.Reply reply){
        long time =  (System.currentTimeMillis() - requestTimes.get(reply.queryId));

        StringBuilder sb = new StringBuilder("--------REPLY FROM SATELLITE SYSTEM--------\n" +
                            "Station name: " + getContext().getSelf().path().name() + "\n" +
                            "Time elapsed: " + time + "\n" +
                            "Percent without timeout: " + reply.respondedPercent + "\n" +
                            "Errors count: " + reply.statusMap.keySet().size() + "\n");

        for(int id : reply.statusMap.keySet()){
            sb.append(id).append(":").append(reply.statusMap.get(id)).append("\n");
        }

        System.out.println(sb);

        requestTimes.remove(reply.queryId);

        return Behaviors.same();
    }

    private Behavior<Command> onQueryReply(MonitorStationActor.QueryReply reply) {

        if(reply.errorsCount != 0) {
            System.out.println("-------------DATABASE RESPONSE------------\n" +
                            "Station name: " + getContext().getSelf().path().name() + "\n" +
                            "Satellite id: " + reply.satelliteId + "\n" +
                            "Errors count: " + reply.errorsCount);
        }

        return Behaviors.same();

    }

    private Behavior<Command> onQueryRequest(MonitorStationActor.QueryRequest request){

        queryActor.tell(new QueryActor.SelectQuery(getContext().getSelf(), request.satelliteId));

        return Behaviors.same();
    }


    }
