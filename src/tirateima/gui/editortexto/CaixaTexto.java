package tirateima.gui.editortexto;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

class FullLineHighlightPainter implements Highlighter.HighlightPainter {
	Color color;
	
	public FullLineHighlightPainter(Color c){
		color = c;
	}
	
	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		Rectangle r0 = null, r1 = null;
		
		try {  // convert positions to pixel coordinates
			r0 = c.modelToView(p0);
			r1 = c.modelToView(p1);
		} catch (BadLocationException ex) { return; }
		
		if ((r0 == null) || (r1 == null)) return;
		
		g.setColor(color);
		
		// special case if p0 and p1 are on the same line
		if (r0.y == r1.y) {
			g.fillRect(0, r0.y, c.getWidth(), r0.height);
		}else{
			g.fillRect(0, r0.y, c.getWidth(), r1.y - r0.y + r1.height);
		}
		return;
	}
}

/**
 * Modela uma caixa de texto com código-fonte em Pascal.
 * 
 * @author Renan e Luciano Santos
 */
@SuppressWarnings("serial")
public class CaixaTexto extends JTextPane implements DocumentListener{
	private boolean analisando = false;
	private boolean mudou = false;
	
	/* Posição inicial de cada linha. */
	List<Integer> linhas;
	
	/*Define se o texto conterá higlighting ou não...*/
	//TODO implementar o highlight para C para poder voltar para true
	private boolean highlighted = true;
	
	/*Define qual linha deve ser destacada. Se for menor que zero, nenhuma.*/
	private int linha_destacada = -1;
	
	/*Índices de acesso aos nomes dos estilos...*/
	public static final String[] estilos = {"regular", "palavra_chave",
		"literal", "comentario", "pontuacao", "num", "tipo"};
	
	/*Cores do highlighting e do destaque.*/
	private Color cores[] = {Color.BLACK, Color.BLUE, Color.RED,
			new Color(180,180,180),Color.BLACK, new Color(180, 50, 180),
			new Color(0, 100, 50), new Color(255, 255, 0)};
	
	/*Fonte básica...*/
	Font fonte_basica = new Font("Courier New", Font.PLAIN, 14);
	
	
	/*Constantes para identificar as cores a serem alteradas...*/
	public static final int REGULAR 	  	= 0;
	public static final int PALAVRA_CHAVE 	= 1;
	public static final int LITERAL 		= 2;
	public static final int COMENTARIO 		= 3;
	public static final int PONTUACAO 		= 4;
	public static final int NUMEROS 		= 5;
	public static final int TIPO 			= 6;
	public static final int DESTAQUE 		= 7;
	
	
	/**
	 * Constrói nova CaixaTexto usando o método construtor básico.
	 */
    public CaixaTexto() {
    	this("");
    }
    
    /**
	 * Constrói nova CaixaTexto.
	 * 
	 * @param texto texto inicial da caixa de texto.
	 */
    public CaixaTexto(String texto){
    	super();
    	addStyles();
    	setText(texto);
    	getStyledDocument().addDocumentListener(this);
    	addKeyListener(new KeyListener() {
    		public void keyPressed(KeyEvent e){}
    		
    		public void keyReleased(KeyEvent e){}
    		
    		public void keyTyped(KeyEvent e){
    			if(mudou){
    				parseText();
    				mudou = false;
    			}
    		}
    	});
    }
	
    public void setText(String texto){    	
    	try{
    		linhas = getLinhas(texto);
    	}catch(Exception e){
    		linhas = new ArrayList<Integer>();
    	}
    	
    	parseText(texto);
    }
    
    public void setText(Reader reader) throws IOException{
    	StringBuffer s = new StringBuffer("");
		BufferedReader r = new BufferedReader(reader);
		String linha = null;
		try{
			while((linha = r.readLine()) != null)
				s.append(linha + "\n");
		}catch(IOException e){
			throw e;
		}
    	
    	this.setText(s.toString());
    }
    
