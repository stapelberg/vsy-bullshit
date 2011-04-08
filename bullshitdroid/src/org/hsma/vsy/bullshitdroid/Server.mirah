# vim:ts=2:sw=2:expandtab:ft=ruby
#
# Helper class to communicate with the server
#

import java.io.InputStreamReader
import java.io.BufferedReader
import android.net.http.AndroidHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ByteArrayEntity
import android.content.SharedPreferences
import org.json.JSONTokener
import org.json.JSONObject
import org.json.JSONArray
import android.util.Log

class Server
  def self.set_server(server:String)
    @server = server
    Log.d('bullshit', "server now #{@server}")
  end

  def self.req(requestType:String, body:String)
    url = @server + requestType
    client = AndroidHttpClient.newInstance("bullshitdroid")
    if !body.equals('')
      post = HttpPost.new(url)
      post.setEntity ByteArrayEntity.new(body.getBytes)
      response = client.execute(post)
    else
      get = HttpGet.new(url)
      response = client.execute(get)
    end

    content = AndroidHttpClient.getUngzippedContent(response.getEntity)
    json = BufferedReader.new(InputStreamReader.new(content), 8192).readLine
    client.close

    Log.d('bullshit', "request #{requestType}, body #{body}, result = #{json}")
    return JSONTokener.new(json).nextValue
  end

  # Registers the player and sets @token
  def self.RegisterPlayer(name:String)
    request = JSONObject.new
    request.put('nickname', name)
    result = JSONObject(Server.req('RegisterPlayer', request.toString))
    if result.getBoolean('success')
      Log.d('bullshit', 'success, saving token')
      @token = result.getString('token')
      true
    else
      false
    end
  end

  def self.CreateGame(title:String, size:int, wordlist:String)
    Log.d('bullshit', "creating game with title #{title}, size #{size}, wordlist #{wordlist}")
    request = JSONObject.new
    request.put('token', @token)
    request.put('name', title)
    request.put('size', size)
    request.put('wordlist', wordlist)
    result = JSONObject(Server.req('CreateGame', request.toString))
    result.getBoolean('success')
  end

  def self.JoinGame(id:String)
    Log.d('bullshit', "joining game #{id}")
    request = JSONObject.new
    request.put('token', @token)
    request.put('id', id)
    result = JSONObject(Server.req('JoinGame', request.toString))
  end

  def self.LeaveGame(id:String)
    Log.d('bullshit', "leaving game #{id}")
    request = JSONObject.new
    request.put('token', @token)
    request.put('id', id)
    result = JSONObject(Server.req('LeaveGame', request.toString))
  end

  def self.MakeMove(id:String, field:Integer)
    Log.d('bullshit', "making move on field #{field}")
    request = JSONObject.new
    request.put('token', @token)
    request.put('id', id)
    request.put('field', field)
    result = JSONObject(Server.req('MakeMove', request.toString))
  end

  def self.CheckWinner(id:String)
    Log.d('bullshit', "checking for winner of game #{id}")
    request = JSONObject.new
    request.put('token', @token)
    request.put('id', id)
    result = JSONObject(Server.req('CheckWinner', request.toString))
  end

  def self.CurrentGames; Server.req('CurrentGames', ''); end
  def self.Wordlists; JSONArray(Server.req('GetWordlists', '')); end
end
