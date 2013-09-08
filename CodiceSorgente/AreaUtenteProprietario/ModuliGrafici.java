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

import java.io.*; // Per la classe PrinterWriter
import java.util.*; // Per leggere i nomi dei file sul file system
import javax.servlet.http.*; // Per HttpServletRequest & HttpServletResponse

// Import per la connessione al database
import Database.Connessione;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;




public class ModuliGrafici {
    
    private HttpServletRequest req;
    private HttpServletResponse res;
    private PrintWriter out;
    private HttpSession session;
    
    /**
     * Oggetto statico di tipo Connessione per stabilire una connessione col database
     */
    private static Connessione con;
    /**
     * Oggetto statico di tipo Connection per stabilire una connessione col database
     */
    private static Connection c;
    
    /** Creates a new instance of ModuliGrafici */
    public ModuliGrafici() {
        
    }
    
    public void setRequestResponse( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        req = request;
        res = response;
        res.setContentType("text/html");
        out = res.getWriter(); //Ottengo lo stream di output
        session = req.getSession(true); //recupera la sessione, se non esiste la crea
    }
    
    public void finalize() {
        out.close();
    }
    
    
    public String getNomeImage(String nomeArticolo) {
        
		//nome articolo in minuscolo
        nomeArticolo = nomeArticolo.toLowerCase();
        
	  /*
          Path del server in cui sono conservate le immagini dei prodotti,
          In questo caso usiamo un path assoluto: questa non Ë una buona pratica! Ci sono
          stati dei problemi portando il progetto dall'ambiente di NetBeans, all'ambiente
          puro di tomcat.
        */
        File path = new File("/Users/ferrara/Consorzio/build/web/prodotti");
        String[] list;  //vettori di stringhe (nomi dei file della directory del server indicata)
        String nomeDaCercare; //prodotto da cercare
        
        nomeDaCercare = new String(nomeArticolo + ".jpg");  //aggiunta di jpg al nome
        String ricordaNome = new String(nomeArticolo);
        nomeArticolo = "notaviable";  //supponiamo di non aver trovato l'articolo
        
        list = path.list();
        if (list != null) {
		    //per tutti i file nella directory
            for (int i=0; i < list.length; i++ )
                if ( list[i].equals(nomeDaCercare) ) {
                nomeArticolo = ricordaNome;  //articolo trovato
                break;
                }
        }
        
        
        return nomeArticolo;
    }
    
    
    public void stampaIntestazione() {
        
        out.print("" + // INIZIO STAMPA INTESTAZIONE
                "<html>" +
                "<head>" +
                "<title>Consorzio di serre \"Natura Viva\"</title>" +
                "</head>" +
                "" +
                "<body bgcolor=\"#E4FCC5\" >" +
                "<table align=\"center\" width=\"790\" border=\"0\"><tr><td>" +
                "" +
                "<!------------------------- START PAGE (790 pixel) ------------------------->" +
                "" +
                "<!-- Tabella INTESTAZIONE -->" +
                "<table border=\"0\" width=\"100%\">" +
                "<tr>" +
                "<td width=\"608\">" +
                "<img src=\"images/logo.jpg\" width=\"608\" height=\"154\">" +
                "</td>" +
                "<td width=\"100%\">" +
                "" +
                "<!-- BEGIN FORM PER IL LOGIN -->" +
                "<table align=\"center\" border=\"1\" cellpadding=\"4\" cellspacing=\"0\" bordercolor=\"#009933\">" +
                "<tr><td align=\"center\">");
        
        String logout = new String("Logout");   
        String cliccato = req.getParameter("cliccato");
        
        if ( logout.equals(cliccato) ) {  //se cliccato sul log-out invalida la sessione
            session.invalidate();
            session = req.getSession(true);  //crea una nuova sessione
        }
        
        //PRENDE GLI ATTRIBUTI DALLA SESSIONE
		String cf_login = (String) session.getAttribute("cf");  
        String nome_login = (String) session.getAttribute("nome");
        String cognome_login = (String) session.getAttribute("cognome");
        
        //se dalla sessione ricaviamo tutti valori null, stampa il benvenuto per un nuovo utente non loggato
		if ( (cf_login != null) && (nome_login != null) && (cognome_login != null) ) {
            stampaBenvenuto();
        } else {     //utente gia' loggato
            
            
            String accedi = new String("Accedi");
            cliccato = req.getParameter("cliccato");
            
            if ( accedi.equals(cliccato) ) { //Se sono stati passati parametri
                
				//prende i parametri dal post
				String cf = req.getParameter("cf");
                String password = req.getParameter("password");
                
                Cookie[] cookies = req.getCookies();     //recupera i cookies
                String jsessionid = new String("JSESSIONID");
                boolean cookiesAbilitati = false;
                
                if (cookies != null) {     //se ci sono cookies
                    for (int i = 0; i < cookies.length; i++) {   //Controlla se e' stato settato il cookie JSESSIONID
                        Cookie c = cookies[i];      //prende il cookie i-esimo
                        String nomeCookie = c.getName();   //prende il nome
                        if ( nomeCookie.equals(jsessionid) ) {  //se esiste il JSESSIONID allora i cookies sono abilitati sul client
                            cookiesAbilitati = true;
                            break;
                        }
                        
                    }
                }
                
                if ( (cookiesAbilitati) && ( cf.length() == 16 ) && ( password.length() > 0 ) && ( password.length() < 256 ) ) {
                                                                       //se i cookie sono abilitati e i parametri sono corretti
					con = Connessione.getConnessione();                //inizializzo la connessione col database 
                    c = con.getConnection();                           //oggetto connessione
                    
                    Statement s;
                    try {
                        s = c.createStatement();
                        
						//in r il risultato della query SQL
                        ResultSet r = s.executeQuery("SELECT nome, cognome FROM cliente WHERE password=PASSWORD('" +
                                password + "') AND cf='" + cf + "';");
                        
                        if (r.next()) {       //il cliente si è loggato è setto le variabili di sessione per il cliente
                            session.setAttribute("cf", cf);
                            session.setAttribute("nome", r.getString("nome"));
                            session.setAttribute("cognome", r.getString("cognome"));
                            String tipoutente = new String("cliente");
                            session.setAttribute("tipoutente", tipoutente);
                            s.close();
                            stampaBenvenuto();
                        } else { //Username o password sbagliate, oppure è un proprietario
                            s.close();
                            
                            s = c.createStatement();
                            //prendo il carrello dalla sessione
                            r = s.executeQuery("SELECT nome, cognome FROM proprietario WHERE password=PASSWORD('" +
                                    password + "') AND cf='" + cf + "';");
                            if (r.next()) {    //il proprietario si è loggato è setto le variabili di sessione per il proprietario
                                session.setAttribute("cf", cf);
                                session.setAttribute("nome", r.getString("nome"));
                                session.setAttribute("cognome", r.getString("cognome"));
                                String tipoutente = new String("proprietario");
                                session.setAttribute("tipoutente", tipoutente);
                                stampaBenvenuto();
                            } else {
                                stampaFormLogin();
                            }
                            s.close(); //chiude lo statement associato alla query
                        }
                        
                    } catch (SQLException ex) {   //in caso di errore
                        out.println("Errore ex");
                        ex.printStackTrace();
                    }
                    
                } else { // Parametri errati
                    if (!cookiesAbilitati) {
                        out.print("Devi abilitare i cookies!");
                    }
                    stampaFormLogin();
                }
                
            } else {
                stampaFormLogin();
            }
        }
        out.print("" +
                "</td></tr>" +
                "</table>" +
                "" +
                "<!-- END FORM PER IL LOGIN -->" +
                "" +
                "</td>" +
                "</tr>" +
                "</table>" +
                ""); // FINE STAMPA INTESTAZIONE
        
    }
    
