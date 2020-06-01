//MyNotes.java (PLURAL!)
package com.joythis.android.privatetextnotes;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/*
Relatively to the previous approach of working directly
with a ArrayList<MyNote>
this new approach brings native support to a TSV database

TSV = Tab Separated Values
Represent the collection of textual notes in a single text file
with this structure
when-text-note-was-taken\tnote-text\n
...
when-text-note-was-taken\tnote-text\n
 */
public class MyNotes {
    public final static String TAG_MY_NOTES = "TAG_MY_NOTES";

    Activity mActivity; //the Activity making the calls
    String mTSVDB; //name of the TSV file holding the DB

    ArrayList<MyNote> mNotes;
    String mEntireTSVContents;

    AmUtil mUtil;

    public ArrayList<MyNote> getMyNotes(){return this.mNotes;}

    public MyNotes(
        Activity pA,
        String pStrTSVDB
    ){
        this.mActivity = pA;
        this.mTSVDB = pStrTSVDB;
        mNotes = new ArrayList<>();

        mUtil = new AmUtil(this.mActivity);
        this.readFromTSV(); //this will populate the yet empty ArrayList for the mNotes
    }//MyNotes

    //not in use
    public MyNotes(
        Activity pA,
        String pStrTSVDB,
        ArrayList<MyNote> pCloneTheseNotes
    ){
        this.mActivity = pA;
        this.mTSVDB = pStrTSVDB;
        mNotes = new ArrayList<>();

        //would break the Adapter
        //this.mNotes = pCloneTheseNotes; //02930293 - 20390
        this.cloneNotes(pCloneTheseNotes);

        mUtil = new AmUtil(this.mActivity);
        this.readFromTSV(); //can only be called if mUtil is not null
    }//MyNotes

    public void cloneNotes(
        ArrayList<MyNote> pCloneThis
    ){
        if (this.mNotes!=null){
            mNotes.clear();
            for (MyNote n : pCloneThis) mNotes.add(n);
        }//if
    }//cloneNotes

    /*
    reads the TSV database file
    extracts, from the read text, the MyNote records
    populate the data member mNotes
    assuring that mNotes is in-sync with the TSV data
     */
    public /*void*/ String readFromTSV(){
        String strContentAtTSV =
            mUtil.genericPrivateInternalStorageFileReader(
                this.mTSVDB //text file name holding the DB
            );

        this.mEntireTSVContents = strContentAtTSV.trim();

        boolean bCanExtractNotesFromTSV =
            !this.mEntireTSVContents.isEmpty();

        if (bCanExtractNotesFromTSV){
            mNotes.clear();

            String[] aDatabaseRecordsEachRepresentingAMyNote=
                this.mEntireTSVContents.split("\n");
            for (
                String dbRecord :
                aDatabaseRecordsEachRepresentingAMyNote
            ){
                String[] aRecord = dbRecord.split("\t");
                boolean bCaution = aRecord.length==2;
                if (bCaution){
                    String strCalendar = aRecord[0].trim();
                    String strText = aRecord[1].trim();

                    try {
                        Calendar c = AmUtil.CalendarFromString(
                            strCalendar
                        );

                        MyNote newNoteJustRead =
                            new MyNote (
                                //strCalendar, //no MyNote constructor
                                c,
                                strText
                            );
                        mNotes.add(newNoteJustRead);
                    }//try
                    catch (Exception e){
                        Log.e(TAG_MY_NOTES, e.getMessage().toString());
                    }//catch
                }//if caution was met
            }//for every record in the database
        }//if

        return strContentAtTSV;
    }//readFromTSV

    public void writeToTSV(){
        String strAllMyNotesAsText = "";

        for (MyNote n : this.mNotes) strAllMyNotesAsText+=n.toTSV();

        mUtil.genericPrivateInternalStorageFileWriter(
            this.mTSVDB,
            strAllMyNotesAsText
        );
    }//writeToTSV

    public void remove (
        int pIndexOfTheMyNoteObjectToBeRemoved
    ){
        if (mNotes!=null){
            this.mNotes.remove(pIndexOfTheMyNoteObjectToBeRemoved);
            //extra: sync the collection with the TSV data
            this.writeToTSV();
        }//if
    }//remove

    public String getNoteString (
            int pIndexOfTheMyNoteObjectToBeRemoved
    ){

        if (mNotes!=null){
            return this.mNotes.get(pIndexOfTheMyNoteObjectToBeRemoved).mText;
            //extra: sync the collection with the TSV data
        }//if
        return "ERROR NO NOTE FOUND";
    }//getNoteString

    public String getNoteCalendar (
            int pIndexOfTheMyNoteObjectToBeMailed
    ){
        if(mNotes!=null) {
            MyNote n = mNotes.get(pIndexOfTheMyNoteObjectToBeMailed);
            String data = AmUtil.CalendarToString(n.mWhenTaken);
            return data;
        }
        return "ERROR NO DATE";
    }//get

    public void add(
        MyNote pNoteToBeAdded
    ){
        if (mNotes!=null){
            this.mNotes.add(pNoteToBeAdded);
            this.writeToTSV();
        }//if
    }//add

    public void reset(){
        if (mNotes!=null){
            this.mNotes.clear();
            this.writeToTSV();
        }//if
    }//reset
}//MyNotes
