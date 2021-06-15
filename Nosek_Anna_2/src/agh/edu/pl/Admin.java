package agh.edu.pl;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class Admin {

    private final Channel publishChannel;
    private final Channel consumeChannel;
    private final Connection connection;
    private Consumer consumer;

    private static final String ADMIN_NAME = "admin";
    private static final String EXCHANGE_NAME = "EXPEDITIONS";

    public Admin() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        this.connection = factory.newConnection();

        this.publishChannel = connection.createChannel();
        this.consumeChannel = connection.createChannel();

        publishChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        consumeChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

    }

    public void registerConsumer(){
        consumer = new DefaultConsumer(consumeChannel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                String message = new String(body, "UTF-8");

                System.out.println("\n--------------------------------------------------");
                System.out.println("Key: " + envelope.getRoutingKey());
                System.out.println("Message: " + message);
                System.out.println("--------------------------------------------------");

                printConsoleDialog();

                consumeChannel.basicAck(envelope.getDeliveryTag(), false);

            }
        };
    }

    public void registerQueue() throws IOException {

        String key = "expedition.#";

        String queueName = publishChannel.queueDeclare(ADMIN_NAME, false, false, false, null).getQueue();
        publishChannel.queueBind(queueName, EXCHANGE_NAME, key);

    }

    public void printConsoleDialog(){
        System.out.print("Enter message recipients (suppliers/crews/all): ");
    }

    public void processInput() throws IOException {

        while(true) {

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            printConsoleDialog();
            String recipient = br.readLine();

            if ("exit".equals(recipient)) {
                return;
            }

            System.out.print("Enter message: ");
            String message = br.readLine();

            String key = "admin." + recipient;

            publishChannel.basicPublish(EXCHANGE_NAME, key, null, message.getBytes("UTF-8"));
            System.out.println("Sent: " + message);
        }

    }


    public void startConsuming() throws IOException {
        consumeChannel.basicConsume(ADMIN_NAME, false, consumer);
    }

    public void close() throws IOException, TimeoutException {
        consumeChannel.close();
        publishChannel.close();
        connection.close();
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        Admin admin = new Admin();
        admin.registerConsumer();
        admin.registerQueue();
        admin.startConsuming();
        admin.processInput();
        admin.close();
    }
}

