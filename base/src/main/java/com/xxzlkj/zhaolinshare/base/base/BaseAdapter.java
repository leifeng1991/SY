package com.xxzlkj.zhaolinshare.base.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:RecyclerView的Adapter的基类
 *
 * @author zhangrq
 *         2016/8/10 15:45
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    private int itemId;
    protected Context mContext;
    private List<T> datas;
    private OnItemClickListener<T> mOnItemClickListener;
    private OnLongItemClickListener<T> mOnLongItemClickListener;
    private static final String TAG = BaseAdapter.class.getSimpleName();


    public BaseAdapter(Context context, int itemId) {
        this.itemId = itemId;
        this.mContext = context;
        this.datas = new ArrayList<>();
    }

    /**
     * 增加一条数据
     *
     * @param position 增加的位置，从0开始，注意下标不要越界
     * @param item     要添加的数据
     */
    public void addItem(int position, T item) {
        this.datas.add(position, item);
        notifyDataSetChanged();
    }

    /**
     * 插入一条数据
     *
     * @param position 增加的位置，从0开始，注意下标不要越界
     * @param item     要添加的数据
     */
    public void insertedItem(int position, T item) {
        this.datas.add(position, item);
        notifyItemInserted(position + 1);
    }

    public void addItemAtLast(T item) {
        this.datas.add(item);
        notifyItemInserted(datas.size() - 1);
    }

    /**
     * 在底部增加，使用于带刷新的RecyclerView
     */
    public void addItemAtLastHasHeader(T item) {
        this.datas.add(item);
        notifyItemInserted(datas.size());
    }

    /**
     * 增加一个集合的数据
     *
     * @param position 增加的开始位置，从0开始，注意下标不要越界
     * @param list     要添加的集合
     */
    public void addList(int position, List<T> list) {
        if (list == null || list.size() == 0)
            return;
        this.datas.addAll(position, list);
        notifyDataSetChanged();
    }

    public void clearAndAddList(List<T> list) {
        this.datas.clear();
        if (list != null)
            this.datas.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 插入一个集合的数据
     *
     * @param position 增加的开始位置，从0开始，注意下标不要越界
     * @param list     要添加的集合
     */
    public void insertedList(int position, List<T> list) {
        if (list == null || list.size() == 0)
            return;
        this.datas.addAll(position, list);
//        notifyItemRangeInserted(position + 1, list.size());
        notifyDataSetChanged();
    }

    /**
     * 在列表最后增加一个集合的数据
     *
     * @param list 要添加的集合
     */
    public void addList(List<T> list) {
        addList(this.datas.size(), list);
    }

    /**
     * 在列表最后插入一个集合的数据
     *
     * @param list 要添加的集合
     */
    public void insertedList(List<T> list) {
        insertedList(this.datas.size(), list);
    }

    /**
     * 移除一条数据
     *
     * @param position 移除的位置，从0开始，注意下标不要越界
     */
    public void removeItem(int position) {
        this.datas.remove(position);
        notifyDataSetChanged();//让数据归原
//        notifyItemRemoved(position );
    }

    public void removeItem(T oldItem) {
        int position = getPosition(oldItem);
        if (position == -1) return;
        this.datas.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 删除一条数据
     *
     * @param position 移除的位置，从0开始，注意下标不要越界
     */
    public void deleteItem(int position) {
        this.datas.remove(position);
        notifyDataSetChanged();
    }

//    /**
//     * 移除一个集合的数据，即移除原集合中在此要移除的集合(参数集合)中存在的数据
//     *
//     * @param list 要移除的集合
//     */
//    public void removeList(List<T> list) {
//        if (list == null || list.size() == 0)
//            return;
//        int containFirstIndex = getContainFirstIndex(this.datas, list);
//        if (containFirstIndex == -1) {
//            return;
//        }
//        int rawSize = this.datas.size();
//        this.datas.removeAll(list);
//        notifyItemRangeRemoved(containFirstIndex + 1, rawSize - this.datas.size());
//    }
//
//    /**
//     * 获取父集合，包含子集合的开始位置
//     */
//    private int getContainFirstIndex(List<T> parentList, List<T> sonList) {
//        if (parentList == null || sonList == null)
//            return -1;
//        for (int i = 0; i < parentList.size(); i++) {
//            T data = parentList.get(i);
//            if (sonList.contains(data))
//                return i;
//        }
//        return -1;
//    }

    /**
     * 改变一条数据
     *
     * @param position 改变的位置，从0开始，注意下标不要越界
     * @param item     要改变的数据
     */
    public void changeItem(int position, T item) {
        this.datas.set(position, item);
        notifyItemChanged(position + 1);
    }

    public void changeItem(T oldItem, T newItem) {
        int position = getPosition(oldItem);
        if (position == -1) return;
        this.datas.set(position, newItem);
        notifyDataSetChanged();
//        notifyItemChanged(position + 1);
    }

    private int getPosition(T oldItem) {
        if (this.datas.contains(oldItem)) {
            for (int i = 0; i < this.datas.size(); i++) {
                T t = datas.get(i);
                if (t == oldItem)
                    return i;
            }
            return -1;
        } else {
            return -1;
        }
    }

    /**
     * 设置一条数据
     *
     * @param position 改变的位置，从0开始，注意下标不要越界
     * @param item     要改变的数据
     */
    public void setItem(int position, T item) {
        this.datas.set(position, item);
        notifyDataSetChanged();
    }

    /**
     * 改变一个集合的数据
     *
     * @param position 改变的开始位置，从0开始，注意下标不要越界
     * @param list     要改变的集合
     */
    public void changeList(int position, List<T> list) {
        if (list == null)
            return;
        for (int i = 0; i < list.size(); i++) {
            this.datas.set(position + i, list.get(i));
        }
        notifyItemRangeChanged(position + 1, list.size());
    }

    /**
     * 设置一个集合的数据
     *
     * @param position 改变的开始位置，从0开始，注意下标不要越界
     * @param list     要改变的集合
     */
    public void setList(int position, List<T> list) {
        if (list == null)
            return;
        for (int i = 0; i < list.size(); i++) {
            this.datas.set(position + i, list.get(i));
        }
        notifyDataSetChanged();
    }

    /**
     * 移动一条的数据
     *
     * @param fromPosition 移动的起始位置，从0开始，注意下标不要越界
     * @param toPosition   移动的结束位置，从0开始，注意下标不要越界
     */
    public void moveItem(int fromPosition, int toPosition) {
        T fromPositionItem = this.datas.get(fromPosition);
        this.datas.remove(fromPosition);
        this.datas.add(toPosition, fromPositionItem);
        notifyItemMoved(fromPosition + 1, toPosition + 1);
    }

    /**
     * 清空数据
     */
    public void clear() {
        int size = datas.size();
        this.datas.clear();
        notifyDataSetChanged();
    }

    /**
     * 清空数据
     */
    public void removeAll() {
        int size = datas.size();
        this.datas.clear();
        notifyItemRangeRemoved(1, size);
    }

    /**
     * 获取此adapter的数据集合
     *
     * @return 数据集合
     */
    public List<T> getList() {
        return datas;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * 当需要使用多itemType时，请重写该方法，返回值就是对应类型的布局id
     */
    @Override
    public int getItemViewType(int position) {
        if (itemId == 0) {
            throw new RuntimeException("请在 " + this.getClass().getSimpleName() + " 中重写 getItemViewType 方法返回布局资源 id");
        }
        return itemId;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    int adapterPosition = holder.getAdapterPosition() - 1;
                    mOnItemClickListener.onClick(position, datas.get(position));
                }
            });
        }

        if (mOnLongItemClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override

                public boolean onLongClick(View v) {
                    mOnLongItemClickListener.onLongClick(position, datas.get(position));
                    return true;
                }
            });
        }
        convert(holder, position, datas.get(position));
    }

    /**
     * 获取View并设置值
     *
     * @param holder   封装的ViewHolder
     * @param itemBean 当前条目的bean值
     * @param position
     */
    public abstract void convert(BaseViewHolder holder, int position, T itemBean);


    public interface OnItemClickListener<T> {
        void onClick(int position, T item);
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnLongItemClickListener<T> {
        void onLongClick(int position, T item);
    }

    public void setOnLongItemClickListener(OnLongItemClickListener<T> mOnLongItemClickListener) {
        this.mOnLongItemClickListener = mOnLongItemClickListener;
    }

}
