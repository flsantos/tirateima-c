package tirateima.gerador;

import tirateima.gui.arquivos.VarText;

/**
 * Comando de criação de arquivo texto (simbólico).
 * 
 * @author Luciano Santos
 */
public class CommandNewVarText extends Command {
	private String name;
	
	public CommandNewVarText(String name) {
		this.name = name;
	}
	
	public void execute(Gerador g)
			throws TiraTeimaLanguageException {
		VarText arq = new VarText(name);
		g.ga.adicionarArquivo(arq);
	}
}
