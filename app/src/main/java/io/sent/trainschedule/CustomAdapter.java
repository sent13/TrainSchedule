package io.sent.trainschedule;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sent13 on 16/06/10.
 */
public class CustomAdapter extends ArrayAdapter<CustomData>{
    private LayoutInflater layoutInflater_;
    private Context context;

    public CustomAdapter(Context context, int textViewResourceId, List<CustomData> objects) {
        super(context, textViewResourceId, objects);
        this.context=context;
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 特定の行(position)のデータを得る
        CustomData item = (CustomData)getItem(position);

        // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
        if (null == convertView) {
            convertView = layoutInflater_.inflate(R.layout.list_custom, null);
        }

        // CustomDataのデータをViewの各Widgetにセットする
        ImageView imageView;
        imageView = (ImageView)convertView.findViewById(R.id.customImage);

        Bitmap bitmap=resizeImage.resize((Activity)context,item.getImageUri());
        imageView.setImageBitmap(bitmap);

        TextView textView;
        textView = (TextView)convertView.findViewById(R.id.customText);
        textView.setText(item.getTextData());

        return convertView;
    }
}
