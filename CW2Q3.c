#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

long x=31, p=5209; // parameters for the hashing function
char *leftOver; // leftover charcters out of a name when reading from file
int leftOverSize = 0; // number of leftover characters

/* struct defines the element of a list from the hash table
 * it holds its value (the name) and pointers to both 
 * the element after and before it. */
struct element
{
    char *value;
    struct element *next, *before;
};

/* struct defines the list from one slot of the hash table
 * it hold its own address so it can be freed when done
 * it holds pointers to the current element and the beginning of the list */
struct list
{
    struct list *address;
    struct element *current, *begin;
};

/* struct defining the hash table as an array of list elements */
struct table
{
    struct list *array;
};

struct table *myTable; // pointer to the hash table which we will use

/* Function creates a new list by dyamically allocating memory
 * @return pointer to the created list */
struct list *makeList(){

    struct list *newList;
    struct element *begin;

    // dynamically allocate memory for both the list and its beginning element
    newList = (struct list*)malloc(sizeof(struct list));
    begin = (struct element*)malloc(sizeof(struct element));
    
    if(newList == NULL || begin == NULL){    
        printf("Allocation of memory for the list failed\n");
        return NULL;
    }
    
    // link the only element so far to both NULL pointers before and after it
    begin->next = NULL;
    begin->before = NULL;
    begin->value = NULL; // default value
    newList->begin = begin;
    
    // keep track of the allocated address so it can be freed when done
    newList->address = newList;
    
    return newList;
}

/* Function creates the table by dynamically allocating memory for it.
 * The table can use p slots max. The array is also initialized here. */
void createTable(){

    myTable = (struct table*)malloc(sizeof(struct table));
    myTable->array = (struct list*)malloc(p * sizeof(struct list));
    
    if(myTable == NULL){
        printf("Memory allocation for the table failed\n");
    }
}

/* Function initializes the table by creating a list for
 * each index of the array. */
void initializeTable(){

    for(int index = 0; index < p; index++){
        (*myTable).array[index] = *makeList();
    }
}

/* Function returns the hash value of a character array.
 * Using polinomyal hashing function.
 * p - prime number
 * @param name[] - array for which the hash value is generated;
 * @return value - integer representing the hash value for the table. */
int hash(char name[]){

    long index = 0, power = 1, value = 0;
    
    while(name[index] != '\0'){
        value += name[index] * power % p;
        power *= x;
        index++;
    }
    
    value%=p;
    return value;
}

/* Funtion compares two characters arrays.
 * @param name1, name2 - char arrays to be compared
 * @return true if name1 = name2
 *         false otherwise*/
bool isEqual(char name1[], char name2[]){

    int aux = 0;
    
    // iterate through each letter and compare
    while(*(name1 + aux) != '\0'){
        if(*(name1 + aux) != *(name2 + aux)) return false;
        if(*(name2 + aux) == '\0') return false;
        aux++;
    }
    
    // check if name2 = name1 or name2 = name1 + some other characters
    if(*(name2 + aux) != '\0') return false;
    return true;
}

/* Function checks if a name is in the table or not.
   @return true if the name exists
           false otherwise */
bool search(char name[]){

    int index = hash(name);
    struct list target = (*myTable).array[index];
    target.current = target.begin -> next;

    // look for the name by comparing the values of the elements in the list
    while(target.current != NULL){
    
        if(isEqual(target.current->value, name)){
            return true;
        }
    
        target.current = target.current->next;
    }

    return false;
}

/* Function adds a new name to the hash table. */
void add(char name[]){

    // check that the name is not a duplicate
    if(search(name)){
        printf("The name already exists.\n");
        return;
    
    }
    int index = hash(name);
    struct list target = (*myTable).array[index];
    
    // allocate memory for a new element and for the value of that element
    struct element *newName = (struct element*)malloc(sizeof(struct element));
    newName->value = (char*)malloc(sizeof(char));
    
    if(newName == NULL || newName->value == NULL){
        printf("Memory allocation for a new element failed\n");
        return;
    }
    
    // navigate until the last element and add the new one
    target.current = target.begin;
    
    while(target.current -> next != NULL){
        target.current = target.current->next;
    }
    
    // assign characters to the value of the element
    int aux = 0;
    
    while(*(name + aux) != '\0'){
        *(newName->value + aux) = *(name + aux);
        aux++;
    }
    
    // update the links between the elements
    *(newName->value + aux) = '\0';
    target.current->next = newName;
    newName->before = target.current;
    newName->next = NULL;
}

/* Function removes an element from the table. */
void myRemove(char name[]){

    // check that the name exists
    if(!search(name)){
        printf("The name could not be found.\n");
        return;
    }
    
    int index = hash(name);
    struct list target = (*myTable).array[index];
    
    // navigate to find the element
    target.current = target.begin -> next;
    
    while(target.current != NULL){
        
        /* if found update the links between the
         * elements and free the allocated memory */
        if(isEqual(target.current->value, name)){
            
            target.current->before->next = target.current->next;
            
            // check if the found element is the last one in the list or not
            if(target.current->next != NULL){
                target.current->next->before = target.current->before;
            }
            
            struct element *tobeRemoved = target.current;
            target.current = target.current->before;
            free(tobeRemoved);
            break;
        }

        target.current = target.current->next;
    }
}

