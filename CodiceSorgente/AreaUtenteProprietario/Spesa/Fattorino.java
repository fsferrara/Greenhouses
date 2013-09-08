/*
 * Progetto di Tecnologie Web - Consorzio di serra "Natura Viva"
 *
 * Prof. Anna Corazza - A.A. 2006/07
 *
 * Colucci Carmine Antonio - 961/87
 * D'Apolito Filomeno - 961/81
 * Ferrara Francesco Saverio - 961/79
 *
 */

package Spesa;
import java.io.PrintWriter; //DEBUG, usato per capire come funziona il progetto
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;


public class Fattorino {
    
    /**
     * attributo statico necessaria per la connessione al database
     */
    private Connection c = null;
    PrintWriter out; //DEBUG
    
    String idSerra = null;
    String cf = null;
    Carrello carrello = null;

	/*
	int numeroSerre; Che cos'e' 999?
	Nel metodo inizializzaSerre() questo valore e' inizializzato al vero numero delle serre.
	Probabilmente c'e' un modo piu' intelligente per fare la cosa, ma al momento abbiamo poca
	conoscenza del linguaggio Java.
	*/
    int numeroSerre = 999;
    
	// Matrice di incidenza contenente le distanze
    int[][] collegamenti = new int[numeroSerre][numeroSerre];
	
	// Vettore che associa un indice all'id della serra contenuto nel database
    int[] serre = new int[numeroSerre];
    
    
    
    /** Creates a new instance of Fattorino */
    public Fattorino(String idSerraInput, String cfInput, Carrello carrelloInput, PrintWriter outInput) {
        /*
		Le informazioni per ogni fattorino sono:
		- idSerra: la serra preferita dal cliente
		- cf: il codice fiscale del cliente, usato come chiave nel database
		- carrello: oggetto contenente gli articoli da acquistare
		*/
		idSerra = idSerraInput;
        cf = cfInput;
        carrello = carrelloInput;
        out = outInput; //Usato per stampare iformazioni di debug
    }
    
