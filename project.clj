(defproject wwoz_to_spotify "0.1.0-SNAPSHOT"
  :description "Read WWOZ's Spinitron RSS feed and add all songs to a Spotify playlist."
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.6.0"]
                 [ring/ring-defaults "0.3.1"]
                 [ring/ring-json "0.4.0"]
                 [uswitch/lambada "0.1.2"]
                 [cheshire "5.7.1"]
                 [ring-apigw-lambda-proxy "0.3.0"]
                 [feedme "0.0.3"]
                 [clj-spotify "0.1.5"]
                 [rotary "0.4.1"]]
  :plugins [[lein-ring "0.9.7"]
            [lein-lambda "0.2.0"]]
  :ring {:handler wwoz_to_spotify.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.1"]]}
   :uberjar {:aot :all}}
  :lambda {:function {:name "wwoz-to-spotify" ; because bucket name cannot contain underscores
                      :handler "wwoz_to_spotify.lambda.LambdaFn"}
           :api-gateway {:name "wwoz_to_spotify"}
           :stages {"production" {:warmup {:enable true}}
                    "staging"    {}}}
  :main wwoz_to_spotify.lambda)
