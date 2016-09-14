/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributedlock;

/**
 *
 * @author chandan5
 */

    // left has val and right has id
public class Pair<L extends Comparable<L>,R extends Comparable<R>> implements Comparable<Pair<L,R>>{

  final L left;
  final R right;

  public Pair(L left, R right) {
    this.left = left;
    this.right = right;
  }

  public L getLeft() { return left; }
  public R getRight() { return right; }

  @Override
  public int hashCode() { return left.hashCode() ^ right.hashCode(); }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Pair)) return false;
    Pair pairo = (Pair) o;
    return this.left.equals(pairo.getLeft()) &&
           this.right.equals(pairo.getRight());
  }
  
  @Override
  public int compareTo(Pair<L,R> pair) {
      int result = left.compareTo(pair.left);
      return (result == 0) ? right.compareTo(pair.right) : result;
  }
}
