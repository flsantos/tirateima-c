package tirateima.gerador;

import java.util.Stack;

/**
 * Comando de atribuição.
 * 
 * @author Luciano Santos
 */
public class CommandAttribution extends Command {
	
	private Stack<Object> var_stack;
	private Object value;
	
	public CommandAttribution(Stack<Object> var_stack, Object value) {
		this.var_stack = var_stack;
		this.value = value;
	}
	
	public void execute(Gerador g)
			throws TiraTeimaLanguageException {
		setValue(g.mostrador, var_stack, value);
	}
}
