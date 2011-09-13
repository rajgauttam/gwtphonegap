package de.kurka.phonegap.client.file;

public interface FileCallback<S, E> {
	public void onSuccess(S entry);

	public void onFailure(E error);
}
