########################################################################
#                                                                      #
#                      Progetto di "Tecnologie Web"                    #
#                                                                      #
########################################################################
#
# Colucci Carmine Antonio - 961/87
# D'Apolito Filomeno - 961/81
# Ferrara Francesco Saverio - 961/79
# 
# Questo file inserisce automaticamente:
#   - password di amministratore
#   - atri dati per provare il progetto
#
# Per eseguire il dump:
#   mysql -u root -p < nomefile.sql


USE consorzio;


# --------------------------------------------------------
#
# Dump dei dati per la tabella proprietario
#

INSERT INTO proprietario VALUES ('CLCCMN83L16A580O', '*F866DE935D1C042FDD4814C29ECF32E44C1F0A75', 'Carmine Antonio', 'Colucci', 'via Pirandello%2C 16', 'Baiano', 'Avellino', 83022);
INSERT INTO proprietario VALUES ('DPLFMN82D18A560D', '*366CD11024FDF67B9F40956A66B0254CCC8C4F8C', 'Filomeno', 'D%27Apolito', 'via Michelangelo%2C 13', 'Sirignano', 'Avellino', 83020);
INSERT INTO proprietario VALUES ('FRRFNC83P20A509X', '*F3DF9CA7528CFCDE1197D9495B0579808C552240', 'Francesco Saverio', 'Ferrara', 'via Roma%2C 11', 'Quadrelle', 'Avellino', 83020);
INSERT INTO proprietario VALUES ('PPPPPPPPPPPPPPPP', '*7B9EBEED26AA52ED10C0F549FA863F13C39E0209', 'Proprietario', 'Tirchio', 'via Proprietario', 'Palermo', 'Palermo', 98777);

# --------------------------------------------------------
#
# Dump dei dati per la tabella serra
#

INSERT INTO serra VALUES (3, 'La gioia della Natura', 'via Circumvallazione', 'Napoli', 'Napoli', 83100, 'CLCCMN83L16A580O');
INSERT INTO serra VALUES (4, 'La Fibra della Natura', 'via Fabio Fibra', 'Roma', 'Roma', 195, 'DPLFMN82D18A560D');
INSERT INTO serra VALUES (5, 'La serra nel mondo di Linux', 'via Slackware', 'Palermo', 'Palermo', 12345, 'FRRFNC83P20A509X');


# --------------------------------------------------------
#
# Dump dei dati per la tabella articolo
#

INSERT INTO articolo VALUES ('Arancio', 'Mediterraneo', 'A volte si e a volte no', 'dal 30% al 31%', 'Potatura nel mese di novembre', '30.00');
INSERT INTO articolo VALUES ('Banano', 'Tropicale', 'Non richiesta', 'Dal 60% al 99%', 'Tre banane al giorno tolgono il medico di torno', '100.00');
INSERT INTO articolo VALUES ('Fichi', 'Ambienti collinari', 'A volte si e a volte no', '30% fisso', 'Attenzione alle radici che tendono ad allungarsi molto velocemente', '50.00');
INSERT INTO articolo VALUES ('Girasole', 'Dietro al giardino', 'Nessuna (si girano verso il sole)', 'Dal 60% al 80%', 'Non calpestarli', '10.00');
INSERT INTO articolo VALUES ('Limone', 'Collinare', 'Media', 'dal 20% al 50%', 'Potatura nel mese di novembre', '30.00');
INSERT INTO articolo VALUES ('Melanzane', 'Melenzane di serra', 'Parecchia', 'dal 5% al 20%', 'A funghetto sono troppo buone', '5.00');
INSERT INTO articolo VALUES ('Melo', 'Collinare', 'Media', 'dal 10% al 45%', 'Concimazione frequente contro i batteri', '35.00');
INSERT INTO articolo VALUES ('Pero', 'Mediterraneo', 'Media', 'dal 20% al 55%', 'Potatura nel mese di marzo', '40.00');
INSERT INTO articolo VALUES ('Pomodori', 'Pomodori di serra', 'Parecchia', 'dal 2% al 8%', 'Verderame ogni mese', '5.00');
INSERT INTO articolo VALUES ('Rose', 'Giardini e mazzi (di rose)', 'Media', 'dal 20% al 40%', 'Per preservare la carnositˆ innaffiarle frequentemente', '8.00');
INSERT INTO articolo VALUES ('Tulipani', 'Mazzi e buchet vari', 'Media', 'dal 10% al 35%', 'Esporli al sole 3 ora al giorno', '6.00');
INSERT INTO articolo VALUES ('w', 'w', 'w', 'w', 'w', '2.00');

# --------------------------------------------------------
#
# Dump dei dati per la tabella cliente
#

