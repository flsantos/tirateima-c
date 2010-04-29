package tirateima.gerador;

/**
 * Modela um índice do Tira-Teima, que pode ser de matriz ou array de uma dimensão.
 * 
 * @author Luciano Santos
 */
public class Index {
	/** Valor do índice na primeira dimensão. */
	public int first;
	
	/** Valor do índice na segunda dimensão. */
	public int second;
	
	/** Define se o índice é de matriz. */
	public boolean isMatrix;
	
	public Index(int first, int second, boolean isMatrix) {
		this.first = first;
		this.second = second;
		this.isMatrix = isMatrix;
	}
	
	public Index(int first, int second) {
		this(first, second, true);
	}
	
	public Index(int first) {
		this(first, 0, false);
	}
	
	public Index() {
		this(0, 0, false);
	}
}
