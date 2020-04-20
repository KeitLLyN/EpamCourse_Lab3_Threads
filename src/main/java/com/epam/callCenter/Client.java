package com.epam.callCenter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends Thread {
    private static final Logger LOG = LogManager.getLogger(Client.class);

    private int timeToAsk;
    private int number;
    private boolean helped = false;
    private List<Operator> operators;
    private BlockingQueue<Client> clientsQueue;
    private ReentrantLock lock = new ReentrantLock();

    public Client(int timeToAsk, int number, List<Operator> operators, BlockingQueue<Client> clientsQueue) {

        super("Client #" + number);
        this.timeToAsk = timeToAsk;
        this.number = number;
        this.operators = operators;
        this.clientsQueue = clientsQueue;
    }

    @Override
    public void run() {
        boolean isTalking = false;

        for(Operator operator: operators){
            if (operator.isFree()){
                synchronized (operator){
                    if (operator.isFree()) {
                        operator.setClient(this);
                        System.out.println("Client #" + number + " was called to operator #" + operator.getNumber());
                        isTalking = true;
                        operator.notify();
                    }
                    else {
                        continue;
                    }
                }
                break;
            }
        }
        if (!isTalking){
            clientsQueue.offer(this);
            synchronized (this) {
                try {
                    this.wait(3000 + new Random().nextInt(3000));
                    if (!helped) {
                        clientsQueue.remove(this);
                        System.out.println("Client #" + number + " cant wait more");
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    LOG.warn(ex.getMessage());
                }
            }
        }
        if (helped){
            System.out.println("Client #" + getNumber() + " was left");
        }
    }

    public void asking(){
        synchronized (this) {
            try {
                Thread.sleep(timeToAsk);
                helped = true;
                this.notifyAll();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                LOG.warn(ex.getMessage());
            }
        }
    }

    public int getNumber(){
        return number;
    }
}
