package com.shaya.poinila.android.utils;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by iran on 6/20/2016.
 */
public class NotificationQueue {

    private static NotificationQueue instance;
    HashMap<String, JSONArray> maps;

    private NotificationQueue(){
        maps = new HashMap<>();
    }

    public static NotificationQueue getInstance(){

        if(instance == null){
            instance = new NotificationQueue();
        }

        return instance;
    }

    public void put(String group, JSONArray jSon){
        maps.put(group, jSon);
    }

    public JSONArray get(String group){
       return maps.get(group);
    }
}
