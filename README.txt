Expected behaviour for each of the files:

CW2Q1.c - Cocktail Shaker sort
The code generates a random array of 100 positive integers using a simple pseudo random number generator
and prints in the terminal the sorted array.

CW2Q2.java - Quicksort 
The file reads a list of names from the file names.txt and sorts them using a quicksort.
The sorted list will be printed to the file sortedNames.txt
The code also uses an already implemented sorting method to CHECK ONLY that the sorted
list is sorted correctly. Apart from the file sortedNames.txt, a message will be outputted in the terminal:
"The list was correctly/incorrectly sorted" based on the result.

CW2Q3.c - Hash table
A hash table is created with all the names from the file names.txt. The terminal displays 
a menu for the user to either add, remove or search names in the hash table. These 3 options
will provide evidence of the working hash table.
NOTE: Table names are case sensitive.

CW2Q4.java - XOR Linked List
When run, there are two options available for the user.
Option 1: See a demonstration of the XOR Linked List implementation which is already coded.
            It provides evidence of each method required working accordingly on a short list of names.
Option 2: There is a XOR Linked List created with all the names from the file names.txt in the given order
            The user is then presented with a menu which has 4 options: insertAfter, insertBefore, removeAter, removeBefore
            Each option will guide the user for appropriate input.
CAUTION: Option 1 demonstration can be seen in the terminal. However, Option 2 results are printed to the file XORList.txt. The file
overwrites itself after each command so it is recommended to refresh and constantly check the contents of the file to notice the changes.
After selecting Option 2, the file contains the whole list of names from names.txt.
Also, names are case sensitive.
NOTE: when dislaying the XOR Linked List, the last element will be NULL to signal the end. Also to suggest that we are dealing with a list,
the format of displaying will be NAME->NAME->NAMe-> ... ->NAME->NULL
NOTE2: when the code finishes, the following message can be seen in the terminal
"Memory occupied before freeing everything: %d
 Memory occupied after freeing everything: %d"
This highlights the implementation of malloc and free (from C) in Java. Hopefully, the 2nd row will have 0 as memory occupied when the code finishes.

CW2Q5.c - Redact the debate
When run the code will create a file (redacted_debate.txt) which will contain the redacted debate (read from debate.txt) according to the redactable words
found in redactQ5.txt
Note: when running the compiled file, use -T as a command line argument to also display the redacted text in the terminal.
EX: if compiled file is Q5, run ./Q5 -T

CW2Q6.java - Redact War and Peace
When run the code creates a new file (redactedWarAndPeace.txt) which will contain the redacted text (read from waradnpeace.txt) according to the redactable
words found in redactQ6.txt and an algorithm which tries to identify proper nouns.

CW2Q7.c - Encrypt using LOVELACE
When run the code creates a new file (encrypted.txt) which contains the encrypted message from text.txt using the given algorithm and the keyword 'LOVELACE'
NOTE: The file does not overwrite itself so if run multiple times, the format of the file will be:
NEW ENCRYPTION
*** new line ***
*** encrypted message ***
*** new line ***
Hence, the encrypted message is strictly what is found between the empty row after NEW ENCRYPTION and the last empty row either from file or before
a new NEW ENCRYPTION text.

CW2Q8.java - Project Euler Problem 19
The code is run and displays in the terminal the number of Tuesdays which fell on the first of a month from 1 Jan 1901 to 31 Dec 2000.

*** END OF DOCUMENT ***