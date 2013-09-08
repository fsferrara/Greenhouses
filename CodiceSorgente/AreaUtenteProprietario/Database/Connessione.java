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

package Database;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;



/**
 *
 * <p>Classe per instaurare una connessione con il database JDBC.
 * Implementata con il pattern singleton.</p>
 * <p>Progetto di "Linguaggi di programmazione II"</p>
 * <p>Universita': Federico II</p>
 *
 * @author D'Apolito Filomeno - 566/131 - menodapolito@virgilio.it
 * @author Colucci Carmine Antonio - 566/794 - coluccic@studenti.unina.it
 * @author Ferrara Francesco Saverio - 566/811 - fsterrar@studenti.unina.it
 */

public class Connessione {
    
    /**
     * attributo statico necessaria per la connessione al database
     */
    static private Connessione conn = null;
    
    /**
     * attributo statico necessaria per la connessione al database
     */
    private Connection c = null;
    
    /**
     * Costruttore privato della classe Connessione (singleton)
     */
    private Connessione() {
        
        String DRIVER = "com.mysql.jdbc.Driver"; //Definisco il driver
        String db = "jdbc:mysql://localhost:3306/consorzio"; //Seleziono il database
        
        try {
            Class.forName(DRIVER);
            c = DriverManager.getConnection(db, "root", "100euro"); //Mi connetto al database
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Metodo statico per permettere a ciascun oggetto di occedere all'istanza
     * @return Connessione connessione al database
     */
    static public Connessione getConnessione() {
        if (conn == null) { //Se non mi sono mai connesso con il database
            conn = new Connessione(); //Mi connetto al database
        }
        // *else* conn gia' contiene l'handler giusto
        
		/*
		* Non creo differenti oggetti perche' questa classe implementa
		* il pattern "singleton"
		*/
        
        return conn;
    }
    
    /**
     * Metodo necessario per instaurare la connessione al database
     * @return Connection connessione al database
     */
    public Connection getConnection() {
        return c; //Ritorno il riferimento al database.
    }
    
    
    public void finalize() { //Metodo chiamato dal garbage collector
        if ( conn != null) { //Se ho istanziato qualcosa
            try {
                c.close(); //Chiude la connessione al database
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
