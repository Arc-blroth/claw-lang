package ai.arcblroth.claw.util;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Cursed ArrayList that also has queue methods.
 * All queue methods push and pop from the end of the array.
 * Not thread safe.
 */
public class ArrayListQueue<T> extends ArrayList<T> implements Queue<T> {

    @Override
    public boolean offer(T t) {
        // Despite the documentation on Queue#offer, both
        // ArrayDeque and LinkedList will always add the
        // element even if the queue is at capacity
        return add(t);
    }

    @Override
    public T remove() {
        var size = this.size();
        if (size == 0) {
            throw new NoSuchElementException();
        } else {
            return remove(size - 1);
        }
    }

    @Override
    public T poll() {
        var size = this.size();
        if (size == 0) {
            return null;
        } else {
            return remove(size - 1);
        }
    }

    @Override
    public T element() {
        var size = this.size();
        if (size == 0) {
            throw new NoSuchElementException();
        } else {
            return get(size - 1);
        }
    }

    @Override
    public T peek() {
        var size = this.size();
        if (size == 0) {
            return null;
        } else {
            return get(size - 1);
        }
    }

    /**
     * Alias for {@link #element()}.
     * Retrieves, but does not remove, the head of this queue.
     *
     * @return the head of this queue
     * @throws NoSuchElementException if this queue is empty
     * @see #element()
     */
    public T last() {
        return element();
    }

}
