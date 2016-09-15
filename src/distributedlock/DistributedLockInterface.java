/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributedlock;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author chandan5
 */
public interface DistributedLockInterface extends Remote{
    public int LockRequest(byte[] r) throws RemoteException;
    public int UnlockRequest(byte [] r) throws RemoteException;
}
