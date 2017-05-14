(ns server.core
  (:require
    [server.helpers :refer [del-todo add-todo mark-todo]]
    [cljs.nodejs :as nodejs]))

(defonce express (nodejs/require "express"))

(defonce body-parser (nodejs/require "body-parser"))

(def todos
  (atom
    [{:name "Learn ClojureScript" :is-done false :id 0}
     {:name "Write a great app in ClojureScript" :is-done false :id 1}]))

(defn get-id [req]
  (-> (.-params req)
      (js->clj)
      (get "id")
      (js/parseFloat)))

(defn get-random-id [] (.random js/Math))

(defn gen-next-todo [state todo-name]
  (let [new-todo {:name todo-name :is-done false :id (get-random-id)}]
    (add-todo state new-todo) new-todo))

(defn -main [& args]
  (let [app (express)]

    (.use app
          (body-parser))

    (.use app
          (.static express "resources/public"))

    (.get app "/todos"
          (fn [req res]
            (.json res (clj->js @todos))))

    (.post app "/todos"
           (fn [req res]
             (->> req
                  (.-body)
                  (js->clj)
                  (#(get % "todo-name"))
                  (gen-next-todo todos)
                  (clj->js)
                  (.json res))))

    (.post app "/todos/:id"
           (fn [req res]
             (->> req
                  (get-id)
                  (#(filter (fn [todo] (= (:id todo) %)) @todos))
                  (first)
                  (mark-todo todos)
                  (clj->js)
                  (.json res))))

    (.delete app "/todos/:id"
             (fn [req res]
               (->> req
                    (get-id)
                    (del-todo todos)
                    (.json res))))

    (.listen app 4000)))

(set! *main-cli-fn* -main)