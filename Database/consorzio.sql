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
#
# Per eseguire il dump:
#   mysql -u root -p < nomefile.sql


DROP DATABASE IF EXISTS `consorzio`;
CREATE DATABASE `consorzio`;
USE `consorzio`;


CREATE TABLE `articolo` (
  `nome` varchar(255) NOT NULL ,
  `ambiente` varchar(255) NOT NULL ,
  `ombra` varchar(255) NOT NULL ,
  `umidita` varchar(255) NOT NULL ,
  `cura` varchar(255) NOT NULL ,
  `prezzo` float(5,2) NOT NULL ,
  PRIMARY KEY  (`nome`)
) TYPE=InnoDB;

CREATE TABLE `cliente` (
  `cf` varchar(16) NOT NULL ,
  `password` varchar(255) NOT NULL ,
  `nome` varchar(255) NOT NULL ,
  `cognome` varchar(255) NOT NULL ,
  `indirizzo` varchar(255) NOT NULL ,
  `citta` varchar(255) NOT NULL ,
  `provincia` varchar(255) NOT NULL ,
  `cap` int(5) NOT NULL ,
  PRIMARY KEY  (`cf`)
) TYPE=InnoDB;

CREATE TABLE `proprietario` (
  `cf` varchar(16) NOT NULL ,
  `password` varchar(255) NOT NULL ,
  `nome` varchar(255) NOT NULL ,
  `cognome` varchar(255) NOT NULL ,
  `indirizzo` varchar(255) NOT NULL ,
  `citta` varchar(255) NOT NULL ,
  `provincia` varchar(255) NOT NULL ,
  `cap` int(5) NOT NULL ,
  PRIMARY KEY  (`cf`)
) TYPE=InnoDB;

CREATE TABLE `serra` (
  `id` int(11) NOT NULL auto_increment,
  `nome` varchar(255) NOT NULL ,
  `indirizzo` varchar(255) NOT NULL ,
  `citta` varchar(255) NOT NULL ,
  `provincia` varchar(255) NOT NULL ,
  `cap` int(5) NOT NULL ,
  `cf_proprietario` varchar(16) NOT NULL ,
  PRIMARY KEY  (`id`) ,
   INDEX ( `cf_proprietario` ) ,
  FOREIGN KEY (`cf_proprietario`)
   REFERENCES proprietario(cf)
   ON DELETE CASCADE
   ON UPDATE CASCADE
) TYPE=InnoDB;

CREATE TABLE `disponibilita` (
  `quantita` int(11) NOT NULL ,
  `nome_articolo` varchar(255) NOT NULL ,
  `id_serra` int(11) NOT NULL ,
   INDEX ( `nome_articolo` ) ,
   INDEX ( `id_serra` ) ,
  FOREIGN KEY (`nome_articolo`)
   REFERENCES articolo(nome)
   ON DELETE CASCADE
   ON UPDATE CASCADE,
  FOREIGN KEY (`id_serra`)
   REFERENCES serra(id)
   ON DELETE CASCADE
   ON UPDATE CASCADE
) TYPE=InnoDB;

CREATE TABLE `distanza` (
  `km` int(7) NOT NULL ,
  `id_p` int(11) NOT NULL ,
  `id_a` int(11) NOT NULL ,
   INDEX ( `id_p` ) ,
   INDEX ( `id_a` ) ,
  FOREIGN KEY (`id_p`)
   REFERENCES serra(id)
   ON DELETE CASCADE
   ON UPDATE CASCADE,
  FOREIGN KEY (`id_a`)
   REFERENCES serra(id)
   ON DELETE CASCADE
   ON UPDATE CASCADE
) TYPE=InnoDB;

CREATE TABLE `ordine` (
  `id` int(11) NOT NULL auto_increment,
  `cf_cliente` char(16) NOT NULL ,
  PRIMARY KEY  (`id`) ,
   INDEX ( `cf_cliente` ) ,
  FOREIGN KEY (`cf_cliente`)
   REFERENCES cliente(cf)
   ON DELETE CASCADE
   ON UPDATE CASCADE
) TYPE=InnoDB;

CREATE TABLE `composizione_ordine` (
  `quantita` int(11) NOT NULL ,
  `nome_articolo` varchar(255) NOT NULL ,
  `id_ordine` int(11) NOT NULL ,
  `id_serra` int(11) NOT NULL ,
   INDEX ( `id_ordine` ) ,
   INDEX ( `nome_articolo` ) ,
   INDEX ( `id_serra` ) ,
  FOREIGN KEY (`id_ordine`)
   REFERENCES ordine(id)
   ON DELETE CASCADE
   ON UPDATE CASCADE,
  FOREIGN KEY (`nome_articolo`)
   REFERENCES articolo(nome)
   ON DELETE CASCADE
   ON UPDATE CASCADE,
  FOREIGN KEY (`id_serra`)
   REFERENCES serra(id)
   ON DELETE CASCADE
   ON UPDATE CASCADE
) TYPE=InnoDB;

CREATE TABLE `amministratore` (
 `password` varchar(255) NOT NULL UNIQUE
) TYPE = MyISAM COMMENT = 'password di amministratore';

# SETTAGGIO DELLA PASSWORD DI AMMINISTRATORE
INSERT INTO `amministratore` VALUES (PASSWORD("admin"));
