(ns wwoz_to_spotify.worker
  (:require
   [aleph.http :as http]
   [byte-streams :as bs]
   [cheshire.core :as cheshire]
   [clojure.string :as str]
   [clj-spotify.core :as spotify]
   [feedme]
   [hickory.core :as h]
   [hickory.select :as s]
   [wwoz_to_spotify.spotify_util :as spotify-util]))

(def config
  {:aws-region            "us-east-1"
   :aws-access-key-id     (System/getenv "AWS_ACCESS_KEY_ID")
   :aws-secret-access-key (System/getenv "AWS_SECRET_ACCESS_KEY")
   :spotify-user-id       "bwisialowski"
   :spotify-client-id     (System/getenv "SPOTIFY_CLIENT_ID")
   :spotify-client-secret (System/getenv "SPOTIFY_CLIENT_SECRET")
   :spotify-refresh-token (System/getenv "SPOTIFY_REFRESH_TOKEN")
   :spotify-playlist-id   "3vjFwtIxnPkNXk0XWTj0wy"})

(defn consume-html
  "Consume WWOZ's Last ~7 songs Played HTML.
   Can't use WWOZ's website because the table
   is populated with JavaScript, I think."
  []
  (-> @(http/get "https://spinitron.com/WWOZ/")
      :body
      bs/to-string
      h/parse
      h/as-hickory))


(defn parse-html [tree]
  (-> (s/select (s/child (s/id :public-spins-0)
                         (s/tag :table)
                         (s/tag :tbody))
                tree)
      first
      :content
      rest
      (->> (take-nth 2))
      (->> (into []
                 (comp
                  (map :attrs)
                  (map :data-spin)
                  (map cheshire/parse-string)
                  (map (fn [d]
                         {:artist (get d "a")
                          :song   (get d "s")
                          :album  (get d "r")})))))))


(defn spotify-token
  "Get OAuth2 token."
  []
  (spotify-util/get-access-token
   (:spotify-client-id config)
   (:spotify-client-secret config)
   (:spotify-refresh-token config)))


(defn search-spotify
  "Search for song on Spotify, return URI."
  [search-term token]
  (let [search-term (-> (str/replace search-term "'" "") (str/replace ":" ""))
        track-uri   (-> (spotify/search {:q search-term :type "track" :limit 1} token)
                        :tracks
                        :items
                        first
                        :uri)]
    track-uri))


(defn spotify-tracks
  "Get the Spotify track from the song and artist names."
  [tracks token]
  {:new (map (fn [track]
               (search-spotify (str (:song track) ", " (:artist track)) token))
             tracks)})


(defn recent-spotify-tracks [tracks token]
  (assoc tracks
         :recent (map
                  #(get-in % [:track :uri])
                  (:items (spotify/get-a-playlists-tracks {:user_id     (:spotify-user-id config)
                                                           :playlist_id (:spotify-playlist-id config)
                                                           :fields      "items(track(uri))"
                                                           :limit       (count tracks)
                                                           :offset      0}
                                                          token)))))





(defn run
  "Start the whole thing."
  []
  (let [token (spotify-token)]
    (-> (consume-html)
        parse-html
        (spotify-tracks token)
        (recent-spotify-tracks token)
        ;; TODO: Use `diff` to remove overlaps
        ))
  ;; TODO: What return value?
  )