/* Function frees all alocated memory, in order:
 * elements from lists, lists, the array and the table */
void freeAll(){

    for(int index = 0; index < p; index++){

        struct list tobeFreed = myTable->array[index];
        tobeFreed.current = tobeFreed.begin -> next;
        
        while(tobeFreed.current != NULL){

            struct element *toFree = tobeFreed.current;
            tobeFreed.current = tobeFreed.current->next;
            
            //free(toFree->value); // free the value
            free(toFree); // free each element
        }

        free(tobeFreed.begin); // free the first element
        
        // find the address of the list
        struct list *pList = (*myTable).array[index].address;
        free(pList);
    }
    
    // free the array and the table
    free(myTable->array);
    free(myTable);
}

/* Function gets passed a char array (read characters from the buffer)
 * and creates the names and adds them to the table. */
void addNames(char buffer[]){

    char *name; // holds the characters from one name at a time
    int nameLength = 0; // length of name
    int beginningIndex; // index of first letter from name in buffer
    int index = 0;
    bool nameStarted = false; // keeps track if we are creating a name or not
    
    while(buffer[index] != '\0'){
        
        int asciiValue = buffer[index];
        
        // if character is letter
        if((asciiValue <= 90 && asciiValue >= 65) ||
         (asciiValue >= 97 && asciiValue <= 122)){
             
             nameLength++;
             
             if(!nameStarted){
                 nameStarted = true;
                 beginningIndex = index;
             }
        }

        else if (nameStarted){

            // we have reached the end of a name and we are ready to add it
            name = (char*)malloc(nameLength * sizeof(char));
            
            if(name == NULL){
                printf("Name memory allocation failed\n");
                return;
            }
            
            for(int aux=0; aux<nameLength; aux++){
                *(name + aux) = buffer[beginningIndex + aux];
            }
            
            // check to see if there are leftOver characters from the previous buffer
            if(leftOverSize != 0){
                
                char *newName = (char*)malloc((leftOverSize+nameLength) * sizeof(char));
                
                if(newName == NULL){
                    printf("Name memory allocation failed\n");
                    return;
                }
                
                for(int aux=0; aux<leftOverSize;aux++){
                    *(newName+aux) = *(leftOver + aux);
                }
                
                for(int aux = 0; aux < nameLength; aux++){
                    *(newName+leftOverSize+aux) = *(name + aux);
                }
                
                add(newName);
                
                // free all allocated memory here
                newName = NULL;
                name = NULL;
                leftOver = NULL;
                
                /* can free both name and newName since
                 * add function allocates memory for element->value */
                free(newName);
                free(name);
                free(leftOver);
                nameLength = 0;
                nameStarted = false;
                leftOverSize = 0;
            }
            else{
                // there are no leftover characters
                add(name);
                name = NULL;
                free(name);
                nameLength = 0;
                nameStarted = false;
            }
        }    
        index++;
    }
    
    /* we reached the end of the buffered but we are in the process of
     * creating a new name. */
    if(nameStarted){
        
        // keep the left over characters in memory until the next buffer
        leftOver = (char*)malloc(nameLength * sizeof(char));
        
        for(int aux=0; aux<nameLength; aux++){
            *(leftOver + aux) = buffer[beginningIndex + aux];
        }
        
        leftOverSize = nameLength;
    }
}

/* Function opens the names.txt file and reads the names byte by byte.*/
void readFile(){

    FILE *filein = fopen("names.txt", "r");
    
    if(filein == NULL){
        printf("Failed to open the file.\n");
        return;
    }
    
    char *buf;
    int nread = 1;
    
    do{
        if(nread == 0){
            break;
        }
        
        buf = (char *)malloc(1024);
        nread = fread(buf, sizeof(char), 1024, filein);
        
        addNames(buf); 
        buf = NULL;
        free(buf);
    
    }while(nread > 0);
    
    fclose(filein);
}

/* Function provides options for the user to add, search and remove
 * names from the table. */
void menu(){

    int option;
    void (*fun_arr[])(char*) = {add, myRemove};
    
    do{
        printf("Input 1 to add a name, 2 to remove a name, 3 to search a");
        printf(" name and 0 to exit: ");
        
        scanf("%d", &option);
        if(option == 0) break;
        
        printf("Input the name: ");
        char *name = (char*)malloc(sizeof(char));
        scanf("%s", name);
        
        if(option == 3){
            if(search(name)) printf("%s is in the table\n", name);
            else printf("%s is not in the table\n", name);
        }
        
        else{
            fun_arr[option-1](name);
        }
        
        free(name);

    } while(option != 0);

    freeAll();

}

/* Main function */
int main(void){
    createTable();
    initializeTable();
    readFile();
    menu();
    return 0;
}