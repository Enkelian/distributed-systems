import akka.actor.typed.*;
import akka.actor.typed.javadsl.Behaviors;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dispatcher.DispatcherActor;
import org.apache.log4j.BasicConfigurator;
import query.QueryActor;
import scala.concurrent.ExecutionContextExecutor;
import scala.util.Random;
import station.MonitorStationActor;
import util.DatabaseInitializer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppRunner {

    public static final int FIRST_STATION_ID = 100;
    public static final int STATION_ID_LIMIT = 200;

    public static Behavior<Void> create(){

        BasicConfigurator.configure();

        Random rand = new Random();

        return Behaviors.setup(
                context -> {

                    final ExecutionContextExecutor ex = context
                            .getSystem()
                            .dispatchers()
                            .lookup(DispatcherSelector.fromConfig("my-dispatcher"));

                    ActorRef<DispatcherActor.DispatcherCommand> dispatcherActor = context.spawn(
                            Behaviors.supervise(DispatcherActor.create())
                                .onFailure(Exception.class, SupervisorStrategy.restart()),
                            "dispatcherActor",
                            DispatcherSelector.fromConfig("my-dispatcher")
                    );

                    ActorRef<QueryActor.DatabaseQuery> queryActor = context.spawn(
                            Behaviors.supervise(QueryActor.create())
                                    .onFailure(Exception.class, SupervisorStrategy.restart()),
                            "queryActor",
                            DispatcherSelector.fromConfig("my-dispatcher")
                    );

                    List<ActorRef<MonitorStationActor.Command>> stations = new ArrayList<>();

                    for(int i = 0; i < 3; i++){

                        ActorRef<MonitorStationActor.Command> station = context.spawn(
                                Behaviors.supervise(MonitorStationActor.create(dispatcherActor, queryActor))
                                    .onFailure(Exception.class, SupervisorStrategy.resume()),
                                "Station" + i,
                                DispatcherSelector.fromConfig("my-dispatcher")
                        );

                        station.tell(new MonitorStationActor.Request(FIRST_STATION_ID + rand.nextInt(50), 50, 300));
                        station.tell(new MonitorStationActor.Request(FIRST_STATION_ID + rand.nextInt(50), 50, 300));

                        stations.add(station);
                    }

                    Thread.sleep(1000);
                    for(int i = FIRST_STATION_ID; i < STATION_ID_LIMIT; i++){
                        stations.get(0).tell((new MonitorStationActor.QueryRequest(i)));
                    }

                    return Behaviors.receive(Void.class)
                            .onSignal(Terminated.class, sig -> Behaviors.stopped())
                            .build();
                }
        );
    }


    public static void main(String[] args) {


        DatabaseInitializer.initializeDatabase();

        File configFile = new File(Objects.requireNonNull(AppRunner.class.getClassLoader()
                        .getResource("dispatcher.conf"))
                        .getFile());

        Config config = ConfigFactory.parseFile(configFile);

        ActorSystem.create(AppRunner.create(), "system", config);

    }
}
