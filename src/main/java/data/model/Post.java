package data.model;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by iran on 2015-06-16.
 * Completely different with post in presentation module
 * This class is only for testing purpose
 */
@Parcel(analyze=Post.class)
@Table(database = data.database.PoinilaDataBase.class)
public class Post extends data.database.PoinilaDataBase.PoinilaDBModel<Post> implements Timed, Identifiable {

    @Column
    @PrimaryKey
    public int id;

    @Column
    @SerializedName(value = "creation_time")
    public Date creationTime;

    // Visible Field
    @SerializedName(value = "like_count")
    public int faveCount;

    @SerializedName(value = "repost_count")
    public int repostCount;

    @SerializedName(value = "comment_count")
    public int commentCount;

    public List<Comment> comments;
    //@SerializedName(value = "last_update_time") public Date lastUpdateTime;

    @SerializedName(value = "is_repost")
    public boolean isRepost;

    @SerializedName(value = "liked_by_me")
    public boolean favedByMe;

    @SerializedName(value = "title")
    public String name;

    @SerializedName(value = "caption")
    public String summary;
    // mese image bayad joda request zade she
    // TODO:chizi ke server mide daghighan chie? processi lazeme in vasat?
    //@SerializedName(value = "content_url") public Content contentUrl;

    @SerializedName(value = "content_url")
    public String contentUrl;

    // only used in uploading a new post
    public String content;

    // tag haye marbut be post
    @SerializedName(value = "tags")
    public List<Tag> tags;
    // TODO: enum?

    @SerializedName(value = "video_url")
    public String videoUrl;

    @SerializedName(value = "type")
    public PostType type;

    //TODO: remove after it moved to imagesUrls array
    @SerializedName(value = "initial_img")
    public String placeholder;

    @SerializedName(value = "images")
    public data.model.ImageUrls imagesUrls;

    @SerializedName(value = "original_poster")
    public Member originalAuthor;

    @SerializedName(value = "poster")
    public Member author;

    @SerializedName(value = "original_collection")
    public Collection originalCollection;

    public Collection collection;
    /**
     * the webpage this post is created from
     */
    @SerializedName(value = "url")
    public String originalWebpage;

    public PrivacyType privacy;

    public SuggestionReason reason;

    public Post() {

    }

    public Post(String name, String summary, String content, List<String> tags) {

        this.name = name;
        this.summary = summary;
        this.content = content;
        this.tags = new ArrayList<>(tags.size());
        for (String tagString : tags) {
            this.tags.add(Tag.invalidIdTag(tagString));
        }
    }

    @Override
    public long getCreationTime() {
        return creationTime.getTime();
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public Post getModel() {
        return getModelFromJson(Post.class);
    }

}
