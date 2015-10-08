### Dataset

The data is retrieved from the musiXmatch dataset and preprocessed in C#. 
There are two files (test and train) with the same format. Each row corresponds to a song. The columns are comma-separated. The first cell of each row contains the MSD-ID of the song; the next cells contain wordcounts. These consist of the word (string) and the frequency of this word in the lyrics of the song (int), separated by a semicolon.
Words that are not in the ANEW-dataset are left out. Lyrics without any words in the ANEW-dataset are also not included.