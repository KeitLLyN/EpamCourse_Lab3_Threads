package com.epam.callCenter;

import com.epam.statistics.Statistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends Thread {
    private static final Logger LOG = LogManager.getLogger(Client.class);

    private int timeToAsk;
    private int number;
    private boolean helped = false;
    private List<Operator> operators;
    private BlockingQueue<Client> clientsQueue;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private static Statistics statistics = new Statistics();

    public Client(int timeToAsk, int number, List<Operator> operators, BlockingQueue<Client> clientsQueue) {

        super("Client #" + number);
        this.timeToAsk = timeToAsk;
        this.number = number;
        this.operators = operators;
        this.clientsQueue = clientsQueue;
    }

    @Override
    public void run(){
        for(Operator operator: operators){
            if (operator.isFree()) {
                lock.lock();
                if (lock.tryLock() && operator.isFree()) {
                    operator.setClient(this);
                    System.out.println("Client #" + number + " was called to operator #" + operator.getNumber());
                    operator.wakeUp();
                    try {
                        condition.await();
                        LOG.info("Client #" + number + " waits, when operator ask him");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                    lock.unlock();
                } else {
                    continue;
                }
                break;
            }
        }
        if (!helped){
            clientsQueue.offer(this);
            if (lock.tryLock()) {
                try {
                    int time = 3000 + new Random().nextInt(3000);
                    LOG.info("Client #" + number + " is in queue, and waiting " + time + " milliseconds");
                    condition.await(time, TimeUnit.MILLISECONDS);

                    if (!helped) {
                        LOG.info("Client #" + number + " cant wait more");
                        clientsQueue.remove(this);
                        System.out.println("Client #" + number + " cant wait more");
                        statistics.addAngry();
                    }
                    else {
                        System.out.println("Client #" + getNumber() + " is happy");
                        statistics.addHappy();
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    LOG.warn(ex.getMessage());
                }finally {
                    lock.unlock();
                }
            }
        }
        else{
            System.out.println("Client #" + getNumber() + " is happy");
            statistics.addHappy();
        }
    }

    public void asking(){
        if (lock.tryLock()) {
            try {
                LOG.info("Client #" + number + " is talking");
                Thread.sleep(timeToAsk);
                helped = true;
                condition.signalAll();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                LOG.warn(ex.getMessage());
            }finally {
                lock.unlock();
            }
        }
    }

    public int getNumber(){
        return number;
    }

    public static Statistics getStatistics(){
        return statistics;
    }
}
