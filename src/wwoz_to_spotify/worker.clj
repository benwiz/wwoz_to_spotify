(ns wwoz_to_spotify.worker
  (:require
   [clj-spotify.core :as spotify]
   [cheshire.core :as cheshire]
   [feedme]
   [wwoz_to_spotify.spotify_util :as spotify-util]))

(def config
  {:aws-region            "us-east-1"
   :aws-access-key-id     (System/getenv "AWS_ACCESS_KEY_ID")
   :aws-secret-access-key (System/getenv "AWS_SECRET_ACCESS_KEY")
   :spotify-client-id     (System/getenv "SPOTIFY_CLIENT_ID")
   :spotify-client-secret (System/getenv "SPOTIFY_CLIENT_SECRET")
   :spotify-refresh-token (System/getenv "SPOTIFY_REFRESH_TOKEN")
   :spotify-playlist-id   "3vjFwtIxnPkNXk0XWTj0wy"})

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

(defn get-recent-tracks-spotify
  "Get `n` most recently added tracks from the playlist"
  [user-id playlist-id n token]
  (map
   (fn [item] (get (get item :track) :uri))
   (get
    (spotify/get-a-playlists-tracks {:user_id user-id
                                     :playlist_id playlist-id
                                     :fields "items(track(uri))"
                                     :limit 50
                                     :offset 0}
                                    token)
    :items)))

(defn search-spotify
  "Search for song on Spotify, return URI."
  [search-term token]
  (let [search-term (clojure.string/replace (clojure.string/replace search-term "'" "") ":" "")]
    (println "Search spotify for:" search-term)
    (let [track-uri (get
                     (get
                      (get
                       (get
                        (spotify/search {:q search-term :type "track" :limit 1} token)
                        :tracks)
                       :items)
                      0)
                     :uri)]
      (println "Track URI:" track-uri)
      track-uri)))

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
  ; For each RSS entry
  (let [user-id             "bwisialowski"
        playlist-id         (System/getenv "SPOTIFY_PLAYLIST_ID") ; "18klOu16oLZfBvRncgAZhO" ; "3vjFwtIxnPkNXk0XWTj0wy"
        token               (get-spotify-token)
        recently-added-uris (get-recent-tracks-spotify user-id playlist-id 50 token)]
    (doall
     (map (fn [entry]
            ; Get track URI and if one was found and not recently
            ; added to playlist then add it to the playlist.
            (let [track-uri (search-spotify (get entry :title) token)]
              (if track-uri
                (if (not (some #{track-uri} recently-added-uris))
                  (println
                   "Added track to playlist"
                   (spotify/add-tracks-to-a-playlist {:user_id     user-id
                                                      :playlist_id playlist-id
                                                      :uris        [track-uri]
                                                      :position    0}
                                                     token))))))
          (consume-wwoz-rss))))
  nil)

(defn run
  "Start the whole thing."
  []
  (println "Run...")
  (wwoz-to-spotify)
  nil)
