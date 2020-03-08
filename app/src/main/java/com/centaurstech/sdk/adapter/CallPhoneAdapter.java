package com.centaurstech.sdk.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.centaurstech.qiwu.entity.MsgEntity;
import com.centaurstech.qiwu.entity.PhoneContactEntity;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ItemCallPhoneBinding;
import com.qiwu.ui.util.UIUtils;
import com.qiwu.ui.view.adapter.DataBindRecyclerViewAdapter;


/**
 * @data 2020/1/9
 * @author: 樊德鹏
 * @description:
 */
public class CallPhoneAdapter extends DataBindRecyclerViewAdapter<PhoneContactEntity, ItemCallPhoneBinding> {


    private MsgEntity mMsgEntity;
    private boolean mHideIcon;

    public CallPhoneAdapter(Context context, MsgEntity msgEntity) {
        super(context);
        mMsgEntity = msgEntity;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_call_phone;
    }


    @Override
    public void onBindHolder(ItemCallPhoneBinding binding, PhoneContactEntity itemData, int position) {
        String name;
        if (!TextUtils.isEmpty(itemData.getTag())) {
            name = itemData.getName() + "（" + itemData.getTag() + "）";
        } else {
            name = itemData.getName();
        }
        RecyclerView.LayoutParams rlp = (RecyclerView.LayoutParams) binding.cvContainer.getLayoutParams();
        if (position == 0) {
            rlp.setMargins(UIUtils.dip2Px(20), UIUtils.dip2Px(12), UIUtils.dip2Px(20), UIUtils.dip2Px(12));
        } else {
            rlp.setMargins(UIUtils.dip2Px(20), UIUtils.dip2Px(0), UIUtils.dip2Px(20), UIUtils.dip2Px(12));
        }
        binding.ivCallPhone.setVisibility(mHideIcon ? View.GONE : View.VISIBLE);
        binding.cvContainer.requestLayout();
        binding.tvPhoneName.setText(itemData.getName());
        String displayName = TextUtils.isEmpty(itemData.getDisplayName()) ? "" : " | " + itemData.getDisplayName();
        String tag = TextUtils.isEmpty(itemData.getTag()) ? "" : " | " + itemData.getTag();
        binding.tvPhoneNumber.setText(itemData.getNumber() + displayName + tag);
    }


    @Override
    public void itemClick(ItemCallPhoneBinding binding, PhoneContactEntity itemData, int position, View v) {
        super.itemClick(binding, itemData, position, v);
        if (mMsgEntity.getExpire() == 0) {

        } else {

        }
    }

    public boolean isHideIcon() {
        return mHideIcon;
    }

    public void setHideIcon(boolean hideIcon) {
        mHideIcon = hideIcon;
    }
}