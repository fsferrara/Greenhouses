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
	char *param;								   //contiene la stringa passata dal post (nome=valore)
	//char valore_out[30];
	char query[MAX_LENGTH];                        //contiene la query da eseguire sul database
	
	char cf[17];                                   //parametri relativi al proprietario da aggiungere
	char password[256];                            //tali parametri relativi al proprietario 
    char nome[256];                                //saranno memorizzati in un record della tabella
	char cognome[256];                             //proprietario del database
	char indirizzo[256];
	char citta[256];
	char provincia[256];
	char cap[6];
	
	printf("Content-type:text/html\n\n\n");        //definizione del content-type
	//apertura tag HTML
    printf("<html>\n");
	printf("<body>\n");
	
	
	// ELABORAZIONE DEI PARAMETRI
	param = init_param();                          //inizializzazione  parametri POST
	if (param == NULL) {                           //controllo sulla variabile "param" che contiene il valore del post
		fatal_error("impossibile ricavare i parametri passati dal post");
	}
	
	if (find_n_param(param, "cf", cf, 17) == 1) {  //controllo sull' estrazione del parametro "cf" 
		param = del_param(param);
		fatal_error("impossibile elaborare i parametri passati dal post: cf");
	}
	
	if (find_n_param(param, "password", password, 256) == 1) {  //controllo sull' estrazione del parametro "password"
		param = del_param(param);
		fatal_error("impossibile elaborare i parametri passati dal post: password");
	}
	
	if (find_n_param(param, "nome", nome, 256) == 1) {          //controllo sull' estrazione del parametro "nome"
		param = del_param(param);
		fatal_error("impossibile elaborare i parametri passati dal post: nome");
	}
	
	if (find_n_param(param, "cognome", cognome, 256) == 1) {    //controllo sull' estrazione del parametro "cognome"
		param = del_param(param);
		fatal_error("impossibile elaborare i parametri passati dal post: cognome");
	}
	
	if (find_n_param(param, "indirizzo", indirizzo, 256) == 1) {//controllo sull' estrazione del parametro "indirizzo"
		param = del_param(param);
		fatal_error("impossibile elaborare i parametri passati dal post: indirizzo");
	}
	
	if (find_n_param(param, "citta", citta, 256) == 1) {        //controllo sull' estrazione del parametro "citta"
		param = del_param(param);
		fatal_error("impossibile elaborare i parametri passati dal post: citta'");
	}
	
	if (find_n_param(param, "provincia", provincia, 256) == 1) {//controllo sull' estrazione del parametro "provincia"
		param = del_param(param);
		fatal_error("impossibile elaborare i parametri passati dal post: provincia");
	}
	
	if (find_n_param(param, "cap", cap, 6) == 1) {             //controllo sull' estrazione del parametro "cap"
		param = del_param(param);
		fatal_error("impossibile elaborare i parametri passati dal post: cap");
	}
	
	//eliminazione  parametri
	param = del_param(param);
	
	
	//INSERIMENTO DATI NEL DATABASE
	if (!isanumber(cap)) {						//controllo sulla validita' numerica del parametro "cap" (5 caratteri)
		fatal_error("Il codice CAP non e' valido!<br><a href=\"../AreaAmministratore/addproprietario.htm\">torna indietro</a><br>");
	}
	
	if ((strlen(cf) == 0) || (strlen(password) == 0) ||         //controllo sull' inserimento di tutti i parametri
               (strlen(nome) == 0) || (strlen(cognome) == 0) ||(strlen(indirizzo) == 0) || 
               (strlen(citta) == 0) || (strlen(provincia) == 0) || (strlen(cap) == 0)) {
		fatal_error("Tutti i campi sono obbligatori!<br><a href=\"../AreaAmministratore/addproprietario.htm\">torna indietro</a><br>");
	}
	

	//esecuzione select
	query[0] = '\0';                                             //costruzione della variabile "query" che verra' usata per la insert SQL
	strcat(query, "INSERT INTO `proprietario` ( `cf` , `password` , `nome` , `cognome` , `indirizzo` , `citta` , `provincia` , `cap` ) VALUES ('");
	strcat(query, cf);
	strcat(query, "', '");
	strcat(query, password);
	strcat(query, "', '");
	strcat(query, nome);
	strcat(query, "', '");
	strcat(query, cognome);
	strcat(query, "', '");
	strcat(query, indirizzo);
	strcat(query, "', '");
	strcat(query, citta);
	strcat(query, "', '");
	strcat(query, provincia);
	strcat(query, "', '");
	strcat(query, cap);
	strcat(query, "');");
	
	//connessione al database
	if (init_mysql() != 0) {                                    //controllo sulla corretta conessione al database
		fatal_error("Impossibile connettersi al database!");
	}
	
	if (insert_mysql(query) != 0) {                             //controllo sull'esito dell'esecuzione della insert SQL
		close_mysql();
		fatal_error("Inserimento nel database, probabilmente gia' esiste questo proprietario.<br><a href=\"../AreaAmministratore/addproprietario.htm\">torna indietro</a><br>");
	}
	
	printf("Proprietario %s %s inserito correttamente!!!<br>", nome, cognome);
	print_menu();

	close_mysql();                                               //chiusura connessione col database

    //chiusura tag HTML
	printf("</body>\n");	
	printf("</html>\n\n");
}


