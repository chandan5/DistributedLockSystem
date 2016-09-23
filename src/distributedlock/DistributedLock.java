/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package distributedlock;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*; 
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chandan5
 */


public class DistributedLock extends UnicastRemoteObject implements DistributedLockInterface {
//    Clock clock = new Clock();
    static DistributedLockInterface[] dls;
    static DistributedLock obj = null;
    public static Clock clock = null;
    static Integer n = -1;
    static Integer pid = -1;
    static RequestQueue requestQueue;
    public static Pair<Clock,Integer> myLockRequest = null;
    static String outputFileName;
    static Integer counter = 0;
    static Integer lockStatus = 1;
    // acks store acks (Used set to avoid duplicates)
    static HashSet<Integer> acks = new HashSet<Integer>();
    
    public DistributedLock() throws RemoteException {
        super(0);    // required to avoid the 'rmic' step, see below
    }
    

    @Override
    public Integer LockRequest(byte [] r) throws RemoteException {
            //depending on lockStatus either return 1 or (queue and return 0)
            // We queue inferior priority lock request compared to our lock request
        Integer flag = -1;
        try {
            
            Pair<Clock,Integer> req = Clock.getClockPidPair(r);
            clock.mergeClock(req.left);
            Integer id = req.right;
            System.out.println("Hello i am " + pid + " .ReceiveLockRequest from " + id + " .His clock was " + req.left.toString() + ". My clock was " + myLockRequest.left.toString());
            
            if(lockStatus == 0 || lockStatus == 3) {
                // not interested; releasing lock or sleeping
                flag = 1;
            }
            else if(lockStatus == 1) {
                // interested in lock => check for condition
                if(myLockRequest.compareTo(req) < 0) { // => myLockRequest has higher priority
                    requestQueue.addRequest(req.left,id);
                    System.out.println("Added request from "  + id);
                    System.out.println(requestQueue);
                    flag = 0;
                }
                else if(myLockRequest.compareTo(req) > 0) { // => myLockRequest has lower(inferior) priority
                    flag = 1;
                }
            }
            else if(lockStatus == 2) {
                // is in critical section
                requestQueue.addRequest(req.left,id);
                System.out.println("Added  request from "  + id);
                System.out.println(requestQueue);
                flag = 0;
            }
            
        } catch (InvalidProtocolBufferException ex) {
            return -1;
        }
        
        return flag;
    }

