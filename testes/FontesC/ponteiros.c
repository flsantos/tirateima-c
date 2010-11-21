#include<stdio.h>

int main(int argc, char *argv[]){
    int numero;
    int *ponteiro;
    ponteiro = NULL;
    ponteiro = &numero;
    numero = 10;
    printf("%d",*ponteiro);
    return 0;
}
