/*	Colucci Carmine Antonio - 961/87
	D'Apolito Filomeno - 961/81
	Ferrara Francesco Saverio - 961/79  */

//elenco librerie usate nella routine
#include <stdio.h>
#include <stdlib.h>


/*
ESEMPIO D'USO

.
.
.
	param = init_param();

	if (find_param(param, "password", valore_out) != 1)
		printf("Trovato = %s<br>", valore_out);
	else
		printf("Non trovato<br>");

	param = del_param(param);
.
.
.

*/


char *init_param() {                                 //permette l' inizializzazione dei parametri
                                                     //estraendoli dal contenuto del POST                   
	//dichiarazioni variabili usate
    char *buffer = NULL;                           
	int i = 0;
	int param_length = 0;
	
	if (getenv("CONTENT_LENGTH") == NULL)            //controllo sul contenuto della variabile d'ambiente "CONTENT_LENGTH"
		return NULL;
	else
		param_length = atoi(getenv("CONTENT_LENGTH"));
	
	buffer = (char *) malloc(param_length+1);        //allocazione memoria per la variabile "buffer" in base alla lunghezza di "param_length" 

	if (buffer != NULL) {                            //controllo sulla corretta allocazione della stringa "buffer"
		while (i < param_length) {                   //ciclo sui caratteri
			buffer[i] = getchar();                   //memorizzazione carattere

			if (buffer[i] == '+')                    //controllo ui caratteri; trovato il carattere "+" si trasforma in ' '
				buffer[i] = ' ';

			i++;
		}
		buffer[i]='\0';                              //inserimento carattere terminatore nella stringa
	}
	
	return buffer;                                   //valore di ritorno
}



int find_param(char *buffer, char *nomeobj,          //estrazione dei parametri dalla stringa "buffer"
                         char *valore_out) {    

    //dichiarazioni variabili usate
	int param_length=atoi(getenv("CONTENT_LENGTH")); //estrazione e conversione del contenuto della variabile d'ambiente "CONTENT_LENGTH"
	int i=0, j=0;
	char nomeparam[255];
	
	if (buffer ==  NULL)                             //controllo sul contenuto della variabile "buffer" 
		return 1;
	
	while(i < param_length) {                        //permette l'estrazione dei parametri; si slatano alcuni caratteri
		j=0;
		while (buffer[i] != '=') {                   //ciclo sulla stringa 
			nomeparam[j] = buffer[i];
			i++;
			j++;
		}
		i++;                                         //permette di saltare il carattere '='
		
		nomeparam[j]='\0';                           //carattere terminatore di stringa
		if (strncmp(nomeparam, nomeobj, 255) == 0) { //controllo sull' eguaglianza delle stringhe
			                                         //estrazione del valore
			j=0;
			while((buffer[i] != '&') && (i<param_length)) { //ciclo per l'estrazione del parametro
				valore_out[j] = buffer[i];
				i++;
				j++;
			}
			valore_out[j] = '\0';                    //carattere terminatore di stringa
			return 0;                                //parametro estratto con successo
		}
		else {
			while((buffer[i] != '&') && (i<param_length))
				i++;
			i++;
		}
	}
	
	return 1;                                        //estrazione parametro non riuscita
}



int find_n_param(char *buffer, char *nomeobj, char *valore_out, int n_max) {

	int param_length = atoi(getenv("CONTENT_LENGTH"));
	int i=0, j=0;
	char nomeparam[255];
	int n_nomeobj = strlen(nomeobj);

	if (buffer ==  NULL)                               //controllo sul contenuto della variabile "buffer"
		return 1;

	while(i < param_length) {                          //permette l'estrazione dei parametri
		j=0;
		while ((buffer[i] != '=') && (j <= n_nomeobj)) {  //ciclo per l'estrazione del parametro; si saltano alcuni caratteri 
			nomeparam[j] = buffer[i];
			i++;
			j++;
		}
		i++;                                           //per saltare il carattere '='
		
		nomeparam[j]='\0';
		if (strncmp(nomeparam, nomeobj, n_max) == 0) { //controllo sull' eguaglianza dele stringhe 
			//estrazione valore
			j=0;
			while((buffer[i] != '&') && (i<param_length)) {  //estrazione parametro
				valore_out[j] = buffer[i];
				i++;
				j++;
			}
			valore_out[j] = '\0';
			return 0;                                  //valore trovato
		}
		else {
			while((buffer[i] != '&') && (i<param_length))
				i++;
			i++;
		}
	}
	
	return 1;                                          //valore non trovato
}


//cancellazione stringa
char *del_param(char *buffer) {
	if (buffer != NULL)
		free(buffer);                                  //rilascio memoria
		
	return NULL;
}
