/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributedlock;
import java.rmi.*;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author chandan5
 */


public class DistributedLock {
    SortedSet<String> clock = new TreeSet<String>();
    int id;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
