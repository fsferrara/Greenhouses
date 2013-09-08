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
	char *param;								//parametri del post (nome=valore)
	char valore_out[30];
	char query[MAX_LENGTH];
	riga_mysql row;
	
	
	printf("Content-type:text/html\n\n\n");                              //definizione del content-type
	//HTML
    printf("<html>\n");
	printf("<body>\n");
	
	param = init_param();                                                //inizializzazione parametri POST
	if (param == NULL) { 
		fatal_error("Devi inserire la password per proseguire!<br><a href=\"../AreaAmministratore\">Riprova</a>");
	}
	
	if (find_n_param(param, "password", valore_out, 255) == 1) {         //controllo sull' estrazione del parametro "password"
		param = del_param(param);
		fatal_error("Devi inserire la password per proseguire!<br><a href=\"../AreaAmministratore\">Riprova</a>");
	}
	
	//connessione al database
	if (init_mysql() != 0) {                                             //controllo sulla corretta conessione al database
		param = del_param(param);
		fatal_error("Impossibile connettersi al database!");
	}

	//esecuzione select
	query[0] = '\0';                                                     //costruzione della variabile "query" che verrà usata per la select SQL
	strcat(query, "SELECT * FROM amministratore WHERE password = PASSWORD(\"");
	strcat(query, valore_out);
	strcat(query, "\") LIMIT 1;");
	
	if (select_start_mysql(query) != 0) {                                //esecuzione query di tipo select
		close_mysql();
		param = del_param(param);
		fatal_error("Errore nell'interrogazione al database<br><a href=\"../AreaAmministratore\">Riprova</a>");
	}
	
	row = select_getrow_mysql();                                         //restituisce una riga del risultato della query
	if (row == NULL) {
		select_stop_mysql();
		close_mysql();
		param = del_param(param);
		fatal_error("Password di amministratore errata<br><a href=\"../AreaAmministratore\">riprova</a>");
	}
	
	select_stop_mysql();                                                 //rilascia la memoria allocata dalla query di tipo SELECT
	
	close_mysql();                                                       //chiusura connessione

	param = del_param(param);                                            //eliminazione parametri

	print_menu();

	//HTML
    printf("</body>\n");
	printf("</html>\n\n");

}


