package sampleapp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class query {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static void main(String[] args) {
        String dateString = "2020-01-01";
        //get next day of date string
        do {
            try {
                // Parse the input string to a Date object
                Date date = dateFormat.parse(dateString);
                // Create a Calendar instance and set it to the parsed date
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                // Add one day to the date
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                // Get the next day
                Date nextDay = calendar.getTime();
                // Format the next day as a string
                String nextDayString = dateFormat.format(nextDay);

                // Print the result
                System.out.println("Next Day: " + nextDayString);
                dateString = nextDayString;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        while(!dateString.equals("2021-01-02"));

    }
}
