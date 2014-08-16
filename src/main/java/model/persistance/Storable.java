package model.persistance;

import java.io.Serializable;

public interface Storable extends Serializable {
	public String getStorageFileName();
}
