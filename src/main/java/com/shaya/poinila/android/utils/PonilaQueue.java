package com.shaya.poinila.android.utils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by iran on 6/14/2016.
 */
public class PonilaQueue {

    private static PonilaQueue instance;
    private LinkedList queue;

    private PonilaQueue(){
        queue = new LinkedList();
    }

    public static PonilaQueue getInstance(){

        if(instance == null){
            instance = new PonilaQueue();
        }

        return instance;
    }

    public void push(Object object){
        queue.add(object);
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }

    public Object pop(){
        return queue.remove();
    }
}
