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
	char *param;								 //parametri passati dal post (nome=valore)
	//char valore_out[30];
	char query[MAX_LENGTH];                      //contiene la query da eseguire sul database
	riga_mysql row;								 //rappresenta una riga del risultato di una query

    char nome[256];                              //parametri relativi alla serra da aggiungere
	char indirizzo[256];                         //tali parametri relativi alla serra  
	char citta[256];                             //saranno memorizzati in un record della tabella
	char provincia[256];                         //serra del database
	char cap[6];
	char cf_proprietario[17];
	
	
	printf("Content-type:text/html\n\n\n");      //definizione del content-type
	//apertura tag HTML
    printf("<html>\n");
	printf("<body>\n");
	printf("<h1>Inserimento di una serra</h1>");
	
	param = init_param();                        //inizializzazione dei parametri POST

	//connessione al database
	if (init_mysql() != 0) {                     //connessione al database impossibile
		param = del_param(param);
		fatal_error("Impossibile connettersi al database!");
	}
	
	if (param == NULL) {                         //nessun parametro - visualizzare il form per l'inserimento

		query[0] = '\0';                         //inizializzazione della variabile 'query' con il carattere terminatore
		strcat(query, "SELECT cognome, nome, cf FROM proprietario;"); //concatenazione della stringa
 
		if (select_start_mysql(query)!=0){       //controllo sull'esito dell'esecuzione della query SQL
			close_mysql();
			param = del_param(param);            //se errore, rilascia la memoria
			fatal_error("Impossibile comunicare con il database\n");
		}
		
		//FORM HTML
		printf("<form name=\"add_serra\" action=\"addserra\" method=\"post\">");
		printf("Proprietario*:<br><select name=\"cf_proprietario\">");
		
		while ((row = select_getrow_mysql()) != NULL) {      //acquisizione degli elementi per la stampa
			//row[0]=cognome, row[1]=nome, row[2]=cf
			printf("<option value=\"%s\">%s %s - %s</option>", row[2], row[0], row[1], row[2]);    //item del tag select
		}

        //fine select html
		printf("</select><br><br>");                         
		//visualizzazione campi del form
		printf("Nome*:<br><input type=\"text\" name=\"nome\" size=\"15\" maxlength=\"255\"><br><br>");
		printf("Indirizzo*:<br><input type=\"text\" name=\"indirizzo\" size=\"15\" maxlength=\"255\"><br><br>");
		printf("Citta'*:<br><input type=\"text\" name=\"citta\" size=\"15\" maxlength=\"255\"><br><br>");
		printf("Provincia*:<br><input type=\"text\" name=\"provincia\" size=\"15\" maxlength=\"255\"><br><br>");
		printf("CAP*:<br><input type=\"text\" name=\"cap\" size=\"5\" maxlength=\"5\"><br><br>");
		printf("<br><input type=\"submit\" name=\"conferma\" value=\"Inserisci\">");
		printf("</form>");
		
		select_stop_mysql();                                           //rilascio  memoria occupata per la select
	}
	else {    //passaggio parametri da inserire (il post è pieno)
	
		if (find_n_param(param, "nome", nome, 256) == 1) {             //controllo sull' estrazione del parametro "nome"
			param = del_param(param);
			close_mysql();
			fatal_error("(nome) Tutti i parametri sono obbligatori!<br><a href=\"addserra\">Torna indetro</a><br>");
		}
		
		if (find_n_param(param, "indirizzo", indirizzo, 256) == 1) {  //controllo sull' estrazione del parametro "indirizzo"
			param = del_param(param);
			close_mysql();
			fatal_error("(indirizzo) Tutti i parametri sono obbligatori!<br><a href=\"addserra\">Torna indetro</a><br>");
		}
		
		if (find_n_param(param, "citta", citta, 256) == 1) {          //controllo sull' estrazione del parametro "citt‡"
			param = del_param(param);
			close_mysql();
			fatal_error("(citta') Tutti i parametri sono obbligatori!<br><a href=\"addserra\">Torna indetro</a><br>");
		}
		
		if (find_n_param(param, "provincia", provincia, 256) == 1) { //controllo sull' estrazione del parametro "provincia"
			param = del_param(param);
			close_mysql();
			fatal_error("(provincia) Tutti i parametri sono obbligatori!<br><a href=\"addserra\">Torna indetro</a><br>");
		}
		
		if (find_n_param(param, "cap", cap, 6) == 1) {               //controllo sull' estrazione del parametro "cap"
			param = del_param(param);
			close_mysql();
			fatal_error("(cap) Tutti i parametri sono obbligatori!<br><a href=\"addserra\">Torna indetro</a><br>");
		}
		
		if (find_n_param(param, "cf_proprietario", cf_proprietario, 17) == 1) { //controllo sull' estrazione del parametro "cf_proprietario"
			param = del_param(param);
			close_mysql();
			fatal_error("Devi prima inserire il proprietario della serra!<br><a href=\"../AreaAmministratore/addproprietario.htm\">inserisci il proprietario</a><br>");
		}
		
		if ((strlen(nome) == 0) || (strlen(indirizzo) == 0) ||        //controllo sull' inserimento di tutti i parametri
            (strlen(citta) == 0) || (strlen(provincia) == 0) ||
			(strlen(cap) == 0) || (strlen(cf_proprietario) == 0)) {          
			param = del_param(param);
			close_mysql();
			fatal_error("Tutti i campi sono obbligatori!<br><a href=\"addserra\">Torna indietro</a><br>");
		}
		
		if (!isanumber(cap)) {                                        //controllo sulla validit‡ del parametro "cap"
			param = del_param(param);
			close_mysql();
			fatal_error("Il codice CAP non e' valido!<br><a href=\"addserra\">Torna indietro</a><br>");
		}
		
		//preparazione della query SQL
		query[0] = '\0';                                              //costruzione della variabile "query" che verr‡ usata per la insert SQL
		strcat(query, "INSERT INTO `serra` ( `nome` , `indirizzo` , `citta` , `provincia` , `cap` , `cf_proprietario` ) VALUES ('");
		strcat(query, nome);
		strcat(query, "', '");
		strcat(query, indirizzo);
		strcat(query, "', '");
		strcat(query, citta);
		strcat(query, "', '");
		strcat(query, provincia);
		strcat(query, "', '");
		strcat(query, cap);
		strcat(query, "', '");
		strcat(query, cf_proprietario);
		strcat(query, "');");
		
		//esecuzione query SQL
		if (insert_mysql(query) != 0) {              //controllo sull'esito dell'esecuzione della insert SQL
			close_mysql();
			param = del_param(param);
			fatal_error("Impossibile inserire nel database<br><a href=\"addserra\">Torna indietro</a><br>");
		}
		
		printf("Serra inserita correttamente!<br>");
		print_menu();   //stampa menu in basso
		
	}
	
	param = del_param(param);                       //eliminazione  parametri

	close_mysql();                                  //chiusura connessione col database

	//tag HTML
    printf("</body>\n");
	printf("</html>\n\n");

}


