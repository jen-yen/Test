package hProjekt.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * The NameGenerator class generates new names based on a given list of names
 * using a markov chain.
 * The order of the markov chain can be specified.
 *
 * A list of german town names is included in the resources folder.
 * The list was taken from the "Gemeindeverzeichnis Deutschland" from the
 * "Statistisches Bundesamt"
 * (https://www.destatis.de/DE/Themen/Laender-Regionen/Regionales/Gemeindeverzeichnis/_inhalt.html)
 * version from the 30.09.2024.
 *
 */
public class NameGenerator {
    public final Map<String, SortedMap<Character, Integer>> ngramCollection = new HashMap<>();
    private final int order;
    private final Random random;

    /**
     * Creates a new NameGenerator trained on the given names with the specified
     * order.
     * Uses the given random instance.
     *
     * @param names  the names to train on
     * @param order  the order of the markov chain
     * @param random the random instance to use
     */
    public NameGenerator(String[] names, int order, Random random) {
        this.random = random;
        this.order = order;
        train(names, order);
    }

    /**
     * Creates a new NameGenerator trained on the given names with the specified
     * order.
     * Uses a new random instance.
     *
     * {@link NameGenerator#NameGenerator(String[], int, Random)} for more
     * information.
     *
     * @param names the names to train on
     * @param order the order of the markov chain
     */
    public NameGenerator(String[] names, int order) {
        this(names, order, new Random());
    }

    /**
     * Trains the markov chain on the given names with the specified order.
     *
     * @param names the names to train on
     * @param order the order of the markov chain
     */
    private void train(String[] names, int order) {
        for (String name : names) {
            name = "^" + name + "$";
            for (int i = order; i < name.length(); i++) {
                String ngram = name.substring(i - order, i);
                char nextChar = name.charAt(i);

                ngramCollection.putIfAbsent(ngram, new TreeMap<>());

                ngramCollection.get(ngram).put(nextChar,
                        ngramCollection.get(ngram).getOrDefault(nextChar, 0) + 1);
            }
        }
    }

    /**
     * Returns a weighted random choice based on the given characters and weights.
     *
     * @param characters the characters to choose from
     * @param weights    the weights of the characters
     * @return the chosen character
     */
    private char weightedRandomChoice(List<Character> characters, List<Integer> weights) {
        int totalWeight = weights.stream().mapToInt(Integer::intValue).sum();
        int randomIndex = random.nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (int i = 0; i < characters.size(); i++) {
            cumulativeWeight += weights.get(i);
            if (randomIndex < cumulativeWeight) {
                return characters.get(i);
            }
        }

        return characters.get(characters.size() - 1);
    }

    /**
     * Generates a new name with at most the given length.
     *
     * @param length the maximum length of the name
     * @return the generated name
     */
    public String generateName(int length) {
        String result = ((Function<List<String>, String>) (ngrams) -> {
            return ngrams.stream().skip(random.nextInt(ngrams.size())).findFirst().get();
        }).apply(ngramCollection.keySet().stream().filter(ngram -> ngram.startsWith("^")).toList());

        for (int i = 0; i < length; i++) {
            String ngram = result.substring(result.length() - order);

            if (!ngramCollection.containsKey(ngram)) {
                break;
            }

            Character nextChar = weightedRandomChoice(ngramCollection.get(ngram).keySet().stream().toList(),
                    ngramCollection.get(ngram).values().stream().toList());

            if (nextChar == '$') {
                break;
            }

            result += nextChar;
        }
        return result.substring(1, 2).toUpperCase() + result.substring(2);
    }
}
