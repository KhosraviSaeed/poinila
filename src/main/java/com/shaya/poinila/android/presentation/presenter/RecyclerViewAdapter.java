package com.shaya.poinila.android.presentation.presenter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import data.database.PoinilaDataBase;
import data.model.Identifiable;
import data.model.Loading;
import data.model.Post;

public abstract class RecyclerViewAdapter<T, VH extends BaseViewHolder<T>> extends RecyclerView.Adapter<VH> {

    public static final int VIEW_TYPE_LOAD_PROGRESS = -1;
    protected static final int VIEW_TYPE_DATA_ROW = 100;
    //private final Class<VH> mViewHolderClazz;loading
    protected LayoutInflater mLayoutInflater;
    private List<T> items;
    private int mItemLayoutID = -1;

    // Provide a reference to the views for each items item
    // Complex items items may need more than one view per item, and
    // you provide access to all the views for a items item in a view holder
    //public static class ViewHolder extends RecyclerView.ViewHolder {
    // each items item is just a string in this case

    // Provide a suitable constructor (depends on the kind of dataset)

    public RecyclerViewAdapter(Context context, @LayoutRes int itemLayoutID) {
        super();
        Log.i(getClass().getName(), "context = " + context);
        mLayoutInflater = LayoutInflater.from(context);
        items = new ArrayList<>();
        mItemLayoutID = itemLayoutID;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {


        if(viewType == VIEW_TYPE_LOAD_PROGRESS){
            View v = mLayoutInflater.inflate(R.layout.progress, parent, false);
            return getProperViewHolder(v, viewType);
        }

        View v = mLayoutInflater.inflate(mItemLayoutID, parent, false);
        return getProperViewHolder(v, viewType);
    }

    protected abstract VH getProperViewHolder(View v, int viewType);

    protected boolean isStaggeredGridLayoutManager(){
        return false;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(VH holder, int position) {

        if (!(getItemViewType(position) == VIEW_TYPE_LOAD_PROGRESS)) {
            holder.fill(getItem(position));
        }

        if(isStaggeredGridLayoutManager() && getItemViewType(position) == VIEW_TYPE_LOAD_PROGRESS){
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        //if (getItem(position) instanceof Identifiable)
        if (hasStableIds())
            return Integer.parseInt(((Identifiable) getItem(position)).getId());
        return position;
    }


    public T getItem(int position){
        return items.get(position);
    }

    public void addItem(T item, int position){
        if (item == null)
            return;
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void addItem(T item){
        if (item == null)
            return;
        items.add(item);

        notifyItemInserted(items.size() - 1);
    }

    public void setLoading(T item){
        if (item == null)
            return;
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }


    public void removeLoading(){
        int position = items.size() - 1;
        removeItem(position);
        notifyItemRemoved(position);
    }

    public void addItems(List data){
        int oldSize = items.size();
        items.addAll(oldSize > 0 ? oldSize - 1 : oldSize, data);
//        items.addAll(data);
        notifyItemRangeInserted(oldSize, data.size());
        // TODO: moshkele inconsistency az ine guya
        //notifyDataSetChanged();
    }

    public void addItemsToListHead(List<T> data){
        items.addAll(0, data);
        notifyItemRangeInserted(0, data.size());
    }

    public void setItem(T item, int position){
        items.set(position, item);
        notifyItemChanged(position);
    }



    public void resetData(List data) {
        // don't use this.items = data snippet. it produces bug in saving suggestions came from server
        this.items.clear();
        this.items.addAll(data);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void clear(){
        this.items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Loading)
            return VIEW_TYPE_LOAD_PROGRESS;
        return VIEW_TYPE_DATA_ROW;
    }

    public List getItems(){
        return items;
    }

    // Don't use it except you have no other choice! I assumed that lists have a single type entity in my design
    // But in Dashboard page there's times we want to show a "please rate us" item which is not a post.
    // 1- I could just add a fake post but it was not clean and triggered unwanted click events and blah blah
    // 2- Replacing current mechanism with new one. It messed the code and produced lots of cast to Post
    // 3- Ignoring generics and use the old api but in adding items to adapter I face the generic problem so I decided to just write a function without generic so be able to add anything other than post.
    // sorry for long comment, here's your potato :))
    public List getUngenericedItems(){
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty() || (items.size() > 0 && items.get(0) instanceof Loading);
    }
}