    public void stampaMenuUtente() {
        
        out.print("" + // INIZIO STAMPA MENU UTENTE
                "<!-- Tabella MENU Cliente-->" +
                "<br>" +
                "<table width=\"70%\" align=\"center\" border=\"1\" cellpadding=\"2\" cellspacing=\"0\" bordercolor=\"#009933\" bgcolor=\"#99FF99\">" +
                "<tr>" +
                "<td align=\"center\"><a href=\"Index\">Vetrina</a></td>" +
                "<td align=\"center\"><a href=\"MioCarrello\">Carrello</a></td>" +
                "<td align=\"center\"><a href=\"NewCliente\">Registrati</a></td>" +
                "</tr>" +
                "</table>" +
                "<!-- Tabella BODY -->" +
                "<p>&nbsp;</p>" +
                "<table>" +
                ""); // FINE STAMPA MENU UTENTE
        
    }
    
    public void stampaMenuProprietario() {
        
        out.print("" + // INIZIO STAMPA MENU PROPRIETARIO
                "<!-- Tabella MENU Proprietario-->" +
                "<br>" +
                "<table width=\"70%\" align=\"center\" border=\"1\" cellpadding=\"2\" cellspacing=\"0\" bordercolor=\"#009933\" bgcolor=\"#99FF99\">" +
                "<tr>" +
                "<td align=\"center\"><a href=\"AddProdotto\">Aggiungi un articolo</a></td>" +
                "<td align=\"center\"><a href=\"DelProdotto\">Elimina un articolo</a></td>" +
                "<td align=\"center\"><a href=\"UpdateProdotto\">Modifica un articolo</a></td>" +
                "</tr>" +
                "</table>" +
                "<!-- Tabella BODY -->" +
                "<p>&nbsp;</p>" +
                "<table>" +
                ""); // FINE STAMPA MENU PROPRIETARIO
        
    }
    
