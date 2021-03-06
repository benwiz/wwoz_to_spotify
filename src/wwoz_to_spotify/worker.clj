(ns wwoz_to_spotify.worker
  (:require
   [aleph.http :as http]
   [byte-streams :as bs]
   [cheshire.core :as cheshire]
   [clojure.data :as d]
   [clojure.string :as str]
   [clj-spotify.core :as spotify]
   [clj-spotify.util :as spotify-util]
   [feedme]
   [hickory.core :as h]
   [hickory.select :as s]))


(def config
  {:aws-region            "us-east-1"
   :aws-access-key-id     (System/getenv "AWS_ACCESS_KEY_ID")
   :aws-secret-access-key (System/getenv "AWS_SECRET_ACCESS_KEY")
   :spotify-user-id       "bwisialowski"
   :spotify-client-id     (System/getenv "SPOTIFY_CLIENT_ID")
   :spotify-client-secret (System/getenv "SPOTIFY_CLIENT_SECRET")
   :spotify-refresh-token (System/getenv "SPOTIFY_REFRESH_TOKEN")
   :spotify-playlist-id   "5P6WEbhcUsmXB08owijHYd"})


(defn consume-html
  "Consume WWOZ's latest several songs played
   from Spinitron website.
   Can't use WWOZ's website because the table
   is populated with JavaScript.
   Spinitron's RSS feed no longer exists and its
   API is closed."
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
  (spotify-util/refresh-access-token
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
                                                           :limit       (* 2 (count (:new tracks))) ;; 2x just incase
                                                           :offset      0}
                                                          token)))))


(defn diff [tracks]
  (-> (d/diff (set (:new tracks)) (set (:recent tracks)))
      first))


(defn add-tracks-to-playlist! [uris token]
  (spotify/add-tracks-to-a-playlist {:user_id     (:spotify-user-id config)
                                     :playlist_id (:spotify-playlist-id config)
                                     :uris        uris
                                     :position    0}
                                    token))


(defn old-spotify-tracks [token]
  (spotify/get-a-playlists-tracks {:user_id     (:spotify-user-id config)
                                   :playlist_id (:spotify-playlist-id config)
                                   :fields      "items(track(uri))"
                                   :limit       100 ;; 100 is max (and default)
                                   :offset      99}
                                  token))


(defn remove-tracks-from-playlist! [tracks token]
  (spotify/remove-tracks-from-a-playlist {:user_id     (:spotify-user-id config)
                                          :playlist_id (:spotify-playlist-id config)
                                          :tracks      tracks}
                                         token))


(defn run []
  (let [token (spotify-token)]
    ;; Add new tracks
    (prn "Add new tracks...")
    (-> (consume-html)
        parse-html
        (spotify-tracks token)
        (recent-spotify-tracks token)
        (update :new #(remove nil? %))
        diff
        (doto prn)
        (add-tracks-to-playlist! token))
    (prn "Done adding new tracks.")

    ;; Delete old tracks
    (prn "Delete old tracks...")
    (-> (old-spotify-tracks token)
        :items
        (->> (map :track))
        (doto prn)
        (remove-tracks-from-playlist! token))
    (prn "Done deleting old tracks."))

  ;; TODO: What return value?
  nil)
