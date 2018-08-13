package net.sargue.mailgun;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A custom implementation of a multi valued map specifically designed for parameter values.
 * <p>
 * Design notes:
 * <ul>
 * <li>Most parameters have a single value.
 * <li>Parameter names and values are always Strings
 * <li>The order of the parameter names is not important.
 * <li>The order of the values <strong>is</strong> important.
 * </ul>
 * <p>
 * Nulls are not allowed either for keys or values.
 */
public class ParameterMap {
    private Map<String, List<String>> store = new HashMap<>();

    /**
     * Set the key's value to be a one item list consisting of the supplied value.
     * Any existing values will be replaced.
     *
     * @param key   the key
     * @param value the single value of the key
     */
    public void putSingle(String key, String value) {
        Objects.requireNonNull(value, "The value can't be null.");
        List<String> values = getValues(key);
        values.clear();
        values.add(value);
    }

    /**
     * Adds a new value to an optionally existing key.
     * <p>
     * If the key had one or more values associated previously to this call this value is
     * added to the end of the list of the key's values.
     * <p>
     * If the key had no previous association a new single value association is created.
     *
     * @param key   the key
     * @param value a new value to associate to the key
     */
    public void add(String key, String value) {
        Objects.requireNonNull(value, "The value can't be null.");
        List<String> values = getValues(key);
        values.add(value);
    }

    /**
     *  Adds multiple values to an optionally existing key.
     *  <p>
     *  If the key had one or more values associated previously to this call these values are
     *  added to the end of the list of the key's values.
     *
     * @param key    the key
     * @param values the new values to add to the key
     */
    public void addAll(String key, List<String> values) {
        getValues(key).addAll(values);
    }

    /**
     * Removes all values associated with the key, rendering the value list associated with
     * the key empty.
     *
     * @param key the key
     */
    public void remove(String key) {
        List<String> values = getValues(key);
        values.clear();
    }

    /**
     * A shortcut to get the first value of the supplied key.
     *
     * @param key the key
     * @return the first value for the specified key if it exists
     */
    public Optional<String> getFirst(String key) {
        List<String> values = getValues(key);
        if (values.isEmpty())
            return Optional.empty();
        else
            return Optional.of(values.get(0));
    }

    /**
     * Return a non-null list of values for the given key.
     * <p>
     * If the key is not present on the map a new empty list is created, associated with the
     * key and returned.
     *
     * @param key the key
     * @return value list associated with the key, always a non null list
     */
    public List<String> getValues(String key) {
        Objects.requireNonNull(key, "The key can't be null.");
        return store.computeIfAbsent(key, x -> new LinkedList<>());
    }

    /**
     * Return the set of keys that have a non empty list associated.
     *
     * @return the set of keys that have a non empty list associated
     */
    public Set<String> keySet() {
        return store.keySet()
                    .stream()
                    .filter(key -> !store.get(key).isEmpty())
                    .collect(Collectors.toSet());
    }

    /**
     * Checks if a key has a non empty list of values.
     *
     * @param key the key
     * @return <tt>true</tt> if the map has a non empty list of values
     */
    public boolean containsKey(String key) {
        return store.containsKey(key) && !store.get(key).isEmpty();
    }
}
