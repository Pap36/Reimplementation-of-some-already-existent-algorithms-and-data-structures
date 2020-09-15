/**
 * Class counts the number of Tuesdays which fell on the first of the month
 * from 1 Jan 1901 to 31 Dec 2000
 */
public class CW2Q8 {
    // declaring fields
    private int year = 1900;
    private int day = 1; // keeps track of the date of the first Tuesday in a month
    private int month = 1;
    /* Array holds the number of days for each month (February is updated accordingly) */
    private int[] numberOfDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int numberOfTuesdays = 0; // solution to the problem

    /**
     * Method checks if the current year is a leap one or not. Since we are checking
     * from 1900 to 2000, there is no need to check for the 'divisible by 400' rule since
     * we know that 1900 is not leap, 2000 is and these are the only years representing
     * centuries.
     */
    private void checkLeap(){
        
        // update the number of days for February accordingly
        if(year == 1900) {
            numberOfDays[1] = 28;
            return;
        }
        
        if(year % 4 == 0){
            numberOfDays[1] = 29;
            return;
        }
        
        numberOfDays[1] = 28;
    }

    /**
     * Method makes the transition to the next month and checks if Tuesday
     * is the first day of the month.
     */
    private void skipToNextMonth(){
        
        day += 21;
        
        while(day + 7 <= numberOfDays[month - 1]){
            day += 7;
        }
        
        day = day + 7 - numberOfDays[month - 1];
        month++;
        
        // do not count the months in 1900 since it is from the previous century
        if(day == 1 && year > 1900){
            numberOfTuesdays++;
        }
    }

    /**
     * Method makes the transition to the next year.
     */
    private void skipToNextYear(){
        
        // transition through each month
        while(month < 12){
            skipToNextMonth();
        }
        
        // Analyze December separately
        day += 21;
        
        while(day + 7 <= 31){
            day += 7;
        }
        
        day = day + 7 - 31;
        month = 1;
        
        if(day == 1) {
            numberOfTuesdays++;
        }
        
        // go to the next year
        year++;
        checkLeap();
    }

    /**
     * Method finds the date of the first Tuesday in 1901 given that
     * 1st Jan 1900 was a Monday.
     */
    private void findFirstTuesday(){
        
        /* set the date to 2nd to record Tuesdays */
        day = 2;
        skipToNextYear();
    }

    /**
     * Method counts the number of Tuesdays from 1901 to 2000 which fell on the
     * first of a month.
     */
    private void countTuesdays(){
        
        findFirstTuesday();
        
        while (year < 2000) {
            skipToNextYear();
        }
        
        /* we are at the beginning of 2000 now; check until
         December (no need to check transition from Dec 00 to Jan 01) */
        while(month < 12) skipToNextMonth();
        System.out.println("There have been " + numberOfTuesdays + " Tuesdays who fell on the first of the month.");
    }

    /**
     * Main method for testing and running the code. Note that the code can be easily changed
     * to count the appearances of any other day of the week by changing the initial value
     * of variable 'day' on line 93. For example, initializing day with value 3 will lead to
     * counting the number of Wednesdays which land on the first of a month.
     * @param args String[] command line arguments
     */
    public static void main(String[] args) {
        CW2Q8 main = new CW2Q8();
        main.countTuesdays();
    }
}
