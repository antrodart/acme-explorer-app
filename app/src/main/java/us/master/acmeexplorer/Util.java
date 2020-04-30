package us.master.acmeexplorer;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static String formateaFecha(long fecha) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(fecha*1000);
        DateFormat formatoFecha=DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
        Date chosenDate = calendar.getTime();
        return(formatoFecha.format(chosenDate));
    }

    public static long calendar2long(Calendar fecha) {
        if (fecha == null) return 0;
        return(fecha.getTimeInMillis()/1000);
    }

    public static String dateFormatSpanish(Calendar calendar){
        String res = "";
        int dayOFMonth = calendar.get(Calendar.DAY_OF_MONTH);;
        int month = calendar.get(Calendar.MONTH);;
        int year = calendar.get(Calendar.YEAR);;

        res = "" + dayOFMonth + "/" + month + "/" + year;

        return  res;
    }

    public static boolean checkStringEmpty(String string) {
        return string == null || string.equals("");
    }

}
