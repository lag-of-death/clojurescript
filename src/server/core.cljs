(ns server.core
  (:require [cljs.nodejs :as nodejs]))

(defonce express (nodejs/require "express"))

(defn -main [& args]
  (let [app (express)]
    (.use app (.static express "resources/public"))
    (.listen app 4000)))

(set! *main-cli-fn* -main)