package com.epam.callCenter;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class CallCenter {

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
    }
}
