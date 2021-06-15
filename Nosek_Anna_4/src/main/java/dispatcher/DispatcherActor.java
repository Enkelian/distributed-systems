package dispatcher;

import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import request.RequestActor;

public class DispatcherActor extends AbstractBehavior<DispatcherActor.DispatcherCommand> {

    public interface DispatcherCommand {
    }

    public DispatcherActor(ActorContext<DispatcherCommand> context) {
        super(context);
    }

    @Override
    public Receive<DispatcherCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(RequestActor.Request.class, this::onRequest)
                .build();
    }

    public static Behavior<DispatcherActor.DispatcherCommand> create(){ return Behaviors.setup(DispatcherActor::new); }

    public Behavior<DispatcherActor.DispatcherCommand> onRequest(RequestActor.Request request){

        getContext().spawn(
                Behaviors.supervise(RequestActor.create())
                    .onFailure(Exception.class, SupervisorStrategy.stop()),
                "RequestActor." + request.replyTo.path().name() + "." +request.details.queryId,
                DispatcherSelector.fromConfig("my-dispatcher")
            ).tell(request);

        return Behaviors.same();
    }

}
