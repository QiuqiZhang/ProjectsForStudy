package data.structure

// allow duplicates, but delete one element at a time
// element type: Int

case class Node(
                 var key: Int = null,
                 var left: Node = null,
                 var right: Node = null,
                 var height: Int = 0
               )

class SimpleAVLTree {
  private var root: Node = null

  private def recursiveSearch(curNode: Node, keyToSearch: Int): Boolean = {
    if(null == curNode) {
      return false
    }

    if(keyToSearch < curNode.key) recursiveSearch(curNode.left, keyToSearch)
    else if (keyToSearch > curNode.key) recursiveSearch(curNode.right, keyToSearch)
    else true
  }

  def contains(target: Int): Boolean = {
    recursiveSearch(root, target)
  }

  private def recursiveInsert(curNode: Node, keyToInsert: Int): Unit = {
    if(keyToInsert >= curNode.key) {
      if (null == curNode.right) curNode.right = Node(key = keyToInsert, height = 1)
      else recursiveInsert(curNode.right, keyToInsert)
    } else {
      if (null == curNode.left) curNode.left = Node(key = keyToInsert, height = 1)
      else recursiveInsert(curNode.left, keyToInsert)
    }
  }

  def insert(num: Int): Unit = {
    if (null == root) root = Node(key = num, height = 1)
    else recursiveInsert(root, num)
  }

  private def deleteMaximum(curNode: Node): Int = {
    var maximum: Int = null
    if (curNode.right != null) {
      maximum = deleteMaximum(curNode.right)
      if (null == curNode.right.key) curNode.right = null
    } else {
      maximum = curNode.key
      curNode.key = null
    }
    maximum
  }

  private def recursiveDelete(curNode: Node, keyToDelete: Int): Boolean = {
    if (null == curNode) {
      return false
    }

    var isSuccessful = false
    if(keyToDelete < curNode.key) {
      isSuccessful = recursiveDelete(curNode.left, keyToDelete)
      if (null == curNode.left.key) {
        curNode.left = null
      }
    } else if (keyToDelete > curNode.key) {
      isSuccessful = recursiveDelete(curNode.right, keyToDelete)
      if (null == curNode.right.key) {
        curNode.right = null
      }
    } else {
      // curNode.key == keyToDelete, i.e. curNode is the node to delete
      if (curNode.left != null && curNode.right != null) {
        // substitute curNode.key with the maximal element in the left subTree and delete the latter
        curNode.key = deleteMaximum(curNode.left)
      } else {
        // delete curNode
        curNode.key = null
      }
    }
    isSuccessful
  }

  def delete(num: Int): Boolean = {
    recursiveDelete(root, num)
  }
}
