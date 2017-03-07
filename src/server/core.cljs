(ns server.core
  (:require [cljs.nodejs :as nodejs]))

(defonce express (nodejs/require "express"))

(defonce todos [{:name "Learn ClojureScript" :is-done false}
                {:name "Write a great app in ClojureScript" :is-done false}])

(defn -main [& args]
  (let [app (express)]
    (.use app (.static express "resources/public"))
    (.get app "/todos" (fn [req res] (.json res (clj->js todos))))
    (.listen app 4000)))

(set! *main-cli-fn* -main)