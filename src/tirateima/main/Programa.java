/* Copyright (C) 2007  Felipe A. Lessa
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package tirateima.main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import tirateima.gui.Principal;


/**
 * 
 * Classe responsável por fazer o AlgoStep se comportar como um programa
 * standalone no computador.
 * 
 * @author Felipe Lessa
 * @author Luciano Santos
 * @author Renan Leandro
 * @author Andrew Biller
 */
@SuppressWarnings("serial")
public class Programa extends JFrame {
	private Principal principal;
	
	/**
	 * Método Construtor que inicia o programa sem nenhum argumento.
	 * Como não foram especificados o arquivo fonte e o roteiro, ele abrirá a tela vazia.
	 */
	private Programa() throws Exception {
		super("Tira-Teima");
		principal = new Principal();
		inicializar();
	}
	/**
	 * Método construtor que recebe 2 strings com os caminhos dos arquivos de parâmetros
	 * Cria e inicializa os componentes
	 * @param arq_fonte Arquivo Fonte .PAS
	 * @param arq_texto Arquivo Texto Roteiro .TXT
	 */
	public Programa(String arq_fonte, String arq_texto) throws Exception{
		this(new FileReader(arq_fonte), new FileReader(arq_texto));
	}
	/**
	 * Método Construtor que lê caracteres com os nome dos arquivos fonte e texto.
	 * Cria e inicializa os componentes
	 * @param fonte Streamer com o texto do arquivo fonte
	 * @param texto Streamer com o texto do arquivo texto
	 * @throws Exception
	 */
	public Programa(Reader fonte, Reader texto) throws Exception{
		super("Tira-Teima");
		principal = new Principal(fonte, texto);
		inicializar();
	}
	
	/**
	 * Cria uma nova janela do programa usando um 'Principal' existente.
	 * Este não deve ser usado em outro 'Programa'.
	 */
	public Programa(Principal principal) {
		super("Tira-Teima");
		this.principal = principal;
		inicializar();
	}
	
	/**
	 * Retorna a classe principal.
	 */
	public Principal getPrincipal() {
		return principal;
	}
	
	private void inicializar() throws HeadlessException{
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(principal, c);
		//Inicia a tela no modo maximizado
		setExtendedState(MAXIMIZED_BOTH);
	}
	
	/**
	 * Função principal, responsável por iniciar o programa.
	 * @param args  Recebe como argumento uma String[2] com o nome do arquivo .pas em String[0] e o roteiro.txt[1] 
	 * @throws IOException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception, IOException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		Programa programa;
		String msg = null;
		int msg_type = JOptionPane.ERROR_MESSAGE;
		
		
		if (args.length > 0) {
			String arq_texto, arq_fonte;
			
			if (args.length == 1) {
				String nome_base = args[0];
				arq_texto = nome_base + ".txt";
				arq_fonte = nome_base + ".pas";
			} else {
				arq_fonte = args[0];
				arq_texto = args[1];
			}
			
			try {
				programa = new Programa(arq_fonte, arq_texto);
			} catch(FileNotFoundException e) {
				e.printStackTrace();
				
				programa = new Programa();
				
				File txt = new File(arq_texto);
				File pas = new File(arq_fonte);
				
				String nome_arq = txt.exists() ?
						pas.getPath() : txt.getPath();
				msg = new String("Não foi possível encontrar o arquivo: " +
						nome_arq + ".");
			}
		} else {
			programa = new Programa();
			msg = new String("Nenhum arquivo fornecido como parâmetro.");
			msg_type = JOptionPane.WARNING_MESSAGE;
		}
		
		programa.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		programa.setSize(800, 600);
		programa.setVisible(true);
		
		if(msg != null){
			JOptionPane.showMessageDialog(null, msg, "AlgoStep", msg_type);
		}
	}
}