INSERT INTO cliente VALUES ('CCCCCCCCCCCCCCCC', '*FDD369C6B7C3C64C7C07EDE4DC5C01BF8970B24D', 'Cliente', 'Clientone', 'via Cliente', 'Clientopoli', 'Napoli', 43567);


# --------------------------------------------------------
#
# Dump dei dati per la tabella ordine
#

INSERT INTO ordine VALUES (6, 'CCCCCCCCCCCCCCCC');
INSERT INTO ordine VALUES (8, 'CCCCCCCCCCCCCCCC');
INSERT INTO ordine VALUES (9, 'CCCCCCCCCCCCCCCC');
INSERT INTO ordine VALUES (10, 'CCCCCCCCCCCCCCCC');
INSERT INTO ordine VALUES (11, 'CCCCCCCCCCCCCCCC');
INSERT INTO ordine VALUES (12, 'CCCCCCCCCCCCCCCC');
INSERT INTO ordine VALUES (13, 'CCCCCCCCCCCCCCCC');
INSERT INTO ordine VALUES (15, 'CCCCCCCCCCCCCCCC');
INSERT INTO ordine VALUES (16, 'CCCCCCCCCCCCCCCC');

# --------------------------------------------------------
#
# Dump dei dati per la tabella composizione_ordine
#

INSERT INTO composizione_ordine VALUES (1, 'Arancio', 6, 3);
INSERT INTO composizione_ordine VALUES (1, 'Banano', 6, 3);
INSERT INTO composizione_ordine VALUES (1, 'Banano', 6, 5);
INSERT INTO composizione_ordine VALUES (1, 'Arancio', 8, 3);
INSERT INTO composizione_ordine VALUES (2, 'Banano', 8, 5);
INSERT INTO composizione_ordine VALUES (1, 'Arancio', 9, 3);
INSERT INTO composizione_ordine VALUES (2, 'Banano', 9, 5);
INSERT INTO composizione_ordine VALUES (5, 'Arancio', 10, 5);
INSERT INTO composizione_ordine VALUES (2, 'Fichi', 10, 5);
INSERT INTO composizione_ordine VALUES (1, 'Banano', 10, 5);
INSERT INTO composizione_ordine VALUES (1, 'Arancio', 11, 5);
INSERT INTO composizione_ordine VALUES (1, 'Fichi', 11, 5);
INSERT INTO composizione_ordine VALUES (1, 'Banano', 11, 5);
INSERT INTO composizione_ordine VALUES (1, 'Arancio', 12, 5);
INSERT INTO composizione_ordine VALUES (1, 'Fichi', 12, 5);
INSERT INTO composizione_ordine VALUES (1, 'Banano', 12, 5);
INSERT INTO composizione_ordine VALUES (1, 'Arancio', 13, 5);
INSERT INTO composizione_ordine VALUES (1, 'Fichi', 13, 5);
INSERT INTO composizione_ordine VALUES (1, 'Banano', 13, 5);
INSERT INTO composizione_ordine VALUES (1, 'Arancio', 15, 5);
INSERT INTO composizione_ordine VALUES (1, 'Banano', 15, 5);
INSERT INTO composizione_ordine VALUES (1, 'Fichi', 15, 5);
INSERT INTO composizione_ordine VALUES (4, 'Arancio', 16, 5);
INSERT INTO composizione_ordine VALUES (1, 'Banano', 16, 5);

# --------------------------------------------------------
#
# Dump dei dati per la tabella disponibilita
#

INSERT INTO disponibilita VALUES (994, 'Arancio', 5);
INSERT INTO disponibilita VALUES (0, 'Arancio', 3);
INSERT INTO disponibilita VALUES (997, 'Banano', 5);
INSERT INTO disponibilita VALUES (998, 'Fichi', 5);
INSERT INTO disponibilita VALUES (0, 'Banano', 3);
INSERT INTO disponibilita VALUES (999, 'Girasole', 5);
INSERT INTO disponibilita VALUES (999, 'Limone', 5);
INSERT INTO disponibilita VALUES (999, 'Melanzane', 5);
INSERT INTO disponibilita VALUES (999, 'Tulipani', 5);
INSERT INTO disponibilita VALUES (999, 'Rose', 5);
INSERT INTO disponibilita VALUES (999, 'Pomodori', 5);

# --------------------------------------------------------
#
# Dump dei dati per la tabella distanza
#

INSERT INTO distanza VALUES (0, 3, 3);
INSERT INTO distanza VALUES (0, 4, 4);
INSERT INTO distanza VALUES (0, 5, 5);
INSERT INTO distanza VALUES (200, 3, 4);
INSERT INTO distanza VALUES (500, 3, 5);
INSERT INTO distanza VALUES (200, 4, 3);
INSERT INTO distanza VALUES (650, 4, 5);
INSERT INTO distanza VALUES (500, 5, 3);
INSERT INTO distanza VALUES (650, 5, 4);
