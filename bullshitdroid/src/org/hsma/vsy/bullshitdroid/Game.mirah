# vim:ts=2:sw=2:expandtab:ft=ruby

#
# data class to hold a Game id/name
#
class Game
  def initialize(id:String, name:String)
    @id = id
    @name = name
  end

  def id; @id; end
  def name; @name; end
end