    public void stampaMenu() {
        String proprietario = new String("proprietario");    //oggetto proprietario
        String utente = (String) session.getAttribute("tipoutente");
        
        if ( (utente != null) && (utente.equals(proprietario)) ) {  //se è un proprietario stampa il menù ad esso associato
            stampaMenuProprietario();
        } else {    //altrimenti è un cliente e stampa il menù ad esso associato
            stampaMenuUtente();
        }
        
    }
    
    public void stampaBenvenuto() {
        String nome = (String) session.getAttribute("nome");
        String cognome = (String) session.getAttribute("cognome");
        out.print( "Benvenuto " + nome + " " + cognome + "<br>" );
        out.print("" +
                "<form action=\"Index\" name=\"logout\" method=\"post\">" +
                "<input type=\"submit\" name=\"cliccato\" value=\"Logout\">" +
                "</form>" +
                "");
    }
    
    public void stampaFormLogin() {
        out.print("" +
                "<form action=\"Index\" name=\"login\" method=\"post\">" +
                "User:<br><input type=\"text\" name=\"cf\" size=\"12\" maxlength=\"16\"><br>" +
                "Password:<br><input type=\"password\" name=\"password\" size=\"12\" maxlength=\"255\"><br>" +
                "<input type=\"submit\" name=\"cliccato\" value=\"Accedi\">" +
                "</form>" +
                "");
    }
    
    public void stampaFooter() {
        
        out.print("" + // INIZIO STAMPA FOOTER
                "</table>" +
                "" +
                "<!-- Tabella FOOTER -->" +
                "<p>&nbsp;</p>" +
                "<table width=\"100%\">" +
                "<tr><td>" +
                "<h5 align=\"center\">Consorzio di Serre \"Natura Viva\"</h5>" +
                "<font size=\"-2\">" +
                "<p align=\"center\">Colucci Carmine Antonio (961-87)<br>" +
                "D'Apolito Filomeno (961-81)<br>" +
                "Ferrara Francesco Saverio (961-79)</p>" +
                "</font>" +
                "</td></tr>" +
                "</table>" +
                "<!------------------------- END PAGE (790 pixel) ------------------------->" +
                "</td></tr></table>" +
                "</body>" +
                "</html>" +
                ""); // FINE STAMPA FOOTER
        
    }
    
}
