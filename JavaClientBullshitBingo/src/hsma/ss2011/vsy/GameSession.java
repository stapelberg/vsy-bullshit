package hsma.ss2011.vsy;

public class GameSession {
	private String id;
	private String[] participants;
	private String winner;
	private String wordlist;
	private String name;
	private int created;
	private int size;
	
	/**
	 * Use this constructor only if you want to set everything using
	 * the set-methods in order to keep the code clean.
	 */
	public GameSession() {
		this.id = null;
		this.participants = null;
		this.name = null;
		this.wordlist = null;
		this.winner = null;
		this.created = -1;
		this.size = 0;
	}
	
	public GameSession(String id, String name, String[] participants, String wordlist, int created, int size) {
		this.id = id;
		this.participants = participants;
		this.name = name;
		this.winner = null;
		this.wordlist = wordlist;
		this.created = created;
		this.size = size;
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
	
	public String getWinner() {
		return winner;
	}
	public void setWinner(String winner) {
		this.winner = winner;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getWordlist() {
		return wordlist;
	}
	public void setWordlist(String wordlist) {
		this.wordlist = wordlist;
	}
	
	public int getCreated() {
		return created;
	}
	public void setCreated(int created) {
		this.created = created;
	}
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
}
