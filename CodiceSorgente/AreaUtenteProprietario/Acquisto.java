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

import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import Database.Connessione;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import Spesa.Carrello;
import Spesa.Fattorino;   //prende un carrello e controlla la disponibilita' di prodotti in tutte le serre (sync)
						  //se tutti i prodotti sono disponibili effettua l'ordine



public class Acquisto extends HttpServlet {
    
    ModuliGrafici moduli;
    /**
     * Oggetto statico di tipo Connessione per stabilire una connessione col database
     */
    private static Connessione con;
    /**
     * Oggetto statico di tipo Connection per stabilire una connessione col database
     */
    private static Connection c;
    
    //cancellazione di un cookie
    private void cancellaCookie(Carrello carrello, HttpServletRequest request, HttpServletResponse response) {
        
        //prende i cookies dalla richiesta http (nome=valore, nome=valore, ..., nome=valore)
		Cookie[] cookies = request.getCookies();
        
		//oggetto stringa che contiene "JSESSIONID" corrispondente al cookie che gestisce la sessione
		String jsessionid = new String("JSESSIONID");
        
		//per tutti i prodotti nel carrello
        for (int i = 0; i < cookies.length; i++) {
            Cookie c = cookies[i];  //singolo cookie
            String nomeArticolo = c.getName();  //prendo il nome del cookie=nome articolo
            
            if (!nomeArticolo.equals(jsessionid)) {   //se il nome  != jsessionid
                
                Cookie cRes = new Cookie(nomeArticolo, new String("0"));
                cRes.setMaxAge(0); // Cancello il cookie (durata nulla)
                response.addCookie(cRes); //scrivi cookie sul client 
                
                carrello.removeArticolo(nomeArticolo);  //cancella articolo dal carrello
            }
        }
        
        
    }
    
	
    public void init( ServletConfig config ) {
        
        con = Connessione.getConnessione();
        c = con.getConnection();
        
        moduli = new ModuliGrafici();
        
        try {
            super.init();
        } catch (ServletException ex) {
            ex.printStackTrace();
        }
        
    }
    
    
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    public void service( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException {
        
        boolean errore = false;     //controlla il flusso corretto dell'esecuzione
        
        PrintWriter out = response.getWriter();   //stream di output
        
        moduli.setRequestResponse(request, response);  //passaggio di request e response a moduli per il corretto funzionamento
        
        moduli.stampaIntestazione();
        moduli.stampaMenuUtente();
        
        
        out.print("<h1 align=center>Conferma dell'ordine</h1>");
        
        HttpSession session;   //sessione
        session = request.getSession(true);  //recupera la sessione, se esiste, altrimenti la crea
        Carrello carrello = null;
        
        if (!errore) {
            carrello = (Carrello) session.getAttribute("carrello");  //prendo il carrello dalla sessione
            if (carrello == null) {  //le info ci devono essere per forza altrimenti a questo stadio c'e' stato un errore
                errore = true;
                out.print("<p align=center>Errore di rete, ripetere l'operazione</p>");
            } else {
                if (carrello.numeroArticoli() == 0) {  //non si puo' acquistare un carrello vuoto
                    errore = true;
                    out.print("<p align=center>Errore di rete, ripetere l'operazione</p>");
                }
            }
        }
        
        String cf = null;
        
        if (!errore) {
            cf = (String) session.getAttribute("cf"); //recupero cf dalla sessione
            if (cf == null) {
                errore = true;
                out.print("<p align=center>Errore di rete, ripetere l'operazione</p>");
            }
        }
        
        String conferma = new String("Conferma");
        String cliccato = request.getParameter("cliccato");  //recupero del value del pulsante dal post
        
        if ( conferma.equals(cliccato) ) { //Se ho premuto in pulsante conferma devo elaborare i dati passati
            String id_serra = request.getParameter("id_serra");  //recupero id_serra dal post
            if ( (id_serra != null) && (id_serra.length() > 0) && (id_serra.length() < 12) ) {
                out.print("Confermo l'ordine basandomi sulla serra con id=" + id_serra + "<br>");
            } else {
                errore = true;
                out.print("<p align=center>Non e' possibile risalire alla serra scelta!</p>");
            }
            
            
            if (!errore) {
                //A questo punto abbiamo id_serra, cf, carrello.
                
                Fattorino fattorino = new Fattorino(id_serra, cf, carrello, out);  //l'oggetto ha il compito di controllare la validita' dell'ordine
                
                int esito = fattorino.effettuaSpesa();  //controlla il carrello e se e' il caso effettua l'ordine
                
                if (esito == 0) {
                    out.print("Ordine inoltrato con successo!<br>");
                    this.cancellaCookie(carrello, request, response);  //azzera il cookie
                } else {
                    out.print("Non abbiamo abbastanza quantita' dell'articolo: " + carrello.getNomeArticolo(esito-1));
                }
                
            }
        }  else {
            
            out.print("<p align=center>Seleziona la serra a te piu' vicina:</p>");
            out.print("<table align=center width=\"100%\"><tr><td align=center>" +
                    "<form name=\"sceltaserra\" action=\"Acquisto\" method=\"post\">" +
                    "<select name=\"id_serra\">");  //menu a tendina delle serre esistenti
            
            Statement s;
            try {
                s = c.createStatement();
                ResultSet r = s.executeQuery("SELECT id, nome, citta FROM serra;");
                
                while (r.next()) {   //aggiungi le serre al <select>
                    out.println( "<option value=\"" + r.getString("id") + "\">" + r.getString("nome") + " di "  + r.getString("citta") + "</option>");
                }
                
                s.close();
            } catch (SQLException ex) {
                out.println("Errore nella comunicazione con il database");
                ex.printStackTrace();
            }
            
            out.print("</select><input type=\"submit\" name=\"cliccato\" value=\"Conferma\"></form></tr></td></table>");
        }
        moduli.stampaFooter();  //footer
        
        out.close();
    }
    
}
