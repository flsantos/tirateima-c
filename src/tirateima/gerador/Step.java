package tirateima.gerador;

import java.util.ArrayList;
import java.util.List;

/**
 * Uma abstração que modela um passo do Tira-Teima.
 * 
 * @author Luciano Santos
 */
public class Step {
	/** A linha à qual esse passo se refere. */
	public int line;
	
	/** Os comandos a serem executados neste passo. */
	public List<Command> commands = null;
	
	/**
	 * Cria um novo passo com a linha dada e lista de comandos vazia.
	 * 
	 * @param line A linha.
	 */
	public Step(int line) {
		this.line = line;
		commands = new ArrayList<Command>();
	}
	
	/**
	 * Adiciona um comando ao final da lista de comandos.
	 * 
	 * @param command
	 */
	public void addCommand(Command command) {
		commands.add(command);
	}
	
	public String toString() {
		return line + ": " + commands.toString();
	}
}
