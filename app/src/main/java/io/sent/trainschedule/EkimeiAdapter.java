package io.sent.trainschedule;

import android.app.Application;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by sent13 on 16/08/01.
 */
public class EkimeiAdapter extends ArrayAdapter<String> {
    int themeNum;
    public EkimeiAdapter( Context context, int resource,int themeNum) {
        super( context, resource);
        this.themeNum=themeNum;
    }


    @Override
    public View getDropDownView( int position, View convertView, ViewGroup parent ) {

        if( convertView == null ) {
            LayoutInflater inflater = LayoutInflater.from( getContext() );
            convertView = inflater.inflate( android.R.layout.simple_spinner_dropdown_item, parent, false );
            //** android.R.layout.simple_spinner_dropdown_item で inflate **//
        }

        this.setCustomTextView( (TextView) convertView, position );

        return convertView;
    }


    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {

        if( convertView == null ) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate( android.R.layout.simple_spinner_item, parent, false );
            //** android.R.layout.sinple_spinner_item で inflate **//
        }

        this.setCustomTextView( (TextView) convertView, position );

        return convertView;
    }


    //** Spinnerの中身のTextViewを作る **//
    private void setCustomTextView( TextView textView, int position ) {

        textView.setText(super.getItem(position));
        if(themeNum==1) {
            textView.setTextColor(Color.BLACK);
        }else{
            textView.setTextColor(Color.WHITE);
        }
    }
}

