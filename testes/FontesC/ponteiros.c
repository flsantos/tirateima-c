#include<stdio.h>

int main(int argc, char *argv[]){
    int numero;
    int *p;
    p = NULL;
    p = &numero;
    numero = 10;
    printf("%d",*p);
    p = NULL;
    return 0;
}
