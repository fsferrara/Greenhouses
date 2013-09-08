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



public class NewCliente extends HttpServlet {
    
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
        moduli.stampaMenuUtente();
        
        String conferma = new String("Conferma");
        String cliccato = request.getParameter("cliccato");
        
        if ( conferma.equals(cliccato) ) {
            
            boolean errore = false;
            
			//recupero parametri dal post
            String cf = request.getParameter("cf");
            String password = request.getParameter("password");
            String nome = request.getParameter("nome");
            String cognome = request.getParameter("cognome");
            String indirizzo = request.getParameter("indirizzo");
            String citta = request.getParameter("citta");
            String provincia = request.getParameter("provincia");
            String cap = request.getParameter("cap");
            
			//controllo sulla validitˆ dei parametri
            if (( cf.length() == 16 ) &&
                    ( cap.length() == 5 ) &&
                    (( password.length() > 0 ) && ( password.length() < 256 )) &&
                    (( nome.length() > 0 ) && ( nome.length() < 256 )) &&
                    (( cognome.length() > 0 ) && ( cognome.length() < 256 )) &&
                    (( indirizzo.length() > 0 ) && ( indirizzo.length() < 256 )) &&
                    (( citta.length() > 0 ) && ( citta.length() < 256 )) &&
                    (( provincia.length() > 0 ) && ( provincia.length() < 256 ))) {
            } else {
                errore = true;
                out.print("Errore nell'inserimento dei dati, controllare la correttezza dei dati<br>");
            }
            
            //String replace - sicurezza (SQL injection)
            
            cf = cf.replaceAll("'", "''");
            password = password.replaceAll("'", "''");
            nome = nome.replaceAll("'", "''");
            cognome = cognome.replaceAll("'","''");
            indirizzo = indirizzo.replaceAll("'", "''");
            citta = citta.replaceAll("'", "''");
            provincia = provincia.replaceAll("'", "''");
            cap = cap.replaceAll("'", "''");
            
            
            if ( !errore ) {
                try {
                    int capParsed = Integer.parseInt(cap);  //cast del cap da stringa ad intero
                    if ( capParsed == 0) {
                        out.print("Il CAP deve essere un numero<br><a href=\"NewCliente\">Torna indietro</a>");
                        errore = true;
                    }
                } catch (Exception NumberFormatException) {
                    out.print("Il CAP deve essere un numero<br><a href=\"NewCliente\">Torna indietro</a>");
                    errore = true;
                }
            }
            
            if ( !errore ) {
                Statement s;
                
                try {
                    s = c.createStatement();
					//inserimento nuovo cliente
                    String sql = new String("" +
                            "INSERT INTO cliente ( cf , password , nome , cognome , indirizzo , citta , provincia , cap ) " +
                            "VALUES ('" + cf + "', PASSWORD('" + password + "'), '" + nome + "', '" + cognome + "', '" + indirizzo + "', '" + citta + "', '" + provincia + "', '" + cap + "') " +
                            "");
                    
                    s.execute(sql);
                    //ResultSet r = s.executeQuery(sql);
                    
                    s.close();
                    
                    out.print("Cliente registrato correttamente");
                    
                } catch (SQLException ex) { //in caso di errore
                    out.print("Errore nella comunicazione con il database<br>" +
                            "Probabilmente hai inserito un codice fiscale errato, o gia' eri iscritto al sito<br>" +
                            "<a href=\"NewCliente\">Riprova</a>");
                    ex.printStackTrace();
                }
                
            }
            
            
        } else {  //visualizzazione del form di inserimento al primo caricamento della pagina
            out.print("" +
                    "<form action=\"NewCliente\" method=\"post\" name=\"newUser\">" +
                    "CF:<br><input type=\"text\" name=\"cf\" size=\"16\" maxlength=\"16\"><br><br>" +
                    "Password:<br><input type=\"password\" name=\"password\" size=\"16\" maxlength=\"255\"><br><br>" +
                    "Nome:<br><input type=\"text\" name=\"nome\" size=\"16\" maxlength=\"255\"><br><br>" +
                    "Cognome:<br><input type=\"text\" name=\"cognome\" size=\"16\" maxlength=\"255\"><br><br>" +
                    "Indirizzo<br><input type=\"text\" name=\"indirizzo\" size=\"16\" maxlength=\"255\"><br><br>" +
                    "Citta':<br><input type=\"text\" name=\"citta\" size=\"16\" maxlength=\"255\"><br><br>" +
                    "Provincia:<br><input type=\"text\" name=\"provincia\" size=\"16\" maxlength=\"255\"><br><br>" +
                    "CAP:<br><input type=\"text\" name=\"cap\" size=\"5\" maxlength=\"5\"><br><br>" +
                    "<input type=\"submit\" name=\"cliccato\" value=\"Conferma\">" +
                    "</form>" +
                    "");
        }
        
        
        
        moduli.stampaFooter();
        
        out.close();
    }
    
}
