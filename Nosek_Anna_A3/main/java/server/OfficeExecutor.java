package server;

import TheOffice.*;
import util.DateTimeConverter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.*;

public class OfficeExecutor<ResponseT> {

    private final ExecutorService threadPool;
    private final Random random;
    private final ConcurrentHashMap<Long, BlockingQueue<ResponseT>> responses;

    public OfficeExecutor(int threadsNo, ConcurrentHashMap<Long, BlockingQueue<ResponseT>> responses){
        this.threadPool = Executors.newFixedThreadPool(threadsNo);
        this.random = new Random();
        this.responses = responses;
    }

    public ResponseT getResponse(long requestID){
        ResponseT response = null;
        try {
            response = responses.get(requestID).take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        responses.remove(requestID);
        return response;

    }

    public void process(RequestData requestData){
        responses.put(requestData.officeData.requestID, new LinkedBlockingQueue<>());
        threadPool.submit(() -> internalProcess(requestData));
    }

    private void internalProcess(RequestData requestData){

        Response response = null;
        try{

            DateTime dateTime = requestData.officeData.expectedResponseDateTime;

            long timeToWait = LocalDateTime.now().until(DateTimeConverter.fromDateTime(dateTime), ChronoUnit.MILLIS);
            if(timeToWait > 0) Thread.sleep(timeToWait);

            response = switch (requestData.request.type) {
                case ID, PASSPORT, LICENSE -> new DocumentResponse(
                        "Collection date",
                        DateTimeConverter.toDateTime(LocalDateTime.now().plusDays(Math.abs(random.nextInt() % 30)))
                );
                case RESIDENTREGISTRATION -> new ResidentRegistrationResponse("Resident registration changed successfully.", ((ResidentRegistrationData) requestData.request.data).address);
            };

//            if(responses.containsKey(requestData.officeData.requestID)){
//                response = new ErrorResponse("Error", "No such request in the system");
//            }

            responses.get(requestData.officeData.requestID).put((ResponseT) response);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
