package io.sent.trainschedule;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


public class SiteMakeCharaFragment extends Fragment implements View.OnKeyListener,RetCharaStrInterface{

    View view;
    private InputMethodManager inputMethodManager;
    Button selectGuestCharaImageBtn;
    EditText charaSerifSiteEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_site_make_chara, container, false);
        findViews();
        initViews();
        return view;
    }

    private void findViews(){
        selectGuestCharaImageBtn=(Button)view.findViewById(R.id.select_guest_chara_image_btn);
        charaSerifSiteEdit=(EditText)view.findViewById(R.id.chara_serif_site);
        inputMethodManager=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initViews(){
        ((MakeCharacterActivity)getActivity()).setCharaSerifInterface();
        selectGuestCharaImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.
                        parse("http://www.character-jikokuhyo.com/%E3%82%AD%E3%83%A3%E3%83%A9%E3%82%AF%E3%82%BF%E3%83%BC/"));
                startActivity(intent);
            }
        });

        charaSerifSiteEdit.setMaxLines(8);
        charaSerifSiteEdit.setOnKeyListener(this);
    }

    @Override
    public String getCharaSerif(){
        return charaSerifSiteEdit.getText().toString();
    }

    @Override
    public void editCharaInit(Character editChara){
        charaSerifSiteEdit.setText(editChara.normalText+","+editChara.noTrainText+","+editChara.noCheckedText);
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if((event.getAction()== android.view.KeyEvent.ACTION_DOWN) && (keyCode== android.view.KeyEvent.KEYCODE_ENTER)){

            switch(v.getId()){
                case R.id.chara_serif_site:
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.RESULT_SHOWN);
                    break;
            }
            return true;
        }
        return false;
    }
}
