#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

int indx = 0; // keeps track of how many redactable words we have

/* Struct defines a redactable word. */
struct redactable{
    char *word;
};

/* Pointer to a redactable word (will use as array of redactable words). */
struct redactable *set;

/* Function checks if a ceratin character is a letter or not. 
 * @param x character to be checked
 * @retturn true if x is letter
 *          false if not */
bool isLetter(char x){

    int asciiValue = x;
    
    return (asciiValue <= 90 && asciiValue >= 65) ||
         (asciiValue >= 97 && asciiValue <= 122);
}

/* Function converts a character (if it is a letter) to lowerCase 
 * @param letter character to be converted 
 * @return the modified value of the character. */
char lowerCase(char letter){

    int asciiValue = letter;
    
    if(isLetter(letter)){
        if(asciiValue <= 90) asciiValue += 32;
    }
    
    return (char)asciiValue;
}

/* Function searches for every apparition of a word in a paragraph 
 * and redacts each apparition.
 * @param word char[] to search for
 * @param paragraph char[] in which we will search.*/
void search(char* word, char* paragraph){

    int index = 0; // used to iterate through the paragraph
    int seqIndex; // keeps track of the index of *(word) in paragraph
    int seqLength = 0; // length of similar characters
    
    // true if we are in the middle of a sequence or not
    bool compareStarted = false; 
    
    while(*(paragraph + index) != '\0'){
        
        // letter coincides (check to be lowercase)
        if(lowerCase(*(paragraph + index)) == *(word + seqLength)){
            
            if(!compareStarted){
                
                compareStarted = true;
                seqIndex = index;
            }
            
            seqLength++;
            
            // redactable word ended need to check if paragraph character is letter or not
            if(*(word + seqLength) == '\0'){
                
                // redact
                if(!isLetter(*(paragraph + index + 1))){
                    
                    for(int i = seqIndex; i <= index; i++){
                        
                        if(isLetter(*(paragraph + i))) *(paragraph + i) = '*';
                    }
                }
            }
        }
        
        // start a new sequence
        else if(compareStarted){
            
            compareStarted = false;
            seqLength = 0;
        }
        
        index++;
    }
}

/* Function redacts a block of text using the list of redactable words. 
 * @param blockOfText char* representing the block of text to be redacted. 
 * @return char* representing the redacted block of text. */
char *redact(char *blockOfText){

    for(int aux = 0; aux < indx; aux++){
        search((set + aux)->word, blockOfText);
    }
    
    return blockOfText;
}

/* Function adds a new word to the set of redactable words. 
 * @param word char* to be added to the set. */
void addToSet(char *word){

    struct redactable *newWord;
    
    // allocate memory and check that it was succsessful.
    newWord = (struct redactable*)malloc(sizeof(struct redactable));
    newWord->word = (char*)malloc(sizeof(char));
    
    if(newWord == NULL || newWord->word == NULL){
        printf("Allocation of memory failed.\n");
        return;
    }
    
    int aux = 0;
    
    while(*(word + aux) != '\n'){
        
        // all words from set are lowercase
        *(newWord->word + aux) = lowerCase(*(word + aux));
        aux++;
    }
    
    *(newWord->word + aux) = '\0';
    *(set + indx) = *newWord;
    
    // free newWord since value is now stored set
    free(newWord);
    indx++;
}

/* Function allocates memory for the set and redacts the redactable words 
 * from the file redactQ5.txt */
void initialzeSet(){

    // max 1024 redactable words
    set = (struct redactable*)malloc(sizeof(struct redactable) * 1024);
    FILE *redactIn = fopen("redactQ5.txt", "r");
    
    if(redactIn == NULL){
        printf("Failed to open file redactQ5.txt\n");
        return;
    }
    
    // format of file should be redactableWord\nredactableWord\netc...\n
    char *buf;
    
    // assumed maximum 255 bytes for a line
    buf = (char*)malloc(255 * sizeof(char));    
    
    while(fgets(buf, 255, redactIn)){
        addToSet(buf);
    }
    
    // close file and free the memory
    fclose(redactIn);
    free(buf);
}

/* Function redacts the file debate.txt and ouputs the result
 * in redacted_debate.txt 
 * @param terminalPrint boolean value representing if the 
 *          redacted text should alse be printed in the terminal*/
void redactFile(bool terminalPrint){
    
    FILE *debateIn = fopen("debate.txt", "r");
    FILE *redactOut = fopen("redacted_debate.txt", "w");
    
    if(debateIn == NULL){
        printf("Failed to open file debate.txt\n");
        return;
    }
    
    if(redactOut == NULL){
        printf("Failed to open file redacted_debate.txt\n");
        return;
    }
    
    // assume maximum 4096 bytes per line
    char *buf;
    buf = (char*)malloc(4096 * sizeof(char));
    
    while(fgets(buf, 4096, debateIn)){
        
        buf = redact(buf);
        
        if(terminalPrint) printf("%s", buf);
        
        fprintf(redactOut, "%s", buf);
    }
    
    // close files and free memory
    fclose(debateIn);
    fclose(redactOut);
    free(buf);
}

/* Function frees the memory which has been allocated to the set
 * when code is ready to finish.*/
void freeAll(){
    
    for(int aux = 0; aux < indx; aux++){
        free((set + aux)->word);
    }
    
    free(set);
}

/* Function compare two char arrays.
 * @param left char* to be compared
 * @param right char* to be compared
 * @return true if left=right
 *          flase otherwise*/ 
bool compare(char *left, char *right){
    
    int aux = 0;
    
    while(*(left + aux) != '\0'){
        
        if(*(right + aux) == '\0') return false;
        
        if(*(left + aux) != *(right + aux)) return false;
        
        aux++;
    }
    
    if(*(right + aux) != '\0') return false;
    
    return true;
}

/* Main function accepts command line arguments (-T) to print
 * the redacted text in the terminal as well as in the file.
 * Default option is printing the redacted text in redacted_debate.txt*/
int main(int argc, char *argv[]){
    
    bool terminalOption = false;
    
    for(int index = 0; index < argc; index++){
        if(compare(argv[index], "-T"))terminalOption = true;
    }
    
    initialzeSet();
    redactFile(terminalOption);
    freeAll();
    
    return 0;
}