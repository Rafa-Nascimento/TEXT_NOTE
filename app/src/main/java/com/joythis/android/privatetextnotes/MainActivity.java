//MainActivity
package com.joythis.android.privatetextnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static String TSV_DB_NAME = "TSV_DB_NAME2";
    AmUtil mUtil; //TODO : DONE : instantiate the mUtil object
    Context mContext;
    TextView mTvDashboard;
    EditText mEtNote;
    ListView mLvNotes; //Model = data  <-- Controller --> ListView = presentation
    //ArrayList<String> mNotes; //option 1
    //ArrayList<MyNote> mNotes; //option 2 better, requires more knowledge
    MyNotes mNotes; //option 3
    Button mBtnConfirmNote;

    //ArrayAdapter<String> mAd; //option 1
    MyNoteAdapter mAd; //option 2

    Button.OnClickListener mButtonClickHandler = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            createNewNote();
        }//onClick
    };

    void createNewNote(){
        String strTextForTheNote = mEtNote.getText().toString();
        MyNote newNote = new MyNote(strTextForTheNote);
        //mNotes.add(0, newNote.toString()); //option 1
        //mNotes.add(0, newNote); //option 2
        mNotes.add(newNote);
        mAd.notifyDataSetChanged();

        updateDashboard();
    }//createNewNote
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rl_new_private_note);

        init();
    }

    void init(){
        mContext = this;
        mUtil = new AmUtil (this);
        mTvDashboard = findViewById(R.id.idTvDashboard);
        mEtNote = findViewById(R.id.idEtNote);
        mLvNotes = findViewById(R.id.idLvNotes);
        mBtnConfirmNote = findViewById(R.id.idBtnConfirmNote);
        mBtnConfirmNote.setOnClickListener(mButtonClickHandler);

        //mNotes = new ArrayList<>();
        mNotes = new MyNotes(
            this,
            TSV_DB_NAME
        );

        //option 1
        /*
        mAd = new ArrayAdapter<>(
            mContext,
            //below, is a system provided layout resource capable
            //of holding a single String. So, to represent a MyNote
            //one must "crush" it to a single String. We can do that
            //because we have created an adequate "toString" method.
            //BUT, if we wanted to get imaginative regarding the
            //presentation of each MyNote, for example, providing a Button
            //to delete it, or writing it in two lines, we would need
            //to provide a layout of our own and some how adapt it to the
            //ListView.

            android.R.layout.simple_list_item_1, mNotes);
        */
        mAd = new MyNoteAdapter(
            mContext,
            R.layout.ll_to_represent_each_and_every_note,
            mNotes, //option 2 and 3.2
            //mNotes.getMyNotes() //option 3.1
            mTvDashboard
        );
        mLvNotes.setAdapter(mAd);

        updateDashboard();
    }//init

    /*
    l1 : override onSaveInstanceState
    and write what you want to save there
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState!=null){
            outState.putSerializable("KEY_NOTES", mNotes.getMyNotes());
        }

        super.onSaveInstanceState(outState);
    }//onSaveInstanceState

    /*
    l2 : override onRestoreInstanceState
    and write that you want to recover there
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState!=null){
            ArrayList<MyNote> temp =
            (ArrayList<MyNote>)
            savedInstanceState.getSerializable("KEY_NOTES");

            if (temp!=null && (temp.size()>0)){
                //there was data to be recovered
                //mNotes.clear(); //option 2
                //for (MyNote n : temp) mNotes.add(n);
                mNotes.cloneNotes(temp);
            }//if
        }
        super.onRestoreInstanceState(savedInstanceState);
    }//onRestoreInstanceState

    //menu attachment to "action bar" related...
    @Override
    public boolean onCreateOptionsMenu(Menu pMenu) {
        MenuInflater mi = this.getMenuInflater();
        mi.inflate(
            R.menu.my_menu,
            pMenu
        );
        return super.onCreateOptionsMenu(pMenu);
    }//onCreateOptionsMenu

    //to respond to the selection of a particular menu item
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem pMenuItem) {
        switch (pMenuItem.getItemId()){
            case R.id.idMenuItemShowTSVContents:
                actionShowTSVContents();
                break;
            case R.id.idMenuItemClearTSV:
                actionClearTSV();
                break;
            case R.id.idMenuItemQuitApp:
                actionQuitApp();
                break;
        }//switch
        return super.onOptionsItemSelected(pMenuItem);
    }//onOptionsItemSelected

    void actionShowTSVContents(){
        Intent goActivityThatDisplaysTheTSVContents =
            new Intent(
                this,
                ActivityThatDisplaysTheTSVContents.class
                //TODO: DONE: new Activity needed!
            );
        startActivity(goActivityThatDisplaysTheTSVContents);
    }//actionShowTSVContents

    void actionClearTSV(){
        mNotes.reset(); //TODO : edit MyNotes class and add it a new "reset" method
        mAd.notifyDataSetChanged();

        updateDashboard();
    }//actionClearTSV

    void actionQuitApp(){
        mUtil.actionQuit();
    }//actionQuitApp

    void updateDashboard(){
        String strMsg = String.format(
            "#%d item(s) in the Database",
            mNotes.getMyNotes().size()
        );
        mTvDashboard.setText(strMsg);
    }//updateDashboard
}
