(ns wwoz_to_spotify.worker)
(require 'feedme)
(require '[clj-spotify.core :as spotify])
; (require '[clj-spotify.util :as spotify-util])
(require '[wwoz_to_spotify.spotify_util :as spotify-util])
(require '[rotary.client :as rotary])
(require '[cheshire.core :as cheshire])

(def aws-credential {:access-key (System/getenv "AWS_ACCESS_KEY_ID"),
                     :secret-key (System/getenv "AWS_SECRET_ACCESS_KEY")})

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

(defn get-spotify-token
  "Get OAuth2 token."
  []
  (spotify-util/get-access-token
   (System/getenv "SPOTIFY_CLIENT_ID")
   (System/getenv "SPOTIFY_CLIENT_SECRET")
   (System/getenv "SPOTIFY_REFRESH_TOKEN")))

(defn spotify-handler
  "Do all Spotify stuff. Eventually will need to take track info from RSS feed."
  [search_term playlist_track_uris]
  (let [token (get-spotify-token)
        search_term (clojure.string/replace (clojure.string/replace search_term "'" "") ":" "")]
    (println "Search spotify for:" search_term)
    (let [track_uri (get
                     (get
                      (get
                       (get
                        (spotify/search {:q search_term :type "track" :limit 1} token)
                        :tracks)
                       :items)
                      0)
                     :uri)]
      (println "Track URI:" track_uri)
      (if track_uri
        (println
         "Added track to playlist"
         (spotify/add-tracks-to-a-playlist {:user_id "bwisialowski" :playlist_id "3vjFwtIxnPkNXk0XWTj0wy" :uris [track_uri]} token)))))
  nil)

(defn consume-wwoz-rss
  "Consume WWOZ's Spinitron RSS feed."
  []
  (println "Consume RSS feed...")
  (get (read-feed "https://spinitron.com/public/rss.php?station=wwoz") :entries))

(defn wwoz-to-spotify
  "Get playlist track list, consume RSS feed, identify each on Spotify,
  if song is not in track list add to new list, add all tracks in new list
  to playlist."
  []
  (println (rotary/list-tables aws-credential))
  (doall
   (map (fn [entry]
          ; TODO: Confirm that `:id` does not exist in database
          (rotary/put-item aws-credential "wwoz-to-spotify" 1)
          ; TODO: Add `:id` to database

          ; (spotify-handler (get entry :title) [])
)
        (consume-wwoz-rss)))
  nil)

(defn run
  "Start the whole thing."
  []
  (println "Run...")
  (wwoz-to-spotify)
  nil)

; Executed for local run using `lein exec src/wwoz_to_spotify/worker.clj`
(run)
