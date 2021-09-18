package com.shaya.poinila.android.presentation.uievent;


import java.util.Arrays;
import java.util.List;

import data.event.BaseEvent;

/**
 * Created by iran on 2015-11-18.
 */
public class PostComponentClickedUIEvent extends BaseEvent {
    public Type type;

    public PostComponentClickedUIEvent(Type type){
        this.type = type;
    }

    public enum Type{
        Fave,
        FaversList,
        Comments,
        RepostersList,
        Repost,
        Reference,
        Collection,
        Poster,
        OriginalCollection,
        FullImage;

        public static List<Type> guestCantPerformActions = Arrays.asList(Fave, Repost);
    }
}
