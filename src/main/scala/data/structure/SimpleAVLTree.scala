package data.structure

import scala.math.max

// allow duplicates, but delete one element at a time
// element type: Int

case class Node(
                 var key: Integer = null,
                 var left: Node = null,
                 var right: Node = null,
                 var height: Int = 0
               )

class KeyNotFoundException extends Exception

class SimpleAVLTree {
  private var root: Node = null

  def contains(target: Int): Boolean = {
    recursiveSearch(root, target)
  }

  def insert(num: Int): Unit = {
    if (null == root) root = Node(key = num, height = 1)
    else root = recursiveInsert(root, num)
  }

  def delete(num: Int): Boolean = {
    try {
      root = recursiveDelete(root, num)
      true
    } catch {
      case e: KeyNotFoundException => false
    }
  }

  private def recursiveSearch(curNode: Node, keyToSearch: Int): Boolean = {
    if(null == curNode) {
      return false
    }

    if(keyToSearch < curNode.key) {
      recursiveSearch(curNode.left, keyToSearch)
    } else if (keyToSearch > curNode.key) {
      recursiveSearch(curNode.right, keyToSearch)
    } else true
  }

  private def height(node: Node): Int = {
    if (null == node) 0 else node.height
  }

  private def getBalance(node: Node): Int = {
    if (null == node) 0 else height(node.left) - height(node.right)
  }

  private def rightRotate(root: Node): Node = {
    // rotate
    val newRoot = root.left
    root.left = newRoot.right
    newRoot.right = root

    // update heights
    root.height = max(height(root.left), height(root.right)) + 1
    newRoot.height = max(height(newRoot.left), height(newRoot.right)) + 1

    newRoot
  }

  private def leftRotate(root: Node): Node = {
    // rotate
    val newRoot = root.right
    root.right = newRoot.left
    newRoot.left = root

    // update heights
    root.height = max(height(root.left), height(root.right)) + 1
    newRoot.height = max(height(newRoot.left), height(newRoot.right)) + 1

    newRoot
  }

  private def recursiveInsert(curNode: Node, keyToInsert: Int): Node = {
    // 1. perform the normal BST insertion
    if(keyToInsert >= curNode.key) {
      if (null == curNode.right) {
        curNode.right = Node(key = keyToInsert, height = 1)
      } else {
        curNode.right = recursiveInsert(curNode.right, keyToInsert)
      }
    } else {
      if (null == curNode.left) {
        curNode.left = Node(key = keyToInsert, height = 1)
      } else {
        curNode.left = recursiveInsert(curNode.left, keyToInsert)
      }
    }

    // 2. update curNode.height
    curNode.height = max(height(curNode.left), height(curNode.right)) + 1

    // 3. balance subTree if needed
    reBalance(curNode)
  }

  private def getMaxNode(root: Node): Node = {
    var curNode = root
    while (curNode.right != null) {
      curNode = curNode.right
    }
    curNode
  }

  private def reBalance(curNode: Node): Node = {
    val balance = getBalance(curNode)

    // if unbalanced, re-balance. There are 4 cases:
    // 1. LL
    if (balance > 1 && getBalance(curNode.left) > 0) {
      return rightRotate(curNode)
    }
    // 2. RR
    if (balance < -1 && getBalance(curNode.right) < 0) {
      return leftRotate(curNode)
    }
    // 3. LR
    if (balance > 1 && getBalance(curNode.left) < 0) {
      curNode.left = leftRotate(curNode.left)
      return rightRotate(curNode)
    }
    // 4. RL
    if (balance < -1 && getBalance(curNode.right) > 0) {
      curNode.right = rightRotate(curNode.right)
      return leftRotate(curNode)
    }

    curNode
  }

  private def recursiveDelete(curNode: Node, keyToDelete: Int): Node = {
    // 1. perform the normal BST deletion
    if (null == curNode) {
      throw new KeyNotFoundException
    }

    if (keyToDelete < curNode.key) {
      curNode.left = recursiveDelete(curNode.left, keyToDelete)
    } else if (keyToDelete > curNode.key) {
      curNode.right = recursiveDelete(curNode.right, keyToDelete)
    } else {
      // curNode.key == keyToDelete, i.e. curNode is the node to delete
      if (curNode.left != null && curNode.right != null) {
        // node with two children
        // substitute curNode.key with the maximal element in the left subTree and delete the latter
        val maxNodeInLeftTree = getMaxNode(curNode.left)
        curNode.key = maxNodeInLeftTree.key
        curNode.left = recursiveDelete(curNode.left, maxNodeInLeftTree.key)
      } else {
        // node with only one child or no child
        // replace curNode with its left child or right child to delete it
        return if (curNode.left != null) curNode.left else curNode.right
      }
    }

    // 2. update curNode.height
    curNode.height = max(height(curNode.left), height(curNode.right)) + 1

    // 3. balance subTree if needed
    reBalance(curNode)
  }

}