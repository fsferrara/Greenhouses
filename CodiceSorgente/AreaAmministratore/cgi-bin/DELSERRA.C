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
	riga_mysql row;    //riga risultante dalla select
	
	char id[12];                                                     //ID della serra da cancellare
	
	
	printf("Content-type:text/html\n\n\n");                          //definizione del content-type
	//tag HTML
    printf("<html>\n");
	printf("<body>\n");
	printf("<h1>Eliminazione di una Serra</h1>");
	
	param = init_param();                                            //inizializzazione dei parametri POST

	//connessione al database
	if (init_mysql() != 0) {                                         //controllo sulla corretta conessione al database
		param = del_param(param);
		fatal_error("Impossibile connettersi al database!");
	}
	
	if (param == NULL) {                                             //nessun parametro - visualizza il form per la cancellazione

		//esecuzione select
		query[0] = '\0';                                             //costruzione della variabile "query" che verrà usata per la select SQL 
		strcat(query, "SELECT id, nome, citta FROM serra;");         //concatenazione  stringa
 
		//esecuzione query SQL
		if (select_start_mysql(query) != 0) {
			close_mysql();
			fatal_error("Impossibile comunicare con il database\n");
		}
		
		//form HTML
		printf("<form name=\"del_serra\" action=\"delserra\" method=\"post\">");
		printf("<select name=\"idserra\">");  //menu a tendina
		
		
        while ((row = select_getrow_mysql()) != NULL) {               //ciclo per controllare la serra
			//row[0]=id, row[1]=nome, row[2]=citta'
			printf("<option value=\"%s\">%s (di %s)</option>", row[0], row[1], row[2]);
		}
		
		//fine select HTML
        printf("</select>");
		printf("&nbsp;<input type=\"submit\" name=\"conferma\" value=\"Elimina\">");
		printf("</form>");
		
		select_stop_mysql();                                          //rilascio la memoria occupata per la select
	}
	else {                                                            //passato come parametro l'id della serra da cancellare
		if (find_n_param(param, "idserra", id, 12) == 1) {            //controllo sull' estrazione del parametro "idserra"
			param = del_param(param);
			print_menu();
			fatal_error("Non ci sono serre da cancellare dal database<br>");
		}
		
		
		//verifica presenza o meno di ordini su questa serra
		query[0] = '\0';                                               //costruzione della variabile "query" che verrà usata per la select SQL                 
		strcat(query, "SELECT id_serra FROM composizione_ordine WHERE id_serra='");
		strcat(query, id);
		strcat(query, "';");
		
		if (select_start_mysql(query) != 0) {                          //controllo sull'esito dell'esecuzione della select SQL
			close_mysql();
			param = del_param(param);
			fatal_error("Impossibile comunicare con il database\n");
		}
		
		row = select_getrow_mysql();                                   //controllo sugli ordini associati alla serra
		if (row != NULL) {
				print_menu();
				printf("<p>Per questa serra ci sono ordini non evasi!<br>");
				fatal_error("Bisogna evadere prima gli ordini<br>");
		}

		select_stop_mysql();                                           //rilascio memoria occupata per la select
		
		//preparazione della query SQL
		query[0] = '\0';                                               //costruzione della variabile "query" che verrà usata per la delete SQL
		strcat(query, "DELETE FROM serra WHERE id='");
		strcat(query, id);
		strcat(query, "';");
		
		//esecuzione query SQL
		if (insert_mysql(query) != 0) {                                //controllo sull'esito dell'esecuzione della insert SQL
			close_mysql();
			param = del_param(param);
			fatal_error("Impossibile cancellare dal database<br><a href=\"delserra\">Torna indietro</a><br>");
		}
		
		printf("Serra cancellata correttamente!<br>");
		print_menu();
		
	}
	
	param = del_param(param);                                          //eliminazione  parametri

	close_mysql();                                                     //chiusura connessione

    //tag HTML
	printf("</body>\n");
	printf("</html>\n\n");

}


