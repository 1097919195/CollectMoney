package com.example.collect.collectmoneysystem.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.collect.collectmoneysystem.R;
import com.example.collect.collectmoneysystem.activity.MainActivity;
import com.example.collect.collectmoneysystem.app.AppConstant;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.jaydenxiao.common.commonutils.ImageLoaderUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/5/18 0018.
 */

public class MyExpandableListViewAdapter extends BaseExpandableListAdapter{
    private Context mContext;
    private LayoutInflater mInflater = null;
    private List<String> mGroupItems = null;
    private List<List<ProductDetails>> mData = null;

    private class GroupViewHolder {
        TextView mGroupName;
        TextView mGroupCount;

    }

    private class ChildViewHolder {
        ImageView sample_photo;
        TextView part;
        TextView spec;
        TextView size;
        TextView price;
    }

    public MyExpandableListViewAdapter(Context context, List<String> groupItems, List<List<ProductDetails>> childItems){
        this.mContext = context;
        this.mData = childItems;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mGroupItems = groupItems;
    }

    @Override
    public ProductDetails getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition).get(childPosition);
    }

//    @Override
//    public Object getChild(int groupPosition, int childPosition) {
//        return mData.get(groupPosition).get(childPosition);
//    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Log.e("childData",groupPosition+"  "+childPosition+"  "+mData.size());
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_product_details, null);
        }
        ChildViewHolder holder = new ChildViewHolder();
        holder.sample_photo = (ImageView) convertView.findViewById(R.id.sample_photo);
        holder.part = (TextView) convertView.findViewById(R.id.part);
        holder.spec = (TextView) convertView.findViewById(R.id.spec);
        holder.size = (TextView) convertView.findViewById(R.id.size);
        holder.price = (TextView) convertView.findViewById(R.id.price);

        ImageLoaderUtils.displaySmallPhoto(mContext,holder.sample_photo, AppConstant.IMAGE_DOMAIN_NAME+getChild(groupPosition, childPosition).getImage());
        holder.part.setText(getChild(groupPosition, childPosition).getName());
        holder.spec.setText(getChild(groupPosition, childPosition).getSpec());
        holder.size.setText(getChild(groupPosition, childPosition).getSize());
        holder.price.setText(String.valueOf(getChild(groupPosition, childPosition).getRetailPrice()));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mData.size() <= groupPosition) {
            return 0;
        }//防止点击没有数据的group造成数组越界
        return mData.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupItems.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mGroupItems.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_expand_group, null);
        }
        GroupViewHolder holder = new GroupViewHolder();
        holder.mGroupName = (TextView) convertView.findViewById(R.id.group_name);
        holder.mGroupCount = (TextView) convertView.findViewById(R.id.group_count);
        holder.mGroupName.setText(mGroupItems.get(groupPosition));
        if (mData.size()<=groupPosition||mData.get(groupPosition).isEmpty()) {
            holder.mGroupCount.setText("0");
        }else {
            holder.mGroupCount.setText(String.valueOf(getChildrenCount(groupPosition)));
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
