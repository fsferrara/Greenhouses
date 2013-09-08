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
import Database.Connessione;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.*;
import javax.servlet.http.*;



public class UpdateProdotto extends HttpServlet {
    
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
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    public void service( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        
        moduli.setRequestResponse(request, response); //passaggio di request e response a moduli per il corretto funzionamento
        moduli.stampaIntestazione(); //Non prendere la sessione prima di chiamare questo
        moduli.stampaMenuProprietario();
        
        // Crea una sessione se non esiste
        HttpSession session = request.getSession(true);
        
        String modifica = new String("Modifica");
        String cliccato = request.getParameter("cliccato");
        
        if ( modifica.equals(cliccato) ) { //Devo elaborare i dati passati
            
            boolean errore = false;
            
			//recupero i parametri dal post
            String id_serra = request.getParameter("id_serra");
            String nome_articolo = request.getParameter("nome_articolo");
            String quantita = request.getParameter("quantita");
            
			//controllo sulla validitˆ dei parametri
            if ( ( id_serra != null ) && ( id_serra.length() > 0 ) && ( id_serra.length() < 12 ) &&
                    ( nome_articolo != null ) && ( nome_articolo.length() > 0 ) && ( nome_articolo.length() < 256 ) &&
                    ( quantita != null ) && ( quantita.length() > 0 ) && ( quantita.length() < 12 )) {
            } else {
                errore = true;
                out.print("Errore nell'inserimento dei dati, probabilmente non ci sono articoli o serre da aggiornare<br>");
            }
            
            if (!errore) {
                
                String sql2;
                
                try {
                    Statement s = c.createStatement();
                    
					//prende la disponibilitˆ del prodotto da aggiornare
                    ResultSet r = s.executeQuery("SELECT * FROM disponibilita WHERE nome_articolo='" +
                            nome_articolo + "' AND id_serra='" + id_serra + "';");
                    
					//se il prodotto giˆ esiste fa l'aggiornamento
                    if (r.next()) {
                        sql2 = new String("UPDATE disponibilita SET quantita='" + quantita + "' WHERE nome_articolo='" +
                                nome_articolo + "' AND id_serra='" + id_serra + "';");
                    } 
					//altrimenti lo inserisce
					else {
                        sql2 = new String("INSERT INTO disponibilita(quantita, nome_articolo, id_serra) VALUES ( '" + quantita + "' , '" + nome_articolo + "' , '" + id_serra + "' );");
                    }
                    
                    s.close();
                    
                    s = c.createStatement();
                    
                    s.execute(sql2);
                    
                    s.close();
                    
                    out.print("Articolo aggiornato correttamente");
                    
                } catch (SQLException ex) {
                    out.print("Errore nella comunicazione con il database<br>");
                    ex.printStackTrace();
                }
            }
            
        } else { //Non mi e' stato passato niente
            String cf = (String) session.getAttribute("cf");
            //utente non loggato
			if (cf == null) {
                out.print("Devi prima fare login al sito!");
            } 
			//form per aggiornare il prodotto
			else {
                out.print("" +
                        "<form action=\"UpdateProdotto\" name=\"updateprodotto\" method=\"post\">" +
                        "Serra per la quale si vuole modificare la quantita':<br><select name=\"id_serra\">" +
                        "");
                
                Statement s;
                try {
                    s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT id, nome, citta FROM serra WHERE cf_proprietario='" + cf + "';");
                    
                    while (r.next()) {
                        out.println( "<option value=\"" + r.getString("id") + "\">" + r.getString("nome") + " di "  + r.getString("citta") + "</option>");
                    }
                    
                    s.close();
                } catch (SQLException ex) {
                    out.println("Errore nella comunicazione con il database");
                    ex.printStackTrace();
                }
                
                out.print("" +
                        "</select>" +
                        "<br><br>Articolo da importare:<br><select name=\"nome_articolo\">" +
                        "");
                
                
                try {
                    s = c.createStatement();
                    
                    ResultSet r = s.executeQuery("SELECT nome FROM articolo");
                    
                    //elenco dei prodotti disponibili
					while (r.next()) {
                        out.println( "<option value=\"" + r.getString("nome") +
                                "\">" + r.getString("nome") + "</option>");
                    }
                    
                    s.close();
                } catch (SQLException ex) {
                    out.println("Errore nella comunicazione con il database");
                    ex.printStackTrace();
                }
                
                out.print("" +
                        "</select>" +
                        "<br><br>Quantita' presente nella serra<br><input type=\"text\" value=\"0\" name=\"quantita\">" +
                        "<br><input type=\"submit\" value=\"Modifica\" name=\"cliccato\">" +
                        "</form>" +
                        "");
            }
        }
        
        moduli.stampaFooter();
        
        out.close();
    }
    
}
