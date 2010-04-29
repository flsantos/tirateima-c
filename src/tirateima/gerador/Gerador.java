package tirateima.gerador;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tirateima.controlador.Estado;
import tirateima.gui.alerta.Alerta;
import tirateima.gui.arquivos.GerenciadorArquivos;
import tirateima.gui.console.Console;
import tirateima.gui.editortexto.EditorTexto;
import tirateima.gui.variaveis.Mostrador;
import tirateima.parser.ParseException;
import tirateima.parser.TiraTeimaParser;

/**
 * Um gerador de estados para o tirateima.
 * 
 * @author Luciano Santos
 * @author Andrew Biller
 */
public class Gerador {
	/** Os tipos declarados pelo usuário. */
	public Map<String, RecordDefinition> declared_records;
	
	/** As funções declaradas pelo usuário. */
	public Map<String, FunctionDeclaration> declared_functions;
	
	/** O Mostrador. */
	public Mostrador mostrador;
	
	/** O editor_texto. */
	public EditorTexto editor_texto;
	
	/** O console. */
	public Console console;
	
	/** Os alertas. */
	public Alerta alerta;
	
	/** O gerador de arquivos. */
	public GerenciadorArquivos ga;
	
	
	/**
	 * Constrói um novo gerador.
	 * 
	 * @param mostrador
	 * @param editor
	 * @param console
	 * @param ga
	 */
	public Gerador(
			Mostrador mostrador,
			EditorTexto editor,
			Console console,
			Alerta alerta,
			GerenciadorArquivos ga) {
		
		this.mostrador = mostrador;
		this.editor_texto = editor;
		this.console = console;
		this.alerta = alerta;
		this.ga = ga;
	}
	
	/**
	 * Analisa um arquivo de comandos do Tira-Teima e gera a lista de estados.
	 *  
	 * @param reader
	 * @return
	 * @throws TiraTeimaLanguageException
	 * @throws ParseException
	 */
	public List<Estado> parse(Reader reader) throws TiraTeimaLanguageException, ParseException {
		declared_records = new HashMap<String, RecordDefinition>();
		declared_functions = new HashMap<String, FunctionDeclaration>();
		
		TiraTeimaParser parser = new TiraTeimaParser(reader);
		
		Step step;
		List<Estado> result = new ArrayList<Estado>();
		while ((step = parser.step()) != null) {
			System.out.println(step.toString());
			for (Command c : step.commands) {
				c.execute(this);
			}
			saveState(result, step.line);
		}
		
		return result;
	}
	
	private void saveState(List<Estado> states, int line) {
		editor_texto.getCaixaTexto().setMarcada(line);
		
		Estado e = new Estado();
		e.est_mostrador = mostrador.getEstado();
		e.est_editor = editor_texto.getEstado();
		e.est_console = console.getEstado();
		e.est_alerta = alerta.getEstado();
		e.est_ga = ga.getEstado();
		
		states.add(e);
	}
}
