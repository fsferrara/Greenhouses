/*	Colucci Carmine Antonio - 961/87
	D'Apolito Filomeno - 961/81
	Ferrara Francesco Saverio - 961/79  */

//elenco librerie usate nella routine
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "utility_post.h"
#include "utility_mysql.h"
#include "utility.h"



int main(int argc, char *argv[]) {

    //dichiarazioni variabili usate
	int i;
	char *param;
	//char valore_out[30];
	char query[MAX_LENGTH];
	riga_mysql row;						//rappresenta una riga del risultato di una query
	int serre_allocate=0;				//flag, indica se il proprietario ha delle serre ad esso associato.
										//In caso contrario è possibile rimuovere il proprietario
	char cf[17];		//codice fiscale del proprietario da cancellare
	
	
	printf("Content-type:text/html\n\n\n");                   //definizione del content-type
	//tag HTML
    printf("<html>\n");
	printf("<body>\n");
	printf("<h1>Eliminazione Proprietario di una serra</h1>");
	
	param = init_param();                                     //inizializzo parametri POST

	//connessione al database
	if (init_mysql() != 0) {                                  //controllo sulla corretta conessione al database
		param = del_param(param);
		fatal_error("Impossibile connettersi al database!");
	}
	
	if (param == NULL) {                                      //nessun parametro - visualizza il form per la cancellazione

		//esecuzione  select
		query[0] = '\0';                                      //inizializzazoine  stringa per la query
		strcat(query, "SELECT cognome, nome, cf FROM proprietario;");    //concatenazione  stringa
 
		//esecuzione query SQL
		if (select_start_mysql(query) != 0) {                 //controllo sull'esito dell'esecuzione della query SQL
			close_mysql();
			fatal_error("Impossibile comunicare con il database\n");
		}
		
		//FORM HTML
        printf("<form name=\"del_proprietario\" action=\"delproprietario\" method=\"post\">");
		printf("<select name=\"cfproprietario\">");
		
		while ((row = select_getrow_mysql()) != NULL) {       //acquisizione degli elementi per la stampa
			//row[0]=cognome, row[1]=nome, row[2]=cf
			printf("<option value=\"%s\">%s %s - %s</option>", row[2], row[0], row[1], row[2]);
		}
		
		//fine select HTML
        printf("</select>");
		printf("&nbsp;<input type=\"submit\" name=\"conferma\" value=\"Elimina\">");
		printf("</form>");
		
		select_stop_mysql();                                 //rilascio  memoria occupata per la select
	}
	else {                                                   //passato come parametro il codice fiscale del proprietario da cancellare
		if (find_n_param(param, "cfproprietario", cf, 17) == 1) { //controllo sull' estrazione del parametro "cfproprietario"
			param = del_param(param);
			print_menu();
			fatal_error("Non ci sono proprietari da cancellare dal database<br>");
		}
		
		
		//cancellazione
		query[0] = '\0';                                      //costruzione della variabile "query" che verr‡ usata per la select SQL
		strcat(query, "SELECT cf_proprietario, nome FROM serra WHERE cf_proprietario='");
		strcat(query, cf);
		strcat(query, "';");
		
		if (select_start_mysql(query) != 0) {                 //controllo sull'esito dell'esecuzione della select SQL
			close_mysql();
			param = del_param(param);
			fatal_error("Impossibile comunicare con il database\n");
		}
		
		while ((row = select_getrow_mysql()) != NULL) {        //ciclo per controllare se ad un proprietario è associata almeno una serra
			if (serre_allocate == 0) { //Esegue questo codice solo alla prima serra trovata nel database
				print_menu();
				printf("<p>Per questo proprietario sono allocate le seguenti serre:<br>");
				serre_allocate = 1;
			}
			printf("-> %s<br>", row[1]); //stampa il nome delle serre associate al proprietario
		}
		
		if (serre_allocate == 1) {
			fatal_error("Bisogna cancellare prima le serre elencate!<br>");
		}

		
		select_stop_mysql();                                    //rilascio memoria occupata per la select
		
		
		//preparazione  query SQL
		query[0] = '\0';                                        //costruzione della variabile "query" che verr‡ usata per la delete SQL
		strcat(query, "DELETE FROM proprietario WHERE cf='");
		strcat(query, cf);
		strcat(query, "';");
		
		//esecuzione query SQL
		if (insert_mysql(query) != 0) {                         //controllo sull'esito dell'esecuzione della insert SQL
			close_mysql();
			param = del_param(param);
			fatal_error("Impossibile cancellare dal database<br><a href=\"delproprietario\">Torna indietro</a><br>");
		}
		
		printf("Proprietario cancellato correttamente!<br>");
		print_menu();
		
	}
		
	param = del_param(param);                                   //eliminazione  parametri

	close_mysql();                                              //chiusura connessione

    //tag HTML
	printf("</body>\n");
	printf("</html>\n\n");

}


