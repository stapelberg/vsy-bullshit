package hsma.ss2011.vsy;

public class GameSession {
	private String id;
	private String[] participants;
	private String name;
	private int created;
	
	/**
	 * Use this constructor only if you want to set everything using
	 * the set-methods in order to keep the code clean.
	 */
	public GameSession() {
		this.id = null;
		this.participants = null;
		this.name = null;
		this.created = -1;
	}
	
	public GameSession(String id, String name, String[] participants, int created) {
		this.id = id;
		this.participants = participants;
		this.name = name;
		this.created = created;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String[] getParticipants() {
		return participants;
	}
	public void setParticipants(String[] participants) {
		this.participants = participants;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCreated() {
		return created;
	}
	public void setCreated(int created) {
		this.created = created;
	}
}
