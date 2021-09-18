package data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by iran on 2015-10-03.
 */
public class PoinilaInvite {
    @SerializedName("email_used_invitation_count")
    public int  usedInvites;

    @SerializedName("email_invitation_limit")
    public int limit;
}
