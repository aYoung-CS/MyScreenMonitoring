package client;

public class ClientShot extends Thread{

	private Client client;
	public ClientShot(Client client) {
		this.client=client;
	}

	public ClientShot() {

	}

	@Override
	public void run() {
//		client.close();
	}
}
