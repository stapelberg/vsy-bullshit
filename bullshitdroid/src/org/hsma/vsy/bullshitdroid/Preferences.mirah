# vim:ts=2:sw=2:expandtab:ft=ruby

import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.EditTextPreference
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener

class Preferences < PreferenceActivity
  implements OnSharedPreferenceChangeListener

  def onCreate(saved:Bundle)
    super(saved)

    addPreferencesFromResource R.xml.preferences
    @sp = getPreferenceScreen.getSharedPreferences
    @pref = EditTextPreference(findPreference('server_url'))
  end

  def onResume
    super

    @sp.registerOnSharedPreferenceChangeListener(self)
    @pref.setSummary @sp.getString('server_url', 'http://x200.rag.lan:8000/')
  end

  def onPause
    super

    @sp.unregisterOnSharedPreferenceChangeListener(self)
  end

  def onSharedPreferenceChanged(sp:SharedPreferences, key:String)
    # Update summary value
    @pref.setSummary @pref.getText
    onContentChanged
  end
end
