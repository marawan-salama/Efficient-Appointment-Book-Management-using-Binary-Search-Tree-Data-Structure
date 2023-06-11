/** 
 * Name: Marawan Salama
 * Worked With: Michael McLaughlin , Christian Oropeza, Julian Moreno, Miguel Garcia, and Matt from the dropIn tutoring
 * Resources: My clone is based on the Book's CopyTree method
 * */


// This is an assignment for students to complete after reading Chapter 3 of
// "Data Structures and Other Objects Using Java" by Michael Main.


package edu.uwm.cs351;


import java.util.function.Consumer;


import edu.uwm.cs.junit.LockedTestCase;


//import edu.uwm.cs351.ApptBook.Node;


/******************************************************************************
 * This class is a homework assignment;
 * An ApptBook ("book" for short) is a sequence of Appointment objects in sorted order.
 * The book can have a special "current element," which is specified and 
 * accessed through four methods that are available in the this class 
 * (start, getCurrent, advance and isCurrent).
 ******************************************************************************/
public class ApptBook implements Cloneable {
	// It should have a constructor but no methods.
	// The constructor should take an Appointment.
	// The fields of Node should have "default" access (neither public, nor private)
	// and should not start with underscores.
	
	
	private static class Node{
		Appointment data;
		Node right;
		Node left;
		
		public Node (Appointment x) {
			data=x;
			right=left=null;
		}	
	}


