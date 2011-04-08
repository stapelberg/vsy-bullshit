# vim:ts=2:sw=2:expandtab:ft=ruby
#
# Makes AsyncTask easier in Mirah
#

import android.app.Activity
import android.os.AsyncTask
import android.util.Log

class MAsyncTask < AsyncTask
  def execute
    super([nil].toArray)
  end

  def execute(object:Object)
    super([object].toArray)
  end
end
