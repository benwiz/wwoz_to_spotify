(ns wwoz_to_spotify.worker)
(require 'feedme)
(require '[clj-spotify.core :as spotify])

(defn clean-feed
  "Clean the feed by replacing java.util.Date with strings.
  Clean envelope and each entry."
  [feed]
  (let [cleaner-feed (update feed :published str)]
    (update cleaner-feed :entries
            (fn [entries]
              (map (fn [entry]
                     (update entry :published str))
                   entries)))))

(defn read-feed
  "Parse the provided feed."
  [url]
  (let [feed (feedme/parse url)]
    (clean-feed feed)))

(defn consume-wwoz-rss
  "Consume WWOZ's Spinitron RSS feed."
  []
  (println "Consume RSS feed...")
  (println
   (get
    (read-feed "https://spinitron.com/public/rss.php?station=wwoz") :entries))
  nil)

(defn wwoz-to-spotify
  "Get playlist track list, consume RSS feed, identify each on Spotify,
  if song is not in track list add to new list, add all tracks in new list
  to playlist."
  []
  (consume-wwoz-rss)
  nil)

(defn run
  "Start the whole thing."
  []
  (println "Run...")
  (wwoz-to-spotify)
  nil)

; Executed for local run using `lein exec src/wwoz_to_spotify/worker.clj`
(run)
