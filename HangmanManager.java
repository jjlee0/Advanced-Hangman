/*  Student information for assignment:
 *
 *  On my honor, Joseph, this programming assignment is my own work
 *  and I have not provided this code to any other student.
 *
 *  Name: Joseph Lee
 */

// add imports as necessary

import java.util.*;

/**
 * Manages the details of EvilHangman. This class keeps
 * tracks of the possible words from a dictionary during
 * rounds of hangman, based on guesses so far.
 *
 */
public class HangmanManager {

    public static final int MEDIUM_FACTOR = 4;
    public static final int EASY_FACTOR = 2;

    // instance variables / fields
    private boolean debugOn;
    private ArrayList<String> dictionary;
    private TreeSet<Character> guessesMade;
    // words that equal wordLen
    private ArrayList<String> currWordList;
    private HangmanDifficulty difficulty;
    private StringBuilder pattern;
    private int guessesLeft;

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     * @param debugOn true if we should print out debugging to System.out.
     */
    public HangmanManager(Set<String> words, boolean debugOn) {
        // check precondition
        if (words == null || words.size() == 0) {
            throw new IllegalArgumentException("words must not be null or words.size()" +
                    "must be larger than 0");
        }
        dictionary = new ArrayList<>();
        // make a deep copy of the given set of words
        dictionary.addAll(words);
        this.debugOn = debugOn;
    }

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * Debugging is off.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     */
    public HangmanManager(Set<String> words) {
        this(words, false);
    }

    /**
     * Get the number of words in this HangmanManager of the given length.
     * pre: none
     * @param length The given length to check.
     * @return the number of words in the original Dictionary
     * with the given length
     */
    public int numWords(int length) {
        int count = 0;
        for (String curr : dictionary) {
            if (curr.length() == length) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get for a new round of Hangman. Think of a round as a
     * complete game of Hangman.
     * @param wordLen the length of the word to pick this time.
     * numWords(wordLen) > 0
     * @param numGuesses the number of wrong guesses before the
     * player loses the round. numGuesses >= 1
     * @param diff The difficulty for this round.
     */
    public void prepForRound(int wordLen, int numGuesses, HangmanDifficulty diff) {
        // check precondition
        if (numWords(wordLen) == 0 || numGuesses < 1) {
            throw new IllegalArgumentException("There were not enough words of" +
                    " given wordLen or numGuesses must be >= 1");
        }
        guessesLeft = numGuesses;
        difficulty = diff;
        guessesMade = new TreeSet<>();
        currWordList = new ArrayList<>();
        // go through dictionary and add all possible words to the current word list
        for (String word : dictionary) {
            if (word.length() == wordLen) {
                currWordList.add(word);
            }
        }
        // make a pattern based on given word length
        pattern = new StringBuilder();
        for (int i = 0; i < wordLen; i++) {
            pattern.append("-");
        }
    }

    /**
     * The number of words still possible (live) based on the guesses so far.
     *  Guesses will eliminate possible words.
     * @return the number of words that are still possibilities based on the
     * original dictionary and the guesses so far.
     */
    public int numWordsCurrent() {
        return currWordList.size();
    }

    /**
     * Get the number of wrong guesses the user has left in
     * this round (game) of Hangman.
     * @return the number of wrong guesses the user has left
     * in this round (game) of Hangman.
     */
    public int getGuessesLeft() {
        return guessesLeft;
    }

    /**
     * Return a String that contains the letters the user has guessed
     * so far during this round.
     * The characters in the String are in alphabetical order.
     * The String is in the form [let1, let2, let3, ... letN].
     * For example [a, c, e, s, t, z]
     * @return a String that contains the letters the user
     * has guessed so far during this round.
     */
    public String getGuessesMade() {
        return guessesMade.toString();
    }

    /**
     * Check the status of a character.
     * @param guess The characater to check.
     * @return true if guess has been used or guessed this round of Hangman,
     * false otherwise.
     */
    public boolean alreadyGuessed(char guess) {
        return guessesMade.contains(guess);
    }

    /**
     * Get the current pattern. The pattern contains '-''s for
     * unrevealed (or guessed) characters and the actual character 
     * for "correctly guessed" characters.
     * @return the current pattern.
     */
    public String getPattern() {
        return pattern.toString();
    }

    /**
     * Update the game status (pattern, wrong guesses, word list),
     * based on the give guess.
     * @param guess pre: !alreadyGuessed(guess), the current guessed character
     * @return return a tree map with the resulting patterns and the number of
     * words in each of the new patterns.
     * The return value is for testing and debugging purposes.
     */
    public TreeMap<String, Integer> makeGuess(char guess) {
        // check precondition
        if (alreadyGuessed(guess)) {
            throw new IllegalStateException("This char has already been guessed");
        }
        // update letters guessed
        guessesMade.add(guess);
        Map<String, ArrayList<String>> families = createFam(guess);
        // check if pattern changed
        String prevPattern = pattern.toString();
        String currPattern = getNewPattern(families);
        if (prevPattern.equals(currPattern)) {
            guessesLeft--;
        } else {
            pattern = new StringBuilder(currPattern);
        }
        // update current list of words based on pattern
        currWordList = families.get(pattern.toString());
        // map of patterns and num words (for testing purposes)
        TreeMap<String, Integer> result = new TreeMap<>();
        for (String curr : families.keySet()) {
            result.put(curr, families.get(curr).size());
        }
        return result;
    }

    // returns map of word families and their associated elements
    private Map<String, ArrayList<String>> createFam(char guess) {
        // map of patterns and list of words
        Map<String, ArrayList<String>> families = new HashMap<>();
        // update active pattern
        for (String word : currWordList) {
            StringBuilder tempPattern = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess) {
                    tempPattern.append(guess);
                } else {
                    tempPattern.append(pattern.charAt(i));
                }
            }
            // check if map contains pattern
            String tempPatternString = tempPattern.toString();
            if (families.containsKey(tempPatternString)) {
                families.get(tempPatternString).add(word);
            } else {
                ArrayList<String> temp = new ArrayList<>();
                temp.add(word);
                families.put(tempPatternString, temp);
            }
        }
        return families;
    }

