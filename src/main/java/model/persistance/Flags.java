package model.persistance;

public class Flags implements Storable {

	private static final long serialVersionUID = -7225585817388472557L;
	private boolean login;
	private boolean autoLogin;
	private boolean autoUpdate;
	private boolean localIliasPathStored;
	private boolean updateCanceled; 
	
	public void setLogin(boolean login) {
		this.login = login;
	}

	public boolean isUserLoggedIn() {
		return login;
	}
	
	public void setAutoLogin(boolean autoLogin) {
		this.autoLogin = autoLogin;
	}

	public boolean isAutoLogin() {
		return autoLogin;
	}
	
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public boolean autoUpdate() {
		return autoUpdate;
	}
	
	public void setLocalIliasPathStored(boolean localIliasPathStored) {
		this.localIliasPathStored = localIliasPathStored;
	}
	
	public boolean isLocalIliasPathStored() {
		return localIliasPathStored;
	}
	
	public void setUpdateCanceled(boolean updateCanceled ) {
		this.updateCanceled = updateCanceled;
	}

	public boolean updateCanceled() {
		return updateCanceled;
	}

	@Override
	public String getStorageFileName() {
		return "flags.ser";
	}
}
