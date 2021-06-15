package agh.edu.pl;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class Crew {

    private final Channel publishChannel;
    private final Channel consumeChannel;
    private final Connection connection;
    private Consumer consumer;
    private String name;

    private static final String EXCHANGE_NAME = "EXPEDITIONS";

    public Crew() throws IOException, TimeoutException {

        setCrewName();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        this.connection = factory.newConnection();

        this.publishChannel = connection.createChannel();
        this.consumeChannel = connection.createChannel();

        publishChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        consumeChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

    }

    public void setCrewName() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter crew name: ");

        this.name = br.readLine();
    }

    public void printConsoleDialog(){
        System.out.print("Enter product: ");
    }

    public void registerConsumer(){
        consumer = new DefaultConsumer(consumeChannel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                String message = new String(body, "UTF-8");

                String sender = envelope.getRoutingKey().startsWith("admin") ? "admin" : "a supplier";

                System.out.println("\nReceived message from " + sender + ": "+ message);

                printConsoleDialog();

                consumeChannel.basicAck(envelope.getDeliveryTag(), false);

            }
        };
    }

    public void registerCrewQueue() throws IOException {
        String key = "expedition.*."  + name + ".*";

        String queueName = consumeChannel.queueDeclare(name, false, false, false, null).getQueue();
        consumeChannel.queueBind(queueName, EXCHANGE_NAME, key);

        String adminKeyCrews = "admin.crews";
        consumeChannel.queueBind(queueName, EXCHANGE_NAME, adminKeyCrews);

        String adminKeyAll = "admin.all";
        consumeChannel.queueBind(queueName, EXCHANGE_NAME, adminKeyAll);

    }

    public void processInput() throws IOException {

        while(true) {

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            printConsoleDialog();
            String product = br.readLine();

            String key = "expedition." + product + "." + name;

            if ("exit".equals(product)) {
                return;
            }

            String message = "crew " + name + " ordered " + product;

            publishChannel.basicPublish(EXCHANGE_NAME, key, null, message.getBytes("UTF-8"));
            System.out.println("Sent: " + message);
        }

    }

    public void startConsuming() throws IOException {
        consumeChannel.basicConsume(name, false, consumer);
    }

    public void close() throws IOException, TimeoutException {
        consumeChannel.close();
        publishChannel.close();
        connection.close();
    }

    public static void main(String[] args) throws IOException, TimeoutException {

        Crew currentCrew = new Crew();
        currentCrew.registerCrewQueue();
        currentCrew.registerConsumer();
        currentCrew.startConsuming();
        currentCrew.processInput();
        currentCrew.close();

    }
}
