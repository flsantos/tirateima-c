// Copyright (C) 2007  Luciano Santos e Ian Schechtman
package tirateima.controlador;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;

import tirateima.IEstado;


public class Controlador {
	/*Botões de navegação.*/
	private JButton btnAnt;
	private JButton btnProx;
	private JButton btnReiniciar;
	private JButton btnPular;
	private JTextField txtLinha;
	
	private IEstado mostrador;
	private IEstado editor;
	private IEstado console;
	private IEstado alerta;
	private IEstado ga;
	
	/*Lista dos estados.*/
	List<Estado> estados = new ArrayList<Estado>();
	
	/*Índice para controle da lista de estados. */
	private int indice;
	
	public Controlador(List<Estado> estados,
	                IEstado mostrador, IEstado ga,
	                IEstado editor, IEstado console,
	                IEstado alerta,
	                JButton btnAnt, JButton btnProx,
	                JButton btnReiniciar, JButton btnPular, JTextField txtLinha) {
	    if(estados != null){
	            this.estados = estados;
	    }
	
	    indice = -1;
	
	    this.mostrador = mostrador;
	    this.ga = ga;
	    this.editor = editor;
	    this.console = console;
	    this.alerta = alerta;
	    
	    mostrador.setEstado(null);
	    ga.setEstado(null);
        editor.setEstado(null);
        console.setEstado(null);
        alerta.setEstado(null);
        
	    /*Botão para estado anterior.*/
	    this.btnAnt = btnAnt;
	    /*Cria evento.*/
	    btnAnt.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
		        antEstado();
		    }
		});
	
        /*Botão para próximo estado.*/
        this.btnProx = btnProx;
        /*Cria evento.*/
        btnProx.addActionListener(new java.awt.event.ActionListener() {
		    public void actionPerformed(java.awt.event.ActionEvent evt) {
		        proxEstado();
		    }
		});
        
        /*Botão para reiniciar.*/
        this.btnReiniciar = btnReiniciar;
        /*Cria evento.*/
        btnReiniciar.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		indice = 0;
        		ajustarBotoes();
        		setEstado(0); //Reinicia
        	}
        });
        
        /*Botão para pular linha.*/
        this.btnPular = btnPular;
        /*Cria evento.*/
        btnPular.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		pularEstado();
        	}
        });
        
        /*Caixa de texto para pular linha.*/
        this.txtLinha = txtLinha;
        /*Cria evento. Le botao 'enter' */
        txtLinha.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		pularEstado();
        	}
        });
        
        ajustarBotoes();
	}
	
	private void ajustarBotoes(){
		/* Ativar/desativar botão "Anterior" */
		btnAnt.setEnabled(indice > 0);
		btnReiniciar.setEnabled(indice > 0);
		btnPular.setEnabled(true);
		
		/* Ativar/desativar botão "Próximo" */
		btnProx.setEnabled(indice < estados.size()-1);
		
		/* Definir título do botão "Próximo" */
		btnProx.setText(indice < 0 ? "Iniciar" : "Próximo");
		final ImageIcon iconeIniciar = new ImageIcon(getClass().getResource("/resources/iniciar.png"));
		final ImageIcon iconeProximo = new ImageIcon(getClass().getResource("/resources/proximo.png"));
		btnProx.setIcon(indice < 0 ? iconeIniciar : iconeProximo);
	}
        
	private void setEstado(Estado e){
		mostrador.setEstado(e.est_mostrador);
		editor.setEstado(e.est_editor);
		console.setEstado(e.est_console);
		alerta.setEstado(e.est_alerta);
		ga.setEstado(e.est_ga);
		ajustarBotoes();
	}
        
	public void proxEstado (){
		if(indice < estados.size()-1){
			Estado e = (Estado) estados.get(++indice);
			setEstado(e);
		}
	}
	
	public void antEstado (){
		if((indice > 0)){
			Estado e = (Estado) estados.get(--indice);
			setEstado(e);
		}
	}
	
	public void pularEstado (){
		try{
			int linhaUsuario = Integer.valueOf(txtLinha.getText());
			if(linhaUsuario>0){
				Integer estado;
				for(int contador=0; contador < estados.size(); contador++){
					estado = (Integer) estados.get(contador).est_editor;
					if(linhaUsuario <= estado){
						setEstado(contador);
						indice = contador;
						break;
					}
				}
				ajustarBotoes();
			}
		}catch (Exception e) {
			// Não é número, ignorar
		}
	}
	
	public void setEstado (int linha){
		if((linha >= 0)){
			Estado e = (Estado) estados.get(linha);
			setEstado(e);
		}
	}
	
}
