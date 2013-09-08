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



public class AddProdotto extends HttpServlet {
    
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
        
        moduli.setRequestResponse(request, response);
        moduli.stampaIntestazione(); //Non prendere la sessione prima di chiamare questo
        moduli.stampaMenuProprietario();
        
        // Crea una sessione se non esiste
        HttpSession session = request.getSession(true);
        
        String invia = new String("Invia");
        String cliccato = request.getParameter("cliccato");
        
        if ( invia.equals(cliccato) ) {   //se cliccato
            
            boolean errore = false;
            
			//recupero valori dal post
            String nome = request.getParameter("nome");
            String ambiente = request.getParameter("ambiente");
            String ombra = request.getParameter("ombra");
            String umidita = request.getParameter("umidita");
            String cura = request.getParameter("cura");
            String prezzo = request.getParameter("prezzo");
            //String file = request.getParameter("file");  //non richiesto dalla prof
            
            
            //controllo validita' dei parametri
            if (    (( prezzo.length() > 0 ) && ( prezzo.length() < 9 )) &&
                    (( nome.length() > 0 ) && ( nome.length() < 256 )) &&
                    (( ambiente.length() > 0 ) && ( ambiente.length() < 256 )) &&
                    (( ombra.length() > 0 ) && ( ombra.length() < 256 )) &&
                    (( umidita.length() > 0 ) && ( umidita.length() < 256 )) &&
                    (( cura.length() > 0 ) && ( cura.length() < 256 ))) {
            } else {
                errore = true;
                out.print("Errore nell'inserimento dei dati, controllare la correttezza dei dati<br>");
            }
            
			//controllo sql injection
            if (!errore) {
                nome = nome.replaceAll("'", "''");
                ambiente = ambiente.replaceAll("'", "''");
                ombra = ombra.replaceAll("'", "''");
                umidita = umidita.replaceAll("'", "''");
                cura = cura.replaceAll("'", "''");
                prezzo = prezzo.replaceAll("'", "");
                prezzo = prezzo.replaceAll(",", ".");
                
                try {
                    Statement s = c.createStatement();
                    String sql = new String("" +
                            "INSERT INTO articolo ( nome , ambiente , ombra , umidita , cura , prezzo ) " +
                            "VALUES ('" + nome + "', '" + ambiente + "', '" + ombra + "', '" + umidita + "', '" + cura + "', '" + prezzo + "') " +
                            "");
                    
                    s.execute(sql);
                    //ResultSet r = s.executeQuery(sql);
                    
                    s.close();
                    
                    out.print("Articolo inserito correttamente");
                    
                } catch (SQLException ex) {
                    out.print("Errore nella comunicazione con il database<br>" +
                            "Probabilmente hai inserito un articolo gia' esistente nel database<br>" +
                            "<a href=\"AddProdotto\">Riprova</a>");
                    ex.printStackTrace();
                }
                
            }
        } else {      //prima apertura servlet
            out.print("" +
                    "<form action=\"AddProdotto\" method=\"post\" name=\"addProdotto\">" +
                    "Nome:<br><input type=\"text\" name=\"nome\" size=\"80\" maxlength=\"255\"><br><br>" +
                    "Ambiente:<br><input type=\"text\" name=\"ambiente\" size=\"80\" maxlength=\"255\"><br><br>" +
                    "Ombra:<br><input type=\"text\" name=\"ombra\" size=\"80\" maxlength=\"255\"><br><br>" +
                    "Umidita':<br><input type=\"text\" name=\"umidita\" size=\"80\" maxlength=\"255\"><br><br>" +
                    "Cura:<br><input type=\"text\" name=\"cura\" size=\"80\" maxlength=\"255\"><br><br>" +
                    "Prezzo:<br><input type=\"text\" name=\"prezzo\" size=\"8\" maxlength=\"255\"><br><br>" +
                    // Immagine       "Includi Immagine (solo formato jpg) :<br><input type=\"file\" name=\"file\" size=\"67\"><br><br>" +
                    "<input type=\"submit\" name=\"cliccato\" value=\"Invia\">" +
                    "</form>" +
                    "");
        }
        
        moduli.stampaFooter();
        
        out.close();
    }
    
}

