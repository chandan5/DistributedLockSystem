/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributedlock;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*; 
import java.util.List;

/**
 *
 * @author chandan5
 */


public class DistributedLock extends UnicastRemoteObject implements DistributedLockInterface {
//    Clock clock = new Clock();
    public static Clock clock = null;
//    public static Clock lastlockClock = null;
    static String outputFileName;
    Integer counter = 0;
    Integer lockStatus = 0;
    public DistributedLock() throws RemoteException {
        super(0);    // required to avoid the 'rmic' step, see below
    }
    

    @Override
    public int LockRequest(byte [] r) throws RemoteException {
//        lastlockClock = clock;
        //depending on lockStatus either return 1 or (queue and return 0)
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int UnlockRequest(byte [] r) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private Integer getLock() {
        if(clock != null) {
            counter++;
            // Send LockRequest to all
        }
        return 0;
    }
    
    private void criticalSection() throws IOException {
        FileWriter fw = new FileWriter(outputFileName,true);
        List<String> lines = Files.readAllLines(Paths.get(outputFileName), StandardCharsets.UTF_8);
        System.out.println(outputFileName);
        Integer globalCounter = 0;
        Integer k = lines.size();
        
        if(k != 0) {
            String[] tokens = lines.get(k-1).split(":");
            globalCounter = Integer.parseInt(tokens[0]);
        }
        globalCounter++;
        System.out.println(globalCounter);
        System.out.println(fw);
        fw.write(globalCounter + ":add a line\n");//appends the string to the file
        fw.close();
    }
    
    private void releaseLock() {
        // send unlock request to all those who are queued in queue
    }
    
    private void work() throws IOException {
        if(counter < 100) {
            lockStatus = 1; // interested in lock
            if(getLock() == 1) {
                lockStatus = 2; // is in critical section => Send no
                criticalSection();
                lockStatus = 3; // means releasing lock => not interested in lock => send ok
                releaseLock();
                lockStatus = 4; // means sleeping => not interested in lock => Send ok
                //sleep();
                work();
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    //                      -i 0 -n 1 -o out1
    public static void main(String[] args) throws Exception {
        DistributedLock[] distributedLocks;
        System.out.println("RMI server started");
        System.out.println("Parsing Arguments...");
        Integer n = 0;
        Integer pid = -1;
        // TODO handle args properly
        
        for(int i=0; i<args.length; i++)
            System.out.println(args[i]);
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

        DistributedLock obj = new DistributedLock();

        // Bind this object instance to the name "RmiServer"
        //Naming.rebind("//localhost/RmiServer"+pid, obj);
        System.out.println("PeerServer bound in registry");
//        for(Integer id=0; id<n; id++) {
//            distributedLocks[id] = (DistributedLock) Naming.lookup("//localhost/RmiServer"+id);
//        }
        obj.work();
//        obj.criticalSection();
        return;
    }
}
