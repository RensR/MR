# Some useful commands
# h5read(filepath, workgroupname)
# h5ls(path to file) - returns 'workgroup' names
#cnames <-  c("TrackID","ArtistID","SongTitle","Loudness","Tempo","Mode","ModeConf","Key","KeyConf","Valence","Energy")

dataread <- function(FILEPATH,limit=2,wait=2,api_KEY){
  # Peforms feature extraction of h5 files, echo nest, and writes the output a csv file.
  #
  # Dependancies:
  #  library(rhdf5)
  #  library(httr)
  #
  # Args:
  #  FILEPATH
  #  limit
  #  wait
  #  apit_KEY
  #
  # Return:
  #  
  #
  # Side effects:
  #   Output file
  #

  echo_URL <- "http://developer.echonest.com/api/v4/song/search?"

  # Parameter for h5read. See h5ls(path to file) for list of group
  group <- "/"
  
  fileList <- list.files(recursive = TRUE,path = FILEPATH, full.names = TRUE, pattern = "*.h5")[1:100]
  rawdata <- lapply(fileList, function(x) h5read(x,group))
  
  # Progress bar
  total <- length(fileList)
  pb <- txtProgressBar(min = 0, max = total, style = 3)
  
  counter <- 1
  for(s in rawdata){
    # Unwrap million song data
    segments <- rawdata$analysis
    
    analysis <- s$analysis$songs
    meta <- s$metadata$songs
    song_id <- meta$song_id
    artist_id <- meta$artist_id
    
    # Proccess EchoNest request
    request <- paste(echo_URL,"api_key=",api_KEY,"&format=json","&results=1&artist_id=",artist_id,"&title=",meta$title,"&bucket=id:7digital-US&bucket=audio_summary&bucket=tracks",sep="")
    request <- URLencode(request)
    r <- GET(request)
    
    # Check is request is not empty
    val <- content(r,"parsed")
    if(length(val$response$songs) !=0){
      
      valence <- val$response$songs[[1]]$audio_summary$valence
      energy <-val$response$songs[[1]]$audio_summary$energy
      
      song <- c(meta$song_id, artist_id,meta$title,analysis$loudness,analysis$tempo,analysis$mode,analysis$mode_confidence,analysis$key,analysis$key_confidence,valence,energy)
      m <- matrix(ncol = 11,nrow = 1)
      m <- data.frame(m)
      m <- rbind(m,song)
      
      # Append to file
      write.table(m[-1,], file = "MusicData.csv",sep=",",append = T,row.names=F, col.names=F)

      # Update progress bars
      setTxtProgressBar(pb, counter)
      counter <- counter+1
      
      # Wait in case limit is close to be filled.
      remaining_limit <- as.integer(headers(r)$'x-ratelimit-remaining')
      
      if(remaining_limit < limit) {
        
        # Wait for a bit...
        Sys.sleep(wait*30)
      } 
      
    } else {
      ; # Do nothing
    }
  }
  close(pb)
}
