package com.yhjoo.dochef.adapter;

import android.text.Html;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Notification;

public class NotificationListAdapter extends BaseQuickAdapter<Notification, BaseViewHolder> {
    public NotificationListAdapter() {
        super(R.layout.li_notification);
    }

    @Override
    protected void convert(BaseViewHolder helper, Notification item) {
        Glide.with(mContext)
                .load(Integer.valueOf(item.getUserImg()))
                .circleCrop()
                .into((AppCompatImageView) helper.getView(R.id.notification_userimg));

//        if (item.getNotificationType() == mContext.getResources().getInteger(R.integer.NOTIFICATION_TYPE_1))
//            helper.setText(R.id.notification_contents, Html.fromHtml(mContext.getString(R.string.format_noti_type_0, item.getUserName()), Html.FROM_HTML_MODE_LEGACY));
//        else if (item.getNotificationType() == mContext.getResources().getInteger(R.integer.NOTIFICATION_TYPE_2))
//            helper.setText(R.id.notification_contents, Html.fromHtml(mContext.getString(R.string.format_noti_type_1, item.getRecipeName()), Html.FROM_HTML_MODE_LEGACY));
//        else if (item.getNotificationType() == mContext.getResources().getInteger(R.integer.NOTIFICATION_TYPE_3))
//            helper.setText(R.id.notification_contents, Html.fromHtml(mContext.getString(R.string.format_noti_type_2, item.getUserName(), item.getRecipeName()), Html.FROM_HTML_MODE_LEGACY));
//        else if (item.getNotificationType() == mContext.getResources().getInteger(R.integer.NOTIFICATION_TYPE_4))
//            helper.setText(R.id.notification_contents, Html.fromHtml(mContext.getString(R.string.format_noti_type_3, item.getUserName(), item.getRecipeName()), Html.FROM_HTML_MODE_LEGACY));
//        else if (item.getNotificationType() == mContext.getResources().getInteger(R.integer.NOTIFICATION_TYPE_5))
//            helper.setText(R.id.notification_contents, Html.fromHtml(mContext.getString(R.string.format_noti_type_4, item.getUserName(), item.getRecipeName()), Html.FROM_HTML_MODE_LEGACY));
        helper.setText(R.id.notification_date, item.getDateTime());
    }
}