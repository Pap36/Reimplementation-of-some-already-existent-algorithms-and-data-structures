import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class CW2Q4 {
    // declaring fields
    private int occupiedMemory = 0; // slots occupied from memory
    private int lastKnownPos = 0; // last known index of an occupied slot
    private final Object[] memory = new Object[10000]; // virtual memory
    private Object first; // always represents the start of the List
    // when searching for an Object, keeps track of the previous one
    private Object importantObject;
    private final Scanner userScanner = new Scanner(System.in);

    /**
     * Class simulates the behaviours of C pointers having an address in memory
     * and a boolean value specifying if it is a NULL pointer or not
     */
    private static final class Pointer{

        private int address;
        private boolean isNull;

        /**
         * Constructor sets the pointer by default as being NULL
         */
        private Pointer(){
            this.setIsNull();
        }

        /**
         * Method sets a new address for the pointer and sets it as
         * not NULL
         * @param newAddress integer representing the slot in memory
         *                   to where this pointer points to
         */
        private void setAddress(int newAddress){
            address = newAddress;
            this.setIsNotNull();
        }

        /**
         * Getter for the address in memory.
         * @return integer representing the address in memory
         */
        private int getAddress(){
            return address;
        }

        /**
         * Method sets the boolean value of isNull to true.
         */
        private void setIsNull(){
            isNull = true;
        }

        /**
         * Method sets the boolean value of isNull to false.
         */
        private void setIsNotNull(){
            isNull = false;
        }

        /**
         * Getter for isNull
         * @return true if Pointer is Null
         *          false otherwise
         */
        private boolean getNull(){
            return isNull;
        }
    }

    /**
     * Class represents an Object of a XOR linked list, it has a char[]
     * representing the value/data/name, a pointer to the address in memory
     * and a pointer which represents the result of the XOR operation
     * on the addresses of its adjacent neighbours in the list.
     */
    public static final class Object{

        private char[] name;
        private Pointer address;
        private Pointer link;

        /**
         * By default both pointers are NULL
         */
        private Object(){
            this.address = new Pointer();
            this.link = new Pointer();
        }

        /**
         * Constructor which also passes a name for the object
         * @param name char[] representing the name
         */
        public Object(char[] name){
            this.name = name;
            this.address = new Pointer();
            this.link = new Pointer();
        }

        /**
         * Getter for the Pointer pointing to the address
         * in memory of the object
         * @return Pointer to the address in memory
         */
        private Pointer getPointer(){
            return this.address;
        }

        /**
         * Getter for the Pointer pointing to the link.
         * @return Pointer to the link.
         */
        private Pointer getLink(){
            return this.link;
        }

        /**
         * Method sets a new Pointer for the link field
         * @param newLink Pointer representing the new link
         */
        private void setLink(Pointer newLink){
            /* we do not want to have this.link = newLink since
             * newLink is temporary and its object will be freed from memory. */
            if(!newLink.getNull()){
                this.link.setAddress(newLink.getAddress());
                this.link.setIsNotNull();
            }
            else{
                this.link.setIsNull();
            }
        }

        /**
         * Getter for the value of the Object
         * @return char[] representing the name of the Object
         */
        public char[] getName(){
            return this.name;
        }

    }

    /**
     * Method initializes the virtual Memory by creating a new Object for each
     * slot. Also initializes the first Object which has value "BEGIN" by
     * default.
     */
    private void initializeMemory(){
        for(int i = 0; i < 10000; i++){
            memory[i] = new Object();
        }
        first = new Object("BEGIN".toCharArray());
        if(!malloc(first)) {
            System.out.println("Allocation of memory for first element failed"); // allocate memory for first
        }
    }

    /**
     * Method gets passed an Object and allocates memory for it (one slot from
     * the array).
     * @param object Object to be allocated memory to.
     * @return true if allocation was successful
     *          false otherwise
     */
    private boolean malloc(Object object){
        // check that there are still free slots
        if(occupiedMemory == 10000){
            return false;
        }
        // check the remaining of the slots (from lastKnownPos to the end)
        for(int index = lastKnownPos; index < 10000; index++){
            // find a slot which holds a NULL address Object
            if(memory[index].getPointer().getNull()){
                memory[index] = object;
                object.getPointer().setAddress(index);
                // update lastKnownPos and occupiedMemory
                lastKnownPos = index;
                occupiedMemory++;
                return true;
            }
        }
        /* if right side of array is full check
         * left side in case one slot has been freed. */
        for(int index = 0; index < lastKnownPos; index++){
            if(memory[index].getPointer().getNull()){
                memory[index] = object;
                object.getPointer().setAddress(index);
                lastKnownPos = index;
                occupiedMemory++;
                return true;
            }
        }
        return false;
    }

    /**
     * Method frees an allocated space in memory.
     * @param object Object to be freed from memory
     * @return true if operation was successful
     *          false otherwise
     */
    private boolean free(Object object){
        // check that the object has not been freed before or is not NULL
        if(object.getPointer().getNull()) return false;
        int index = object.getPointer().getAddress();
        memory[index] = new Object(); // create new Object in memory
        // update Pointers for object to be freed
        object.getPointer().setIsNull();
        object.getLink().setIsNull();
        occupiedMemory--;
        return true;
    }

    /**
     * Method returns the result of XOR operation on the address of 2 Pointers
     * @param pointer1 Pointer parameter
     * @param pointer2 Pointer parameter
     * @return Pointer result of the XOR operation
     */
    private Pointer XOR(Pointer pointer1, Pointer pointer2){
        // check for the NULL cases
        if(pointer1.getNull() && pointer2.getNull())return new Pointer();
        if(pointer1.getNull()) return pointer2;
        if(pointer2.getNull()) return pointer1;
        if(pointer1.getAddress() == pointer2.getAddress()) return new Pointer();
        // create result Pointer and return it
        Pointer result = new Pointer();
        result.setIsNotNull();
        result.setAddress(pointer1.getAddress() ^ pointer2.getAddress());
        return result;
    }

    /**
     * Method prints the elements of the current list to the file
     * XORList.txt.
     * Last element printed is always "NULL" to signal
     * that we reached the end of the list.
     * @param toFile true if printing the list to the file.
     *                false if printing the list to the terminal.
     */
    private void print(boolean toFile){
        try{
            PrintWriter out = new PrintWriter(new File(System.getProperty("user.dir") + "/XORList.txt"));
            Object current = first;
            Object prev = new Object();
            do{
                if(current != first) {
                    if(toFile){
                        out.write(current.getName());
                        out.write("->");
                    }
                    else {
                        System.out.print(current.getName());
                        System.out.print("->");
                    }
                }
                Pointer destination = XOR(prev.getPointer(), current.getLink());
                if(destination.getNull()) break;
                prev = current;
                current = memory[destination.getAddress()];
            }while(!current.getPointer().getNull());
            if(toFile) out.write("NULL\n");
            else System.out.println("NULL");
            out.close();
        } catch (FileNotFoundException e){
            System.out.println("File XORList.txt not found");
        }
    }

    /**
     * Method finds the length of a char array
     * @param arr char[] for which we need to find the length
     * @return integer representing the length
     */
    private int length(char[] arr){
        int length = 0;
        for(char ignored : arr){
            length++;
        }
        return length;
    }

    /**
     * Method compares two char arrays
     * @param name1 char[] parameter
     * @param name2 char[] parameter
     * @return true if name1 = name2
     *          false otherwise
     */
    private boolean compare(char[] name1, char[] name2){
        int length1 = length(name1);
        int length2 = length(name2);
        if(length1 != length2) return false;
        for(int aux = 0; aux < length1; aux++){
            if(name1[aux] != name2[aux]) return false;
        }
        return true;
    }

    /**
     * Method searches in the list of a specific Object
     * @param name char[] representing value of the Object
     * @return the Object if found
     *          null if not
     */
    private Object search(char[] name){
        Object current = first;
        Object prev = new Object();
        while(!current.link.getNull()){
            if(compare(current.getName(), name)){
                // keep track of object before
                importantObject = prev;
                return current;
            }
            Pointer destination = XOR(prev.getPointer(), current.getLink());
            if(destination.getNull()) return null;
            prev = current;
            current = memory[destination.getAddress()];
        }
        return null;
    }

    /**
     * Method inserts a new Object after a specified one
     * @param after char[] value of Object after which we will insert the new one
     * @param name char[] value of Object to be inserted
     */
    private void insertAfter(char[] after, char[] name){
        Object target = search(after);
        // check that the Object exists
        if(target == null){
            System.out.print("The name ");
            System.out.print(after);
            System.out.println(" is not in the list.");
            return;
        }
        // create the new Object
        Object newObject = new Object(name);
        if(!malloc(newObject)) {
            System.out.println("Allocation of memory failed");
            return;
        }
        /* find the Pointer address of the Object which
         * is originally after the target Object in the list */
        Pointer destination = XOR(importantObject.getPointer(), target.getLink());
        // if the target Object is the last one in the list
        if(destination.getNull()){
            newObject.setLink(target.getPointer());
            target.setLink(XOR(target.getLink(), newObject.getPointer()));
            return;
        }
        // update the links for the list
        Object rightObject = memory[destination.getAddress()];
        newObject.setLink(XOR(target.getPointer(), rightObject.getPointer()));
        target.setLink(XOR(importantObject.getPointer(), newObject.getPointer()));
        // First XOR operation results in the Pointer address of the right neighbour of the rightObject
        rightObject.setLink(XOR(XOR(rightObject.getLink(), target.getPointer()), newObject.getPointer()));
    }

    /**
     * Method inserts a new Object before a specified one
     * @param before char[] value of Object before which we need to insert the new one
     * @param name char[] value of new Object to be inserted
     */
    private void insertBefore(char[] before, char[] name){
        // check that the specified object exists
        Object target = search(before);
        if(target == null){
            System.out.print("The name ");
            System.out.print(before);
            System.out.println(" is not in the list.");
            return;
        }
        // create the new object
        Object newObject = new Object(name);
        Object leftObject = importantObject;
        if(!malloc(newObject)) {
            System.out.println("Allocation of memory failed");
            return;
        }
        newObject.setLink(XOR(target.getPointer(), leftObject.getPointer()));
        /* Pointer address of the Object which is originally after
         * the target Object in the list */
        Pointer rightside = XOR(target.getLink(), importantObject.getPointer());
        // First XOR operation results in the Pointer address to the left neighbour of importantObject
        importantObject.setLink(XOR(XOR(importantObject.getLink(), target.getPointer()), newObject.getPointer()));
        target.setLink(XOR(rightside, newObject.getPointer()));
    }

    /**
     * Method removes the Object after a specified one
     * @param after char[] representing value of specified Object
     */
    private void removeAfter(char[] after){
        // check that it exists
        Object target = search(after);
        if(target == null){
            System.out.print("The name ");
            System.out.print(after);
            System.out.println(" is not in the list.");
            return;
        }
        // find the address of the Object to be removed
        Pointer destination = XOR(target.getLink(), importantObject.getPointer());
        if(destination.getNull()){
            System.out.print("The name ");
            System.out.print(after);
            System.out.println(" is the last one in the list. Nothing can be removed");
            return;
        }
        Object toRemove = memory[destination.getAddress()];
        /* redo the links (impObj->target->toRemove->rightObj)
         * must become (impObj->target->rightObj)
         * redo target */
        Pointer rightAddress = XOR(toRemove.getLink(), target.getPointer());
        target.setLink(XOR(importantObject.getPointer(), rightAddress));
        // redo rightObj only if it is not NULL
        if(!rightAddress.getNull()){
            Object rightObject = memory[rightAddress.getAddress()];
            // First XOR operation results in the Pointer address of the right neighbour of rightObject
            rightObject.setLink(XOR(XOR(rightObject.getLink(), toRemove.getPointer()), target.getPointer()));
        }
        // free the memory
        if(!free(toRemove))System.out.println("Freeing of memory failed.");
    }

    /**
     * Method removes an Object before a specified one
     * @param before char[] value of specified Object
     */
    private void removeBefore(char[] before){
        // check that it exists
        Object target = search(before);
        if(target == null){
            System.out.print("The name ");
            System.out.print(before);
            System.out.println(" is not in the list.");
            return;
        }
        if(importantObject == first){
            System.out.print("The name ");
            System.out.print(before);
            System.out.println(" is the first one in the list. Nothing can be removed");
            return;
        }
        // important object is the same with object to be removed
        Pointer leftSide = XOR(importantObject.getLink(), target.getPointer());
        Object leftObject = memory[leftSide.getAddress()];
        // First XOR operation results in the Pointer address of the left neighbour of leftObject
        leftObject.setLink(XOR(XOR(leftObject.getLink(), importantObject.getPointer()), target.getPointer()));
        // First XOR operation results in the Pointer address of the right neighbour of target
        target.setLink(XOR(XOR(target.getLink(), importantObject.getPointer()), leftSide));
        // free memory
        if(!free(importantObject))System.out.println("Freeing of memory failed.");
    }

    /**
     * Method adds a new Object to the beginning of the list
     * @param newObject Object to be added to the list
     */
    private void addToBeginning(Object newObject){
        if(!malloc(newObject)) {
            System.out.println("Allocation of memory failed");
            return;
        }
        newObject.setLink(XOR(first.getLink(), first.getPointer()));
        if(!first.getLink().getNull()){
            Object rightObject = memory[first.getLink().getAddress()];
            // First XOR operation results in Pointer address of right neighbour of rightObject
            rightObject.setLink(XOR(XOR(rightObject.getLink(), first.getPointer()), newObject.getPointer()));
        }
        first.setLink(newObject.getPointer()); // left neighbour of first is NULL
    }

    /**
     * Method reads the file "names.txt" and adds each name to the beggining of the list
     * @throws FileNotFoundException in case File is not found
     */
    private void createListFromFile() throws FileNotFoundException{
        String[] listOfNames;
        String path = System.getProperty("user.dir") + "/names.txt";
        Scanner scanner = new Scanner(new File(path));
        String content = scanner.nextLine();
        // make sure string has format name","name","name etc. (get rid of first and last ")
        content = content.substring(1, length(content.toCharArray()) - 1);
        listOfNames = content.split("\",\"");
        String lastAdded = "";
        for(String name: listOfNames){
            Object nObject = new Object(name.toCharArray());
            if(compare(lastAdded.toCharArray(), "".toCharArray())) addToBeginning(nObject);
            else insertAfter(lastAdded.toCharArray(), name.toCharArray());
            lastAdded = name;
        }
        scanner.close();
        print(true);
        fileListMenu();
    }

    /**
     * Main menu method when operating the list from the file names.txt.
     */
    private void fileListMenu(){
        System.out.println("Operating the list made from the names from names.txt.\nTo see the changes" +
                " made after each command check the contents of XORList.txt.\nCaution: the file overwrites" +
                " itself after every command.\nRight now the file contains the initial list.\n");
        System.out.println("Press 1 to insert a new name after a specified one.\nPress 2 to insert a new name" +
                " before a specified one.\nPress 3 to remove the name after a specified one.\nPress 4 to remove" +
                " the name before a specified one.\nPress 0 to exit the program.");
        String option = userScanner.nextLine();
        String name1, name2;
        while (!compare(option.toCharArray(), "0".toCharArray())){
            switch (option){
                case "1":
                    System.out.println("Please enter the name after which you wish to insert the new one:");
                    name1 = userScanner.nextLine();
                    System.out.println("Please enter the new name you wish to insert:");
                    name2 = userScanner.nextLine();
                    insertAfter(name1.toCharArray(), name2.toCharArray());
                    print(true);
                    break;
                case "2":
                    System.out.println("Please enter the name before which you wish to insert the new one:");
                    name1 = userScanner.nextLine();
                    System.out.println("Please enter the new name you wish to insert:");
                    name2 = userScanner.nextLine();
                    insertBefore(name1.toCharArray(), name2.toCharArray());
                    print(true);
                    break;
                case "3":
                    System.out.println("Please enter the name after which you want to remove:");
                    name1 = userScanner.nextLine();
                    removeAfter(name1.toCharArray());
                    print(true);
                    break;
                case "4":
                    System.out.println("Please enter the name before which you want to remove:");
                    name1 = userScanner.nextLine();
                    removeBefore(name1.toCharArray());
                    print(true);
                    break;
                default:
                    System.out.println("Not an option. Please try again:");
                    break;
            }
            System.out.println("Enter your option:");
            option = userScanner.nextLine();
        }
    }

    /**
     * Method frees all memory before exiting the program.
     */
    private void freeAll(){
        System.out.println("Occupied memory before starting to free everything: " + occupiedMemory);
        Object current = first;
        Object prev = new Object();
        do{
            Pointer destination = XOR(prev.getPointer(), current.getLink());
            if(current != first){
                if(!free(prev))System.out.println("Freeing of memory failed.");
            }
            if(destination.getNull()) break;
            prev = current;
            current = memory[destination.getAddress()];
        }while(!current.getPointer().getNull());
        if(!free(current))System.out.println("Freeing of memory failed.");
        System.out.println("Occupied memory at the end of the program: " + occupiedMemory);
    }

    /**
     * Method which creates a basic list and demonstrates each method
     */
    private void createTestList(){
        Object nObject = new Object("LARRY".toCharArray());
        Object nObject2 = new Object("JOHN".toCharArray());
        Object nObject3 = new Object("JAMES".toCharArray());
        addToBeginning(nObject);
        addToBeginning(nObject2);
        addToBeginning(nObject3);
        System.out.println("This is the initial list:");
        print(false);
        System.out.println("Inserting LUKE after LARRY and MARY before JOHN: ");
        insertAfter("LARRY".toCharArray(), "LUKE".toCharArray());
        insertBefore("JOHN".toCharArray(), "MARY".toCharArray());
        print(false);
        System.out.println("Removing after LARRY and before MARY");
        removeAfter("LARRY".toCharArray());
        removeBefore("MARY".toCharArray());
        print(false);
    }

    /**
     * Menu method which allows the user to select between two options:
     * 1. see a demo of a test list in action
     * 2. operate on the linked list created with the names from names.txt
     */
    private void menu(){
        System.out.println("Press 1 to see a demonstration of the XOR Linked List implementation" +
                " or 2 if you wish to operate the list created from the file names.txt");
        String input = userScanner.nextLine();
        boolean invalid = true;
        while (invalid){
            switch (input){
                case "1":
                    createTestList();
                    invalid = false;
                    break;
                case "2":
                    try {
                        createListFromFile();
                        invalid = false;
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Not an option. Please try again: ");
                    input = userScanner.nextLine();
            }
        }
        userScanner.close();
    }

    /**
     * Main method
     * @param args command line arguments
     * @throws FileNotFoundException if File "names.txt" is not found
     */
    public static void main(String[] args) throws FileNotFoundException{
        CW2Q4 main = new CW2Q4();
        main.initializeMemory();
        main.menu();
        main.freeAll();
    }

}
