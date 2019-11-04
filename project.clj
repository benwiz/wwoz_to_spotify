(defproject wwoz_to_spotify "0.1.0-SNAPSHOT"
  :description "Read WWOZ's Spinitron RSS feed and add all songs to a Spotify playlist."
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [aleph "0.4.6"]
                 [byte-streams "0.2.4"]
                 [cheshire "5.9.0"]
                 [compojure "1.6.0"]
                 [clj-spotify "0.1.9"] ;; NOTE: Was using 0.1.9
                 [feedme "0.0.3"] ;; TODO: remove
                 [hickory "0.7.1"]
                 [ring/ring-defaults "0.3.1"]
                 [ring/ring-json "0.4.0"]
                 [ring-apigw-lambda-proxy "0.3.0"]
                 [rotary "0.4.1"]
                 [uswitch/lambada "0.1.2"]]
  :plugins [[lein-ring "0.9.7"]
            [lein-lambda "0.2.0"]]
  :ring {:handler wwoz_to_spotify.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.1"]]}
   :uberjar {:aot :all}}
  :lambda {:function {:name "wwoz-to-spotify" ; bucket name cannot contain underscores
                      :handler "wwoz_to_spotify.lambda.LambdaFn"}
           :api-gateway {:name "wwoz_to_spotify"}
           :stages {"production" {:warmup {:enable true}}
                    "staging"    {}}}
  ; For local runs
  :main wwoz_to_spotify.lambda)
