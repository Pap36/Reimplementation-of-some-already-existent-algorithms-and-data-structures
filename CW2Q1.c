#include <stdio.h>

int array[100], length = 100;
/* parameters to use when generating pseudo-random numbers */
int base = 343, mod = 271;

/* Function generates 100 (length) "random"
 * numbers and initializes the array. */
void generateArray(){
    for(int i = 0; i < length; i++){
        array[i] = (length - i) * base % mod;
    }
}

/* Function sorts the array using the improved cocktail sort algorithm.
 * It keeps track of the indexes for the last swaps
 * in both direction to improve efficiency. */
void cocktailSort(int array[]){
    /* Variables represent left and right
     * indexes for which the numbers in between aren't sorted. */
    int left=0, right = length - 1; 

    while(left <= right){
        /* Variables to keep track of updated values for the indexes.
         * Assume everything is in order before sorting (indexes inverted) */
        int start = right;
        int end = left;

        for(int i = left; i < right; i++){
            if(array[i+1] < array[i]){
                int aux = array[i];
                array[i] = array[i+1];
                array[i+1] = aux;
                end = i; // keep track of last swap index
            }
        }

        right = end; // update value, everything after this point is ordered

        for(int i = right; i > left; i--){
            if(array[i] < array[i-1]){
                int aux = array[i];
                array[i] = array[i-1];
                array[i-1] = aux;
                start = i; // keep track of last swap index
            }
        }

        left = start; // update value, everything before this point is ordered
    }
} 

/* Main function generates and sorts the array. */
int main(void){
    generateArray();
    cocktailSort(array);
    /* Print the sorted array. */
    for(int i = 0; i < length; i++){
        printf("%d ", array[i]);
    }
    printf("\n");
    return 0;
}