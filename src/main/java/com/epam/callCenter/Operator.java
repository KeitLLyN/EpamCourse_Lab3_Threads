package com.epam.callCenter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Operator extends Thread {
    private static final Logger LOG = LogManager.getLogger(Operator.class);

    private int operatorNumber;
    private boolean isFree = true;
    private BlockingQueue<Client> clients;
    private Client currentClient;
    private ReentrantLock lock = new ReentrantLock();

    public Operator(int operatorNumber, BlockingQueue<Client> clients) {
        super("Operator #" + operatorNumber);
        this.operatorNumber = operatorNumber;
        this.clients = clients;
    }

    @Override
    public void run() {
        while (true) {
            if (currentClient == null) {
                synchronized (this) {
                    try {
                        isFree = true;
                        wait();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        LOG.warn(ex.getMessage());
                    }
                }

            } else {
                synchronized (currentClient){
                    isFree = false;
                    System.out.println("Operator #" + operatorNumber + "  is start talking with Client #" + currentClient.getNumber());
                    currentClient.asking();
                }
                currentClient = clients.poll();
                if (currentClient != null) {
                    System.out.println("Operator #" + operatorNumber + " took Client #" + currentClient.getNumber());
                }
            }
        }
    }


    public void setClient(Client client) {
        currentClient = client;
    }

    public boolean isFree() {
        return isFree;
    }

    public int getNumber() {
        return operatorNumber;
    }

}
