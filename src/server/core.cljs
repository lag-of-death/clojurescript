(ns server.core
  (:require
    [shared.core :refer [del-todo]]
    [cljs.nodejs :as nodejs]))

(defonce express (nodejs/require "express"))

(def todos (atom [{:name "Learn ClojureScript" :is-done false :id "0"}
                  {:name "Write a great app in ClojureScript" :is-done false :id "1"}]))

(defn get-id [req]
  (-> (.-params req)
      (js->clj)
      (get "id")))

(defn -main [& args]
  (let [app (express)]

    (.use app (.static express "resources/public"))

    (.get app "/todos"
          (fn [req res]
            (->> (clj->js @todos)
                 (.json res))))


    (.delete app "/todos/:id"
             (fn [req res]
               (->> req
                    (get-id)
                    (del-todo todos)
                    (.json res))))

    (.listen app 4000)))

(set! *main-cli-fn* -main)