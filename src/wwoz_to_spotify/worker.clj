(ns wwoz_to_spotify.worker)

(defn wwoz-to-spotify
  "Get playlist track list, consume RSS feed, identify each on Spotify,
  if song is not in track list add to new list, add all tracks in new list
  to playlist."
  []
  nil)

(defn run
  "Start the whole thing."
  []
  (println "running...")
  (wwoz-to-spotify)
  nil)
