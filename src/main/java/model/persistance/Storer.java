package model.persistance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.log4j.Logger;

public class Storer {

	private String storePath;
	private Key key;

	public Storer(String storePath) {
		this.storePath = storePath;
	}

	protected void store(Storable storableObject) {
		createStorageFolder();
		String thisStorePath = createStorePath(storableObject);
		// TODO check if file already exists and throw exception!
		serializeAndEncrypt(storableObject, thisStorePath);
	}

	private void createStorageFolder() {
		File file = new File(storePath);
		if (!file.exists()) {
			file.mkdir();
		}
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			try {
				Files.setAttribute(file.toPath(), "dos:hidden", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected Storable load(Storable storableObject) {
		return (Storable) deserializeAndDecrypt(createStorePath(storableObject));
	}

	private String createStorePath(Storable storableObject) {
		String thisStorePath = storePath + "/" + storableObject.getStorageFileName();
		return thisStorePath;
	}

	private void serializeAndEncrypt(Serializable object, String path) {
		SealedObject sealedObject = encryptStorableObject(object);
		Object storableObject;

		if (sealedObject == null) {
			storableObject = object;
		} else {
			storableObject = sealedObject;
		}
		serialize(storableObject, path);
	}

	private void serialize(Object object, String path) {
		ObjectOutputStream out = null;
		try {
			FileOutputStream output = new FileOutputStream(path);
			out = new ObjectOutputStream(output);
			out.writeObject(object);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Object deserializeAndDecrypt(String path) {
		Object object = deserialize(path);
		Storable storable = decryptSealedObject(object);
		return storable;
	}

	private Object deserialize(String path) {
		Object object = null;
		try {
			final FileInputStream fileInputStream = new FileInputStream(new File(path));
			ObjectInputStream o = new ObjectInputStream(fileInputStream);
			object = o.readObject();
			o.close();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
		return object;
	}

	private SealedObject encryptStorableObject(Serializable object) {
		Cipher cipher = null;
		KeyGenerator keyGen = null;
		SealedObject sealedObject = null;
		try {
			keyGen = KeyGenerator.getInstance("AES");
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			keyGen.init(128);
			if (key == null) {
				key = keyGen.generateKey();
				serialize(key, storePath + "/" + "key.ser");
			}
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
			sealedObject = new SealedObject(object, cipher);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| IllegalBlockSizeException | IOException | InvalidAlgorithmParameterException e1) {
			Logger.getLogger(Storer.class).warn("No Cipher.", e1);
			e1.printStackTrace();
		}
		return sealedObject;
	}

	private Storable decryptSealedObject(Object object) {
		if (!(object instanceof SealedObject)) {
			return null;
		}

		SealedObject sealedObject = (SealedObject) object;
		Cipher cipher = null;
		Storable storable;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKey key = (SecretKey) deserialize(storePath + "/" + "key.ser");

			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			cipher.init(Cipher.DECRYPT_MODE, key, ivspec);

			storable = (Storable) sealedObject.getObject(cipher);

		} catch (ClassCastException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeyException | IllegalBlockSizeException | IOException
				| ClassNotFoundException | BadPaddingException | InvalidAlgorithmParameterException e1) {
			Logger.getLogger(Storer.class).warn("No Cipher.", e1);
			e1.printStackTrace();
			return null;
		}
		return storable;
	}
}
