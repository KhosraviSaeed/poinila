package data.event;

import com.shaya.poinila.android.presentation.view.fragments.notification.NPostListFragment;

/**
 * Created by iran on 2015-06-16.
 */
public abstract class BaseEvent {
    public int requestType;

    public BaseEvent(){

    }

    public BaseEvent(ReceiverName receiverName) {
        this.receiverName = receiverName;
    }

    public enum ReceiverName {
        DashboardFragment,
        MyFollowedCollections,
        SearchFragment,
        PostListFragment,
        CollectionPageFragment,
        CollectionListFragment,
        MemberListFragment,
        RepostCollectionsList,
        PostsImagesDialog,
        NotificationFragment,
        PostRelatedPosts,
        CollectionDetailFragment,
        MyProfileFragment,
        ProfileFragment,
        SelectInterest, ChangePassword, ExploredTagPosts,
        NPostListFragment
    }
    public ReceiverName receiverName;
}
