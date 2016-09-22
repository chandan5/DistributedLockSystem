/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributedlock;

import distributedlock.RequestProto.RequestMsg;
import distributedlock.RequestProto.ClockMsg;
import java.util.HashMap;
import java.util.Set;
/**
 *
 * @author chandan5
 */


// Clock class contains generalised clock as a hash map
// Request can be easily obtains from Clock by calling getProtoBytes

public class Clock implements Comparable<Clock> {
    HashMap<Integer, Integer> _clock = new HashMap<Integer,Integer>();
//    SortedSet<Pair<Integer,Integer> > clock = new TreeSet<Pair<Integer,Integer> >();

    public Clock(Integer processId) {
        _clock.put(processId, 0);
    }
    
    public void mergeClock(Clock c) {
        
    }
    
    @Override
    public int compareTo(Clock c) {
        Set<Integer> cKeys = c._clock.keySet();
        Set<Integer> _clockKeys = _clock.keySet();
        cKeys.retainAll(_clockKeys);
        Integer smaller = 1;
        Integer greater = 1;
        for(Integer key: cKeys) {
            if(_clock.get(key) == c._clock.get(key)) {
                smaller = 0;
                greater = 0;
            }
            else if(_clock.get(key) < c._clock.get(key)) {
                greater = 0;
            }
            else {
                smaller = 0;
            }
        }
        if(smaller == 1)
            return -1;
        if(greater == 1)
            return 1;
        return 0;
    }
    
    public String toString() {
        String s = new String();
        s += '{';
        for (HashMap.Entry<Integer, Integer> entry : _clock.entrySet())
        {
            s += '(' + entry.getKey() + "," + entry.getValue() + "),";
        }
        if(s.length() >= 1) {
            s = s.substring(0,s.length()-1);
        }
        s += '}';
        return s;
    }
    
    byte[] getProtoBytes(Integer processId) {
        RequestMsg.Builder request = RequestMsg.newBuilder();
        request.setPid(processId);
        
        for (HashMap.Entry<Integer, Integer> entry : _clock.entrySet()) {
            ClockMsg.Builder clockmsg = ClockMsg.newBuilder();
            clockmsg.setId(entry.getKey());
            clockmsg.setVal(entry.getValue());
            request.addClocks(clockmsg);
        }
        RequestMsg r = request.build();
        return r.toByteArray();
    }
}
