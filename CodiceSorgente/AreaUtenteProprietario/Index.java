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



public class Index extends HttpServlet {
    
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
        moduli.stampaIntestazione();
        moduli.stampaMenu();
        
        //recupera la sessione, se non esiste la crea
        HttpSession session = request.getSession(true);
		
        String proprietario = new String("proprietario");
        String utente = (String) session.getAttribute("tipoutente");
        
        if ( (utente != null) && (utente.equals(proprietario)) ) {
            out.print("<h1>Benvenuto proprietario</h1><p>Scegli una voce dal menu per iniziare</p>");
        } else {
            
            //stampa vetrina
            out.print("<h1>I nostri prodotti</h1>");
            
            Statement s;
            try {
                s = c.createStatement();
                ResultSet r = s.executeQuery("SELECT nome, prezzo FROM articolo;");
                
                while (r.next()) {
                    out.print("<br><hr><br>" +
                            "<table width=\"80%\" cellspacing=\"4\" cellpadding=\"4\" align=center>" +
                            "<tr>" +
                            "<td align=center>" +
                            "<img src=\"prodotti/" + moduli.getNomeImage((r.getString("nome"))) + //controlla la presenza dell'immagine del prodotto sul server, se non esiste ritorna notaviable
                            ".jpg\">" +
                            "</td>" +
                            "<td align=center>" +
                            "<p>Nome Articolo: <strong>" + r.getString("nome")  + "</strong></p>" +
                            "<p>Prezzo: " + r.getString("prezzo") + " euro</p>" +
                            "</td>" +
                            "<td> <form action=\"DettagliProdotto\" nome=\"" + r.getString("nome") + "\">" +
                            "<input type=\"hidden\" name=\"nomeArticolo\" value=\"" + r.getString("nome")  + "\">" +
                            "<input type=\"submit\" name=\"dettagli\"value=\"dettagli\"" +
                            "</form>" +
                            "</td>" +
                            "</tr>" +
                            "</table>" +
                            "");
                }
                
                s.close();
            } catch (SQLException ex) {
                out.println("Errore nella comunicazione con il database");
                ex.printStackTrace();
            }
            
            moduli.stampaFooter();
            
            out.close();
        }
    }
    
}
