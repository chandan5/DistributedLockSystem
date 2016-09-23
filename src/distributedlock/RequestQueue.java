/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package distributedlock;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
//import distributedlock.Pair;
//import distributedlock.RequestProto.RequestMsg;
//import distributedlock.RequestProto.ClockMsg;
//import java.util.Iterator;
//import distributedlock.Clock;
/**
 *
 * @author chandan5
 */


//public class Pair<T extends Comparable<T>, E extends Comparable<E>> implements Comparable<Pair<T, E>> {
//    private E e;
//    private T t;
//
//    public int compareTo(Pair<T, E> pair) {
//        int result = t.compareTo(pair.t);
//        return (result == 0) ? e.compareTo(pair.e) : result;
//    }
//}

// RequestQueue is maintained using a SortedSet

public class RequestQueue {
    SortedSet<Pair<Clock,Integer> > queue = new TreeSet<Pair<Clock,Integer> >();
    
    public RequestQueue() {//(Clock c, Integer processId) {
//        Pair<Clock,Integer> p = new Pair<Clock,Integer>(c,processId);
//        queue.add(p);
    }
    
    // We queue inferior priority lock requests
    public void addRequest(Clock c, Integer processId) {
        Pair<Clock,Integer> p = new Pair<Clock,Integer>(c,processId);
        queue.add(p);
    }
    
    public Pair<Clock,Integer> getFirst() {
        return queue.first();
    }
    
    public Integer deleteRequest(Clock c, Integer processId) {
//        Pair p = new Pair(c,processId);
        Pair<Clock,Integer> p = new Pair<Clock,Integer>(c,processId);
        if(queue.contains(p)) {
            queue.remove(p);
            return 1;
        }
        else
            return 0;
    }
    
    public String toString() {
        Iterator it = queue.iterator();
        String s = new String();
       
        while (it.hasNext()) {
            Pair<Clock,Integer> p = (Pair<Clock,Integer>) it.next();
            s += p.left.toString();
            s += " -> ";
            s += p.right;
            s += " ::: ";
        }
        return s;
    }
}
