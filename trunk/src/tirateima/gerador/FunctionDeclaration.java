package tirateima.gerador;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import tirateima.gui.variaveis.Function;
import tirateima.gui.variaveis.Variavel;
import tirateima.parser.TiraTeimaParserConstants;

/**
 * Armazena uma declaração de função a ser executada pelo Tira-Teima.
 * 
 * @author Luciano Santos
 */
public class FunctionDeclaration extends Command
		implements TiraTeimaParserConstants {
	
	private String name;
	private List<VarDefinition> param;
	private Type type;
	private List<VarDefinition> local_vars;
	
	public FunctionDeclaration(
			String name,
			List<VarDefinition> param,
			Type type,
			List<VarDefinition> local_vars) {
		this.name = name;
		this.param = param;
		this.type = type;
		this.local_vars = local_vars;
	}
	
	public void execute(Gerador g)
			throws TiraTeimaLanguageException {
		
		if (g.declared_functions.containsKey(name))
			gerarErro("Função '" + name + "' redeclarada!");
		
		g.declared_functions.put(name, this);
	}
	
	/**
	 * Cria uma nova função, com os parâmetros passados por valor.
	 * 
	 * @param g
	 * @param args
	 * @return
	 * @throws TiraTeimaLanguageException
	 */
	public Function newFunction(Gerador g, List<Object> args)
			throws TiraTeimaLanguageException {
		
		if (args.size() != param.size())
			gerarErro("Número de parâmetros inválido!");
		
		List<Variavel> params = new ArrayList<Variavel>();
		ListIterator<Object> i = args.listIterator();
		int cont = 1;
		Variavel vaux;
		for (VarDefinition v : param) {
			vaux = newVar(g, v);
			try {
				vaux.setValor(i.next());
			} catch (ClassCastException e) {
				gerarErro("Argumento " + cont + " passado à função " + name
						+ " incompatível.");
			}
			params.add(vaux);
			cont++;
		}
		
		List<Variavel> vars = new ArrayList<Variavel>();
		for (VarDefinition v : local_vars) {
			vars.add(newVar(g, v));
		}
		
		return new Function(name, type, params, vars);
	}
}
