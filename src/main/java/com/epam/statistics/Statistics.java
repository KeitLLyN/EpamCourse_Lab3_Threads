package com.epam.statistics;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Statistics {
    private int countOfAngryClients;
    private int countOfHappyClients;
    private Lock lock = new ReentrantLock();

    public Statistics(){
        countOfAngryClients = 0;
        countOfHappyClients = 0;
    }

    public void addAngry(){
        lock.lock();
        countOfAngryClients++;
        lock.unlock();
    }
    public void addHappy(){
        lock.lock();
        countOfHappyClients++;
        lock.unlock();
    }

    @Override
    public String toString(){
        return String.format("Happy clients : %d\nAngry clients : %d ",countOfHappyClients,countOfAngryClients);
    }
}