	// using a binary search tree.
	int manyItems;
	Node root;
	Node cursor;


	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}
	

	/**
	 * Return true if the given subtree has height no more than a given bound.
	 * In particular if the "tree" has a cycle, then false will be returned
	 * since it has unbounded height.
	 * @param r root of subtree to check, may be null
	 * @param max maximum permitted height (null has height 0)
	 * @return whether the subtree has at most tgis height
	 */
	private boolean checkHeight(Node r, int max) {
		if(r==null && max>=0)return true;
		if(max < 0 ) return false;
		return checkHeight(r.left,max-1) && checkHeight(r.right,max-1);
	}
	

	/**
	 * Return the number of nodes in a subtree that has no cycles.
	 * @param r root of the subtree to count nodes in, may be null
	 * @return number of nodes in subtree
	 */
	private int countNodes(Node r) {
		// TODO
		if(r!=null) {
			return countNodes(r.right)+countNodes(r.left)+1;
		}else {
			return 0;
		}
	}
	

	/**
	 * Return whether all the nodes in the subtree are in the given range,
	 * and also in their respective subranges.
	 * @param r root of subtree to check, may be null
	 * @param lo inclusive lower bound, may be null (no lower bound)
	 * @param hi exclusive upper bound, may be null (no upper bound)
	 * @return
	 */
	private boolean allInRange(Node r, Appointment lo, Appointment hi) {
		if(r==null)return true;
		if(r.data==null) return report ("Data is Null");
		if(lo!=null && r.data.compareTo(lo)<0) return report ("Out of Range");
		if(hi!=null && r.data.compareTo(hi)>=0) return report ("Out of Range");
		Appointment app=r.data;
		return allInRange(r.left, lo, app) &&
				allInRange(r.right, app, hi);
	}

	
	/**
	 * Return whether the cursor was found in the tree.
	 * If the cursor is null, it should always be found since 
	 * a binary search tree has many null pointers in it.
	 * This method doesn't examine any of the data elements;
	 * it works fine even on trees that are badly structured, as long as
	 * they are height-bounded.
	 * @param r subtree to check, may be null, but must have bounded height
	 * @return true if the cursor was found in the subtree
	 */
	private boolean foundCursor(Node r) {
		if(cursor==null) return true;
		if(r==cursor) return true;
		if(root==null && cursor==null) return false;
		if(r==null) return false;
		return foundCursor(r.right)|| foundCursor(r.left);
	}


	private boolean wellFormed() {
		// Check the invariant.
		// Invariant:
		// 1. The tree must have height bounded by the number of items
		if(checkHeight(root,manyItems)==false) return report("error");
		// 2. The number of nodes must match manyItems
		if(manyItems!=countNodes(root)) return report("ManyItem not equal to number of node");
		// 3. Every node's data must not be null and be in range.
		if(allInRange(root, null,null)==false) return false;
		// 4. The cursor must be null or in the tree.
		if(foundCursor(root)==false) return report("error");
		return true;
	}

	
	// This is only for testing the invariant.  Do not change!
	private ApptBook(boolean testInvariant) { }

	
	/**
	 * Initialize an empty book. 
	 **/   
	public ApptBook( )
	{
		manyItems = 0 ;
		root = null;
		cursor = null ;
		assert wellFormed() : "invariant failed at end of constructor";
	}

	
	/**
	 * Determine the number of elements in this book.
	 * @return
	 *   the number of elements in this book
	 **/ 
	public int size( )
	{
		assert wellFormed() : "invariant failed at start of size";
		return manyItems;
	}

	
	/**
	 * Return the first node in a non-empty subtree.
	 * It doesn't examine the data in teh nodes; 
	 * it just uses the structure.
	 * @param r subtree, must not be null
	 * @return first node in the subtree
	 */
	private Node firstInTree(Node r) {
		if(r==null) throw new NullPointerException();
		Node n = r;
		while (n.left != null) n = n.left;
		return n;
	}

	
	/**
	 * Set the current element at the front of this book.
	 * @postcondition
	 *   The front element of this book is now the current element (but 
	 *   if this book has no elements at all, then there is no current 
	 *   element).
	 **/ 
	public void start( )
	{
		assert wellFormed() : "invariant failed at start of start";
		if(root!=null)cursor=root;
		while(cursor!=null) {
			if(cursor.left==null)break;
			cursor=cursor.left;
		}
		assert wellFormed() : "invariant failed at end of start";
	}

	
	/**
	 * Accessor method to determine whether this book has a specified 
	 * current element that can be retrieved with the 
	 * getCurrent method. 
	 * @return
	 *   true (there is a current element) or false (there is no current element at the moment)
	 **/
	public boolean isCurrent( )
	{
		assert wellFormed() : "invariant failed at start of isCurrent";
		return cursor!=null;
	}

	
	/**
	 * Accessor method to get the current element of this book. 
	 * @precondition
	 *   isCurrent() returns true.
	 * @return
	 *   the current element of this book
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   getCurrent may not be called.
	 **/
	public Appointment getCurrent( )
	{
		assert wellFormed() : "invariant failed at start of getCurrent";
		if(!isCurrent()) throw new IllegalStateException();	
		return cursor.data;
	}

	
	/**
	 * Find the node that has the appt (if acceptEquivalent) or the first thing
	 * after it.  Return that node.  Return the alternate if everything in the subtree
	 * comes before the given appt.
	 * @param r subtree to look into, may be null
	 * @param appt appointment to look for, must not be null
	 * @param acceptEquivalent whether we accept something equivalent.  Otherwise, only
	 * appointments after the appt are accepted.
	 * @param alt what to return if no node in subtree is acceptable.
	 * @return node that has the first element equal (if acceptEquivalent) or after
	 * the appt.
	 */
	private Node nextInTree(Node r, Appointment appt, boolean acceptEquivalent, Node alt) {
		if(r==null) return alt;
		Node res=alt;
		while (r != null) {
			Appointment ap = r.data;
			if(acceptEquivalent) {
				if (ap.compareTo(appt)<0) r = r.right;
				else { res = r; r = r.left; }
			}
			if(! acceptEquivalent) {
				if (ap.compareTo(appt)<=0) r = r.right;
				else { res = r	; r = r.left; }
			}
		}
		return res;
	}

	
	/**
	 * Move forward, so that the current element will be the next element in
	 * this book.
	 * @precondition
	 *   isCurrent() returns true. 
	 * @postcondition
	 *   If the current element was already the end element of this book 
	 *   (with nothing after it), then there is no longer any current element. 
	 *   Otherwise, the new element is the element immediately after the 
	 *   original current element.
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   advance may not be called.
	 **/
	public void advance( )
	{
		assert wellFormed() : "invariant failed at start of advance";

		if (!isCurrent()) throw new IllegalStateException("no current");
		if(cursor.right==null ) {
			cursor=nextInTree(root, cursor.data,false, null) ;
		}else {
			cursor=cursor.right;
			cursor=firstInTree(cursor);
		}

		assert wellFormed() : "invariant failed at end of advance";
	}

	
	/**
	 * Remove the current element from this book.
	 * NB: Not supported in Homework #8
	 **/
	public void removeCurrent( )
	{
		assert wellFormed() : "invariant failed at start of removeCurrent";
		throw new UnsupportedOperationException("remove is not implemented");
	}

	
	/**
	 * Set the current element to the first element that is equal
	 * or greater than the guide.  This operation will be efficient
	 * if the tree is balanced.
	 * @param guide element to compare against, must not be null.
	 */
	public void setCurrent(Appointment guide) {
		assert wellFormed() : "invariant failed at start of setCurrent";
		if(guide ==null ) throw new NullPointerException ();
		cursor=nextInTree(root, guide, true, null) ;
		assert wellFormed() : "invariant failed at end of setCurrent";
	}

	
	// OPTIONAL: You may define a helper method for insert.  The solution does
	private void AddHelper(Node n, Appointment p, Node lag) {
		if (lag == null) {
			root = n;
		} else if (p.compareTo(lag.data) >= 0) {
			lag.right = n;
		} else {
			lag.left = n;
		}
	}

	
	/**
	 * Add a new element to this book, in order.  If an equal appointment is already
	 * in the book, it is inserted after the last of these. 
	 * The current element (if any) is not affected.
	 * @param element
	 *   the new element that is being added, must not be null
	 * @postcondition
	 *   A new copy of the element has been added to this book. The current
	 *   element (whether or not is exists) is not changed.
	 * @exception IllegalArgumentException
	 *   indicates the parameter is null
	 **/
	public void insert(Appointment element)
	{
		assert wellFormed() : "invariant failed at start of insert";
		if(element==null) throw new IllegalArgumentException();
		Node n = root;
		Node lag = null;
		while (n != null) {
			Appointment p=n.data;
			lag=n;
			if(element.compareTo(p) >= 0) n=n.right;
			else n=n.left;
		}
		if(n==null) {
			n=new Node(element);
			AddHelper(n,element,lag);
			manyItems++;

		}
		assert wellFormed() : "invariant failed at end of insert";
	}

	
	// - Must be recursive.
	// - Must add in "pre-order"
	private void addAllHelper(Node r) {
		if(r!=null) {
			insert(r.data);
			addAllHelper(r.left);
			addAllHelper(r.right);
		}
	}

	
	/**
	 * Place all the appointments of another book (which may be the
	 * same book as this!) into this book in order as in {@link #insert}.
	 * The elements should added one by one.
	 * @param addend
	 *   a book whose contents will be placed into this book
	 * @precondition
	 *   The parameter, addend, is not null. 
	 * @postcondition
	 *   The elements from addend have been placed into
	 *   this book. The current el;ement (if any) is
	 *   unchanged.
	 **/
	public void insertAll(ApptBook addend)
	{
		assert wellFormed() : "invariant failed at start of insertAll";
		// Watch out for the this==addend case!
		// Cloning the addend is an easy way to avoid problems.
		ApptBook x =addend.clone();
		addAllHelper(x.root);
		assert wellFormed() : "invariant failed at end of insertAll";
		assert addend.wellFormed() : "invariant of addend broken in insertAll";
	}

	
	// - Must be recursive
	// - Take the answer as a parameter so you can set the cloned cursor
	private Node CloneHellper(Node r,ApptBook answer) {
		if (r == null) {
			return null;
		}
		Node newNode = new Node(r.data);
		newNode.left = CloneHellper(r.left,answer);
		newNode.right = CloneHellper(r.right,answer);
		if(r==cursor)answer.cursor=newNode;
		return newNode;
	}

	
	/**
	 * Generate a copy of this book.
	 * @return
	 *   The return value is a copy of this book. Subsequent changes to the
	 *   copy will not affect the original, nor vice versa.
	 **/ 
	public ApptBook clone( ) { 
		assert wellFormed() : "invariant failed at start of clone";
		ApptBook answer;

		try
		{
			answer = (ApptBook) super.clone( );
		}
		catch (CloneNotSupportedException e)
		{  // This exception should not occur. But if it does, it would probably
			// indicate a programming error that made super.clone unavailable.
			// The most common error would be forgetting the "Implements Cloneable"
			// clause at the start of this class.
			throw new RuntimeException
			("This class does not implement Cloneable");
		}

		answer.root=CloneHellper(root, answer);

		assert wellFormed() : "invariant failed at end of clone";
		assert answer.wellFormed() : "invariant on answer failed at end of clone";
		return answer;
	}

	
	// don't change this nested class:
	public static class TestInvariantChecker extends LockedTestCase {
		protected ApptBook self;

		protected Consumer<String> getReporter() {
			return reporter;
		}

		protected void setReporter(Consumer<String> c) {
			reporter = c;
		}

		private static Appointment a = new Appointment(new Period(new Time(), Duration.HOUR), "default");

		protected class Node extends ApptBook.Node {
			public Node(Appointment d, Node n1, Node n2) {
				super(a);
				data = d;
				left = n1;
				right = n2;
			}
			public void setLeft(Node l) {
				left = l;
			}
			public void setRight(Node r) {
				right = r;
			}
		}

		protected Node newNode(Appointment a, Node l, Node r) {
			return new Node(a, l, r);
		}

		protected void setRoot(Node n) {
			self.root = n;
		}

		protected void setManyItems(int mi) {
			self.manyItems = mi;
		}

		protected void setCursor(Node n) {
			self.cursor = n;
		}

		protected void setUp() {
			self = new ApptBook(false);
			self.root = self.cursor = null;
			self.manyItems = 0;
		}


		/// relay methods for helper methods:

		protected boolean checkHeight(Node r, int max) {
			return self.checkHeight(r, max);
		}

		protected int countNodes(Node r) {
			return self.countNodes(r);
		}

		protected boolean allInRange(Node r, Appointment lo, Appointment hi) {
			return self.allInRange(r, lo, hi);
		}

		protected boolean foundCursor(Node r) {
			return self.foundCursor(r);
		}

		protected boolean wellFormed() {
			return self.wellFormed();
		}

		protected Node firstInTree(Node r) {
			return (Node)self.firstInTree(r);
		}

		protected Node nextInTree(Node r, Appointment a, boolean acceptEquiv, Node alt) {
			return (Node)self.nextInTree(r, a, acceptEquiv, alt);
		}


		/// Prevent this test suite from running by itself

		public void test() {
			assertFalse("DOn't attempt to run this test", true);
		}
	}
}

