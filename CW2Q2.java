import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Implementing a quick sort algorithm in Java
 * for a list of names, read from a file.
 */
public class CW2Q2 {

    private String[] listOfNames;

    /**
     * Creates the list of names by reading them from the file and extracting them
     * accordingly.
     * @throws FileNotFoundException if specified file is not found
     */
    private void initializeList() throws FileNotFoundException {
        String path = System.getProperty("user.dir") + "/names.txt";
        Scanner scanner = new Scanner(new File(path));
        String content = scanner.nextLine();
        // make sure string has format name","name","name etc. (get rid of first and last ")
        content = content.substring(1, length(content) - 1);
        listOfNames = content.split("\",\"");
        scanner.close();
    }

    /**
     * Method checks if our sorted list of names is indeed sorted
     * by using the Arrays.sort method and then comparing the two.
     * Note: method used to verify the result only.
     * @return true if the arrays coincide
     *          false otherwise
     */
    private boolean checkList(){
        String[] testing = listOfNames;
        Arrays.sort(testing);
        int index = 0;
        for(String name: listOfNames){
            if(compareNames(name, testing[index]) != 0) return false;
            index++;
        }
        return true;
    }

    /**
     * Method prints the list to the file
     * sortedNames.txt
     */
    private void printList(){
        try {
            File sorted = new File(System.getProperty("user.dir") + "/sortedNames.txt");
            PrintWriter out = new PrintWriter(sorted);
            boolean isFirst = true;
            for (String name : listOfNames) {
                if(isFirst) isFirst = false;
                else out.write(",");
                out.write("\"" + name + "\"");
            }
            out.write("\n");
            out.close();
        } catch (FileNotFoundException e){
            System.out.println("File sortedNames.txt was not found");
        }
        System.out.println();
    }

    /**
     * Method returns the length of a string
     * @param string String passed
     * @return integer representing the length of the string
     */
    private int length(String string){
        char[] arr = string.toCharArray();
        int index = 0;
        for(char ignored : arr){
               index++;
        }
        return index;
    }

    /**
     * Method returns the length of a String array
     * @param arr Array of strings
     * @return integer representing the length of the array
     */
    private int length(String[] arr){
        int index = 0;
        for(String ignored : arr){
            index++;
        }
        return index;
    }

    /**
     * Compares to strings representing two names.
     * @param name1 String representing one name
     * @param name2 String representing the other name
     * @return 1 if name1 > name2
     *         -1 if name1 < name2
     *         0 if name1 = name2
     */
    private int compareNames(String name1, String name2){
        char[] nameA = name1.toCharArray(), nameB = name2.toCharArray();
        int index = 0, lengthA = length(name1), lengthB = length(name2);
        // compare letter by letter
        if(lengthA <= lengthB){
            for(char letter : nameA){
                if(letter < nameB[index]){
                    return -1;
                }
                if(letter > nameB[index]){
                    return 1;
                }
                index++;
            }
            // either nameA = nameB or nameB = nameA + more characters
            if(lengthA == lengthB) return 0;
            return -1;
        }
        else{
            for(char letter : nameB){
                if(letter < nameA[index]){
                    return 1;
                }
                if(letter > nameA[index]){
                    return -1;
                }
                index++;
            }
            // nameA = nameB + more characters
            return 1;
        }
    }

    /**
     * Quick sort an array.
     * @param array String array to be sorted
     * @param left starting index for the part of the array we want to quick sort
     * @param right ending index for the part of the array we want to quick sort
     */
    private void quickSort(String[] array, int left, int right){
        String pivot = array[(left + right)/2];
        int start, end;
        start = left;
        end = right;
        // find and place the pivot on its right position
        while(start <= end){
            // find out of order name from left side of pivot
            while(compareNames(array[start], pivot) == -1){
                start++;
            }
            // find out of order name from right side of pivot
            while(compareNames(array[end], pivot) == 1){
                end--;
            }
            // exchange them if they are on different sides of the pivot
            if(start <= end){
                String temp = array[start];
                array[start] = array[end];
                array[end] = temp;
                //update values to check the next elements
                start++;
                end--;
            }
        }
        // calling the method recursively; pivot is in position start - 1 or end + 1
        if(left < end){
            quickSort(array, left, end - 1);
        }
        if(start < right){
            quickSort(array, start, right);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        CW2Q2 main = new CW2Q2();
        main.initializeList();
        main.quickSort(main.listOfNames, 0, main.length(main.listOfNames) - 1);
        if(main.checkList()){
            System.out.println("The list has been correctly sorted");
        }
        else{
            System.out.println("The list has been incorrectly sorted");
        }
        main.printList();
    }
}
