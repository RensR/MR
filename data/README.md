### Dataset

The data is retreived from the MillionSong Dataset and pre-processed in R.

#### Features

The features present are the following; 

- TrackID
- ArtistID
- SongTitle 
- Loudness
- Tempo 
- Mode
- ModeConf
- Key
- KeyConf
- Valence 
- Energy (Arousal)


#### External Links

- [Million Song Subset](http://labrosa.ee.columbia.edu/millionsong/pages/getting-dataset#subset)
- [Music X Match - Lyrics](http://labrosa.ee.columbia.edu/millionsong/musixmatch#getting)
- [EchoNest Attributes](http://developer.echonest.com/acoustic-attributes.html)


### How to create and fill the database in two query's
"""
CREATE TABLE `magicalmusic`.`music` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `artist` VARCHAR(45) NOT NULL COMMENT '',
  `song` VARCHAR(45) NOT NULL COMMENT '',
  `valence` DOUBLE NOT NULL COMMENT '',
  `arousal` DOUBLE NOT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '');
"""
"""
LOAD DATA LOCAL INFILE 'PATH TO CSV' INTO TABLE music FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
"""