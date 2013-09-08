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
 
 /*
 * Questo e' un file di prova non facente parte del progetto di Tecnologie Web
 */

package Database;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.*;
import javax.servlet.http.*;



public class Prova extends HttpServlet {
    
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
        
        try {
            super.init();
        } catch (ServletException ex) {
            ex.printStackTrace();
        }
        
    }
    
    
    public void service( HttpServletRequest req, HttpServletResponse res )
    throws ServletException, IOException {
        
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet Prova</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>La mia prima servlet</h1>");
        
        Statement s;
        try {
            s = c.createStatement();
            
            ResultSet r = s.executeQuery("SELECT * FROM proprietario");
            
            while (r.next()) {
                out.println( "qqq" + r.getString("nome") );
            }
            
            s.close();
            
            
        } catch (SQLException ex) {
            out.println("Errore ex");
            ex.printStackTrace();
        }
        
        out.println("Fine Servlet");
        
        
        out.println("</body>");
        out.println("</html>");
        
    }
    
}
