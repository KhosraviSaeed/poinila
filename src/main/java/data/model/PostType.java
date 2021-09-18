package data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by iranian on 6/25/2015.
 */
public enum PostType {
    @SerializedName(value = "img")
    IMAGE,
    @SerializedName(value = "text")
    TEXT,
    @SerializedName(value = "video")
    VIDEO

   /* String code;

    PostType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static final PostType findByCode(String code){
        if(code==null){
            return null;
        }
        for (PostType postType:  PostType.values()){
            if(postType.code.equals(code)){
                return postType;
            }
        }
        return null;
    }*/
}
