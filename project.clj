(defproject app "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license
  {:name "Eclipse Public License"
   :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.taoensso/sente "1.14.0-RC2"]
                 [org.clojure/clojurescript "1.9.229"]
                 [cljs-http "0.1.42"]
                 [lein-doo "0.1.7"]
                 [reagent "0.6.0"]
                 [org.clojure/core.async
                  "0.2.395"
                  :exclusions
                  [org.clojure/tools.reader]]]

  :plugins
  [[lein-kibit "0.1.5"]
   [lein-doo "0.1.7"]
   [lein-cljsbuild "1.1.5" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild
  {:builds
   [{:id           "server"
     :source-paths ["src"]
     :compiler     {:target     :nodejs
                    :main       server.core
                    :output-to  "resources/server/index.js"
                    :output-dir "resources/server"}}
    {:id           "client"
     :source-paths ["src"]

     :compiler     {:main          client.core
                    :asset-path    "js/compiled/out"
                    :output-to     "resources/public/js/compiled/client.js"
                    :output-dir    "resources/tmp/out"
                    :optimizations :advanced}}
    {:id           "test"
     :source-paths ["src" "test"]
     :compiler     {:main          tests
                    :optimizations :none
                    :output-to     "resources/public/cljs/tests/all-tests.js"}}]})
