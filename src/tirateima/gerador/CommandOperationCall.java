package tirateima.gerador;

import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import tirateima.gui.arquivos.AbstractArquivo;
import tirateima.gui.arquivos.VarText;
import tirateima.gui.variaveis.VarChar;
import tirateima.gui.variaveis.VarGrade;
import tirateima.gui.variaveis.VarInteger;
import tirateima.gui.variaveis.VarReal;
import tirateima.gui.variaveis.VarRecord;
import tirateima.gui.variaveis.VarString;
import tirateima.gui.variaveis.Variavel;
import tirateima.parser.TiraTeimaParserConstants;
import tirateima.parser.Token;

/**
 * Comando de chamada de operação, que pode ser funções do sistema
 * (manipulação de arquivos, write) ou de função definida pelo
 * usuário.
 * 
 * @author Luciano Santos
 */
@SuppressWarnings("unchecked")
public class CommandOperationCall extends Command
		implements TiraTeimaParserConstants {
	
	private Token cmd;
	List<Object> args;
	
	public CommandOperationCall(Token operation, List<Object> args) {
		this.cmd = operation;
		this.args = args;
	}
	
	/**
	 * Implementa o execute herdado do comando.
	 * 
	 * Recebe os tipos de operações e as executa.
	 */
	public void execute(Gerador g)
			throws TiraTeimaLanguageException {
		
		AbstractArquivo arq;
		
		switch (cmd.kind) {
			case KW_ASSIGN:
			case KW_RESET:
			case KW_REWRITE:
			case KW_READ:
			case KW_READLN:
			case KW_CLOSE:
				if ((arq = getFile(g, args)) == null)
					gerarErro("Arquivo não encontrado!");
				args.remove(0);
				executeFileOperation(g, cmd, arq, args);
				break;
			case KW_WRITE:
			case KW_WRITELN:
				try {
					if ((arq = getFile(g, args)) == null)
						gerarErro("Arquivo não encontrado!");
					args.remove(0);
					executeWriteOperation(g, cmd, arq, args);
					break;
				} catch (TiraTeimaLanguageException e) {
					executeWriteOperation(g, cmd, null, args);
				}
				break;
			case KW_COMMENT:
				executeAlertOperation(g, cmd, args);
				break;
			case KW_SOUND:
				executeSoundOperation(g, cmd);
				break;
		}
	}

	/**
	 * Recebe um gerador e argumentos e retorna um arquivo.
	 * 
	 * Usada para pegar o arquivo do gerenciador de arquivos.
	 * @param g
	 * @param args
	 * @return
	 * @throws TiraTeimaLanguageException
	 */
	private AbstractArquivo getFile(Gerador g, List<Object> args)
			throws TiraTeimaLanguageException {
		ListIterator<Object> i = args.listIterator();
		if (!i.hasNext())
			gerarErro("Nome de arquivo era esperado");
		
		/** checa se o primeiro é um arquivo*/
		Object obj = i.next();
		if (obj instanceof Stack<?>){
			Stack<Object> stack = (Stack<Object>) obj;
			if (stack.size() == 1) {
				String name = (String) stack.pop();
				return g.ga.getArquivo(name);
			}
			else
				gerarErro("Nome de arquivo era esperado.");
		}
		else
			gerarErro("Nome de arquivo era esperado.");
		
		return null;
	}

	/**
	 * Executa uma operação de arquivo.
	 * 
	 * Recebe um tipo de operação sobre o arquivo e gera erro se houver problema. 
	 * Caso não haja erro, executa a operação solicitada sobre o arquivo.
	 * 
	 * @param g
	 * @param cmd
	 * @param arq
	 * @param args
	 * @throws TiraTeimaLanguageException
	 */
	private void executeFileOperation(
			Gerador g,
			Token cmd,
			AbstractArquivo arq,
			List<Object> args) throws TiraTeimaLanguageException {
		Object aux;
		
		try {
			switch (cmd.kind) {
				case KW_ASSIGN:
					if (args.size() != 1)
						gerarErro("Número de parâmetros incorreto para comando 'assign'.");
					aux = args.get(0);
					if (!(aux instanceof String)) {
						gerarErro("Uma string era esperada.");
					}
					arq.assign((String) aux);
					break;
				case KW_RESET:
					if (args.size() != 0)
						gerarErro("Número de parâmetros incorreto para comando 'reset'.");
					arq.reset();
					break;
				case KW_REWRITE:
					if (args.size() != 0)
						gerarErro("Número de parâmetros incorreto para comando 'rewrite'.");
					arq.rewrite();
					break;
				case KW_CLOSE:
					if (args.size() != 0)
						gerarErro("Número de parâmetros incorreto para comando 'close'.");
					arq.close();
					break;
				case KW_READ:
				case KW_READLN:
					executeReadOperation(g, cmd, arq, args);
					break;
			}
		} catch (Exception e) {
			gerarErro("Falha ao executar operação no arquivo.");
		}
	}

	/**
	 * Executa operação de leitura do arquivo solicitada pelo usuário. 
	 * @param g
	 * @param cmd
	 * @param arq
	 * @param args
	 * @throws TiraTeimaLanguageException
	 * @throws Exception
	 */
	private void executeReadOperation(
			Gerador g,
			Token cmd,
			AbstractArquivo arq,
			List<Object> args) throws TiraTeimaLanguageException, Exception {
		/** verifica número de parâmetros */
		int size = args.size();
		if ((size < 1) || (size > 2)) {
			gerarErro("Número de parâmetros incorreto para comando 'read'.");
		}
		
		/** pega o tipo da variável a ser lida */
		Object first = args.get(0);
		if (!(first instanceof Stack))
			gerarErro("Era esperada uma variável.");
		ListIterator<Object> i = ((Stack) first).listIterator();
		Variavel v = g.mostrador.getCopiaVariavel((String) i.next());
		while (i.hasNext()) {
			Object aux = i.next();
			if (aux instanceof String) {
				v = ((VarRecord) v).getCopiaCampo((String) aux);
			} else {
				v = ((VarGrade) v).getCopiaTipo();
			}
		}
		
		Object value = null;
		if (v instanceof VarString) {
			if (size == 2) {
				Object aux = args.get(1);
				if (!(aux instanceof Integer)) {
					gerarErro("Era esperado um inteiro positivo.");
				}
				
				int length = (Integer) aux;
				value = ((VarText) arq).readString(length);
				
				if (cmd.kind == KW_READLN)
					((VarText) arq).readln();
			} else {
				value = ((VarText) arq).readLine();
			}
		} else {
			if (size > 1)
				gerarErro("Número de parâmetros incorreto para comando 'read'.");
			
			if (v instanceof VarReal) {
				value = ((VarText) arq).readReal();
			} else if (v instanceof VarInteger) {
				value = ((VarText) arq).readInt();
			} else if (v instanceof VarChar) {
				value = ((VarText) arq).read();
			}
			
			if (cmd.kind == KW_READLN)
				((VarText) arq).readln();
		}
		
		setValue(g.mostrador, (Stack) first, value);
	}
	
	/**
	 * Executa operação de escrita no arquivo.
	 * @param g
	 * @param cmd
	 * @param arq
	 * @param args
	 */
	private void executeWriteOperation(
			Gerador g,
			Token cmd,
			AbstractArquivo arq,
			List<Object> args) {
		
		ListIterator<Object> i = args.listIterator();
		Object obj;
		
		while (i.hasNext()) {
			obj = i.next();
			
			if (obj instanceof List<?>)
				obj = getValue(g, (List<Object>) obj);
			
			//escreve no arquivo
			if (arq != null) {
				((VarText) arq).write(obj.toString());
			}
			else { //escreve no console
				g.console.print(obj.toString());
			}
		}
		
		if (cmd.kind == KW_WRITELN) {
			if (arq != null) {
				((VarText) arq).writeln("");
			}
			else { //escreve no console
				g.console.println();
			}
		}
	}
	
	/**
	 * Executa a operação de alerta mostrando um alerta no tirateima 
	 * @param g
	 * @param cmd
	 * @param args
	 */
	private void executeAlertOperation(
			Gerador g,
			Token cmd,
			List<Object> args) {
		
		ListIterator<Object> i = args.listIterator();
		Object obj;
		
		while (i.hasNext()) {
			obj = i.next();
			
			if (obj instanceof List<?>)
				obj = getValue(g, (List<Object>) obj);
			
			//marca estado para mostrar um comentario
			g.alerta.print(obj.toString());
		}
	}
	
	/**
	 * Executa operação de som, tocando um som para o usuário.
	 * @param g
	 * @param cmd
	 */
	private void executeSoundOperation(
			Gerador g,
			Token cmd) {
			//marca estado para tocar um som
			g.alerta.tocaSom();
		}
}
