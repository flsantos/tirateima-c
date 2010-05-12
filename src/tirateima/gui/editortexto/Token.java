package tirateima.gui.editortexto;

import java.util.HashMap;
import java.util.Map;

/**
 * Modela um token da linguagem Pascal.
 * 
 * @author Luciano Santos
 */
public class Token {
	private static Map<String, String> keywords;
	private static Map<String, String> types;
	
	private static final String palavras[] = {
	"auto","break","case","continue",
	"default","do","else","enum","extern",
	"for","goto","if","long","register","return",
	"sizeof","static","struct","switch",
	"typedef","union","void","volatile","while" };

	private static final String tipos[] = {
	"char", "int", "float", "double", "void",
	"signed","unsigned", "short", "long", "const"};
	
	static {
		keywords = new HashMap<String, String>();
		types = new HashMap<String, String>();
		
		for (int i = 0; i < palavras.length; i++) {
			keywords.put(palavras[i], palavras[i]);
		}
		
		for (int i = 0; i < tipos.length; i++) {
			types.put(tipos[i], tipos[i]);
		}
	}
	
	/* Constantes de identificação dos tokens */
	public static final int WHITESPACE					= 0;
	public static final int BEGINCOMMENT_CH				= 1;
	public static final int ENDCOMMENT_CH				= 2;
	public static final int BEGINCOMMENT_PAR			= 3;
	public static final int ENDCOMMENT_PAR				= 4;
	public static final int OTHER						= 5;
	public static final int EOLINE						= 6;
	public static final int PONT						= 7;
	public static final int IDENTIFIER					= 8;
	public static final int STRING						= 11;
	public static final int BAD_STRING					= 12;
	public static final int NUM							= 12;
	public static final int EOB							= 13;
	
	public static final int AUTO                        = 14;
	public static final int BREAK                       = 15;
	public static final int CASE                        = 16;
	public static final int CONTINUE                    = 17;
	public static final int DEFAULT                     = 18;
	public static final int DO                          = 19;
	public static final int ELSE                        = 20;
	public static final int ENUM                        = 21;
	public static final int EXTERN                      = 22;
	public static final int FOR                         = 23;
	public static final int GOTO                        = 24;
	public static final int IF                          = 25;
	public static final int REGISTER                    = 26;
	public static final int RETURN                      = 27;
	public static final int SIZEOF                      = 28;
	public static final int STATIC                      = 29;
	public static final int STRUCT                      = 30;
	public static final int SWITCH                      = 31;
	public static final int TYPEDEF                     = 32;
	public static final int UNION                       = 33;
	public static final int VOLATILE                    = 34;
	public static final int WHILE                       = 35;	
	
	public static final int CHAR                        = 36;
	public static final int INT                         = 37;
	public static final int FLOAT                       = 38;
	public static final int DOUBLE                      = 39;
	public static final int VOID                        = 40;
	public static final int SIGNED                      = 41;
	public static final int UNSIGNED                    = 42;
	public static final int SHORT                       = 43;
	public static final int LONG                        = 44;
	public static final int CONST                       = 45;
	
	private int id_token;
	private String valor_token;
	
	public Token(){
		this.id_token = 0;
		this.valor_token = "";
	}
	
	public Token(int id_token, String valor_token){
		this.id_token = id_token;
		this.valor_token = valor_token;
	}
	
	public int getId(){
		return id_token;
	}
	
	public String getValor(){
		return valor_token;
	}
	
	public void setId(int id){
		this.id_token = id;
	}
	
	public void setValor(String valor){
		this.valor_token = valor;
	}
	
	public void appendToValor(char a) {
		valor_token += a;
	}
	
	public void appendToValor(String s) {
		valor_token += s;
	}
	
	public boolean ehPalavraChave() {
		return ((id_token >= AUTO) && (id_token <= WHILE));
	}
	
	public boolean ehTipo(){
		return ((id_token >= CHAR) && (id_token <= CONST));
	}
	
	public static boolean ehPalavraChave(String id) {
		return keywords.containsKey(id.toLowerCase());
	}
	
	public static boolean ehTipo(String id) {
		return types.containsKey(id.toLowerCase());
	}
}
