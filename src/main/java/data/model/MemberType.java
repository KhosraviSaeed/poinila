package data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by iranian on 6/25/2015.
 */
public enum MemberType {
    @SerializedName(value = "entity")
    Entity,
    @SerializedName(value = "people")
    People

/*    String code;


    MemberType(String code) {
        this.code = code;
    }

    public static final MemberType findByCode(String code){
        if(code==null){
            return null;
        }
        for (MemberType memberType:  MemberType.values()){
            if(memberType.code.equals(code)){
                return memberType;
            }
        }
        return null;
    }*/
}
