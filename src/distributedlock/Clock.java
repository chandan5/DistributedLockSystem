/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//ackage distributedlock;

//import distributedlock.RequestProto.RequestMsg;
//import distributedlock.RequestProto.ClockMsg;
//import RequestProto.RequestMsg;
//import RequestProto.ClockMsg;

import com.google.protobuf.InvalidProtocolBufferException;
import static java.lang.Integer.max;
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
        System.out.println(processId);
        _clock.put(processId, 0);
    }
    
    public Clock() {
        
    }
    
    public void mergeClock(Clock c) {
        Set<Integer> cKeys = c._clock.keySet();
        for(Integer key: cKeys) {
            if(_clock.containsKey(key)) {
                this.add(key,max(_clock.get(key), c._clock.get(key)));
            }
            else
                this.add(key, c._clock.get(key));
        }
    }
    
    public void add(Integer processId, Integer clockVal) {
        _clock.put(processId, clockVal);
    }
    
    @Override
    public int compareTo(Clock c) {
        Set<Integer> cKeys = c._clock.keySet();
        Set<Integer> _clockKeys = _clock.keySet();
        cKeys.retainAll(_clockKeys);
        Integer less = 0;
        Integer more = 0;
        for(Integer key: cKeys) {
            if(_clock.get(key) > c._clock.get(key)) {
                more++;
            }
            else if(_clock.get(key) < c._clock.get(key)) {
                less++;
            }
        }
        if(more == 0 && less != 0)
            return -1;
        if(more != 0 && less == 0)
            return 1;
        return 0;
    }
    
    public String toString() {
        String s = new String();
        s += "{";
        for (HashMap.Entry<Integer, Integer> entry : _clock.entrySet())
        {
            s += "(" + entry.getKey() + "," + entry.getValue() + "),";
        }
        if(s.length() > 1) {
            s = s.substring(0,s.length()-1);
        }
        s += "}";
        return s;
    }
    
    byte[] getRequestProtoBytes(Integer processId, Integer withClock) {
        RequestProto.Request.Builder request = RequestProto.Request.newBuilder();
        request.setPid(processId);
        if(withClock == 1) {
            for (HashMap.Entry<Integer, Integer> entry : _clock.entrySet()) {
                RequestProto.ClockMsg.Builder clockmsg = RequestProto.ClockMsg.newBuilder();
                clockmsg.setId(entry.getKey());
                clockmsg.setVal(entry.getValue());
                request.addClocks(clockmsg);
            }
        }
        RequestProto.Request r = request.build();
        return r.toByteArray();
    }
    
    static Pair<Clock,Integer> getClockPidPair(byte [] r) throws InvalidProtocolBufferException {
        RequestProto.Request request = RequestProto.Request.parseFrom(r);
        Integer processId = request.getPid();
        Clock _c = new Clock();
        for(RequestProto.ClockMsg c : request.getClocksList()) {
            _c.add(c.getId(), c.getVal());
        }
        Pair<Clock,Integer> p = new Pair<Clock,Integer>(_c,processId);
        return p;
    }
}
