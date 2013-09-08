/*	Colucci Carmine Antonio - 961/87
	D'Apolito Filomeno - 961/81
	Ferrara Francesco Saverio - 961/79  */

//elenco librerie usate nella routine
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <mysql/mysql.h>

//definizine costanti
#define MAX_LENGTH 1024



/*
* TYPE DEFINITION
*/

typedef MYSQL_ROW riga_mysql;


/*
* GLOBAL DECLARATION
*/

/* These are the default values for the mysql variables
static char *opt_host_name = NULL;      // server host (default=localhost)
static char *opt_user_name = NULL;      // username (default=login name)
static char *opt_password = NULL;       // password (default=none)
static unsigned int opt_port_num = 0;   // port number (use built-in value)
static char *opt_socket_name = NULL;    // socket name (use built-in value)
static char *opt_db_name = NULL;        // database name (default=none)
static unsigned int opt_flags = 0;      // connection flags (none) 
static MYSQL *conn;                     // pointer to connection handler
*/

static char *opt_host_name = "localhost";      // server host (default=localhost)
static char *opt_user_name = "root";           // username (default=login name)
static char *opt_password = "100euro";         // password (default=none)
static unsigned int opt_port_num = 0;          // port number (use built-in value)
static char *opt_socket_name = NULL;           // socket name (use built-in value)
static char *opt_db_name = "consorzio";            // database name (default=none)
static unsigned int opt_flags = 0;             // connection flags (none) 

/*
* Variabili globali usate per nascondere i dettagli
*
* In questo modo l'utente della libreria avrˆ a che fare solo con l'oggetto ROW,
* e pu˜ concentrarsi sullo sviluppo della pagine web.
*
*/
static MYSQL *conn = NULL;                     // pointer to connection handler 
static MYSQL_RES *result = NULL;               // pointer to the current resultset


int init_mysql() {

	conn = mysql_init(NULL);                   //inizializzazione  variabile "conn" per la connessione
	if (conn == NULL) {                        //controllo su presenza di errori
		fprintf(stderr, "mysql_init() failed (probably out of memory)\n"); 
		return 1;                              //impossibile negoziare una connessione
	}

	//esecuzione reale della connessione con il database
	if (mysql_real_connect (conn, opt_host_name, opt_user_name, opt_password, opt_db_name, opt_port_num, opt_socket_name, opt_flags) == NULL) {
		fprintf(stderr, "init_mysql() -> mysql_real_connect() failed:\nError %u (%s)\n", mysql_errno(conn), mysql_error(conn)); 
		mysql_close(conn);                     //chiusura  link alla connessione
		return 2;                              //impossibile effettuare la connessione
	}

	return 0;                                  //operazione riuscita; nessun errore
}

int select_start_mysql(char *query) {          //esecuzione query di tipo SELECT

	if (mysql_query(conn, query) != 0) {       //esecuzione query passata per parametro
		fprintf(stderr, "select_start_mysql() -> mysql_query() statement failed\n"); //errore nell'esecuzione della query
		return 1;                              //impossibile eseguire la query
	}
	
	//query eseguita correttamente
	result = mysql_use_result(conn);           //preparazione risultato della query alla lettura
	
	return 0;                                  //operazione riuscita; nessun errore
}

riga_mysql select_getrow_mysql() {             //restituisce una riga (row) della query effettuata
	return (riga_mysql) mysql_fetch_row(result);
}

int select_numfields_mysql() {                 //ritorna il numero di campi di ogni riga della query effettuata
	return mysql_num_fields(result);
}

void select_stop_mysql() {                     //rilascia la memoria allocata dalla query di tipo SELECT
	mysql_free_result(result);
	result = NULL;
}

int insert_mysql(char *query) {                //esecuzione query di tipo INSERT

	if (mysql_query(conn, query) != 0) {       //esecuzione query passata per parametro
		fprintf(stderr, "insert_mysql() -> mysql_query() statement failed!!!\n");
		return 1;                              //impossibile eseguire la query
	}

	/*
	  Se si desidera sapere l'esito della query:
	  printf("%lu rows affected\n", (unsigned long) mysql_affected_rows (conn));
	*/
	
	return 0;                                  //operazione riuscita; nessun errore
}

void close_mysql() {                           //chiusura connessione con il database
	mysql_close (conn);
}

/********* CONNETTERSI E DISCONNETTERSI

	if (init_mysql() != 0) {
		printf("Impossibile connettersi al database!\n");
		exit(EXIT_FAILURE);
	}

	.
	.
	.

	close_mysql();
*/

/********* ESEGUIRE UNA SELECT
	
	char query[max_length];
	int i;
	riga_mysql row;
	.
	.
	.
	if (select_start_mysql(query) != 0) {
		printf("Errore select");
		close_mysql();
		exit(EXIT_FAILURE);
	}
	
	while ((row = select_getrow_mysql()) != NULL) {
		for(i=0 ; i<select_numfields_mysql() ; i++) {
			printf("%s\t\t\t\t\t", row[i]);
		}
		printf("\n");
	}
	
	select_stop_mysql();
	.
	.
	.
*/

/********* ESEGUIRE UNA INSERT

	char query[max_length];
	.
	.
	.
	if (insert_mysql(query) != 0) {
		printf("Errore insert");
		close_mysql();
		exit(EXIT_FAILURE);
	}
	.
	.
	.
*/