    public void setHighlighted(boolean high){
    	if(highlighted != high){
    		highlighted = high;
    		parseText();
    	}
    }
    
    public void setMarcada(int linha){
    	if(linha != linha_destacada){
	    	if(linha > linhas.size()){
	    		linha_destacada = linhas.size();
	    	}else{
	    		linha_destacada = linha;
	    	}
	    	
	    	try{
		    	DefaultHighlighter dh = (DefaultHighlighter) this.getHighlighter();
		    	dh.removeAllHighlights();
		    	
		    	if(linha_destacada > 0){
		    		int i = linhas.get(linha_destacada - 1);
		    		dh.addHighlight(i, i, new FullLineHighlightPainter(cores[DESTAQUE]));
		    		
		    	}
		    	
		    	this.update(this.getGraphics());
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
    	}
    }
    
    public void setBaseFont(Font f){
    	this.fonte_basica = f;
    	addStyles();
    	parseText();
    }
    
    /**
     * 	Muda uma das cores de highlighting/destaque.
     * 
     *  Espera uma constante definindo qual cor deve ser mudada.
     *  
     * @param const_cor Espera uma das constantes informando qual cor deve ser alterada.
     * @param c Nova cor.
     */
    public void setColor(int const_cor, Color c){
    	if((const_cor >= REGULAR) && (const_cor <= TIPO)){
    		cores[const_cor] = c;
    	}else{
    		cores[REGULAR] = c;
    	}
    	
    	addStyles();
    	
    	parseText();
    }
    
    public int getMarcada(){
    	return linha_destacada;
    }
    
    public String adicionaNumeracao(String texto){
    	StringBuffer sb = new StringBuffer();
    	int carret = 0;
    	for(int i=0; i<texto.length(); i++){
    		sb.append(i+1 + " ");
    		int index = texto.substring(carret).indexOf("\n");
    		if(index > -1){
    			sb.append(texto.substring(carret, carret + index) + "\n");
    			carret += ++index;
    		}else{
    			break;
    		}
    	}
    	
    	return sb.toString();
    }
    
    public int getTotalLinhas(){
    	StringTokenizer st = new StringTokenizer(getText(), "\n");
    	return st.countTokens();
    }
    
    private void parseText(){
    	parseText(null);
    }
    
    private void parseText(String text){
    	String texto = text == null ? getText() : text;
    	super.setText("");
    	StyledDocument doc = getStyledDocument();
    	analisando = true;
    	if(highlighted){
	    	Anlex anlex = new Anlex(texto);
	    	Token t = anlex.getToken();
	    	boolean coment_chave = false;
	    	boolean coment_par = false;
	    	String estilo;
	    	int line = 1;
	    	boolean insert_line = true;
	    	while(t.getId() != Token.EOB){
	    		estilo = estilos[REGULAR];
	    		switch(t.getId()){
					case Token.IDENTIFIER:
						if(Token.ehPalavraChave(t.getValor())){
							estilo = estilos[PALAVRA_CHAVE];
						}else if(Token.ehTipo(t.getValor())){
							estilo = estilos[TIPO];
						}
						break;
					case Token.STRING:
						estilo = estilos[LITERAL];
						break;
					case Token.BEGINCOMMENT_CH:
						if(!coment_par){
							estilo = estilos[COMENTARIO];
							coment_chave = true;
						}
						break;
					case Token.ENDCOMMENT_CH:
						if(coment_chave){
							estilo = estilos[COMENTARIO];
							coment_chave = false;
						}
						break;
					case Token.BEGINCOMMENT_PAR:
						if(!coment_chave){
							estilo = estilos[COMENTARIO];
							coment_par = true;
						}
						break;
					case Token.ENDCOMMENT_PAR:
						if(coment_par){
							estilo = estilos[COMENTARIO];
							coment_par = false;
						}
						break;
					case Token.PONT:
						estilo = estilos[PONTUACAO];
						break;
					case Token.NUM:
						estilo = estilos[NUMEROS];
						break;
				}
	    		
	    		if(coment_par || coment_chave)
	    			estilo = estilos[COMENTARIO];
	    		
	    		try{
	    			if(insert_line){
//	    				doc.insertString(doc.getLength(), line + " ", doc.getStyle(estilos[NUMEROS]));
	    				insert_line = false;
	    				line++;
	    			}
	    			if("\n".equals(t.getValor())){
	    				insert_line = true;
	    			}
	    			doc.insertString(doc.getLength(), t.getValor(), doc.getStyle(estilo));
	    		}catch(Exception e){}
	    		
	    		t = anlex.getToken();
	    	}
    	}else{
    		try{
    			doc.insertString(0, texto, doc.getStyle(estilos[REGULAR]));
    		}catch(Exception e){}
    	}
    	analisando = false;
    }
    
    /**
     * Divide o texto em linhas e retorna a posição de cada uma.
     * 
     * @param origem Origem do texto.
     * @return Posição inicial de cada linha no texto.
     * 
     * @throws IOException
     */
    private List<Integer> getLinhas(String origem) throws IOException{
		List<Integer> lista = new ArrayList<Integer>();
		
		BufferedReader br = new BufferedReader(new StringReader(origem));
		String linha = null;
		int i = 0;
		while((linha = br.readLine()) != null){
			lista.add(i);
			/* Tamanho da linha + \n */
			i += linha.length() + 1;
		}
		return lista;
	}
    
	private void addStyles(){
		StyledDocument doc = getStyledDocument();
		Style def = StyleContext.getDefaultStyleContext().
        getStyle(StyleContext.DEFAULT_STYLE);
		
		/*Remove estilos antes de adicionar...*/
		for(int i = 0; i <= TIPO; i++){
			doc.removeStyle(estilos[i]);
			doc.addStyle(estilos[i], def);
		}
		
		/*Estilo para texto qualquer...*/
		Style s = doc.getStyle("regular");
		StyleConstants.setFontFamily(def, fonte_basica.getFamily());
		StyleConstants.setFontSize(def, fonte_basica.getSize());
		
		/*Estilo para palavras chave...*/
		s = doc.getStyle("palavra_chave");
		StyleConstants.setBold(s, true);
		StyleConstants.setForeground(s, cores[PALAVRA_CHAVE]);
		
		/*Estilo para literais...*/
		s = doc.getStyle("literal");
		StyleConstants.setBold(s, false);
		StyleConstants.setForeground(s, cores[LITERAL]);
		
		/*Estilo para comentários...*/
		s = doc.getStyle("comentario");
		StyleConstants.setBold(s, false);
		StyleConstants.setItalic(s, true);
		StyleConstants.setForeground(s, cores[COMENTARIO]);
		
		/*Estilo para pontuação...*/
		s = doc.getStyle("pontuacao");
		StyleConstants.setBold(s, true);
		StyleConstants.setItalic(s, false);
		StyleConstants.setForeground(s, cores[PONTUACAO]);
		
		/*Estilo para numeração...*/
		s = doc.getStyle("num");
		StyleConstants.setBold(s, false);
		StyleConstants.setItalic(s, false);
		StyleConstants.setForeground(s, cores[NUMEROS]);
		
		/*Estilo para tipos...*/
		s = doc.getStyle("tipo");
		StyleConstants.setBold(s, true);
		StyleConstants.setItalic(s, false);
		StyleConstants.setForeground(s, cores[TIPO]);
	}
	
	public void insertUpdate(DocumentEvent e){
		mudou();
	}
	
	public void removeUpdate(DocumentEvent e){
		mudou();
	}
	
	public void changedUpdate(DocumentEvent e){}
	
	private void mudou(){
		if(!analisando){
			mudou = true;
		}
	}
}
