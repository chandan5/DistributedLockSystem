/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributedlock;

import java.util.SortedSet;
import java.util.TreeSet;
import distributedlock.RequestProto.Request;
import distributedlock.RequestProto.Clocks;

/**
 *
 * @author chandan5
 */

public class Pair<L extends Comparable<L>,R extends Comparable<R>> implements Comparable<Pair<L,R>>{

  private final L left;
  private final R right;

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
    int id;
    byte[] getProtoBytes() {
        Request.Builder request = Request.newBuilder();
//        for(Student student: students) {
//            StudentMsg.Builder studentMsg = StudentMsg.newBuilder();
//            studentMsg.setName(student.Name);
//            studentMsg.setRollNum(student.RollNo);
//            for(Course course: student.CourseMarks) {
//                CourseMarks.Builder courseMarks = CourseMarks.newBuilder();
//                courseMarks.setName(course.CourseName);
//                courseMarks.setScore(course.CourseScore);
//                studentMsg.addMarks(courseMarks);
//            }
//            result.addStudentmsg(studentMsg);
//        }
        return request.build();
    }
}
