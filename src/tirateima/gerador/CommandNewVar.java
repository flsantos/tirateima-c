package tirateima.gerador;


/**
 * Comando de criação de variável.
 * 
 * @author Luciano Santos
 */
public class CommandNewVar extends Command {
	private VarDefinition ref;
	
	public CommandNewVar(VarDefinition ref) {
		this.ref = ref;
	}
	
	public void execute(Gerador g)
			throws TiraTeimaLanguageException {
		g.mostrador.adicionarVariavel(newVar(g, ref));
	}
}
