package worms.parser;

import be.kuleuven.cs.som.annotate.Value;

@Value
public class KeyValuePair<K, V>
{
    public KeyValuePair(K key, V value)
    {
        this.key = key;
        this.value = value;
    }

    private final K key;
    
    public K getKey()
    {
        return this.key;
    }

    private final V value;
    
    public V getValue()
    {
        return this.value;
    }
}

