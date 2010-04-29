package tirateima.gerador;

public class CommandEndFunction extends Command {
	public void execute(Gerador g) throws TiraTeimaLanguageException {
		g.mostrador.endFunction();
	}
}
