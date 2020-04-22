package com.epam.callCenter;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class CallCenter {
    private static final Logger LOG = LogManager.getLogger(Operator.class);
    private List<Operator> operators = new ArrayList<>();
    private BlockingQueue<Client> clients = new LinkedBlockingDeque<>();

    public static void main(String[] args) throws InterruptedException {
        CallCenter callCenter = new CallCenter();
        int numberOfOperators = 10; // or Input.getInt()
        for (int i = 0; i < numberOfOperators; i++) {
            Operator operator = new Operator(i,callCenter.clients);
            callCenter.operators.add(operator);
        }

        callCenter.operators.forEach(Thread::start);

        int numberOfClients = 100;
        for (int i = 0; i < numberOfClients; i++) {
            new Client(900 + new Random().nextInt(500),i,callCenter.operators,callCenter.clients).start();
            Thread.sleep(10);
        }
        Thread.sleep(10000);
        System.out.println("------------");
        LOG.info("statistics :\n" + Client.getStatistics());
        System.out.println(Client.getStatistics());

    }
}
