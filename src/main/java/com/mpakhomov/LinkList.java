package com.mpakhomov;

import java.util.concurrent.locks.*;  // New in Java 5.0

/**
 * A partial implementation of a linked list of values of type E.
 * It demonstrates hand-over-hand locking with Lock
 */
public class LinkList<E> {
    E value;                // The value of this node of the list
    LinkList<E> rest;       // The rest of the list
    Lock lock;              // A lock for this node
    LinkList<E> head;

    public LinkList(E value) {  // Constructor for a list
        head = this;
        this.value = value;          // Node value
        rest = null;                 // This is the only node in the list
        lock = new ReentrantLock();  // We can lock this node
    }

    /**
     * Append a node to the end of the list, traversing the list using
     * hand-over-hand locking. This method is threadsafe: multiple threads
     * may traverse different portions of the list at the same time.
     **/
    public LinkList<E> append(E value) {
        LinkList<E> node = this;  // Start at this node
        node.lock.lock();         // Lock it.

        // Loop 'till we find the last node in the list
        while(node.rest != null) {
            LinkList<E> next = node.rest;

            // This is the hand-over-hand part.  Lock the next node and then
            // unlock the current node.  We use a try/finally construct so
            // that the current node is unlocked even if the lock on the
            // next node fails with an exception.
            try { next.lock.lock(); }  // lock the next node
            finally { node.lock.unlock(); } // unlock the current node
            node = next;
        }

        // At this point, node is the final node in the list, and we have
        // a lock on it.  Use a try/finally to ensure that we unlock it.
        try {
            node.rest = new LinkList<E>(value); // Append new node
        }
        finally { node.lock.unlock(); }
        return node;
    }

    public static void main(String[] args) {
        LinkList<Integer> list = new LinkList<Integer>(1);
        list.append(2).append(3).append(4);
        for (LinkList<Integer> node = list.head;  node != null; node = node.rest) {
            System.out.print(node.value);
        }
    }
}
