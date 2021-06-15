package server;

import TheOffice.*;
import com.zeroc.Ice.Current;
import util.DateTimeConverter;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.*;

public class OfficeI<ResponseT extends Response> implements Office {

    private final ConcurrentSkipListSet<Long> allActiveIds;
    private final OfficeExecutor<ResponseT> executor;

    private final Random random;

    public OfficeI(ConcurrentSkipListSet<Long> allActiveIds,
                   OfficeExecutor<ResponseT> executor){
        this.allActiveIds = allActiveIds;
        this.executor = executor;
        this.random = new Random();
    }


    @Override
    public OfficeDetails sendRequest(Request request, Current current) {

        long id = request.hashCode();

        LocalDateTime responseDateTime = LocalDateTime.now().plusSeconds(Math.abs(random.nextLong() % 100));

        OfficeDetails officeDetails = new OfficeDetails(id, DateTimeConverter.toDateTime(responseDateTime));
        allActiveIds.add(id);

        executor.process(new RequestData(request, officeDetails));

        return officeDetails;
    }

    @Override
    public Response getResult(long requestID, Current current) {
        if(!allActiveIds.contains(requestID)){
            return new ErrorResponse("Error", "No such request in the system");
        }

        allActiveIds.remove(requestID);

        return executor.getResponse(requestID);

    }


}
