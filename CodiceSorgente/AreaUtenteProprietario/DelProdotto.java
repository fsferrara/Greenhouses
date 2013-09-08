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



public class DelProdotto extends HttpServlet {
    
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
        
		//stream di output
		PrintWriter out = response.getWriter();
        
        moduli.setRequestResponse(request, response);  //passaggio di request e response a moduli per il corretto funzionamento
        moduli.stampaIntestazione();
        moduli.stampaMenuProprietario();
        
        // Crea una sessione se non esiste
        HttpSession session = request.getSession(true);
        
        String cancella = new String("Cancella");
        String cliccato = request.getParameter("cliccato");
        
        if ( cancella.equals(cliccato) ) {
            boolean errore = false;
            
            String nome = request.getParameter("nome");   //nome del prodotto dal post
            if (( nome != null ) && ( nome.length() > 0 ) && ( nome.length() < 256 )) {
            } else {
                errore = true;
                out.print("Errore nell'inserimento dei dati, probabilmente non ci sono articoli da cancellare<br>");
            }
            
            if (!errore) {
                try {
                    Statement s = c.createStatement();
                    
					//e' possibile cancellare un prodotto solo se non e' presente nelle serre
                    ResultSet r = s.executeQuery("SELECT * FROM disponibilita WHERE nome_articolo='" +
                            nome + "' AND quantita>0;");
                    
                    if (r.next()) {
                        errore = true;
                    }
                    
                    s.close();
                    
                    if (!errore) {
                        s = c.createStatement();
                        //e' possibile cancellare un prodotto se non presente in nessun ordine
                        r = s.executeQuery("SELECT * FROM composizione_ordine WHERE nome_articolo='" +
                                nome + "';");
                        
                        if (r.next()) {
                            errore = true;
                        }
                        
                        s.close();
                    }
                    
                    if (errore) {
                        out.print("Errore: l'articolo scelto e' presente in altre serre oppure ancora in consegna!<br>");
                    }
                    
                } catch (SQLException ex) {
                    out.print("Errore nella comunicazione con il database<br>");
                    ex.printStackTrace();
                }
            }
            
			//se fino a questo punto non ci sono stati errori
            if (!errore) {
                try {
                    Statement s = c.createStatement();
					//ok cancello il prodotto
                    String sql = new String("DELETE FROM articolo WHERE nome='" + nome + "'");
                    
                    s.execute(sql);
                    
                    s.close();
                    
                    out.print("Articolo cancellato correttamente");
                    
                } catch (SQLException ex) {
                    out.print("Errore nella comunicazione con il database<br>" +
                            "Probabilmente hai inserito un articolo gia' esistente nel database<br>" +
                            "<a href=\"AddProdotto\">Riprova</a>");
                    ex.printStackTrace();
                }
            }
            
        } else {       //se non ho cliccato il pulsante cancella (primo caricamento della pagina)
            out.print("" +
                    "<form action=\"DelProdotto\" name=\"delprodotto\" method=\"post\">" +
                    "Articolo:&nbsp;<select name=\"nome\">" +
                    "");
            
            Statement s;
            try {
                s = c.createStatement();
                //riempio il select html
                ResultSet r = s.executeQuery("SELECT nome FROM articolo");
                
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
                    "&nbsp;<input type=\"submit\" value=\"Cancella\" name=\"cliccato\">" +
                    "</form>" +
                    "");
        }
        
        moduli.stampaFooter();
        
        out.close();
    }
    
}
