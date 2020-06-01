//MyNote.java (SINGULAR)
package com.joythis.android.privatetextnotes;

import java.io.Serializable;
import java.util.Calendar;

public class MyNote implements Serializable {
    /*
    String mWhoIsTaking;
    String mStrCategory;
    Boolean mSecret;
    */

    Calendar mWhenTaken;
    String mText;

    public MyNote(
        String pTextForTheNote
    ){
        this.mText = pTextForTheNote;
        this.mWhenTaken = Calendar.getInstance();
    }//MyNote

    /*
    born when we first needed to read specific
    MyNote objects from a TSV file
    a specific MyNote object has a specific calendar
     */
    public MyNote(
        Calendar pWhenTaken,
        String pTextForTheNote
    ){
        this.mText = pTextForTheNote;
        this.mWhenTaken = pWhenTaken;
    }//MyNote

    @Override
    public String toString(){
        String strRet = "";

        strRet = String.format(
            "Created on: %s\n%s\n",
            AmUtil.CalendarToString(this.mWhenTaken), //Y-m-d H:m:s
            this.mText
        );

        return strRet;
    }//toString

    public String toTSV(){
        String strRet = "";
        String strSafeText = this.mText.replace(
            "\t", " "
        );
        strSafeText = this.mText.replace(
                "\n", " "
        );

        strRet = String.format(
            "%s\t%s\n",
            AmUtil.CalendarToString(this.mWhenTaken), //Y-m-d H:m:s
            /*
            this.mText is
            NOT TRUSTABLE, because with are supporting a multi-line EditText
            object, in which users can input the reserved symbols \n \t

            solution: replace the reserved symbols with something else
             */
            //this.mText
            strSafeText
        );

        return strRet;
    }//toTSV

}//MyNote


