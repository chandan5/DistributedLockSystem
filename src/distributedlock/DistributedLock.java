/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributedlock;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*; 

/**
 *
 * @author chandan5
 */


public class DistributedLock extends UnicastRemoteObject implements DistributedLockInterface {
//    Clock clock = new Clock();
    public static Clock clock = null;
    public DistributedLock() throws RemoteException {
        super(0);    // required to avoid the 'rmic' step, see below
    }
    

    @Override
    public int LockRequest(Clock c) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int UnlockRequest(Clock c) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * @param args the command line arguments
     */
    //                      -i 0 -n 1 -o out1
    public static void main(String[] args) throws Exception {
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
        clock = new Clock(pid);
        System.out.println("Yo!");
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
        Naming.rebind("//localhost/RmiServer", obj);
        System.out.println("PeerServer bound in registry");
    }
}
