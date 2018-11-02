package com.ali.jar2proxy.generic.model;

import com.ali.jar2proxy.generic.enums.TOPIC_TYPE;

import java.io.Serializable;

/**
 * @author coolme200
 */
public class Pair<K, V> implements Serializable {

    private static final long serialVersionUID = 7038358316963820952L;

    /**
     * first object
     */
    private K                 first;

    /**
     * second object
     */
    private V                 second;

    public Pair() {
        super();
    }

    public Pair(K first, V second) {
        super();
        this.first = first;
        this.second = second;
    }


    public K getFirst() {
        return first;
    }

    public void setFirst(K first) {
        this.first = first;
    }

    public V getSecond() {
        return second;
    }

    public void setSecond(V second) {
        this.second = second;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Pair)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        Pair<?, ?> that = (Pair<?, ?>) obj;
        return that.getFirst().equals(this.getFirst()) && that.getSecond().equals(this.getSecond());
    };

    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }

    @Override
    public String toString() {
        return "Pair [first=" + first + ", second=" + second + "]";
    }

}
