package agh.edu.pl;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class Supplier {

    private final Channel publishChannel;
    private final Channel consumeChannel;
    private final Connection connection;
    private Consumer consumer;

    private long lastOrderID;
    private String[] parsedProducts;
    private String name;

    private static final String EXCHANGE_NAME = "EXPEDITIONS";

    public Supplier() throws IOException, TimeoutException {

        setName();
        this.lastOrderID = 0;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.connection = factory.newConnection();

        this.publishChannel = connection.createChannel();
        this.consumeChannel = connection.createChannel();

        this.consumeChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        this.publishChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        this.consumeChannel.basicQos(1);

        setOfferedProducts();

    }

    public void setName() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter supplier name: ");

        this.name = br.readLine();
    }

    public void setOfferedProducts() throws IOException {

        System.out.print("Type in offered products, space separated: ");

        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        String products = consoleReader.readLine();

        parsedProducts = products.split(" ");
        for (String product: parsedProducts) addProductQueue(product);

    }

    public void addProductQueue(String productName) throws IOException {

        String key = appendToMsg("expedition", productName);
        key = appendToMsg(key, "*");

        String queueName = consumeChannel.queueDeclare(productName, false, false, false, null).getQueue();
        consumeChannel.queueBind(queueName, EXCHANGE_NAME, key);

    }

    public void registerSupplierQueue() throws IOException {

        String queueName = consumeChannel.queueDeclare(name, false, false, false, null).getQueue();

        String adminKeyCrews = "admin.suppliers";
        consumeChannel.queueBind(queueName, EXCHANGE_NAME, adminKeyCrews);

        String adminKeyAll = "admin.all";
        consumeChannel.queueBind(queueName, EXCHANGE_NAME, adminKeyAll);

    }

    public void incrementLastOrderID(){
        this.lastOrderID++;
    }

    public void registerConsumer(){
        consumer = new DefaultConsumer(consumeChannel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                String sender = envelope.getRoutingKey().startsWith("admin") ? "admin" : "a crew";


                System.out.println("Received message from " + sender + ": " + message);

                if(!sender.equals("admin")) processOrder(envelope.getRoutingKey());

                consumeChannel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
    }

    public void processOrder(String message) throws IOException {

        String product = message.split("[.]")[1];

        String responseKey = appendToMsg(message, name + lastOrderID);
        incrementLastOrderID();

        String responseMessage = "order for " + product  + " confirmed by " + name;

        sendConfirmation(responseKey, responseMessage);

        System.out.println("Confirmation sent: " + responseMessage);

    }

    public void startConsuming() throws IOException {
        for(String product: parsedProducts){
            consumeChannel.basicConsume(product, false, consumer);
            System.out.println("Registered product: " + product);
        }
        consumeChannel.basicConsume(name, false, consumer);
    }

    public String appendToMsg(String msg, String content){
        return msg + "." + content;
    }


    public void sendConfirmation(String key, String msg) throws IOException {
        publishChannel.basicPublish(EXCHANGE_NAME, key, null, msg.getBytes("UTF-8"));
    }

    public void close() throws IOException, TimeoutException {
        consumeChannel.close();
        publishChannel.close();
        connection.close();
    }

    public static void main(String[] args) throws IOException, TimeoutException {

        Supplier currentSupplier = new Supplier();
        currentSupplier.registerSupplierQueue();
        currentSupplier.registerConsumer();
        currentSupplier.startConsuming();
//        currentSupplier.close();
    }
}
