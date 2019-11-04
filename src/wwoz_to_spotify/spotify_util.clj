(ns wwoz_to_spotify.spotify_util
  (:require [clj-http.client :as client]))

;; TODO: I may be able to deprecate this and use what I added to clj-spotify

(defn get-access-token
  "Copied from clj-clojure repo and modified to handle refresh.

  Requests an access token from Spotify's API via the Client Credentials flow.
  The returned token cannot be used for endpoints which access private user information;
  use the OAuth 2 Authorization Code flow for that."
  [client-id client-secret refresh_token]
  (-> "https://accounts.spotify.com/api/token"
      (client/post {:form-params {:grant_type "refresh_token"
                                  :refresh_token refresh_token}
                    :basic-auth [client-id client-secret]
                    :as :json})
      :body
      :access_token))
