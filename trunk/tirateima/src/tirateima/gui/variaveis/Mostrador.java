/* Copyright (C) 2007  Felipe A. Lessa e Luciano H. O. Santos
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
package tirateima.gui.variaveis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tirateima.IEstado;


/**
 * 
 * Mostra um conjunto de variáveis.
 * 
 * @author Felipe Lessa
 * @author Luciano Santos
 * @author Andrew Biller
 * @author Vinícius
 */
@SuppressWarnings("serial")
public class Mostrador extends JScrollPane implements IEstado {
	private static final Comparator<Color> comparadorColor = 
		new Comparator<Color>() {
			public int compare(Color c1, Color c2) {
				return new Integer(c1.getRGB()).compareTo(c2.getRGB());
			}
		};
	
	private static final Comparator<Variavel> comparadorVariavel = 
		new Comparator<Variavel>() {
			public int compare(Variavel v1, Variavel v2) {
				return v1.getName().compareToIgnoreCase(v2.getName());
			}
		};
	
	
	private double prop = -100.0;
	private Point ultimoPonto;
	private Variaveis vars = new Variaveis();
	private Setas setas = new Setas();
	private JPanel painelPrincipal;
	private Painel painelVars = null;
	private Function function = null;
	private JPanel tudo = new JPanel();
	public static enum zoom {AUMENTA, REINICIA, DIMINUI}
	public Enum<zoom> acaoZoom = null;
	
	/**
	 * Cria um novo mostrador vazio.
	 */
	public Mostrador() {
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
		      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		painelPrincipal = new JPanel();
		painelPrincipal.setLayout(new GridBagLayout());
		this.viewport.setView(painelPrincipal);
	}
	
	/**
	 * A proporção atual de todas as janelas.
	 * @return  a proporção (1.0 = 100%).
	 */
	public double getProporcao() {
		return prop;
	}
	
	/**
	 * Define a proporção a ser usada em todas as janelas.
	 * @param nova_prop
	 */
	public void setProporcao(double nova_prop) {
		if (prop == nova_prop)
			return;
			
		//esconde durante o zoom: evita flicker na tela
		viewport.setVisible(false);
		
		if (painelVars != null)
			painelVars.setProporcao(nova_prop);
		prop = nova_prop;
		this.validate();
		
		if (function != null) {
			function.setProporcao(nova_prop);
		}
		
		//mostra depois de desenhar o estado
		viewport.setVisible(true);
	}
	
	@Override
	public Insets getInsets() {
		// Força a proporção inicial
		if (prop <= 0)
			setProporcao(1.0);
		return super.getInsets();
	}
	
	/**
	 * Adiciona uma variável ao mostrador.
	 * @param v  variável a ser adicionada.
	 */
	public void adicionarVariavel(Variavel v) {
		if (function != null) {
			function.adicionarVariavel(v);
		} else {
			vars.adicionarVariavel(v);
		}
	}
	
	/**
	 * Remove uma variável do mostrador.
	 * @param nome  nome da variável a ser removida.
	 */
	public void removerVariavel(String nome) {
		vars.removerVariavel(nome);
	}
	
	/**
	 * Cria uma seta em uma variável
	 * @param nome
	 * @param direcao
	 * @param tamanho
	 */
	public void adicionarSeta(String nome, Seta s) {
		if(!hasVariavel(nome)){
			throw new RuntimeException("Variavel " + nome + " nao localizada.");
		}		
		if (function != null) {
			function.adicionarSeta(nome,s);
		} else {
			setas.adicionarSeta(nome, s);
		}
	}
	
	/**
	 * Modifica o valor de uma variável.
	 * @param nome   nome da variável a ser modificada.
	 * @param valor  seu novo valor.
	 */
	public boolean modificarVariavel(String nome, Object valor) {
		if (function != null) {
			if (function.modificarVariavel(nome, valor))
				return true;
		}
		
		if (hasVariavel(nome)) {
			vars.modificarVariavel(nome, valor);
			return true;
		}
		
		return false;
	}

	/**
	 * Retorna uma cópia de uma variável.
	 * 
	 * @param nome
	 * 
	 * @return
	 */
	public Variavel getCopiaVariavel(String nome) {
		return vars.getCopiaVariavel(nome);
	}
	
	public Object getEstado() {
		return new EstadoMostrador(
				new Painel(vars,setas),
				function);
	}

