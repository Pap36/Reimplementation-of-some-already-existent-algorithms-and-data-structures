import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class CW2Q6 {
    // declaring fields
    String[] redactableWords; // redactable words from file redact.txt
    int length = 0; // total number of redactable words from file
    String allWords = ""; // contains content of file redact.txt
    boolean beginSentence = true; // true if we are at the beginning of a sentence (first word)
    boolean wordStarted = false; // true if we are analysing characters from an uppercase letter word

    /**
     * Method reads the file redactQ6.txt and adds all the redactable words
     * to the String array.
     */
    private void readRedactable(){

        try{

            Scanner scanner = new Scanner(new File(System.getProperty("user.dir") + "/redactQ6.txt"));
            // add each word to a string
            while(scanner.hasNext()){
                length++;
                allWords += scanner.next() + " ";
            }

            scanner.close();
            redactableWords = new String[length];
            redactableWords = allWords.split(" ");
            scanner.close();

        } catch (FileNotFoundException e){
            System.out.println("File redact.txt not found.");
        }
    }

    /**
     * Method reads the file warandpeace.txt line by line
     * and redacts its line accordingly. Prints the output to the
     * file redactedWarAndPeace.txt .
     */
    private void redactTextFromFile(){
        try{

            Scanner scanner = new Scanner(new File(System.getProperty("user.dir") + "/warandpeace.txt"));
            PrintWriter out = new PrintWriter(new File(System.getProperty("user.dir") + "/redactedWarAndPeace.txt"));

            while (scanner.hasNextLine()){
                out.write(redact((scanner.nextLine()+" ").toCharArray()));
            }

            scanner.close();
            out.close();

        }catch (FileNotFoundException e){
            System.out.println("File warandpeace.txt not found");
        }
    }

    /**
     * Method checks if a passed character is a letter or not.
     * @param x character to check
     * @return true if x is letter
     *          false otherwise
     */
    private boolean isLetter(char x){
        return (x >= 65 && x <= 90) || (x <= 122 && x >= 97);
    }

    /**
     * Method checks if a passed character is an uppercase letter
     * @param x character to check
     * @return true if x is an uppercase letter
     *          false if not
     */
    private boolean isUppercase(char x){

        if(isLetter(x)){
            return x <= 90;
        }

        return false;
    }

    /**
     * Method determines the length of a char array.
     * @param arr char[] for which the length must be determined
     * @return integer representing the length
     */
    private int length(char[] arr){

        int index = 0;

        for(char ignored: arr){
            index++;
        }

        return index;
    }

    /**
     * Method checks if two char arrays are equal or not
     * @param left char[] to be compared
     * @param right char[] to be compared
     * @return true if left = right
     *          false otherwise
     */
    private boolean compare(char[] left, char[] right){

        if(length(left) != length(right)) return false;

        int index = 0;

        for(char letter: left){
            if(letter != right[index]) return false;
            index++;
        }

        return true;
    }

    /**
     * Method searches for a word in the list of redactable words.
     * @param word char[] to search for
     * @return true is word is in redactableWords
     *          false if it isn't
     */
    private boolean search(char[] word){

        for(int index = 0; index < length; index++){

            if(compare(word, redactableWords[index].toCharArray())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method redacts the proper nouns from a char array
     * @param blockOfText char[] to be redacted
     * @return String representing the redacted version
     */
    private String redact(char[] blockOfText){

        int index = 0; // index of current letter from block of text
        int beginIndex = 0; // index of first letter from a possible proper noun

        // blockOfText is a new line
        if(blockOfText[0] == ' '){
            wordStarted = false;
            beginSentence = true;
            return "\n";
        }

        for(char character: blockOfText){
            // defining chapters or books
            if(index > 0){
                if(isUppercase(character) && isUppercase(blockOfText[index-1])){
                    wordStarted = false;
                    beginSentence = false;
                    index++;
                    continue;
                }
            }
            // I is not a proper noun even if in the middle of sentence
            if (character == 'I' && !isLetter(blockOfText[index + 1])) {
                index++;
                beginSentence = false;
                wordStarted = false;
                // if I is at end of sentence
                if(blockOfText[index] == '.') beginSentence = true;
                continue;
            }
            // analyzing character in the middle of an uppercase word
            if(wordStarted){
                // either end of sentence or redactable (St. Petersburg)
                if(character == '.'){
                    /* Create a new char[] to search in redactable set*/
                    char[] newWord = new char[index - beginIndex + 1];
                    for(int aux = beginIndex; aux <= index; aux++) newWord[aux - beginIndex] = blockOfText[aux];

                    /*if redactable with . still in sentence*/
                    if(search(newWord)){
                        for(int aux = beginIndex; aux <= index; aux++)blockOfText[aux] = '*';
                    }

                    /*if not redactable with . redact and end sentence ( beginSentence = true )*/
                    /*check if it is not a single word sentence ex: Yes.*/
                    else{
                        if(beginSentence){
                            char[] add = new char[index - beginIndex];
                            for(int aux = beginIndex; aux < index; aux++){
                                add[aux - beginIndex] = blockOfText[aux];
                            }
                            // redact only if in the list
                            if(search(add)){
                                for(int aux = beginIndex; aux <= index; aux++)blockOfText[aux] = '*';
                            }
                        }
                        // check for multiple . ("Ohh...")
                        else if (blockOfText[index+1] != '.'){
                            char[] add = new char[index - beginIndex];
                            for(int aux = beginIndex; aux < index; aux++){
                                add[aux - beginIndex] = blockOfText[aux];
                                blockOfText[aux] = '*';
                            }
                            beginSentence = true;
                        }
                    }
                    /*word has ended*/
                    wordStarted = false;
                }
                // if character is not '.' and not a letter
                else if(!isLetter(character)){
                    if(beginSentence) {
                        // search in list to know if needed to be redacted
                        char[] newWord = new char[index - beginIndex];
                        // don't include non letter character
                        for(int aux = beginIndex; aux < index; aux++) newWord[aux - beginIndex] = blockOfText[aux];

                        if(search(newWord)){
                            for(int aux = beginIndex; aux < index; aux++) blockOfText[aux] = '*';
                        }
                        // these characters start a new sentence
                        if(character != '?' && character != '!')beginSentence = false;
                        wordStarted = false;
                    }
                    // word started, not first in the sentence, non letter character => redact
                    else{
                        char[] add = new char[index - beginIndex];
                        for(int aux = beginIndex; aux < index; aux++){
                            add[aux - beginIndex] = blockOfText[aux];
                            blockOfText[aux] = '*';
                        }
                        wordStarted = false;
                        // these characters also start a new sentence
                        if(character == '?' || character == '!' || character == (char)8220 || character == (char)8216 ||
                            character == ':' || character == '(' || character == ')' || character == ',' ||
                                character == '—' || character == (char)8221 || character == (char)8217){
                            beginSentence = true;
                        }
                    }
                }
            }
            // word not started but we encountered a character which starts a new sentence
            else if(character == '.' || character == '?' || character == '!' || character == (char)8220 ||
                    character == (char)8216 || character == ':' || character == '(' || character == ')' ||
                     character == ',' || character == '—' || character == (char)8221 || character == (char)8217){
                beginSentence = true;
            }
            // assume there are no words with multiple capital letters except for CHAPTER, BOOK, etc.
            if(isUppercase(character)){
                wordStarted = true;
                beginIndex = index;
            }
            // if we think we are at beginning of sentence but we encounter a lowercase word
            if(beginSentence && isLetter(character) && !isUppercase(character) && !wordStarted){
                beginSentence = false;
            }

            index++;
        }
        // whole char[] has been redacted
        return String.copyValueOf(blockOfText) + "\n";
    }

    /**
     * Main method redacts the file properly and outputs
     * the result to the file redactedWarAndPeace.txt
     * @param args String array of command line arguments
     */
    public static void main(String[] args) {
        CW2Q6 main = new CW2Q6();
        main.readRedactable();
        main.redactTextFromFile();
    }
}
