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



public class MioCarrello extends HttpServlet {
    
    ModuliGrafici moduli;
    /**
     * Oggetto statico di tipo Connessione per stabilire una connessione col database
     */
    private static Connessione con;
    /**
     * Oggetto statico di tipo Connection per stabilire una connessione col database
     */
    private static Connection c;
    
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
    
	//sincronizza il carrello dal cookie
    private void sincronizzaCarrelloCookie(Carrello carrello, HttpServletRequest request) {
        
		//prendi tutti i cookies
        Cookie[] cookies = request.getCookies();
        String jsessionid = new String("JSESSIONID");
        
        if ( cookies != null ) {   //se esistono cookies
			//per tutti i cookie
            for (int i = 0; i < cookies.length; i++) {
				Cookie c = cookies[i];
                String nomeArticolo = c.getName();
                
				//controlla nel cookie la quantita' scelta per l'articolo
				int quantita;
                try {
                    quantita = Integer.parseInt(c.getValue());
                    if (quantita <= 0)
                        quantita = 1;
                } catch (Exception NumberFormatException) {
				    //se il cookie e' corrotto impostiamo la quantita ad 1
                    quantita = 1;
                }
                
                if (!nomeArticolo.equals(jsessionid)) {                     //se non incontri jsessionid
                    carrello.insertIntoCarrello(nomeArticolo, quantita);    //aggiungi il prodotto al carrello
                }
            }
        }
        
    }
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    public void service( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException {
        
		//stream di output
        PrintWriter out = response.getWriter();
		
        //passaggio di request e response a moduli per il corretto funzionamento
        moduli.setRequestResponse(request, response);
        
        moduli.stampaIntestazione();
        moduli.stampaMenuUtente();
        
        // Prendo il mio carrello
        Carrello carrello;
        HttpSession session;
        session = request.getSession(true);  //recupera la sessione, se esiste, altrimenti la crea
        carrello = (Carrello) session.getAttribute("carrello"); //prendo il carrello dalla sessione
        if (carrello == null) {        //se il carrello e' vuoto
            carrello = new Carrello(); //nuovo carrello
            session.setAttribute("carrello", carrello);   //registro il carrello nella sessione
        }
        
        String nomeArticolo = request.getParameter("nome_articolo");  //nome articolo dal post
        
        out.print("<h1 align=center>Il Tuo Carrello</h1>");
        
        if ( ( nomeArticolo != null ) && (nomeArticolo.length() > 0) && ( nomeArticolo.length() < 256) ) {
            
            int quantita;
            
            try {
                quantita = Integer.parseInt(request.getParameter("quantita"));  //preleva quantita' dal post
                //controllo sul post se ci sono corruzioni
				if (quantita <= 0)
                    quantita = 1;
            } catch (Exception NumberFormatException) {
                quantita = 1;
            }
            
            carrello.insertIntoCarrello(nomeArticolo, quantita);  //inserimento articolo nel carrello
            
            //inserimento articolo nel Cookie
            Cookie c = new Cookie(nomeArticolo, Integer.toString(quantita));
			c.setMaxAge(604800); // Il cookie scade dopo una settimana!
            response.addCookie(c); //memorizza
            
            
            out.print("Aggiunto " + nomeArticolo + " al tuo carrello...");
        }
        
        
        sincronizzaCarrelloCookie(carrello, request); //sincronizza il carrello con i cookie
        
        
        if ( carrello.numeroArticoli() == 0 ) {
            out.print("<p align=center>Il tuo carrello e' vuoto!<br>");
        } else {
            out.print("Nel tuo carrello ci sono i seguenti articoli: <br><br>");
            
			//stampa il contenuto del carrello
            for (int i=0 ; i<carrello.numeroArticoli() ; i++) {
                out.print("Nome: " + carrello.getNomeArticolo(i) + "(");
                out.print("quantita'=" + carrello.getQuantita(i) + ")<br>");
            }
            
			
            String cliente = new String("cliente");
            String tipoutente = (String) session.getAttribute("tipoutente"); //riconosciamo se e' un cliente o un proprietario
			
            if ( (tipoutente != null) && (tipoutente.equals(cliente)) ) {   //se e' un cliente loggato
                //Posso acquistare
                out.print("<br><br><form name=\"acquisto\" action=\"Acquisto\" method=\"post\">" +
                        "<input type=\"submit\" name=\"acquisto\" value=\"Acquista\">" +
                        "</form><br></p>");
            } else {
                //Devo prima registrarmi
                out.print("<br><br>Devi prima registarti al sito per effettuare gli acquisti!!!<br></p>");
            }
            
        }
        
        moduli.stampaFooter();
        
        out.close();
    }
    
}

