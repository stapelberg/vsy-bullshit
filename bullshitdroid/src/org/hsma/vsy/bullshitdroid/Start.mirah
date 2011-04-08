# vim:ts=2:sw=2:expandtab:ft=ruby

import android.app.Activity
import android.app.ListActivity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.ImageView
import android.widget.EditText
import android.widget.Button
import android.util.Log
import android.os.AsyncTask
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.List
import java.util.ArrayList
import org.json.JSONTokener
import org.json.JSONObject
import org.json.JSONArray

class Start < ListActivity
  def onCreate(state)
    super state

    setContentView(R.layout.main)

    @registered = false

    # store a reference to 'self' to use it inside the OnItemClickListener
    activity = self

    getListView().setOnItemClickListener do |parent, view, pos, id|
      game = Game(parent.getAdapter().getItem(pos))
      intent = Intent.new(activity, PlayGame.class)
      intent.putExtra('id', game.id)
      activity.startActivity(intent)
    end
  end

  def onResume
    super

    sp = PreferenceManager.getDefaultSharedPreferences self
    Server.set_server sp.getString('server_url', 'http://x200.rag.lan:8000/')

# TODO: namen aus SharedPrefs holen
    nickname = 'sECuRE'
    if !@registered
      Log.d('bullshit', 'should register now')

      activity = self

      alert = AlertDialog.Builder.new(self)
      input = EditText.new(self)
      alert.setView input
      alert.setTitle 'Your nickname:'
      alert.setPositiveButton 'Register' do |dialog, which|
        text = input.getText.toString.trim
        Log.d('bullshit', "User entered #{text}")
        RegisterTask.new(activity).execute([text].toArray)
      end

      alert.show

      @registered = true
    else
      Log.d('bullshit', 'already registered')
    end

    GetGames.new(self).execute
  end

  def onCreateOptionsMenu(menu)
    getMenuInflater().inflate(R.menu.main, menu)
    true
  end

  def onOptionsItemSelected(item)
    Log.d('bullshit', 'menu item selected')
    if item.getItemId() == R.id.m_create
      Log.d('bullshit', 'should create game')
      startActivity Intent.new(self, CreateGame.class)
    elsif item.getItemId() == R.id.m_prefs
      Log.d('bullshit', 'opening prefs')
      startActivity Intent.new(self, Preferences.class)
    else
      Log.d('bullshit', 'unknown menu entry')
    end
    false
  end
end

class RegisterTask < AsyncTask
  def initialize(activity:Activity)
    @activity = activity
  end

  def onPreExecute()
    @dialog = ProgressDialog.new @activity
    @dialog.setTitle 'Registering'
    @dialog.setMessage 'Registering nickname...'
    @dialog.show
  end

  def doInBackground(params)
    Boolean.new Server.RegisterPlayer String(params[0])
  end

  def onPostExecute(result)
    @dialog.dismiss
    if Boolean(result)
      Log.d('bullshit', 'aha, was alright')
# TODO: set registered
    else
      Log.d('bullshit', 'nah, error')
# TODO: re-open the dialog, with an error message
    end
  end
end

#
# Task to request /CurrentGames and put the result in the ListView
#
class GetGames < MAsyncTask
  #
  # Because nested classes cannot access variables of their "parents",
  # we extend the constructor to get the Activity.
  #
  def initialize(activity:ListActivity)
    @activity = activity
  end

  def doInBackground(params)
    games = JSONArray(Server.CurrentGames)

    list = ArrayList.new
    0.upto(games.length()-1) do |i|
      entry = games.getJSONObject(i)
      
      # show only games which are still running
      if entry.isNull('winner')
        list.add(Game.new(entry.getString('id'), entry.getString('name')))
      end
    end

    list
  end

  def onPostExecute(list)
    adapter = GameAdapter.new(@activity, R.layout.simple_list_item_1, ArrayList(list))
    @activity.setListAdapter(adapter)
  end
end

#
# Simple ArrayAdapter which displays Game names
#
class GameAdapter < ArrayAdapter
  def initialize(activity:Activity, layout:int, list:List)
    super(Context(activity), layout, list)
    @layout = layout
    @activity = activity
  end

  def getView(position, convertView, parent)
    row = convertView
    if !row
      inflater = @activity.getLayoutInflater
      row = inflater.inflate(@layout, parent, false)
      text = TextView(row.findViewById(R.id.text))
      row.setTag(R.id.text, text)
    else
      text = TextView(row.getTag(R.id.text))
    end

    entry = Game(getItem(position))

    text.setText(entry.name)

    return row
  end
end
