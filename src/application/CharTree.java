package application;

public class CharTree implements Comparable<CharTree> { // implements Comparable → حتى نقدر نقارن الـ Nodes داخل PriorityQueue حسب الـ frequency.

	char c;
	int freq;
	CharTree left;
	CharTree right;

	public CharTree(char c, int freq, CharTree left, CharTree right) { // يستخدم لبناء internal nodes أثناء الدمج في خوارزمية Huffman:

		this.c = c;
		this.freq = freq;
		this.left = left;
		this.right = right;

	}

	public CharTree(char c, int freq) { // عندما تأخذ أقلّ عقدتين من الـ PriorityQueue وتدمجهما.

		this.c = c;
		this.freq = freq;

	}

	public CharTree(char c) {

		this.c = c;

	}

	public boolean isLeaf() {
		return (left == null && right == null);
	}

	// compare the freqs of diff chars ;
	@Override
	public int compareTo(CharTree x) {
		return (this.freq - x.freq);
	}

	public char getC() {
		return c;
	}

	public void setC(char c) {
		this.c = c;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public CharTree getLeft() {
		return left;
	}

	public void setLeft(CharTree left) {
		this.left = left;
	}

	public CharTree getRight() {
		return right;
	}

	public void setRight(CharTree right) {
		this.right = right;
	}

}