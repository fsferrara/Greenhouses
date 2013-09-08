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



public class DettagliProdotto extends HttpServlet {
    
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
        
        moduli.setRequestResponse(request, response);  //passaggio di request e response a moduli per il corretto funzionamento
        moduli.stampaIntestazione();
        moduli.stampaMenu();
        
        
        boolean errore = false;
        String nomeArticolo = request.getParameter("nomeArticolo");  //recupero articolo dal post
        
        if (nomeArticolo != null) {
            out.print("<h1 align=center>" + nomeArticolo  + "</h1>");
        } else {
            out.print("<h1>Articolo non trovato</h1><p>Possibile errore di rete</p>");
            errore = true;
        }
        
        if (!errore) {
            
            Statement s;
            try {
                s = c.createStatement();
				//recupero info del prodotto dal db
                ResultSet r = s.executeQuery("SELECT * FROM articolo WHERE nome=\"" + nomeArticolo + "\";");
                
				//output info profotto
                if (r.next()) {
                    out.print("" +
                            "<p align=center><img src=\"prodotti/" + moduli.getNomeImage((r.getString("nome"))) + ".jpg\"></p>" +
                            "<p align=center><br><br><br>" +
                            "Nome: <strong>" + r.getString("nome") + "</strong><br><br><br>" +
                            "Ambiente: <strong>" + r.getString("ambiente") + "</strong><br><br><br>" +
                            "Ombra: <strong>" + r.getString("ombra") + "</strong><br><br><br>" +
                            "Umidita: <strong>" + r.getString("umidita") + "</strong><br><br><br>" +
                            "Cura: <strong>" + r.getString("cura") + "</strong><br><br><br>" +
                            "Prezzo: <strong>" + r.getString("prezzo") + " euro</strong><br><br>" +
                            "</p>" +
                            "");
                } else {
                    out.print("" +
                            "<p align=center>Prodotto non nel database</p>" +
                            "");
                }
                
                s.close();
                
                // Controllo quanti prodotti sono disponibili nel database
                s = c.createStatement();
                r = s.executeQuery("SELECT sum(quantita) AS somma FROM disponibilita WHERE nome_articolo=\"" + nomeArticolo + "\";");
                
                if (r.next()) {
                    if ((r.getString("somma") != null) && (Integer.parseInt(r.getString("somma")) != 0) ) {
                        out.print("" +
                                "<p align=center>Sono disponibili " + r.getString("somma") + " prodotti</p>" +
                                "<table width=\"100%\"><tr><td align=center><form name=\"vendita\" action=\"MioCarrello\" method=\"post\">" +
                                "<input type=\"hidden\" name=\"nome_articolo\" value=\"" + nomeArticolo + "\">" +
                                "Quantita' di articoli desiderata:&nbsp;<select name=\"quantita\">" +
                                "");
                        //mette nel select la disponibilita' del prodotto
						for (int k=1 ; k <= r.getInt("somma") ; k++) {
                            out.print("<option value=\"" + k + "\">" + k + "</option>");
                        }
                        out.print("" +
                                "</select>" +
                                "&nbsp;<input type=\"submit\" name=\"aggiungi\" value=\"Aggiungi al carrello\">" +
                                "</form></td></tr></table>" +
                                "");
                    } else {
                        out.print("" +
                                "<p align=center>Prodotto non disponibile al momento</p>" +
                                "");
                    }
                } else {
                    out.print("" +
                            "<p align=center>Prodotto non disponibile al momento</p>" +
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

