package com.joythis.android.privatetextnotes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyNoteAdapter extends ArrayAdapter<MyNote> {
    Context mContext;
    int mLayout;
    //ArrayList<MyNote> mNotes; //option 2
    MyNotes mNotes; //option 3

    TextView mTvDashboard;

    public MyNoteAdapter(
        @NonNull Context pContext, //the context of the Activity needing to adapt objects to views
        int pLayoutWhereToDisplayEachObject, //identifies the custom layout created to represent some object
        //@NonNull ArrayList<MyNote> pMyNotes //our collection to be dispayed in the LV
        MyNotes pMyNotes,
        /*
        if you have more interface and EXTERNAL objects to be notified
        in function of whatever ops this adapter performs, since they are
        external, they must be received, for the adapter to be able to
        work with them
         */
        TextView pTextViewAffectedByActionsThanCanOnlyHappenHere
    )
    {
        super(
            pContext,
            pLayoutWhereToDisplayEachObject,
            //pMyNotes //option 2
            pMyNotes.getMyNotes()
        );

        this.mContext = pContext;
        this.mLayout = pLayoutWhereToDisplayEachObject;
        this.mNotes = pMyNotes;

        mTvDashboard = pTextViewAffectedByActionsThanCanOnlyHappenHere;
    }//MyNoteAdapter

    @NonNull
    @Override
    public View getView(
            final int piIndexOfCurrentNote,
            @Nullable View pViewForTheCurrentNote,
            @NonNull ViewGroup pContainerForAllTheNotes
    )
    {
        /*
        an inflater is an object to bridge XML to Java
         */
        LayoutInflater inflater =
            (LayoutInflater) mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        if (inflater!=null){
            boolean bNeedToInflate = pViewForTheCurrentNote==null;
            if (bNeedToInflate){
                pViewForTheCurrentNote = inflater.inflate(
                    mLayout,
                    pContainerForAllTheNotes,
                    false
                );
            }//if

            /*
            pViewForTheCurrentNote is a Java object corresponding to our
            custom layout
             */
            if (pViewForTheCurrentNote!=null){
                TextView tvWhen = pViewForTheCurrentNote.
                        findViewById(R.id.idTvWhen);
                TextView tvText = pViewForTheCurrentNote.
                        findViewById(R.id.idTvText);
                ImageButton ibtnDel = pViewForTheCurrentNote.
                        findViewById(R.id.idIBtnDelete);
                ImageButton ibtnMail = pViewForTheCurrentNote.
                        findViewById(R.id.idIBtnMail);

                MyNote noteToBeDisplayedIntoTheView =
                    //mNotes.get(piIndexOfCurrentNote); //option 2
                    mNotes.getMyNotes().get(piIndexOfCurrentNote); //option 3

                if (noteToBeDisplayedIntoTheView!=null){
                    String strWhen =
                        AmUtil.
                            CalendarToString(noteToBeDisplayedIntoTheView.mWhenTaken);
                    tvWhen.setText(strWhen);
                    tvText.setText(noteToBeDisplayedIntoTheView.mText);

                    ibtnDel.setOnClickListener(new ImageButton.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeTheChoosenNote(piIndexOfCurrentNote);
                        }//onClick
                    });

                    ibtnMail.setOnClickListener(new ImageButton.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mailTheChosenNote(piIndexOfCurrentNote);
                        }//onClick
                    });

                    return pViewForTheCurrentNote;
                }//if
            }//if
        }//if

        return super.getView(piIndexOfCurrentNote, pViewForTheCurrentNote, pContainerForAllTheNotes);
    }//getView

    void removeTheChoosenNote(int pIndexToDelete){
        mNotes.remove(pIndexToDelete); //option 2, 3.1
        //mNotes.getMyNotes().remove(pIndexToDelete); //option 3 would remove from the collection, but not sync with the TSV DB
        this.notifyDataSetChanged();

        updateDashboard();
    }//removeTheChoosenNote

    void mailTheChosenNote(int pIndex){
        String msg = mNotes.getNoteString(pIndex);
        String data = mNotes.getNoteCalendar(pIndex);
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("message/rfc822");//standard for arpa Internet text
        sendIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"170100468@esg.ipsantarem.pt", "170100472@esg.ipsantarem.pt"});
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "This note was written on "+data);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);

        //fazer o metodo get para obter o text, "igual" ao remove
        try{
            mContext.startActivity(Intent.createChooser(sendIntent, "Send mail..."));
        } catch(android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext,"There are no email clients installed", Toast.LENGTH_LONG).show();
        }

        this.notifyDataSetChanged();
    }

    void updateDashboard(){
        String strMsg = String.format(
                "#%d item(s) in the Database",
                mNotes.getMyNotes().size()
        );
        if (mTvDashboard!=null){
            mTvDashboard.setText(strMsg);
        }//if
    }//updateDashboard



}
