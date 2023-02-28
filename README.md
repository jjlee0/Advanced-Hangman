# Programming-Assignment-4
Implemented a program that uses data structures including lists, maps, and sets to create a very challenging version of Hangman.

Unlike the normal game of "Hangman," this version waits to pick the secret word for as long as possible. The program starts with a dictionary of words and as the user guesses certain characters, the computer narrows down the list of possible words. The list of possible words all have the same "pattern" as displayed throughout the playthrough.

As the user guesses, the computer creates patterns based on the letters guessed. Each pattern will have "word families" or groups of words that fit each pattern. The program then decides which pattern to reveal based on the difficulty chosen by the player. The hardest difficulty has the program pick the pattern with the most words in its word family. The program repeats this process until there is a tie, which the equals() method breaks.
