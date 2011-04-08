# vim:ts=2:sw=2:expandtab:ft=ruby

import android.R as AR
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Button
import android.widget.Spinner
import android.widget.AdapterView.OnItemSelectedListener
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams
import android.util.Log
import android.os.AsyncTask
import java.util.List
import java.util.ArrayList
import org.json.JSONArray

class CreateGame < Activity
  def onCreate(state)
    super state

    setContentView(R.layout.creategame)
    getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT)

    # Find objects in layout
    activity = self
    game_title = EditText(findViewById(R.id.cg_name))
    game_size = EditText(findViewById(R.id.cg_size))
    @cg_wordlist = Spinner(findViewById(R.id.cg_wordlist))
    createbtn = Button(findViewById(R.id.cg_createbtn))

    # Setup listeners
    wordlistselect = WordListSelect.new
    @wordlistselect = wordlistselect
    @cg_wordlist.setOnItemSelectedListener(@wordlistselect)
    createbtn.setOnClickListener do |v|
      Log.d('bullshit', 'should create game')
      Log.d('bullshit', "title = #{game_title.getText.toString}")
      Log.d('bullshit', "size = #{game_size.getText.toString}")
      Log.d('bullshit', "wordlist = #{wordlistselect.current_entry}")

      CreateGameTask.new(activity).execute(
        [game_title.getText.toString,
         game_size.getText.toString,
         wordlistselect.current_entry].toArray)
    end

    Log.d('bullshit', 'creategame launched')

    GetWordlists.new(self).execute
  end

  def setWordlistAdapter(adapter:ArrayAdapter)
    @cg_wordlist.setAdapter(adapter)
    @wordlistselect.set_current_entry(String(adapter.getItem(0)))
  end
end

class WordListSelect
  implements OnItemSelectedListener

  def set_current_entry(current:String); @current = current; end
  def current_entry; @current; end

  def onItemSelected(parent, view, pos, id)  
    @current = String(parent.getAdapter.getItem(pos))
  end
end

class GetWordlists < MAsyncTask
  def initialize(activity:CreateGame)
    @activity = activity
  end

  def doInBackground(params)
    wordlists = Server.Wordlists

    list = ArrayList.new
    0.upto(wordlists.length()-1) { |i| list.add(wordlists.getString(i)) }
    list
  end

  def onPostExecute(list)
    adapter = ArrayAdapter.new(@activity, AR.layout.simple_spinner_item, ArrayList(list))
    adapter.setDropDownViewResource(AR.layout.simple_spinner_dropdown_item)
    @activity.setWordlistAdapter(adapter)
  end
end

class CreateGameTask < AsyncTask
  def initialize(activity:Activity)
    @activity = activity
  end

  def onPreExecute()
    @dialog = ProgressDialog.new @activity
    @dialog.setTitle 'Creating Game'
    @dialog.setMessage 'Creating new game...'
    @dialog.show
  end

  def doInBackground(params)
    Log.d('bullshit', "title = #{String(params[0])}")
    Log.d('bullshit', "size = #{String(params[1])}")
    Log.d('bullshit', "wordlist = #{String(params[2])}")
    size = Integer.parseInt(String(params[1]))
    Log.d('bullshit', "int size #{size}")
    Boolean.new Server.CreateGame(String(params[0]), size, String(params[2]))
  end

  def onPostExecute(result)
    @dialog.dismiss
    if Boolean(result)
      Log.d('bullshit', 'game created, closing activity')
      @activity.finish
    end
  end
end
