using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Unstemmer
{
    class Program
    {
        static void Main(string[] args)
        {
            string pathDir = "C:\\Users\\Daphne\\Desktop\\MR\\";
            string test = pathDir + "mxm_dataset_test.txt";
            string train = pathDir + "mxm_dataset_train.txt";
            string mapping = pathDir + "mxm_reverse_mapping.txt";
            string testNew = pathDir + "mxm_dataset_testUnstemmed.txt";
            string trainNew = pathDir + "mxm_dataset_trainUnstemmed.txt";

            Stem(test, testNew, mapping);
            Stem(train, trainNew, mapping);

            string testEmo = pathDir + "mxm_dataset_testOnlyEmo.txt";
            string trainEmo = pathDir + "mxm_dataset_trainOnlyEmo.txt";
            string emoWords = pathDir + "anewWords.txt";

            RemoveNonEmotional(testNew, testEmo, emoWords);
            RemoveNonEmotional(trainNew, trainEmo, emoWords);
        }

        static void Stem(string pathFrom, string pathTo, string pathMapping)
        {
            // Load the mapping information into two arrays (stemmed and unstemmed)
            StreamReader mapr = new StreamReader(pathMapping);
            string[] stemmed = new string[5000];
            string[] unstemmed = new string[5000];
            for (int i = 0; i < 5000; i++)
            {
                string rule = mapr.ReadLine();
                string[] ruleParts = rule.Split(new string[] { "<SEP>" }, StringSplitOptions.None);
                stemmed[i] = ruleParts[0];
                unstemmed[i] = ruleParts[1];
            }
            mapr.Close();

            // Replace the stemmed words in the rule starting with % by their unstemmed versions
            StreamReader testr = new StreamReader(pathFrom);
            StreamWriter testw = new StreamWriter(pathTo);
            string line;
            while ((line = testr.ReadLine()) != null)
            {
                if (line[0] == '%') // This is the line with words
                {
                    StringBuilder sb = new StringBuilder();
                    sb.Append('%');
                    string[] words = (line.Substring(1)).Split(',');
                    int nrWords = words.Length;
                    for (int i = 0; i < nrWords; i++)
                    {
                        bool added = false;
                        for (int j = 0; j < 5000 && !added; j++)
                            if (stemmed[j] == words[i])
                            {
                                sb.Append(unstemmed[j]);
                                added = true;
                            }
                        if (!added)
                            sb.Append(words[i]);
                        if (i < nrWords)
                            sb.Append(',');
                    }
                    testw.WriteLine(sb.ToString());
                }
                else
                    testw.WriteLine(line);
            }
            testr.Close();
            testw.Close();
        }

        static void RemoveNonEmotional(string pathFrom, string pathTo, string pathEmoWords)
        {
            // Read emotional words from file 
            StreamReader emoR = new StreamReader(pathEmoWords);
            List<string> emoWords = new List<string>();
            string w;
            while ((w = emoR.ReadLine()) != null)
                emoWords.Add(w);

            List<int> oldKeys = new List<int>();
            string[] words = new string[1]; // words needs an initial value...

            StreamReader testr = new StreamReader(pathFrom);
            StreamWriter testw = new StreamWriter(pathTo);

            string line;
            while ((line = testr.ReadLine()) != null)
            {
                if (line[0] == '%') // The rule with word names (the first interesting one)
                {
                    words = (line.Substring(1)).Split(',');
                    int nrWords = words.Length;
                    for (int i = 0; i < nrWords; i++)
                        if (emoWords.Contains(words[i]))
                            // The word that had key (i + 1) is an interesting (emotional) word
                            oldKeys.Add(i + 1);
                }
                else if (line[0] != '#') // Rule corresponding to a lyric
                {
                    string[] parts = line.Split(',');
                    StringBuilder sb = new StringBuilder();

                    // The MillionSongDatabase ID (ignore the musiXmatch ID)
                    sb.Append(parts[0] + ',');

                    // Append the word counts: the new emotional word keys and the frequency
                    bool anyEmoWords = false;
                    for (int i = 2; i < parts.Length; i++)
                    {
                        string[] tup = parts[i].Split(':');
                        int oldKey = int.Parse(tup[0]);
                        int count = int.Parse(tup[1]);

                        if (oldKeys.Contains(oldKey))
                        {
                            anyEmoWords = true;
                            int j = oldKeys.IndexOf(oldKey);
                            sb.Append(words[j]);
                            sb.Append(':');
                            sb.Append(count);
                            sb.Append(',');
                        }
                    }

                    // Remove the last comma and write to file
                    if (anyEmoWords)
                    {
                        string l = sb.ToString();
                        testw.WriteLine(l.Substring(0, l.Length - 1));
                    }
                }
            }

            testw.Close();
            testr.Close();
        }
    }
}