    // returns a String of the new pattern based on given difficulty and length of
    // new word family
    private String getNewPattern(Map<String, ArrayList<String>> families) {
        ArrayList<Pattern> list = new ArrayList<>();
        // create Pattern objects for each key
        for (String curr : families.keySet()) {
            Pattern temp = new Pattern(curr, families.get(curr));
            list.add(temp);
        }
        // compare each object, hardest should be first element
        Collections.sort(list);
        if (list.size() > 1) {
            if (difficulty == HangmanDifficulty.EASY && guessesMade.size()
                    % EASY_FACTOR == 0) {
                // second-hardest pattern should be the second element in the list
                return list.get(1).getPattern();
            } else if (difficulty == HangmanDifficulty.MEDIUM && guessesMade.size()
                    % MEDIUM_FACTOR == 0) {
                return list.get(1).getPattern();
            } else {
                // hard difficulty
                return list.get(0).getPattern();
            }
        }
        // only one element in the list of patterns
        return list.get(0).getPattern();
    }

    // object to help compare and sort Patterns
    private class Pattern implements Comparable<Pattern> {

        private String pattern;
        // list of elements in the pattern's family
        private ArrayList<String> elem;

        // create Pattern which contains the pattern itself and
        // the elements associated in an ArrayList<String>
        public Pattern(String pattern, ArrayList<String> elem) {
            this.pattern = pattern;
            this.elem = elem;
        }

        // return a String representing the current pattern
        public String getPattern() {
            return pattern;
        }

        // returns an int representing the number of chars revealed in the pattern
        public int numCharsRev() {
            int count = 0;
            for (int i = 0; i < pattern.length(); i++) {
                if (pattern.charAt(i) != '-') {
                    count++;
                }
            }
            return count;
        }

        // pick the word based on difficulty
        // prioritize in following order:
        // 1. family with most elements
        // 2. reveals the fewest characters
        // 3. lexicographical order
        public int compareTo(Pattern other) {
            // compare number of elem
            if (this.elem.size() - other.elem.size() == 0) {
                // compare number of unique chars
                if (this.numCharsRev() - other.numCharsRev() == 0) {
                    // compare lexicographically
                    return this.pattern.compareTo(other.pattern);
                } else {
                    return this.numCharsRev() - other.numCharsRev();
                }
            } else {
                return other.elem.size() - this.elem.size();
            }
        }
    }

    /**
     * Return the secret word this HangmanManager finally ended up
     * picking for this round.
     * If there are multiple possible words left one is selected at random.
     * <br> pre: numWordsCurrent() > 0
     * @return return the secret word the manager picked.
     */
    public String getSecretWord() {
        // check precondition
        if (numWordsCurrent() == 0) {
            throw new IllegalStateException("There are no words left in the" +
                    "current word list.");
        }
        if (numWordsCurrent() == 1) {
            return currWordList.get(0);
        } else {
            Random r = new Random();
            int index = r.nextInt(numWordsCurrent());
            return currWordList.get(index);
        }
    }
}