    @Override
    public Integer UnlockRequest(byte [] r) throws RemoteException {
        
        try {
            Pair<Clock,Integer> ureq = Clock.getClockPidPair(r);
            System.out.println("Hello i am " + pid + " .ReceiveUnlockRequest from " + ureq.right);
            acks.add(ureq.right);
            // if received total n-1 acks call receivedAllAcks()
            if(acks.size() == n-1)
                receivedAllAcks();
            return 1;
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(DistributedLock.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        } catch (IOException ex) {
            Logger.getLogger(DistributedLock.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
    }
    
    private static Integer getLock() throws RemoteException {
        System.out.println(pid + " Inside getLock function");
        if(clock != null) {
            // increase counter 
            counter++;
            clock._clock.put(pid, clock._clock.get(pid) + 1);
            // Queue in local queue
            
            myLockRequest = new Pair<Clock,Integer>(clock,pid);
            
            byte[] r = clock.getRequestProtoBytes(pid, 1);
            
            requestQueue.addRequest(clock, pid);
            System.out.println("Added my request to queue");
//            System.out.println();
            // Send LockRequest to all except yourself
            
            for(Integer id=0; id<n; id++) {
                if(id != pid) {
                    System.out.println("Hello i am " + pid + " .SendingLockRequest to " + id + " .My clock was " + myLockRequest.left.toString());
                    Integer ret = dls[id].LockRequest(r);
                    if(ret == 1) {
                        System.out.println(id + " said ok, so adding it to acks.");
                        acks.add(id);
                    }
                    
                    if(ret == -1)
                    {
                        System.out.println(pid + " Something went wrong..");
                        return 0;
                    }
                }
            }
            if(acks.size() == n-1) {
                return 1;
            }
            else
                return 0;
            // if ret was 0 from any server => did not get request
        }
        return 0; // clock was null
        
    }
    
    private static void releaseLock() throws RemoteException {
        // delete your lockRequest from local queue and verify it is at the top
        if(acks.size() != n-1) {
            System.out.println("Error at " + pid + " . Not enough acks bro");
            return;
        }
//        System.out.println(requestQueue.getFirst().left);
//        System.out.println(requestQueue.getFirst().right);
//        System.out.println(myLockRequest.left);
        if(requestQueue.getFirst() != null  && requestQueue.getFirst().right == myLockRequest.right && requestQueue.getFirst().left == myLockRequest.left) {
            
//        if(requestQueue.getFirst().equals(myLockRequest)) {
//            if(requestQueue.deleteRequest(myLockRequest.left, myLockRequest.right) == 0)
            System.out.println(requestQueue.toString());
            System.out.println("Deleting my lock request");
            requestQueue.deleteRequest(myLockRequest.left, myLockRequest.right);
            System.out.println(requestQueue.toString());
        }
        else
        {
            System.out.println("Error at " + pid + " . Request is not at the top of the queue.");
            // Print Queue
            System.out.println(requestQueue.toString());
            return;
        }
        
        // send unlock request to all those who are queued (inferior priority) in queue
        byte[] r = clock.getRequestProtoBytes(pid, 0);
        Iterator it = requestQueue.queue.iterator();
        Integer flag = 1;
        while (it.hasNext()) {
            Pair<Clock,Integer> p = (Pair<Clock,Integer>) it.next();
            Integer id = p.right;
            if(id != pid) {
                System.out.println("Hello i am " + pid + " .SendingUnlockRequest to " + id + " .Released clock was " + myLockRequest.left.toString());
                Integer ret = dls[id].UnlockRequest(r);
                if(ret == 0)
                {
                    System.out.println("Something went wrong while unlocking " +id);
                    flag = 0;
                }
            }
         }
        if(flag == 1)
        {
            // remove all requests in queue
            acks.clear();
            requestQueue.queue.clear();
        }
    }
    
    private static void criticalSection() throws IOException {
        FileWriter fw = new FileWriter(outputFileName,true);
        List<String> lines = Files.readAllLines(Paths.get(outputFileName), StandardCharsets.UTF_8);
        Integer globalCounter = 0;
        Integer k = lines.size();
        
        if(k != 0) {
            String[] tokens = lines.get(k-1).split(":");
            globalCounter = Integer.parseInt(tokens[0]);
        }
        globalCounter++;
        System.out.println("globalCounter: " + globalCounter);
//        System.out.println(fw);
        fw.write(globalCounter + ":add a line\n");//appends the string to the file
        fw.close();
    }
    
    private static void receivedAllAcks() throws IOException {
        lockStatus = 2; // is in critical section => Send no
        System.out.println("lockStatus of " + pid +  " : " +lockStatus);
        criticalSection();
        lockStatus = 3; // means releasing lock => not interested in lock => send ok
        System.out.println("lockStatus of " + pid +  " : " +lockStatus);
        releaseLock();
        lockStatus = 0; // means sleeping => not interested in lock => Send ok
        System.out.println("lockStatus of " + pid +  " : " +lockStatus);
        //sleep();
        work();
    }
    
    private static void work() throws IOException {
        System.out.println(pid + " worked.. :)");
        if(counter < 100) {
            lockStatus = 1; // interested in lock
            System.out.println("lockStatus of " + pid +  " : " +lockStatus);
            
            if(getLock() == 1) {        
                receivedAllAcks();
            }
        }
        else
            return;
    }
    
    /**
     * @param args the command line arguments
     */
    //                      -i 0 -n 1 -o out1
    public static void main(String[] args) throws Exception {
        System.out.println("RMI server started");
        
        // TODO handle args properly
        
//        for(int i=0; i<args.length; i++)
//            System.out.println(args[i]);
        if(args[0].equals("-i"))
            pid = Integer.parseInt(args[1]);
        else
            return;
        if(pid != -1 && args[2].equals("-n"))
            n = Integer.parseInt(args[3]);
        else
            return;
        outputFileName = args[5];
        
        clock = new Clock(pid);
        
        try { //special exception handler for registry creation
            LocateRegistry.createRegistry(1099); 
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            //do nothing, error means registry already exists
            System.out.println("java RMI registry already exists.");
        }
           
        //Instantiate RmiServer
        
        obj = new DistributedLock();
        
        // Bind this object instance to the name "RmiServer"
        Naming.rebind("//localhost/RmiServer"+pid, obj);
        System.out.println("PeerServer bound in registry");
        
        dls = new DistributedLockInterface[n];
        requestQueue = new RequestQueue();
        for(Integer id=0; id<n; id++) {
            try {
                if(id != pid)
                    dls[id] = (DistributedLockInterface) Naming.lookup("//localhost/RmiServer"+id);
                else
                    dls[id] = obj;
            }
            catch (NotBoundException e) {
                id--;
            }
        }
        try {
            obj.work();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return;
    }
}
