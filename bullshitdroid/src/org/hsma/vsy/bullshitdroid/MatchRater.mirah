# vim:ts=2:sw=2:expandtab:ft=ruby

import android.util.Log

import java.util.Collections
import java.util.ArrayList

class MatchRater
  def initialize(words:ArrayList)
    @words = words
  end

  def getMatch(word:String)
    @matches.each { |o| m = RecognizerMatch(o); return m if m.getMatch.equals(word) }
    RecognizerMatch(nil)
  end

  def getBestMatch(candidates:ArrayList)
    @matches = ArrayList.new
    candidates.each do |r|
      @words.each do |w|
        c = String(r).toLowerCase
        check = String(w).toLowerCase
        distance = 0
        distance = LevenshteinDistance.compute(check, c)
        if distance == 0
          Log.d('matcher', "PERFECT MATCH: #{c} (vs. #{check})")
          return String(w)
        end
        if distance > (check.length / 2)
          Log.d('matcher', "BAD MATCH (r = #{c} vs. #{check}, distance #{distance}, length = #{String(w).length / 2}")
          next
        end
        Log.d('matcher', "MATCHES BEFORE: #{@matches}")
        existing = getMatch(String(w))
        if existing
          existing.improveDistance(distance)
        else
          @matches.add(RecognizerMatch.new(String(w), distance))
        end
        Log.d('matcher', "MATCHES NOW: #{@matches}")
      end
    end

    Collections.sort(@matches)
    Log.d('matcher', "---- results (sorted) ---");
    @matches.each { |m| Log.d('matcher', "  #{m}") }
    Log.d('matcher', "---- end of results ---");

    return nil if @matches.size() == 0

    first = RecognizerMatch(@matches.get(0))

    # Exactly one match? Perfect!
    return first.getMatch if @matches.size() == 1

    # If there is a tie (multiple matches with equal distance), we do not decide
    return nil if first.getDistance == RecognizerMatch(@matches.get(1)).getDistance

    # If one is clearly better than the other, return it
    return first.getMatch
  end
end
