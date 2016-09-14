/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributedlock;

import java.util.SortedSet;
import java.util.TreeSet;
import distributedlock.RequestProto.Request;
import distributedlock.RequestProto.ClockMsg;
import java.util.Iterator;
import distributedlock.Pair;
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

public class Clock {
    SortedSet<Pair<Integer,Integer> > clock = new TreeSet<Pair<Integer,Integer> >();
    Integer pid;

    public Clock(Integer processId) {
        pid = processId;
        Integer x = 0;
        Pair p = new Pair(x,processId);
        clock.add(p);
    }
    
    byte[] getProtoBytes() {
        Request.Builder request = Request.newBuilder();
        request.setPid(pid);
        Iterator it = clock.iterator();
        while (it.hasNext()) {
            Pair<Integer,Integer> element = (Pair<Integer,Integer>) it.next();
            ClockMsg.Builder clockmsg = ClockMsg.newBuilder();
            clockmsg.setId(element.right);
            clockmsg.setVal(element.left);
            request.addClocks(clockmsg);
        }
        Request r = request.build();
        return r.toByteArray();
    }
}