	public void setEstado(Object estado) {
		//esconde durante o desenho: evita flicker na tela
		painelPrincipal.setVisible(false);
		
		painelPrincipal.removeAll();
		GridBagConstraints gbc = new GridBagConstraints(
				0, 0,
				1, 1,
				1.0, 1.0,
				GridBagConstraints.FIRST_LINE_START,
				GridBagConstraints.FIRST_LINE_START,
				new Insets(0, 0, 0, 0),
				0, 0);
		
		if (estado != null) {
			EstadoMostrador e = (EstadoMostrador) estado;
			painelVars = e.painel;
			function = e.function;
			
			painelPrincipal.add(painelVars, gbc);
			painelVars.criar();
			
			painelVars.setProporcao(prop < 0 ? 1.0 : prop);
			
			if (function != null) {
				function.setEstado(e.estadoFunction);
				gbc.gridy++;
				gbc.ipadx = 100;
				gbc.ipady = 100;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				painelPrincipal.add(function, gbc);
			}
			
			Rectangle r1 = this.getBounds();
			Rectangle r2 = viewport.getViewRect();
			double x = r1.getCenterX() - r2.width/2,
			       y = r1.getCenterY() - r2.height/2;
			Point p = new Point((int) Math.round(x), (int) Math.round(y));
			viewport.setViewPosition(p);
		}

		
		validate();
		repaint();

		
		//mostra depois de ter desenhado o estado
		painelPrincipal.setVisible(Boolean.TRUE);
	}

	
	@Override
	public void processMouseEvent(MouseEvent e) {
		try {
			if (e.getID() != MouseEvent.MOUSE_PRESSED)
			{
				return;
			}
			ultimoPonto = e.getPoint();
		} finally {
			super.processMouseEvent(e);
		}
	}
      
	@Override
	public void processMouseMotionEvent(MouseEvent e) {
		try {
			if (painelVars == null || e.getID() != MouseEvent.MOUSE_DRAGGED)
			{
				return;
			}
			
			int diffX = e.getX() - ultimoPonto.x;
			int diffY = e.getY() - ultimoPonto.y;
			Dimension s = viewport.getExtentSize();
			Dimension m = painelVars.getSize();
			Point p = viewport.getViewPosition();
			double mult = -1.0; //Math.sqrt(prop);
			p.x += diffX * mult;
			p.y += diffY * mult;
			if (p.x < 0) p.x = 0;
			if (p.y < 0) p.y = 0;
			if (p.x + s.width > m.width) p.x = m.width - s.width;
			if (p.y + s.height > m.height) p.y = m.height - s.height;
			
			viewport.setViewPosition(p);
			
			ultimoPonto = e.getPoint();
		} finally {
			super.processMouseMotionEvent(e);
		}
	}
	
	public void startFunction(Function f) {
		if (function != null) {
			function.startFunction(f);
		} else {
			function = f;
		}
	}
	
	public boolean endFunction() {
		if (function != null) {
			if (!function.endFunction()) {
				function = null;
			}
			
			return true;
		}
		
		return false;
	}
	
	public boolean hasVariavel(String nome) {
		return vars.contains(nome) ||
			(function != null ? function.hasVariavel(nome) : false);
	}	
	
	/**
	 * Contém as variáveis *sem* mostrá-las.
	 */
	protected class Variaveis {
		private HashMap<String, Variavel> variaveis;

		/**
		 * Constrói um contâiner vazio.
		 */
		public Variaveis() {
			this.variaveis = new HashMap<String, Variavel>();
		}

		/**
		 * Adiciona uma variável ao contêiner.
		 * @param v  variável a ser adicionada.
		 */		
		public void adicionarVariavel(Variavel v) {
			variaveis.put(v.getName(), v);
		}
		
		/**
		 * Remove uma variável do contêiner.
		 * @param nome  nome da variável a ser removida.
		 */
		public void removerVariavel(String nome) {
			variaveis.remove(nome);
		}
		
		/**
		 * Modifica o valor de uma variável no contêiner.
		 * @param nome   nome da variável a ser modificada.
		 * @param valor  seu novo valor.
		 */
		public void modificarVariavel(String nome, Object valor) {
			Variavel v = variaveis.get(nome);
			if (v == null)
				throw new RuntimeException("Variável " + nome + " não existe.");
			v.setValor(valor);
		}
		
		/**
		 * Retorna uma cópia de uma variável.
		 * 
		 * @param nome
		 * @return
		 */
		public Variavel getCopiaVariavel(String nome) {
			Variavel v = variaveis.get(nome);
			if (v == null)
				throw new RuntimeException("Variável " + nome + " não existe.");
			return v.criarCopia();
		}
		
		/**
		 * Retorna uma cópia das variáveis como um HashMap separado
		 * por cores.
		 */
		public HashMap<Color, ArrayList<Variavel>> criarCopia() {
			HashMap<Color, ArrayList<Variavel>> ret = new
				HashMap<Color, ArrayList<Variavel>>();
			for (Variavel v : variaveis.values()) {
				Color cor = v.getCorTitulo();
				Variavel copia = v.criarCopia();
				if (ret.containsKey(cor)) {
					ret.get(cor).add(copia);
				} else {
					ArrayList<Variavel> novo = new ArrayList<Variavel>();
					novo.add(copia);
					ret.put(cor, novo);
				}
			}
			return ret;
		}
		
