package data.structure

import org.scalatest.FunSuite

class SimpleAVLTreeTest extends FunSuite {

  test("empty tree test") {
    val tree = new SimpleAVLTree
    assert(false equals tree.contains(0))
    assert(false equals tree.delete(0))
  }

  test("one element test") {
    val tree = new SimpleAVLTree
    tree.insert(0)
    assert(true equals tree.contains(0))
    tree.delete(0)
    assert(false equals tree.contains(0))
  }

  test("comprehensive massive test") {
    val tree = new SimpleAVLTree

    import java.util.Random
    val random = new Random()
    val testCaseArray = new Array[Int](50000) // may duplicate
    for(i <- 1 to 50000){
      testCaseArray(i-1) = random.nextInt()
      tree.insert(testCaseArray(i-1))
    }
    for(x <- testCaseArray){
      assert(true equals tree.contains(x))
    }
    for(x <- testCaseArray){
      assert(true equals tree.delete(x))
    }
    for(x <- testCaseArray){
      assert(false equals tree.contains(x))
    }
  }

}
