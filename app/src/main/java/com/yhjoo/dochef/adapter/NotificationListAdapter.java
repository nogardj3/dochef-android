package com.yhjoo.dochef.adapter;

import androidx.appcompat.widget.AppCompatImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Notification;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.Utils;

public class NotificationListAdapter extends BaseQuickAdapter<Notification, BaseViewHolder> {
    public NotificationListAdapter() {
        super(R.layout.li_notification);
    }

    @Override
    protected void convert(BaseViewHolder helper, Notification item) {
        ImageLoadUtil.loadUserImage(
                mContext, item.getImage(), (AppCompatImageView) helper.getView(R.id.notification_userimg));

        helper.setText(R.id.notification_contents, item.getContents());
        helper.setText(R.id.notification_date, Utils.convertMillisToText(item.getDateTime()));

        helper.setBackgroundColor(R.id.notification_item, item.getRead() == 0
                ? mContext.getColor(R.color.white) : mContext.getColor(R.color.grey));
    }
}