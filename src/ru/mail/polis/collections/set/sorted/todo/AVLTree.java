package ru.mail.polis.collections.set.sorted.todo;

import ru.mail.polis.collections.set.sorted.ISelfBalancingSortedTreeSet;
import ru.mail.polis.collections.set.sorted.UnbalancedTreeException;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * A AVL tree based {@link ISelfBalancingSortedTreeSet} implementation.
 *
 * <a href="https://en.wikipedia.org/wiki/AVL_tree>AVL_tree</a>
 *
 * @param <E> the type of elements maintained by this set
 */
public class AVLTree<E extends Comparable<E>> implements ISelfBalancingSortedTreeSet<E> {
    protected static class AVLNode<E extends Comparable<E>> {
        E value;
        AVLNode<E> left;
        AVLNode<E> right;
        int height = 1;
        public AVLNode(E value) {
            this.value = value;
        }
    }
    /**
     * The comparator used to maintain order in this tree map.
     */
    protected final Comparator<E> comparator;
    protected AVLNode<E> root;
    protected int length;
    public AVLTree() {
        this(Comparator.naturalOrder());
    }
    /**
     * Creates a {@code ISelfBalancingSortedTreeSet} that orders its elements according to the specified comparator.
     *
     * @param comparator comparator the comparator that will be used to order this priority queue.
     * @throws NullPointerException if the specified comparator is null
     */
    public AVLTree(Comparator<E> comparator) {
        if (comparator == null) {
            throw new NullPointerException();
        }
        this.comparator = comparator;
        this.length = 0;
    }
    /**
     * Adds the specified element to this set if it is not already present.
     * <p>
     * Complexity = O(log(n))
     *
     * @param value element to be added to this set
     * @return {@code true} if this set did not already contain the specified
     * element
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int prevSize = length;
        root = insert(root, value);
        return prevSize < length;
    }
    private AVLNode<E> insert(AVLNode<E> node, E value) {
        if (node == null) {
            length++;
            node = new AVLNode<>(value);
        } else {
            int cmp = comparator.compare(node.value, value);
            if (cmp > 0) {
                node.left = insert(node.left, value);
            } else if (cmp < 0) {
                node.right = insert(node.right, value);
            }
        }
        return getBalanced(node);
    }
    private AVLNode<E> getBalanced(AVLNode<E> node) {
        if (node == null) {
            throw new NullPointerException();
        }
        upHeight(node);
        int childrenDiff = getHDiff(node);
        if (childrenDiff == 2) {
            return getHDiff(node.left) >= 0 ? sRRotate(node) : bRRotate(node);
        } else if (childrenDiff == -2) {
            return getHDiff(node.right) <= 0 ? sLRotate(node) : bLRotate(node);
        }
        return node;
    }
    private void upHeight(AVLNode<E> node) {
        int leftHeight;
        int rightHeight;
        leftHeight = node.left != null ? node.left.height : 0;
        rightHeight = node.right != null ? node.right.height : 0;
        node.height = Math.max(leftHeight, rightHeight) + 1;
    }
    private int getHDiff(AVLNode<E> node) {
        if (node == null) {
            return 0;
        }
        int leftHeight;
        int rightHeight;
        leftHeight = node.left != null ? node.left.height : 0;
        rightHeight = node.right != null ? node.right.height : 0;
        return leftHeight - rightHeight;
    }
    private AVLNode<E> sRRotate(AVLNode<E> node) {
        if (node == null) {
            return node;
        }
        AVLNode<E> left = node.left;
        node.left = left.right;
        left.right = node;
        upHeight(node);
        upHeight(left);
        return left;
    }
    private AVLNode<E> bRRotate(AVLNode<E> node) {
        node.left = sLRotate(node.left);
        return sRRotate(node);
    }
    private AVLNode<E> sLRotate(AVLNode<E> node) {
        if (node == null) {
            return node;
        }
        AVLNode<E> right = node.right;
        node.right = right.left;
        right.left = node;
        upHeight(node);
        upHeight(right);
        return right;
    }
    private AVLNode<E> bLRotate(AVLNode<E> node) {
        node.right = sRRotate(node.right);
        return sLRotate(node);
    }
    /**
     * Removes the specified element from this set if it is present.
     * <p>
     * Complexity = O(log(n))
     *
     * @param value object to be removed from this set, if present
     * @return {@code true} if this set contained the specified element
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int prevSize = length;
        root = delete(root, value);
        return prevSize > length;
    }
    private AVLNode<E> delete(AVLNode<E> node, E value) {
        if (node == null) {
            return null;
        }
        int cmp = comparator.compare(node.value, value);
        if (cmp == 0) {
            length--;
            AVLNode<E> left = node.left;
            AVLNode<E> right = node.right;
            if (right == null) {
                node.left = null;
                return left;
            }
            AVLNode<E> minRight = findMin(right);
            minRight.right = deleteMin(right);
            minRight.left = left;
            return getBalanced(minRight);
        } else if (cmp < 0) {
            node.right = delete(node.right, value);
        } else {
            node.left = delete(node.left, value);
        }
        return getBalanced(node);
    }
    private AVLNode<E> deleteMin(AVLNode<E> node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = deleteMin(node.left);
        return getBalanced(node);
    }
    protected AVLNode<E> findMin(AVLNode<E> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
    protected AVLNode<E> findMax(AVLNode<E> node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }
    /**
     * Returns {@code true} if this collection contains the specified element.
     * aka collection contains element el such that {@code Objects.equals(el, value) == true}
     * <p>
     * Complexity = O(log(n))
     *
     * @param value element whose presence in this collection is to be tested
     * @return {@code true} if this collection contains the specified element
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException();
        }
        if (isEmpty()) {
            return false;
        }
        AVLNode<E> current = root;
        int cmp;
        while ((cmp = comparator.compare(current.value, value)) != 0) {
            if (cmp > 0) {
                current = current.left;
            } else {
                current = current.right;
            }
            if (current == null) {
                return false;
            }
        }
        return true;
    }
    /**
     * Returns the first (lowest) element currently in this set.
     * <p>
     * Complexity = O(log(n))
     *
     * @return the first (lowest) element currently in this set
     * @throws NoSuchElementException if this set is empty
     */
    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        AVLNode<E> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }
    /**
     * Returns the last (highest) element currently in this set.
     * <p>
     * Complexity = O(log(n))
     *
     * @return the last (highest) element currently in this set
     * @throws NoSuchElementException if this set is empty
     */
    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        AVLNode<E> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }
    /**
     * Returns the number of elements in this collection.
     *
     * @return the number of elements in this collection
     */
    @Override
    public int size() {
        return length;
    }
    /**
     * Returns {@code true} if this collection contains no elements.
     *
     * @return {@code true} if this collection contains no elements
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
    /**
     * Removes all of the elements from this collection.
     * The collection will be empty after this method returns.
     */
    @Override
    public void clear() {
        this.root = null;
        this.length = 0;
    }
    /**
     * Обходит дерево и проверяет что высоты двух поддеревьев
     * различны по высоте не более чем на 1
     *
     * @throws UnbalancedTreeException если высоты отличаются более чем на один
     */
    @Override
    public void checkBalance() throws UnbalancedTreeException {
        traverseTreeAndCheckBalanced(root);
    }
    private int traverseTreeAndCheckBalanced(AVLNode<E> curr) throws UnbalancedTreeException {
        if (curr == null) {
            return 0;
        }
        int leftHeight = traverseTreeAndCheckBalanced(curr.left);
        int rightHeight = traverseTreeAndCheckBalanced(curr.right);
        if (Math.abs(leftHeight - rightHeight) > 1) {
            throw UnbalancedTreeException.create("The heights of the two child subtrees of any node must be differ by at most one",
                    leftHeight, rightHeight, curr.toString());
        }
        return Math.max(leftHeight, rightHeight) + 1;
    }
}