		public boolean contains(String nome) {
			return variaveis.containsKey(nome);
		}
	}
	
	protected class Setas {
		private HashMap<String, Seta> setas;
		
		/**
		 * Constroi um container vazio.
		 */
		public Setas(){
			this.setas = new HashMap<String, Seta>();
		}
		
		
		
		public HashMap<String, Seta> getSetas() {
			return setas;
		}



		/**
		 * Adiciona uma seta
		 * @param String nome a ser adicionado.
		 * @param Seta s a ser adicionada.
		 */
		public void adicionarSeta(String nome, Seta s){
			this.setas.put(nome, s);
		}
		
		/**
		 * Remove uma seta
		 */
		public void removerSeta(String nome){
			setas.remove(nome);
		}
		
		/**
		 * Descobre se uma seta esta contida no conjunto
		 * @param nome
		 * @return
		 */
		public boolean contains(String nome) {
			return setas.containsKey(nome);
		}



		public Setas criarCopia() {
			Setas setasCopia = new Setas();
			for(Seta seta : setas.values()){
				Seta setaCopia = seta.criarCopia();
				setasCopia.adicionarSeta(seta.nome, setaCopia);
			}
			return setasCopia;
		}

	}
	
	/**
	 * Painel que mostra variáveis *estaticamente*, i.e. o conteúdo 
	 * delas *não* deve ser alterado. Este é o estado! =)
	 */
	protected class Painel extends JPanel {
		private HashMap<Color, ArrayList<Variavel>> mapaVariaveis;
		private ArrayList<Janela> janelas = new ArrayList<Janela>();
		private Setas setas;

		/**
		 * Cria um painel. 
		 * @see #criar()
		 */
		public Painel(Variaveis v,Setas setas) {
			super();
			assert (v != null);
			this.mapaVariaveis = v.criarCopia();
			this.setas = setas.criarCopia();
			setLayout(new GridBagLayout());
		}


		public void adicionarSeta(String nome, Seta s) {
			if(this.setas == null){
				this.setas = new Setas();
			}
			this.setas.adicionarSeta(nome, s);
		}


		/**
		 * Cria os painéis interiores. Chame este método *após* adicionar
		 * este painel a outro componente.
		 * Desenha todas as variáveis na tela do mostrador
		 */
		public void criar() {
			if (mapaVariaveis == null) return;
			
			//Absolute Positioning (sem layout para permitir o posicionamento pelo usuario)
			tudo.setLayout(null);
			tudo.removeAll();
			this.add(tudo);
			List<Point> coordenadas = new ArrayList<Point>();
			int maxHeight = 0;
			int maxWidth = 0;
			Color[] cores = mapaVariaveis.keySet().toArray(new Color[] {});
			Arrays.sort(cores, comparadorColor);
			for (Color cor : cores) {
				Variavel[] vars = mapaVariaveis.get(cor).toArray(new Variavel[] {});
				Arrays.sort(vars, comparadorVariavel);			
				for (Variavel v : vars) {
					Janela j = new Janela(v);
					tudo.add(j);
					janelas.add(j);
					j.validate();
					Dimension size = v.dimensao;
					if(size == null) size = j.getSize();
					if(size.height > maxHeight) maxHeight = size.height;
					if(size.width > maxWidth) maxWidth = size.width;
					
					Point point = v.posicao;
					if(point == null) point = j.getLocation();
					coordenadas.add(point);
					
					j.setPreferredSize(size);
					j.setLocation(point);
					j.posicaoOriginal = point;
					if (this.setas.contains(v.nome)){
						Seta seta = setas.setas.get(v.nome);
						tudo.add(seta);
						seta.validate();
						Point pontoSeta = seta.calculaPosicao(v);
						coordenadas.add(pontoSeta);
						seta.setLocation(pontoSeta);
						seta.posicaoOriginal = pontoSeta;
						tudo.setComponentZOrder(seta, tudo.getComponentZOrder(j) - 1);
					}
				}
			}
		}

		/**
		 * Define a proporção de todas as janelas contidos neste painel.
		 * @param prop   nova propoprção.
		 */
		public void setProporcao(double prop) {
			for (Janela j : janelas){
				j.setProporcao(prop);
			}
			for (Seta s : setas.setas.values()){
				s.setProporcao(prop);
			}
			
			calculaZoom(janelas,setas, prop);
			this.validate();
			this.validate();
			this.validate();
		}
	
