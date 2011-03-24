package hsma.ss2011.vsy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class GameManagement {
	private URL baseURL;
	private String nick;
	private String token;
	private String gameID;
	private String[] words;
	private int size;
	
	public GameManagement(String server, int port, String nick) throws MalformedURLException {
		this.baseURL = new URL("http", server, port, "/");
		this.nick = nick;
		this.token = null;
		this.gameID = null;
		this.size = -1;
		this.words = null;
	}
	
	public String[] getWords() {
		return words;
	}
	
	public int getSize() {
		return size;
	}
	
	public String getGameID() {
		return gameID;
	}

	public void setGameID(String gameID) {
		this.gameID = gameID;
	}

	/**
	 * Request the currently running game sessions from the server.
	 * @return The current game sessions or null if no exist.
	 */
	public GameSession[] currentGames() {
		GameSession[] gameSessions = null;
		HttpGet request = new HttpGet(baseURL.toString() + "CurrentGames");
		HttpResponse responseFromServer = null;
		HttpEntity entity = null;
		BufferedReader reader = null;
		JSONArray responseAsJSON = null;
		
		HttpClient httpclient = new DefaultHttpClient();
		
		/* Request for the raw list of currently running sessions
		 */
		try {
			responseFromServer = httpclient.execute(request);
			entity = responseFromServer.getEntity();
			if (entity != null) {
				InputStream stream = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(stream), 8192);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* Now convert the raw response into a JSONArray
		 * and extract the information from that.
		 */
		try {
			responseAsJSON = new JSONArray(new JSONTokener(reader));
			gameSessions = (responseAsJSON.length()>0) ? new GameSession[responseAsJSON.length()] : null;
			
			// Convert the JSONArray to a GameSession Array
			for (int i=0; i < gameSessions.length; i++) {
				JSONObject item = responseAsJSON.getJSONObject(i);
				JSONArray participants = item.getJSONArray("participants");
				
				GameSession entry = new GameSession();
				entry.setCreated(item.getInt("created"));
				entry.setId(item.getString("id"));
				entry.setName("name");
				
				// extract the playernames from the participants JSONArray
				String[] players = new String[participants.length()];
				for (int j=0; j< players.length; j++)
					players[j] = participants.getString(j);
				entry.setParticipants(players);
				
				gameSessions[i] = entry;
				item = null; // just to make sure, that nothing happens by accident
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return gameSessions;
	}

	public String createGame(String name, int size) {
		
		return null;
	}
	
	/**
	 * Register the player at the server and save the token for this session
	 * @return A error message if there was a problem.
	 */
	public String registerPlayer() {
		String errorMessage = null;
		HttpPost request = new HttpPost(baseURL.toString() + "RegisterPlayer");
		HttpResponse responseFromServer = null;
		HttpEntity entity = null;
		BufferedReader reader = null;
		JSONObject responseAsJSON = null;
		
		HttpClient httpclient = new DefaultHttpClient();
		
		try {
			// Create Post: { "nickname":"$NICK" }
			request.setEntity(new ByteArrayEntity(("{ \"nickname\":\""+this.nick+"\" }").getBytes()));
			responseFromServer = httpclient.execute(request);
			entity = responseFromServer.getEntity();
			
			if (entity != null) {
				InputStream stream = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(stream), 8192);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* Check if it worked, set the token else return the error message
		 */
		try {
			responseAsJSON = new JSONObject(new JSONTokener(reader));
			if (responseAsJSON.getBoolean("success"))
				this.token = responseAsJSON.getString("token");
			else
				errorMessage = responseAsJSON.getString("error");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return errorMessage;
	}
	
	/**
	 * Join a game, words are kept in here
	 * @param gameId
	 * @return null if everything's fine, else the error message.
	 */
	public String joinGame(String gameId) {
		String error = null;
		HttpPost request = new HttpPost(baseURL.toString() + "RegisterPlayer");
		HttpResponse responseFromServer = null;
		HttpEntity entity = null;
		BufferedReader reader = null;
		JSONObject responseAsJSON = null;
		
		HttpClient httpclient = new DefaultHttpClient();
		
		try {
			// Create Post: { "token":"$Token", "id":"$ID" }
			String msg = "{ \"token\":" + this.token + "\", \"id\":\"" + this.gameID + "\" }";
			request.setEntity(new ByteArrayEntity(msg.getBytes()));
			responseFromServer = httpclient.execute(request);
			
			entity = responseFromServer.getEntity();
			if (entity != null) {
				InputStream stream = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(stream), 8192);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* if it worked, return the words in an Array else return
		 * the error message in a 1 field Array.
		 */
		try {
			responseAsJSON = new JSONObject(new JSONTokener(reader));
			if (responseAsJSON.getBoolean("success")) {
				this.size = responseAsJSON.getInt("size");
				
				JSONArray w = responseAsJSON.getJSONArray("words");
				
				this.words = new String[w.length()];
				for (int i=0; i<words.length; i++)
					this.words[i] = w.getString(i);
			} else { // request failed
				error = responseAsJSON.getString("error");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return error;
	}
	
	/**
	 * Leave the current game
	 * @return null if everythings fine, else the error message
	 */
	public String leaveGame() {
		String errorMessage = null;
		HttpPost request = new HttpPost(baseURL.toString() + "RegisterPlayer");
		HttpResponse responseFromServer = null;
		HttpEntity entity = null;
		BufferedReader reader = null;
		JSONObject responseAsJSON = null;
		
		HttpClient httpclient = new DefaultHttpClient();
		
		try {
			// Create Post: { "token":"$Token", "id":"$ID" }
			String msg = "{ \"token\":" + this.token + "\", \"id\":\"" + this.gameID + "\" }";
			request.setEntity(new ByteArrayEntity(msg.getBytes()));
			responseFromServer = httpclient.execute(request);
			
			entity = responseFromServer.getEntity();
			if (entity != null) {
				InputStream stream = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(stream), 8192);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* Check if it worked if not return the error message
		 */
		try {
			responseAsJSON = new JSONObject(new JSONTokener(reader));
			if (!responseAsJSON.getBoolean("success"))
				errorMessage = responseAsJSON.getString("error");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return errorMessage;
	}
	
	/**
	 * Send the server which field was activated
	 * @param field the number of the field
	 * @return
	 */
	public String makeMove(int field) {
		String errorMessage = null;
		HttpPost request = new HttpPost(baseURL.toString() + "RegisterPlayer");
		HttpResponse responseFromServer = null;
		HttpEntity entity = null;
		BufferedReader reader = null;
		JSONObject responseAsJSON = null;
		
		HttpClient httpclient = new DefaultHttpClient();
		
		try {
			// Create Post: { "token":"$Token", "id":"$ID", "field":$FIELD }
			String msg = "{ \"token\":" + this.token + "\",";
				msg += " \"id\":\"" + this.gameID + "\",";
				msg += "\"field\":"+field+" }";
			request.setEntity(new ByteArrayEntity(msg.getBytes()));
			responseFromServer = httpclient.execute(request);
			
			entity = responseFromServer.getEntity();
			if (entity != null) {
				InputStream stream = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(stream), 8192);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* Check if it worked if not return the error message
		 */
		try {
			responseAsJSON = new JSONObject(new JSONTokener(reader));
			if (!responseAsJSON.getBoolean("success"))
				errorMessage = responseAsJSON.getString("error");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return errorMessage;
	}
}
