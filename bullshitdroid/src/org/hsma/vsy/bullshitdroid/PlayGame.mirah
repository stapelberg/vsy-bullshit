# vim:ts=2:sw=2:expandtab:ft=ruby

import android.app.Activity
import android.app.ListActivity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.ImageView
import android.widget.Button
import android.widget.ViewSwitcher
import android.view.View
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams
import android.net.http.AndroidHttpClient
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.content.Intent
import org.apache.http.client.methods.HttpGet
import android.util.Log
import android.os.AsyncTask
import android.os.Handler
import android.content.Context
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.List
import java.util.ArrayList
import org.json.JSONTokener
import org.json.JSONObject
import org.json.JSONArray

class PlayGame < Activity
  def onCreate(state)
    super state

    setContentView(R.layout.game)

    @game_layout = RelativeLayout(findViewById(R.id.game_layout))

    @id = getIntent.getStringExtra('id')

    @handler = Handler.new
    @runnable = CheckWinnerR.new self
    @rec = SpeechRecognizer.createSpeechRecognizer(self)
    @wordlist = ArrayList.new

    activity = self
    switcher = ViewSwitcher(findViewById(R.id.switcher));
    voice_input = Button(findViewById(R.id.vi_btn))
    voice_input.setOnClickListener do |v|
      switcher.showNext
      activity.startRec
    end

    rec = @rec
    cancel = Button(findViewById(R.id.vs_cancel))
    cancel.setOnClickListener do |v|
      switcher.showNext
      rec.cancel
    end
  end

  def onResume
    super
    @paused = false
    JoinGame.new(self).execute @id
    schedule_checkwinner
  end

  def onPause
    super
    @paused = true
    @handler.removeCallbacks(@runnable)
    LeaveGame.new(self).execute @id
  end

  def game_id; @id; end
  def wordlist; @wordlist; end

  def schedule_checkwinner
    if !@paused
      @handler.postDelayed(@runnable, 1000)
    end
  end

  def immediate_checkwinner
    @handler.removeCallbacks(@runnable)
    @handler.post(@runnable)
  end

  def setWordLayout(layout:View)
    Log.d('bullshit', 'adding word layout')
    @game_layout.addView(layout, 0)
  end

  def setWordList(list:ArrayList)
    @wordlist = list
  end

  def startRec
    vs_status = TextView(findViewById(R.id.vs_status))
    vs_status.setText("Listening...")

    intent = Intent.new RecognizerIntent.ACTION_RECOGNIZE_SPEECH
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo")
    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "org.hsma.vsy.bullshitdroid")

    @rec.setRecognitionListener Listener.new(self)
    @rec.startListening intent
  end

  def start_voice_rec
    # (re-)trigger voice recognition
    @handler.postDelayed StartRecR.new(self), 500
  end

  # Called as soon as the best match was calculated
  def voiceResult(best:String)
    Log.d('bullshit', "best result = #{best}")

    start_voice_rec

    # Find the corresponding button and trigger it
    top_layout = LinearLayout(findViewById(R.id.game_top_layout))
    Log.d('bullshit', "this ll has #{top_layout.getChildCount} children")
    0.upto(top_layout.getChildCount - 1) do |i|
      row_layout = LinearLayout(top_layout.getChildAt(i))
      0.upto(row_layout.getChildCount - 1) do |j|
        btn = Button(row_layout.getChildAt(j))
        if btn.getText.toString.equals(best)
          btn.performClick
          #btn.setBackgroundColor(int(0xFFFF0000))
          return nil
        end
        Log.d('bullshit', "this btn is labeled #{btn.getText}")
      end
    end

    nil
  end
end

#
# Runnable that triggers voice recognition
#
class StartRecR
  implements Runnable

  def initialize(activity:PlayGame)
    @activity = activity
  end

  def run
      Log.d('bullshit', 'should start voice recognition')
      @activity.startRec
  end
end