		/**
		 * Reconfigura o tamanho da tela após o zoom por causa do layout de Absolute Positioning
		 * @param janelas
		 * @param prop
		 */
		private void calculaZoom(ArrayList<Janela> janelas, Setas setas, double prop){
			prop = Math.nextUp(prop);
			Dimension real = new Dimension(0,0);
			for (Janela j : janelas){
				int novaAltura = (int) ((j.getHeight() + j.getY() + 100 ));
				int novaLargura = (int) (( j.getWidth() + j.getX() + 100));
				
				if(novaAltura > real.height) real.height = novaAltura;
				if(novaLargura > real.width) real.width = novaLargura;
				
				//Configura o tamanho visível da tela com todas as variáveis em consideração
				tudo.setPreferredSize(new Dimension(real));
				
				int tmp;
				//Aumenta a distancia entre as janelas também
				if(acaoZoom != null){
					if(j.getLocation().x > 0){
						tmp = j.getLocation().y;
						if(acaoZoom == zoom.AUMENTA && prop > 1){
							j.setLocation((int) (j.posicaoOriginal.x * (prop)), tmp);
						}
						
						if(acaoZoom == zoom.DIMINUI && prop > 1){
							j.setLocation((int) (j.posicaoOriginal.x * (prop)), tmp);
						}
					}
						
					if(j.getLocation().y > 0){
						tmp = j.getLocation().x;
						if(acaoZoom == zoom.AUMENTA && prop > 1){
							j.setLocation(tmp, (int) (j.posicaoOriginal.y * (prop)));
						}
						if(acaoZoom == zoom.DIMINUI && prop > 1){
							j.setLocation(tmp, (int) (j.posicaoOriginal.y * (prop)));
						}
					}
					
					//Reset de zoom
					if(acaoZoom == zoom.REINICIA){
						if(j.getVariavel().posicao != null)
							j.setLocation(j.getVariavel().posicao);
					}
					
				//Atualizacao se refere a uma nova tela. Manter zoom escolhido
				}else{
					if(j.getLocation().x > 0){
						tmp = j.getLocation().y;
						if(prop > 1)
							j.setLocation((int) (j.getLocation().x * (prop)), tmp);
					}
						
					if(j.getLocation().y > 0){
						tmp = j.getLocation().x;
						if(prop > 1)
							j.setLocation(tmp, (int) (j.getLocation().y * (prop)));
					}
					
					//Reset de zoom
					if(prop == 1){
						if(j.getVariavel().posicao != null)
							j.setLocation(j.getVariavel().posicao);
						else
							j.setLocation(j.getVariavel().getLocation());
					}
				}
				
			}
			for (Seta s : setas.setas.values()){				
				int tmp;
				//Aumenta a distancia entre as janelas também
				if(acaoZoom != null){
					if(s.getLocation().x > 0){
						tmp = s.getLocation().y;
						if(acaoZoom == zoom.AUMENTA && prop > 1){
							s.setLocation((int) (s.posicaoOriginal.x * (prop)), tmp);
						}
						
						if(acaoZoom == zoom.DIMINUI && prop > 1){
							s.setLocation((int) (s.posicaoOriginal.x * (prop)), tmp);
						}
					}
						
					if(s.getLocation().y > 0){
						tmp = s.getLocation().x;
						if(acaoZoom == zoom.AUMENTA && prop > 1){
							s.setLocation(tmp, (int) (s.posicaoOriginal.y * (prop)));
						}
						if(acaoZoom == zoom.DIMINUI && prop > 1){
							s.setLocation(tmp, (int) (s.posicaoOriginal.y * (prop)));
						}
					}
					
					//Reset de zoom
					if(acaoZoom == zoom.REINICIA){
						s.setLocation(s.posicaoOriginal);
					}
					
				//Atualizacao se refere a uma nova tela. Manter zoom escolhido
				}else{
					if(s.getLocation().x > 0){
						tmp = s.getLocation().y;
						if(prop > 1)
							s.setLocation((int) (s.getLocation().x * (prop)), tmp);
					}
						
					if(s.getLocation().y > 0){
						tmp = s.getLocation().x;
						if(prop > 1)
							s.setLocation(tmp, (int) (s.getLocation().y * (prop)));
					}
					
					//Reset de zoom
					if(prop == 1){
						s.setLocation(s.posicaoOriginal);
					}
				}				
			}	
			acaoZoom = null;
			validate();
			repaint();
		}
	}
	
	private class EstadoMostrador {
		public Painel painel;
		public Function function;
		public Object estadoFunction = null;
		
		public EstadoMostrador(Painel painel, Function function) {
			this.painel = painel;
			this.function = function;
			
			if (function != null)
				this.estadoFunction = function.getEstado();
		}
	}
	
	/**
	 * Remove a seta relativa a variável, caso haja se ela for um ponteiro.
	 * @param nome_var
	 */
	public void removerSeta(String nome_var) {
		if(setas.setas.containsKey(nome_var)){
			setas.setas.remove(nome_var);
		}		
	}
}