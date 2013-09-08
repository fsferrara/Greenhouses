/*	Colucci Carmine Antonio - 961/87
	D'Apolito Filomeno - 961/81
	Ferrara Francesco Saverio - 961/79  */

//librerie usate nella routine
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "utility_post.h"
#include "utility_mysql.h"
#include "utility.h"



int main(int argc, char *argv[]) {

    //dichiarazione variabili
	int i;
	char *param;
	//char valore_out[30];
	char query[MAX_LENGTH];
	riga_mysql row;									//rappresenta una riga del risultato di una query
	
	char id_p[12];                                  //ID della serra di partenza
	char id_a[12];                                  //ID della serra di arrivo
	char km[8];                                     //km tra le due serre
	
	
	
	printf("Content-type:text/html\n\n\n");         //definizione del content-type
	//apertura tag HTML
    printf("<html>\n");
	printf("<body>\n");
	printf("<h1>Inserimento delle distanze tra Serra</h1>");
	
	param = init_param();                           //inizializzazione  parametri POST

	//Connessione al database
	if (init_mysql() != 0) {                        //controllo sulla corretta conessione al database
		param = del_param(param);
		fatal_error("Impossibile connettersi al database!");
	}
	
	if (param == NULL) {                            //nessun parametro - visualizza il form per la cancellazione

		//esecuzione select
		query[0] = '\0';                            //costruzione della variabile "query" che verrà usata per la select SQL
		strcat(query, "SELECT id, nome, citta FROM serra;"); //concatenazione stringa
 
		//esecuzione query SQL
		if (select_start_mysql(query) != 0) {       //controllo sull'esito dell'esecuzione della query SQL
			close_mysql();
			fatal_error("Impossibile comunicare con il database\n");
		}
		
		//HTML
		printf("<form name=\"distanza\" action=\"distanza\" method=\"post\">");
		printf("Serra di partenza<br>&nbsp;<select name=\"id_p\">");
		
		
		while ((row=select_getrow_mysql())!=NULL){  //ciclo per controllare le distanze  
			//row[0]=id, row[1]=nome, row[2]=citta
			printf("<option value=\"%s\">%s (di %s)</option>", row[0], row[1], row[2]);
		}
		
		printf("</select>");
		select_stop_mysql();                        //Rilascio  memoria occupata per la select

		//esecuzione select
		query[0] = '\0';                            //costruzione della variabile "query" che verrà usata per la select SQL
		strcat(query, "SELECT id, nome, citta FROM serra;"); //concatenazione stringa
 
		//esecuzione query SQL
		if (select_start_mysql(query) != 0) {        //controllo sull'esito dell'esecuzione della select SQL
			close_mysql();
			fatal_error("Impossibile comunicare con il database\n");
		}
		
		//HTML
        printf("<br>Serra di arrivo<br>&nbsp;<select name=\"id_a\">");
		
		while ((row = select_getrow_mysql()) != NULL) {  //ciclo sulle righe del risultato della query
			//row[0]=id, row[1]=nome, row[2]=citta
			printf("<option value=\"%s\">%s (di %s)</option>", row[0], row[1], row[2]);
		}
		
		printf("</select>");

		select_stop_mysql();                        //rilascio memoria occupata per la select

        //HTML
		printf("<br>Distanza:<br><input type=\"text\" name=\"km\" size=\"6\" maxlength=\"6\">Inserire il punto come separatore dei decimali!<br><br>");
		printf("&nbsp;<input type=\"submit\" name=\"conferma\" value=\"Inserisci\">");
		printf("</form>");
	}
	else {                                          //passaggio parametri non vuoto
		if (find_n_param(param, "id_p", id_p, 12) == 1) { //controllo sull' estrazione del parametro "id_p"
			param = del_param(param);
			print_menu();
			fatal_error("Impossibile elaborare i parametri<br>");
		}
		
		if (find_n_param(param, "id_a", id_a, 12) == 1) { //controllo sull' estrazione del parametro "id_a"
			param = del_param(param);
			print_menu();
			fatal_error("Impossibile elaborare i parametri<br>");
		}
		
		if (find_n_param(param, "km", km, 8) == 1) {      //controllo sull' estrazione del parametro "km"
			param = del_param(param);
			print_menu();
			fatal_error("Impossibile elaborare i parametri<br>");
		}
		
		if ((strlen(id_p) == 0) || (strlen(id_a) == 0) || (strlen(km) == 0)) {  //controllo sull' inserimento di tutti i parametri
			fatal_error("Tutti i campi sono obbligatori!<br><a href=\"distanza\">Torna Indietro</a><br>");
		}
		
		if ( strcmp(id_p, id_a)  == 0 )
			strcpy(km, "0");
	
		
		//calcolo distanza tra serre
		query[0] = '\0';                                    //costruzione della variabile "query" che verrà usata per la select SQL
		strcat(query, "SELECT id_p, id_a FROM distanza WHERE id_p='");
		strcat(query, id_p);
		strcat(query, "' AND id_a='");
		strcat(query, id_a);
		strcat(query, "';");
		
		if (select_start_mysql(query) != 0) {               //controllo sull'esito dell'esecuzione della select SQL
			close_mysql();
			param = del_param(param);
			fatal_error("Impossibile comunicare con il database\n");
		}
		
		row = select_getrow_mysql();
		if (row != NULL) {
			query[0] = '\0';                                //costruzione della variabile "query" che verrà usata per la update SQL
			strcat(query, "UPDATE distanza SET km='");      //la distanza tra le due serre gia' esiste, allora aggiorno
			strcat(query, km);
			strcat(query, "' WHERE id_p='");
			strcat(query, id_p);
			strcat(query, "' AND id_a='");
			strcat(query, id_a);
			strcat(query, "';");
		}
		else {
			query[0] = '\0';                                //costruzione della variabile "query" che verrà usata per la insert SQL
			strcat(query, "INSERT INTO distanza(km, id_p, id_a) VALUES ('"); //primo inserimento delle distanze
			strcat(query, km);
			strcat(query, "', '");
			strcat(query, id_p);
			strcat(query, "', '");
			strcat(query, id_a);
			strcat(query, "');");
		}

		
		select_stop_mysql();                                //rilascio memoria occupata per la select
		
		//esecuzione query SQL
		if (insert_mysql(query) != 0) {                     //controllo sull'esito dell'esecuzione della insert SQL
			close_mysql();
			param = del_param(param);
			fatal_error("Impossibile inserire nel database<br><a href=\"distanza\">Torna indietro</a><br>");
		}
		
		printf("Distanza inserita correttamente!<br>");
		print_menu();
		
	}
	
	param = del_param(param);                                //eliminazione parametri

	close_mysql();                                           //chiusura connessione

    //chiusura tag HTML
	printf("</body>\n");	
	printf("</html>\n\n");

}


