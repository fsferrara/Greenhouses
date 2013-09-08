/*	Colucci Carmine Antonio - 961/87
	D'Apolito Filomeno - 961/81
	Ferrara Francesco Saverio - 961/79  */

//elenco librerie usate nella routine
#include <stdio.h>
#include <stdlib.h>
#include <string.h>


//in caso di situazioni anomale c'è l'uscita dalla routine
void fatal_error(char *string) {
	printf("<p align=center>ERRORE nel caricameto della pagina: %s<br></p>\n", string);
	printf("</body>\n");
	printf("</html>\n");
	exit(EXIT_FAILURE);
}

int isanumber(char *number) {
	
	int i;
	
	for (i=0 ; i < strlen(number) ; i++ ) {
		if ( !((number[i] >= '0') && (number[i] <= '9')) )               //se non è un carattere-numero
			return 0;                                                    //non è un numero
	}
	
	return 1;                                                            //è un numero
}

//stampa il menù amministratore
void print_menu() {
	//HTML
    printf("<h2 align=center>Menu di gestione</h2>");
	printf("<p align=center>");
	printf("<a href=\"../AreaAmministratore/addproprietario.htm\">Aggiungi un proprietario della serra</a><br>");
	printf("<a href=\"addserra\">Aggiungi una serra</a><br>");
	printf("<a href=\"distanza\">Inserisci le distanze tra le serre</a><br>");
	printf("<a href=\"delserra\">Elimina una serra</a><br>");
	printf("<a href=\"delproprietario\">Elimina un proprietario</a><br>");
	printf("</p>");
	printf("<br>");
}
