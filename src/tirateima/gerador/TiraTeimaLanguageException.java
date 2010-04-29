package tirateima.gerador;

/**
 * Uma exceção lançada pelo lexer/parser/gerador.
 * 
 * @author Luciano Santos
 */
public class TiraTeimaLanguageException extends Exception {
	private static final long serialVersionUID = 1L;

	private int line;
	private int column;
	
	public TiraTeimaLanguageException(String message, int line, int column) {
		super(message);
		
		this.line = line;
		this.column = column;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}
}
