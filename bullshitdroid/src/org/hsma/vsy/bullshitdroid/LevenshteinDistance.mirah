# vim:ts=2:sw=2:expandtab:ft=ruby

import android.util.Log

class LevenshteinDistance
  def self.min(a:int, b:int, c:int)
    h = (a < b ? a : b)
    return (h < c ? h : c)
  end
    
  def self.compute(s:String, t:String)
    #Log.d('leven', "called for s=#{s}, t=#{t}")
    n = s.length
    m = t.length

    return m if (n == 0)
    return n if (m == 0)

    d = int[(m > n ? m : n) + 1]

    0.upto(n-1) do |i|
      e = i+1
      0.upto(m-1) do |j|
        #Log.d('leven', "i = #{i}, j = #{j}, comparing #{s[i]} with #{t[j]}")
        cost = (s[i] == t[j]) ? 0 : 1
        x = min(
          d[j+1] + 1, # insertion
          e + 1,      # deletion
          d[j] + cost # substitution
        )
        d[j] = e
        e = x
      end
      d[m] = x
    end

    x
  end
end
