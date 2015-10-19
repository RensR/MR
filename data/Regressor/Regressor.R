library(e1071)

# Read file
MusicData <- read.csv("~/GitHub/MR/data/Regressor/songsConf.csv", header=FALSE)
names(MusicData) <- c("TrackID","ArtistID","SongTitle","Loudness","Tempo","Mode","Key","MeanTimbreSeg1","MeanTimbreSeg2","MeanTimbreSeg3","MeanTimbreSeg4","MeanTimbreSeg5","MeanTimbreSeg6","MeanTimbreSeg7","MeanTimbreSeg8","MeanTimbreSeg9","MeanTimbreSeg10","MeanTimbreSeg11","MeanTimbreSeg12","SDTimbreSeg1","SDTimbreSeg2","SDTimbreSeg3","SDTimbreSeg4","SDTimbreSeg5", "SDTimbreSeg6","SDTimbreSeg7","SDTimbreSeg8","SDTimbreSeg9","SDTimbreSeg10","SDTimbreSeg11","SDTimbreSeg12","Valence","Energy")

# Split test and training set
set.seed(1)
train_ind <- sample(seq_len(nrow(MusicData)), size = 2000)
train <- MusicData[train_ind, ]
test <- MusicData[-train_ind, ]

# R squared evaluation measure
rsq <- function(observed, predicted) {
  (cov(observed, predicted) ^ 2) / (var(observed) * var(predicted))
}

getResults <- function(valenceRegressor, energyRegressor) {
  c(
    rsq(predict(valenceRegressor, train), train$Valence),
    rsq(predict(valenceRegressor, test), test$Valence),
    rsq(predict(energyRegressor, train), train$Energy),
    rsq(predict(energyRegressor, test), test$Energy))
}

resultsToDataFrame <- function()
{
  dataframe <- data.frame()
  formulaV <- Valence ~ Loudness + Tempo + Mode + Key + MeanTimbreSeg1 + MeanTimbreSeg2 + MeanTimbreSeg3 + MeanTimbreSeg4 + MeanTimbreSeg5 + MeanTimbreSeg6 + MeanTimbreSeg7 + MeanTimbreSeg8 + MeanTimbreSeg9 + MeanTimbreSeg10 + MeanTimbreSeg11 + MeanTimbreSeg12 + SDTimbreSeg1 + SDTimbreSeg2 + SDTimbreSeg3 + SDTimbreSeg4 + SDTimbreSeg5 +  SDTimbreSeg6 + SDTimbreSeg7 + SDTimbreSeg8 + SDTimbreSeg9 + SDTimbreSeg10 + SDTimbreSeg11 + SDTimbreSeg12
  formulaE <- Energy ~ Loudness + Tempo + Mode + Key + MeanTimbreSeg1 + MeanTimbreSeg2 + MeanTimbreSeg3 + MeanTimbreSeg4 + MeanTimbreSeg5 + MeanTimbreSeg6 + MeanTimbreSeg7 + MeanTimbreSeg8 + MeanTimbreSeg9 + MeanTimbreSeg10 + MeanTimbreSeg11 + MeanTimbreSeg12 + SDTimbreSeg1 + SDTimbreSeg2 + SDTimbreSeg3 + SDTimbreSeg4 + SDTimbreSeg5 +  SDTimbreSeg6 + SDTimbreSeg7 + SDTimbreSeg8 + SDTimbreSeg9 + SDTimbreSeg10 + SDTimbreSeg11 + SDTimbreSeg12
  train.lmValence <- lm(formulaV, data=train)
  train.lmEnergy <- lm(formulaE, data=train)
  dataframe <- rbind(dataframe, getResults(train.lmValence, train.lmEnergy))
  for (k in c("linear", "polynomial", "radial")) {
    train.svmValence <- svm(formulaV, data=train, kernel=k)
    train.svmEnergy <- svm(formulaE, data=train, kernel=k)
    dataframe <- rbind(dataframe, c(getResults(train.svmValence, train.svmEnergy)))
  }
  dataframe <- cbind(c("MLR", "SVM linear", "SVM polynomial", "SVM radial"), dataframe)
  names(dataframe) <- c("Regressor", "Valence train", "Valence test", "Energy train", "Energy test")
  dataframe
}
