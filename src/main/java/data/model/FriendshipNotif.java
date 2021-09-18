package data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by iran on 2015-08-16.
 */
public class FriendshipNotif implements Timed {
    public boolean seen;
    @SerializedName(value = "creation_time") public Date creationTime;



    public Member member;

    @Override
    public long getCreationTime() {
        return 0;
    }
}
