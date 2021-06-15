package query;

import akka.actor.TypedActor;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.Signal;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import dispatcher.DispatcherActor;
import station.MonitorStationActor;

import java.sql.*;

public class QueryActor extends AbstractBehavior<QueryActor.DatabaseQuery> {

    private Connection connection;
    private PreparedStatement updateStatement;
    private PreparedStatement selectStatement;

    public interface DatabaseQuery{ }

    public static final class UpdateQuery implements DatabaseQuery {

        public final int errorNo;
        public final int satelliteId;

        public UpdateQuery(int satelliteId, int errorNo) {
            this.satelliteId = satelliteId;
            this.errorNo = errorNo;
        }
    }
    public static final class SelectQuery implements DatabaseQuery {

        public final ActorRef<MonitorStationActor.Command> replyTo;
        public final int satelliteId;

        public SelectQuery(ActorRef<MonitorStationActor.Command> replyTo, int satelliteId) {
            this.replyTo = replyTo;
            this.satelliteId = satelliteId;
        }

    }

    private QueryActor(ActorContext<QueryActor.DatabaseQuery> context){
        super(context);
        try {

            Class.forName("AppRunner");
            connection = DriverManager.getConnection("jdbc:sqlite:test.db");

        } catch (SQLException | ClassNotFoundException throwable ) {
            throwable.printStackTrace();
        }
    }

    public static Behavior<QueryActor.DatabaseQuery> create(){ return Behaviors.setup(QueryActor::new); }


    @Override
    public Receive<DatabaseQuery> createReceive() {
        return newReceiveBuilder()
                .onMessage(UpdateQuery.class, this::onUpdateQuery)
                .onMessage(SelectQuery.class, this::onSelectQuery)
                .onSignal(PostStop.class, this::onPostStop)
                .build();
    }

    private int getCurrentErrors(int satelliteId) throws SQLException {

        selectStatement = connection.prepareStatement("SELECT ERROR_COUNT FROM ERRORS WHERE SATELLITE_ID = ?");
        selectStatement.setString(1, Integer.toString(satelliteId));
        ResultSet result = selectStatement.executeQuery();
        int res = result.getInt("ERROR_COUNT");
        selectStatement.close();
        return res;


    }

    private Behavior<QueryActor.DatabaseQuery> onUpdateQuery(UpdateQuery updateQuery) throws SQLException {

        int newErrors = getCurrentErrors(updateQuery.satelliteId) + updateQuery.errorNo;

        updateStatement = connection.prepareStatement("UPDATE ERRORS SET ERROR_COUNT = ? WHERE SATELLITE_ID = ?");
        updateStatement.setString(1, Integer.toString(newErrors));
        updateStatement.setString(2, Integer.toString(updateQuery.satelliteId));
        updateStatement.executeUpdate();
        updateStatement.close();

        return Behaviors.same();
    }

    private Behavior<QueryActor.DatabaseQuery> onSelectQuery(SelectQuery selectQuery) throws SQLException {
        selectQuery.replyTo.tell(
                new MonitorStationActor.QueryReply(selectQuery.satelliteId,
                getCurrentErrors(selectQuery.satelliteId))
        );

        return Behaviors.same();
    }

    private Behavior<QueryActor.DatabaseQuery> onPostStop(Signal signal) {
        try {
            connection.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return this;
    }

}
