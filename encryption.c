#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

/* array which stores the indexes order when the keyword is encrypted */
int *indexes;
FILE *fileIn, *writeOut; // pointers to files (text.txt, encrypted.txt)

/* Function which determines the length of a char array.
 * @param word char array for which the length is determined
 * @return integer representing the length. */
int length(char* word){
    
    int index = 0;
    while(*(word + index) != '\0')index++;
    
    return index;
}

/* Function gets passed the keyword, sorts its letters in alphabetical
 * order and keeps track of the indexes order. 
 * @param keyword char array containing the keyword. */
void determineOrder(char *keyword){
    
    int limit = length(keyword);
    char *key = (char*)malloc(limit * sizeof(char));
    indexes = (int*)malloc(limit * sizeof(int));
    
    // intialize default indexes
    for(int aux = 0; aux < limit; aux++){
        *(indexes + aux) = aux;
        *(key + aux) = *(keyword + aux);
    }
    
    // bubble sort characters
    bool swap = true;
    
    while(swap){
        
        swap = false;
        for(int aux = 0; aux < limit - 1; aux++){
            
            if(*(key + aux) > *(key + aux + 1)){
                
                char temp = key[aux];
                int tempo = indexes[aux];
                
                key[aux] = key[aux + 1];
                indexes[aux] = indexes[aux + 1];
                key[aux + 1] = temp;
                indexes[aux + 1] = tempo;
                
                swap = true;
            }
        }
    }
}

/* Function gets passed a char array and writes the encrypted version
 * to the file to which writeOut points to.
 * @param buffer char array to be encrypted. */
void process(char* buffer){
    
    // output the letter according to the sorted indexes
    for(int aux = 0; aux < length(buffer); aux++){
        fprintf(writeOut, "%c", *(buffer + *(indexes + aux)));
    }
}

/* Function encrypts the file text.txt using a passed keyword.
 * @param keyword char array consisting of the keyword.*/
void encryptFile(char* keyword){

    writeOut = fopen("encrypted.txt", "a");
    fileIn = fopen("text.txt", "r");
    // file appends new information so let user know that this is a new encryption
    fprintf(writeOut, "%s", "NEW ENCRYPTION\n\n");

    if(fileIn == NULL || writeOut == NULL){
        printf("Failed to open files.");
    }
    // sort the keyword and determine the indexes order
    determineOrder(keyword);
    
    char *buf;
    buf = (char*)malloc(length(keyword) * sizeof(char));
    int nread = 0;
    
    do{
        // read exactly length(keyword) characters in buffer
        nread = fread(buf, sizeof(char), length(keyword), fileIn);
        
        if(nread == 0) break;
        
        // pad the message if not enough characters were read
        if(nread > 0 && nread < length(keyword)){
            
            for(int aux = nread; aux < length(keyword); aux++){
                *(buf + aux) = 'X';
            }
        }
        
        process(buf);
    }while(nread > 0);
    
    fprintf(writeOut, "%s", "\n\n");

    free(buf);
    
    fclose(fileIn);
    fclose(writeOut);
    
    free(indexes);
}

/* Main function calls the encryptFile method with LOVELACE as a keyword. */
int main(void){
    
    encryptFile("LOVELACE");
    
    return 0;
}
