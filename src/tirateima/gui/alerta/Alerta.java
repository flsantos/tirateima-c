package tirateima.gui.alerta;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import tirateima.IEstado;
import tirateima.ui.Som;

/**
 * Classe que mostra um alerta e que controla a hora de tocar um som, caso especificado
 * @author Andrew
 *
 */
@SuppressWarnings("serial")
public class Alerta extends JTextPane implements IEstado {
	private Color corFundo = new Color(160, 0, 0);
	private Color corTexto = Color.white;
	private String comentario = null;
	private boolean som = false;
	private boolean somAtivado = true;
	
	public Alerta(){
		super();
		setBackground(corFundo);
		setForeground(corTexto);
		setEditable(false);
		setFontStyle();
		setText("");
	}
	
	public void setFontStyle(){
		SimpleAttributeSet bSet = new SimpleAttributeSet();  
        StyleConstants.setAlignment(bSet, StyleConstants.ALIGN_CENTER);  
        StyleConstants.setFontFamily(bSet, "lucida bold");  
        StyleConstants.setFontSize(bSet, 12);
        
        StyledDocument doc = getStyledDocument();  
        doc.setParagraphAttributes(0, 104, bSet, false);
	}
	
	public Alerta(String s){
		this();
		setText(s);
	}
	
	public void print(String s){
		comentario = s;
	}

	public void limpar(){
		comentario = null;
		som = false;
	}
	
	public void setEstado(Object estado){
		if (estado != null) {
			EstadoAlerta e = (EstadoAlerta) estado;
			
			if(e.comentario != null){
				//Mostra comentario
				super.setText(e.comentario);
			}else{
				super.setText("");
			}
			
			if(e.som){
				//Toca som se estiver ativo
				if(isSomAtivado()){
					new Som(true).start();
				}
			}
			
		}
	}
	
	public void tocaSom(){
		som = true;
	}
	
	public boolean isTocarSom() {
		return som;
	}
	
	public Object getEstado(){
		return new EstadoAlerta(comentario, som);
	}
	
	public boolean isSomAtivado() {
		return somAtivado;
	}

	public void setSomAtivado(boolean somAtivado) {
		this.somAtivado = somAtivado;
	}

	
	
	
	private class EstadoAlerta {
		public String comentario;
		public boolean som;
		
		public EstadoAlerta(String comentario, boolean som) {
			this.comentario = comentario;
			this.som = som;
			
			//limpa as variáveis externas para evitar lixo nos proximos estados
			limpar();
		}
	}

}
