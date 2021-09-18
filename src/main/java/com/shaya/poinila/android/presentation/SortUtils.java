package com.shaya.poinila.android.presentation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import data.model.Timed;

/**
 * Created by iran on 2015-07-07.
 */
public class SortUtils {
    private static Comparator timeComparator = new Comparator() {
        @Override
        public int compare(Object left, Object right) {
            if (!(left instanceof Timed) || !(right instanceof Timed))
                throw new RuntimeException("objects must implement Timed interface");
            Timed lhs = (Timed) left;
            Timed rhs = (Timed) right;
            if (lhs == rhs)
                return 0;
            // newer item in less index
            if (lhs.getCreationTime() > rhs.getCreationTime())
                return -1;
            // older item in greate index
            if (lhs.getCreationTime() < rhs.getCreationTime())
                return 1;
            // same time
            return 0;
        }
    };

    public static void sortByTime(List list){
         Collections.sort(list, timeComparator);
    }
}
