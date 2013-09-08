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

import java.util.ArrayList;


public class Carrello {
    
    private ArrayList listaArticoli; // Il carrello contiene una lista di articoli
    
    /** Creates a new instance of Carrello */
    public Carrello() {
        listaArticoli = new ArrayList(); // Creo una lista vuota
    }
    
    public int numeroArticoli() { //Ritorno il numero di articoli presenti nel carrello
        return listaArticoli.size();
    }
    
	// Inserisco l'articolo nomeArticolo nel carrello
    public void insertIntoCarrello(String nomeArticolo, int quantita) {
        Articolo articolo;
        String nomeArticoloCarrello;
        boolean esistente = false; // vale 'true' se l'articolo gia' esiste nel carrello
        
        for ( int i=0 ; i<listaArticoli.size() ; i++ ) { //Per tutti gli articoli presenti nel carrello
            articolo = (Articolo) listaArticoli.get(i); //Prendo l'articolo
            nomeArticoloCarrello = articolo.nomeArticolo; //Controllo il nome
            if ( nomeArticoloCarrello.equals(nomeArticolo) ) { //Se e' uguale all'articolo da inserire
				/*
				In questo caso dobbiamo evitare di aggiungere due volte lo stesso articolo!
				La politica adottata:
					-> Aggiorniamo solo la quantita' dell'articolo!
				*/
                articolo.setQuantita(quantita); //Aggiorno solo la quantita'
                esistente = true; // Non devo inserire nessun altro articolo
                break; // Non ho bisogno di cercare tra gli altri articoli nel carrello
            }
        }
        
        if (!esistente) { // Se non ho trovato l'articolo nel carrello lo inserisco
            articolo = new Articolo(nomeArticolo, quantita);
            listaArticoli.add( (Object) articolo);
        }
    }
    
	// Restituisce l'articolo nel carrello posizionato alla posizione passata per parametro
    public String getNomeArticolo(int posizione) {
        Articolo articolo;
        
        articolo = (Articolo) listaArticoli.get(posizione);
        
        return articolo.getNomeArticolo();
    }
    
	// Restituisce la quantita' dell'articolo nel carrello posizionato alla posizione passata per parametro
    public int getQuantita(int posizione) {
        Articolo articolo;
        
        articolo = (Articolo) listaArticoli.get(posizione);
        
        return articolo.getQuantita();
    }
    
	// Cancella l'articolo dal carrello posizionato alla posizione passata per parametro
    public void removeArticolo(int posizione) {
        listaArticoli.remove(posizione);
    }
    
	// Restituisce l'articolo 'nomeArticolo' dal carrello
    public void removeArticolo(String nomeArticolo) {
        int i;
        Articolo articolo;
        String nomeEstrapolato;
        
        for ( i=0 ; i<listaArticoli.size() ; i++ ) { //Scorro tutti gli articoli
            articolo = (Articolo) listaArticoli.get(i); //Prendo l'i-esimo articolo
            nomeEstrapolato = articolo.getNomeArticolo(); //Controllo il nome
            if (nomeEstrapolato.equals(nomeArticolo)) { //Se e' proprio l'articolo da cancellare
                listaArticoli.remove(i); //Cancello l'articolo
            }
        }
        
    }
    
    public void svuota() { //Svuota il carrello
        for ( int i=0 ; i<listaArticoli.size() ; i++ ) {
            this.removeArticolo(i);
        }
        
    }
    
    
    
    //Classe innestata 'Articolo'
    private class Articolo {
		/*
		* Ogni articolo e' rappresentato da un oggetto di questo tipo!
		*/
	
        private String nomeArticolo; // Nome dell'articolo
        private int quantita; // Quantita' chiesta dal cliente
        
		// Crea un oggetto: costruttore
        public Articolo(String inpNomeArticolo, int inpQuantita) {
            nomeArticolo = inpNomeArticolo; // imposto il nome
            quantita = inpQuantita; // imposto la quantita'
        }
        
		// Crea un oggetto: costruttore con un solo parametro
        public Articolo(String inpNomeArticolo) {
            nomeArticolo = inpNomeArticolo; // imposto il nome
            quantita = 1; // assumo la quantita' minima
        }
        
        public String getNomeArticolo() {
            return nomeArticolo;
        }
        
        public int getQuantita() {
            return quantita;
        }
        
        public void setQuantita(int inp_quantita) {
            if (inp_quantita >= 0) { // Se e' una quantita' valida
                quantita = inp_quantita;
            } else { // Assumo che la quantita' sia la minima
                quantita = 1;
            }
        }
    }
    
}
