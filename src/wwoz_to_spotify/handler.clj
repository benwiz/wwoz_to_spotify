(ns wwoz_to_spotify.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.util.response :as response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response]]
            [wwoz_to_spotify.worker :refer [run]]))

(defroutes app-routes
  (GET "/run" []
    (println "RUN 1")
    (response {:message "Hello World"})) ; TODO: "Hello World" -> (run)

  (route/not-found
   (response/not-found {:message "Not Found"})))

(def app
  (-> app-routes
      (wrap-json-response)
      (wrap-defaults api-defaults)))