class Listener
  implements RecognitionListener

  def initialize(activity:PlayGame)
    @activity = activity
    @vs_status = TextView(@activity.findViewById(R.id.vs_status))
    @vs_mic = ImageView(@activity.findViewById(R.id.vs_mic))
  end

  def onBeginningOfSpeech
    Log.d('bullshit', 'User started to speak')
    @vs_mic.setImageResource R.drawable.mic_full
  end

  def onEndOfSpeech
    Log.d('bullshit', 'User stopped to speak')

    @vs_mic.setImageResource R.drawable.mic_base
    @vs_status.setText("Analyzing...")
  end

  def onBufferReceived(buffer); end
  def onEvent(type, params); end
  def onPartialResults(results); end
  def onRmsChanged(rmsdB); end
  def onReadyForSpeech(params); end

  def onResults(results)
    r = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
    Log.d('bullshit', "got results: #{r}")
    rater = MatchRater.new(@activity.wordlist)
    @activity.voiceResult(rater.getBestMatch(r))
    @vs_status.setText("Done!")
  end

  def onError(error)
    Log.d('bullshit', "got error: #{error}")
    @activity.start_voice_rec
  end
end


#
# Runnable that triggers a CheckWinner AsyncTask
#
class CheckWinnerR
  implements Runnable

  def initialize(activity:PlayGame)
    @activity = activity
  end

  def run
      Log.d('bullshit', 'should check for winner')
      CheckWinner.new(@activity).execute
  end
end

class CheckWinner < MAsyncTask
  def initialize(activity:PlayGame)
    @activity = activity
  end

  def doInBackground(params)
    result = JSONObject(Server.CheckWinner(@activity.game_id))
    result.isNull('winner') ? String(nil) : result.getString('winner')
  end

  def onPostExecute(result)
    winner = String(result)
    if winner && !winner.equals('')
      Log.d('bullshit', "game won by #{winner}")

      activity = @activity
      alert = AlertDialog.Builder.new(@activity)
      alert.setTitle 'Game over'
      alert.setMessage "#{winner} won the game!"
      alert.setPositiveButton 'OK' do |dialog, which|
        activity.finish
      end

      alert.show
    else
      @activity.schedule_checkwinner
    end
  end
end

class LeaveGame < MAsyncTask
  def initialize(activity:PlayGame)
    @activity = activity
  end

  def doInBackground(params)
    Server.LeaveGame String(params[0])
  end
end

class JoinGame < MAsyncTask
  def initialize(activity:PlayGame)
    @activity = activity
  end

  def onPreExecute()
    @dialog = ProgressDialog.new @activity
    @dialog.setTitle 'Joining Game'
    @dialog.setMessage 'Receiving words from server...'
    @dialog.show
  end

  def doInBackground(params)
    Server.JoinGame String(params[0])
  end

  def onPostExecute(result)
    @dialog.dismiss

    # wordlist to store in the activity class
    wordlist = ArrayList.new

    r = JSONObject(result)
    size = r.getInt 'size'
    words = r.getJSONArray 'words'
    listener = WordClick.new @activity

    inflater = LayoutInflater(@activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))

    top = LinearLayout.new @activity
    top.setOrientation LinearLayout.VERTICAL
    top.setId(R.id.game_top_layout)

    0.upto(size-1) do |i|
      layout = LinearLayout.new @activity
      layout.setOrientation LinearLayout.HORIZONTAL
      0.upto(size-1) do |j|
        button = Button(inflater.inflate(R.layout.wordbutton, layout, false))
        word = words.getString((i * size) + j)
        button.setText word
        wordlist.add word
        button.setOnClickListener listener
        button.setTag(R.string.word_id, Integer.new((i * size) + j))
        layout.addView button
      end
      top.addView layout
    end
    @activity.setWordLayout top
    @activity.setWordList wordlist
  end
end

#
# OnClickListener for all words on the field. Starts a task for calling
# MakeMove on click.
#
class WordClick
  implements OnClickListener

  def initialize(activity:PlayGame)
    @activity = activity
  end

  def onClick(view)
    Log.d('bullshit', 'clicked on word')
    id = Integer(view.getTag(R.string.word_id))
    Log.d('bullshit', "id = #{id}")
    view.setBackgroundColor(int(0xFFFF0000))

    MakeMove.new(@activity).execute(id)
  end
end

class MakeMove < MAsyncTask
  #
  # Because nested classes cannot access variables of their "parents",
  # we extend the constructor to get the Activity.
  #
  def initialize(activity:PlayGame)
    @activity = activity
  end

  def doInBackground(params)
    Server.MakeMove(@activity.game_id, Integer(params[0]))
  end

  def onPostExecute(list)
# TODO: button entsprechend markieren
      @activity.immediate_checkwinner
  end
end
