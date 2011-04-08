# vim:ts=2:sw=2:expandtab:ft=ruby

class RecognizerMatch
  implements Comparable

  def initialize(match:String, distance:int)
    @match = match
    @distance = distance
  end

  def compareTo(other:Object)
    if @distance < RecognizerMatch(other).getDistance
      return -1
    elsif @distance > RecognizerMatch(other).getDistance
      return 1
    else
      return 0
    end
  end

  def toString; "#{@match} (#{@distance})"; end

  def getMatch; @match; end
  def getDistance; @distance; end

  def improveDistance(distance:int)
    if distance < @distance
      @distance = distance
    end
  end
end