    // Inizializza l'array delle serre
    private void inizializzaSerre() {
        Statement s;
        try {
            
            s = c.createStatement();
            ResultSet r = s.executeQuery("SELECT id FROM serra;");
            
            
            int i=0;
            while (r.next()) { //Prendo dal database tutte le serre
                serre[i] = r.getInt("id"); //Associo la serra "id" all'indice i
                i++;
            }
            
            this.numeroSerre = i; //Registro il numero delle serre (serre.length e' 999)
            
            s.close();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    // inizializza la matrice contenente i collegamenti tra serre
    private void inizializzaCollegamenti() {
	
		//Inizializzo la matrice con tutti -1
        for (int i=0 ; i<numeroSerre ; i++) {
            for (int j=0 ; j<numeroSerre ; j++) {
                collegamenti[i][j] = -1;
            }
        }
    }
    
	// Dato l'id della serra "serra", ritorno l'indice associato nella matrice
    private int indice(int serra) {
        
        for (int i=0 ; i<numeroSerre ; i++) { //Scorro tutte le serre
            if (serre[i] == serra) // Ho trovato l'indice della serra chiesta
                return i; //Ritorno l'indice ad essa associata
        }
        
        return -1; // Non e' stata trovata la serra
    }
    
	// Funzione uguale a "indice(int serra)", che prevede il parametro sotto forma di String
    private int indice(String serra) {
        return this.indice(Integer.parseInt(serra));
    }
    
	//Preleva le distanze dal database, e le inserisce nella matrice di adiacenza
    private void prelevaDistanze() {
	
        Statement s;
        try {
            
            s = c.createStatement();
			/*
			Questo codice non costruisce una matrice di adiacenza completa! Supponiamo che per ogni
			serra diversa da "idSerra" ci sia nel database la relativa distanza. Questa supposizione e'
			per semplificarci la vita, ma in un progetto reale ci vuole un codice migliore.
			*/
            ResultSet r = s.executeQuery("SELECT id_a, km FROM distanza WHERE id_p ='" + idSerra + "';");
            
            int indicePartenza= this.indice(idSerra);
            int indiceArrivo=0;
            int km=0;
            
            while (r.next()) {
                indiceArrivo = this.indice(r.getInt("id_a"));
                km = r.getInt("km");
                collegamenti[indicePartenza][indiceArrivo] = km;
                collegamenti[indiceArrivo][indicePartenza] = km;
            }
            
            s.close();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Le stesse serre sulla diagonale hanno distanza 0
        for (int i=0 ; i<numeroSerre ; i++) {
            collegamenti[i][i] = 0;
        }
        
    }
    
	/*
	Dato un indicePartenza, indicante l'indice della serra preferita dall'utente, questa procedura
	ritorna un vettore di interi contenente i riferimenti alle altre serre, in ordine di distanza.
	In particolare:
	- alla posizione 0 c'è il riferimento della serra stessa (indicePartenza)
	- nelle numeroSerre-1 posizioni ci sono le restanti serre, ordinate secondo la distanza da indicePartenza
	*/
    private int[] calcolaSerreVicine(int indicePartenza) {
        int[] serreVicine = new int[numeroSerre];
        int[] distanze = new int[numeroSerre];
        
        
        for (int i=0 ; i<numeroSerre ; i++) { //Per tutte le serre
			//Controllo la distanza da indicePartenza alla serra i-esima
            if (collegamenti[indicePartenza][i] == -1) { //Non sono collegate
                distanze[i] = Integer.MAX_VALUE; //Distanza infinita :-P
            } else { //Le serre sono collegate tra loro
                distanze[i] = collegamenti[indicePartenza][i]; //Copio la distanza nel vettore
            }
        }
		
		/*
		A questo punto nel vettore distanze[], ci sono numeroSerre elementi.
		distanze[i] rappresenta la distanza tra l'i-esima serra e indicePartenza.
		- In particolare distanze[indicePartenza] == 0 , questo ci assicura che la serra stessa
		  sia alla posizione 0 (in quanto distanza più piccola)
		*/
        
        int i=0;  //Indice usato per registrare gli elementi nel vettore serreVicine[]
        int j = 0;
        int minValue;
        while (i < numeroSerre) {
            minValue = Integer.MAX_VALUE;
            for ( j=0 ; j<numeroSerre ; j++ ) { //Per tutte le serre
                if ( (distanze[j] != -1) && (distanze[j] < minValue)) { //Se trovo una serra con distanza minore
                    minValue = distanze[j]; //Registro la distanza minore
                    serreVicine[i] = j; //Ricordo l'indice della serra
                }
            }
            distanze[serreVicine[i]] = -1; //Modifico la distanza per questa serra, poiche' e' stata gia' elaborata
            i++;
        }
        
        for (i=0 ; i<numeroSerre ; i++) { //Sostituisco l'indice di array con l'id di serra
            serreVicine[i] = serre[serreVicine[i]];
        }
        
        /*
		Ritorno il vettore serreVicine[] che contiene l'id delle serre in ordine di distanza da serreVicine[0].
		Considero serreVicine[0] perche' e' la prima serra dalla quale prelevare i prodotti da acquistare
		*/
        return serreVicine;
    }
    
	/*
	Questa funzione ha il compito di controllare la disponibilita' della merce, edi registrare effettivamente
	l'ordine nel database. Deve anche aggiornare la disponibilita' dei prodotti per le serre.
	
	Il valore ritornato da questa funzione e':
	- 0 se tutto e' andato a buon fine
	- valore>0 se l'ordine non e' stato registrato perche' non c'e' disponibilita' per l'articolo posizionato
	           nel carrello proprio alla posizione indicata dal valore resituito, ad esempio se e' restituito
			   il numero 3, significa che per il terzo articolo del carrello non c'e' abbastanza disponibilita'
	*/
    private int registraOrdine() {
        int retValue = 0;
        
		//Indice della serra di partenza
        int indiceSerraPartenza = this.indice(idSerra);
		
		//Calcolo gli "id" delle serre vicine ordinate per distanza
        int serreVicine[] = this.calcolaSerreVicine(indiceSerraPartenza);
        
        
        try {
			/*
			Mi metto nella modalita' AutoCommit==false, quindi stiamo per iniziare una transazione
			*/
            c.setAutoCommit(false);
			
			//idOrdine ci servira' per inserire i valori nella tabella composizione_ordine
            int idOrdine = 0;
            
			//DEBUG
            out.print("Setto autocommit a false per eseguire una transazione sicura<br>");
            
            /*
			CREAZIONE NUOVO ORDINE
			*/
            Statement s = c.createStatement();
            String sql = new String("INSERT INTO ordine ( cf_cliente ) VALUES ('" + cf + "');");
            
            s.execute(sql);
            
            s.close();
            
			//DEBUG
            out.print("Ho inserito il codice fiscale in un nuovo ordine<br>");
            
            /*
			VERIFICA ID NUOVO ORDINE
			Prendiamo l'id del nuovo ordine; l'id piu' alto e' il piu' recente
			*/
            s = c.createStatement();
            ResultSet r = s.executeQuery("SELECT max(id) AS idmax FROM ordine WHERE cf_cliente='" + cf + "';");
            
            if (r.next()) {
                idOrdine = r.getInt("idmax");
            }
            
			//DEBUG
            out.print("L'ID del nuovo ordine e' " + idOrdine + "<br>");
            
            s.close();
            
			
			/*
			INOLTRO ORDINE
			controlliamo la disponibilita' e aggiorniamo le tabelle
			*/
            String nomeArticolo = null;
            int quantita = 0;
            boolean ordinato = false;
            
            for (int i=0 ; i<carrello.numeroArticoli() ; i++) {
				/*
				ELABORO ARTICOLO i-ESIMO
				*/
                nomeArticolo = carrello.getNomeArticolo(i); //Prendo il nome
                quantita = carrello.getQuantita(i); //Prendo la quantita'
                
                out.print("ELABORO quantita' " + quantita + " di " + nomeArticolo + "<br>");
                
                ordinato = false; //Non ancora ho ordinato questo articolo
                //Elaboriamo l'i-esimo articolo chiesto
                int prossimaSerra = 0; // La serra 0 è quella preferita dall'utente
                while ((!ordinato) && (prossimaSerra<numeroSerre)) { //Per tutte le serre (in ordine di distanza)
                    
                    // Controlliamo la disponibilita' per serreVicine[prossimaSerra];
                    
                    out.print("SELECT quantita FROM disponibilita WHERE nome_articolo='" +
                            nomeArticolo + "' AND id_serra='" + serreVicine[prossimaSerra] + "';<br>");
                    
                    s = c.createStatement();
                    r = s.executeQuery("SELECT quantita FROM disponibilita WHERE nome_articolo='" +
                            nomeArticolo + "' AND id_serra='" + serreVicine[prossimaSerra] + "';");
                    
                    
                    
                    int quantitaSerra = 0;
                    if (r.next()) {
						//Prendo la quantita' disponibile in serreVicine[prossimaSerra] per il prodotto nomeArticolo
                        quantitaSerra = r.getInt("quantita");
                    }
					
                    s.close();
					
                    
                    if (quantitaSerra>0) { //Se l'articolo nomeArticolo e' presente nella serra
                        int nuovaDisponibilita = 0; //Conterra la nuova disponibilita' per l'articolo nomeArticolo nella serra serreVicine[prossimaSerra]
                        int parteOrdinata = 0; //Conterra' il numero di articoli ordinati in serreVicine[prossimaSerra]
                        int scarto = 0;
                        scarto = quantitaSerra - quantita;
                        if (scarto < 0) {
                            //L'ordine e' effettuato solo in parte
                            //La serra serreVicine[prossimaSerra] ha nuova disponibilita nulla
                            // l'altra quantita' da ordinare e' quantita - quantitaSerra;
                            parteOrdinata = quantitaSerra;
                            nuovaDisponibilita = 0;
                            quantita = quantita - quantitaSerra;
                        } else {
                            //scarto e' la nuova disponibilita per serreVicine[prossimaSerra]
                            //L'ordine e' stato completamente espletato (per questo prodotto)
                            parteOrdinata = quantita;
                            nuovaDisponibilita = scarto;
                            quantita = 0;
                            ordinato=true;
                        }
                        
                        //DEBUG
                        out.print("Nella serra " + serreVicine[prossimaSerra] + " prendo " + parteOrdinata + " " + nomeArticolo + "<br>");
                        out.print("La nuova disponibilita' della serra e' uguale a " + nuovaDisponibilita + "<br>");
                        
                        //Inseriamo la parte dell'ordine
                        s = c.createStatement();
                        sql = new String("INSERT INTO composizione_ordine (quantita, nome_articolo, id_ordine, id_serra) VALUES ('" +
                                parteOrdinata + "', '" + nomeArticolo + "', '" + idOrdine + "', '" + serreVicine[prossimaSerra] + "');");
                        
                        s.execute(sql);
                        s.close();
                        
						//DEBUG
                        out.print("Aggiorno la tabella composizione_ordine<br>");
                        
                        
                        
                        //Aggiorniamo la disponibilita' nella serra
                        s = c.createStatement();
                        sql = new String("UPDATE disponibilita SET quantita='" + nuovaDisponibilita + "' WHERE nome_articolo='" +
                                nomeArticolo + "' AND id_serra='" + serreVicine[prossimaSerra] + "';");
                        s.execute(sql);
                        s.close();
                        
						//DEBUG
                        out.print("Aggiorno la tabella disponibilita<br>");
                        
                    }
                    
                    // Controllimo la prossima serra
                    prossimaSerra++;
                } //END WHILE
                /*
				Si esce dal while in due casi:
				- 1° Caso: l'ordine e' stato completato (ordinato==true)
				- 2° Caso: l'ordine non e' stato completato (ordinato==false),
				           in questo caso il prodotto i-1 ha problemi di disponibilita'
				*/
				
				
                if (!ordinato) { //Se non sono riuscito ad effettuare l'ordine
                    retValue = i+1; //Ritorno l'indice "i-esimo -1"
                    c.rollback(); //Annullo la transazione
                    return retValue; //L'i-esimo-1 articolo ha dato problemi nell'ordinazione
                }
                
            } //END FOR
            
            c.commit(); //Confermo la transazione
            c.close(); //Chiudo la connessione con il database
            
        } catch (SQLException ex) { //Errore con il database, annulliamo la transazione
		
            try {
                c.rollback(); //Annullo la transazione
            } catch (SQLException exc) {
                exc.printStackTrace(); //Errore nell'annullare la transazione (di default la transazione si dovrebbe annullare da sola!)
            }
			
            ex.printStackTrace();
        }
        
        
        
        return retValue;
    }
    
	/*
	Questo metodo e' synchronized perche' solo un cliente per volta puo' modificare il database con questa operazione.
	Da notare che questa classe si connette al database usando una connessione dedicata
	(non quella offerta dalla classe Database.Connessione usata dalle altre servlet, questo e' dovuto al fatto
	che in questa operazione effettuiamo una transazione).
	
	Comunque puo' capitare che un proprietario di serre aggiorni il database da un'altra servlet...
	...necessita dunque di maggiore sincronizzazione, ma non prevediamo questo per semplificare il progetto.
	*/
    synchronized public int effettuaSpesa() {
        
        int retValue = 0;
        
        //Connessione al database
        String DRIVER = "com.mysql.jdbc.Driver"; //Seleziono il driver
        String db = "jdbc:mysql://localhost:3306/consorzio"; //Scelgo il database
        try {
            Class.forName(DRIVER);
            c = DriverManager.getConnection(db, "root", "100euro"); //Mi connetto al database
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        // Body della funzione
        if (c != null) { //Se la connessione al database e' riuscita
		
            this.inizializzaSerre(); //Inizializzo il vettore delle serre
            this.inizializzaCollegamenti(); //Inizializzo la matrice dei collegamenti
            this.prelevaDistanze(); //Leggo le distanze dal database
            
            retValue = this.registraOrdine(); //Effettuo l'ordine
			
        } else { //Non sono riuscito a connettermi al database
            retValue = -1; //Impossibile comunicare con il database
        }
        
        
        // Chiusura della connessione
        if ( c != null) {
            try {
                c.close(); //Chiude la connessione al database
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        return retValue; //Ritorno il valore restituito da 'this.registraOrdine()'
    }
    
}
