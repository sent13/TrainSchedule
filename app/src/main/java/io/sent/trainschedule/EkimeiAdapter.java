package io.sent.trainschedule;

import android.content.Context;
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

    public EkimeiAdapter( Context context, int resource) {

        super( context, resource);
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

        textView.setText( super.getItem( position ) );
        textView.setTextColor(Color.BLACK);
    }
}

