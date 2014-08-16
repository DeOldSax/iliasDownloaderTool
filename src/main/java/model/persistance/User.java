package model.persistance;


public class User implements Storable {

	private static final long serialVersionUID = 5177868027321120942L;
	private String name; 
	private String password;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return password; 
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getStorageFileName() {
		return "user.ser";
	}
}
