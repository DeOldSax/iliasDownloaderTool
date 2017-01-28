package plugin;

import control.IliasManager;
import plugin.IliasPlugin.LoginStatus;

public class TuebIliasTest {
	public static void main(String args[]) {
		TuebIlias ilias = new TuebIlias();
		IliasManager manager = IliasManager.getInstance();
		manager.setIliasPlugin(ilias);
		LoginStatus status = manager.login("", "");
		System.out.println(status);
	}
}
