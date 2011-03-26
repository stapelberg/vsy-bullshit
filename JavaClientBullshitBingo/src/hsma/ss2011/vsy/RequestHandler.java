package hsma.ss2011.vsy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class RequestHandler {
	private URL baseURL;
	
	public RequestHandler(URL baseURL) {
		this.baseURL = baseURL;
		
	}
	
	/**
	 * Send a POST request to the server and return the answer as JSONObject.
	 * @param path The path to be appended to the baseURL
	 * @param request JSONObject containing the request.
	 * @return JSONObject containing the response
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public JSONObject postRequest(String path, JSONObject request) throws ClientProtocolException, IOException, JSONException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost req = new HttpPost(baseURL.toString() + path);
		HttpResponse rawResponse = null;
		HttpEntity entity = null;
		BufferedReader reader = null;
		JSONObject responseAsJSON = null;
		
		// Send request
		req.setEntity(new ByteArrayEntity(request.toString().getBytes()));
		rawResponse = httpclient.execute(req);
		
		// Take care of the response
		entity = rawResponse.getEntity();
		if (entity != null) {
			InputStream stream = entity.getContent();
			reader = new BufferedReader(new InputStreamReader(stream), 8192);
		}
		
		responseAsJSON = new JSONObject(new JSONTokener(reader));
		
		return responseAsJSON;
	}

	public JSONArray getRequest(String path) throws ClientProtocolException, IOException, JSONException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet req = new HttpGet(this.baseURL + path);
		HttpResponse rawResponse = null;
		HttpEntity entity = null;
		BufferedReader reader = null;
		JSONArray responseAsJSON = null;
		
		// Send request
		rawResponse = httpclient.execute(req);
		
		// Take care of the response
		entity = rawResponse.getEntity();
		if (entity != null) {
			InputStream stream = entity.getContent();
			reader = new BufferedReader(new InputStreamReader(stream), 8192);
		}
		
		responseAsJSON = new JSONArray(new JSONTokener(reader));
		
		return responseAsJSON;
	}
}
