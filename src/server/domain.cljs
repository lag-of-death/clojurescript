(ns server.domain
  (:require
    [server.helpers :refer [get-random-id]]
    [shared.domain :as shared]))

(def passes (atom {:xyz "xyz"}))

(def todos
  (atom
   {:xyz [{:name "Learn ClojureScript" :is-done false :id 0}
          {:name "Write a great app in ClojureScript" :is-done false :id 1}]}))

(defn get-todos [uid]
  (or ((keyword uid) @todos) []))


(defn add-todo [uid todos todo]
  (let [uid-todos ((keyword uid) @todos)]
    (swap! todos assoc (keyword uid) (cons todo uid-todos)) todo))


(defn gen-next-todo [uid state todo-name]
  (let [new-todo {:name todo-name :is-done false :id (get-random-id)}]
    (add-todo uid state new-todo) new-todo))

(defn mark-todo [uid todos todo-to-mark]
  (swap! todos assoc (keyword uid)
         (shared/change-todo-status ((keyword uid) @todos) (:id todo-to-mark)))
  todo-to-mark)

(defn mark-as-done [uid todo-id]
  (->> todo-id
       (#(filter (fn [todo] (= (:id todo) %)) ((keyword uid) @todos)))
       (first)
       (mark-todo uid todos)))


(defn del-todo [uid todos id]
  (let [uid-todos ((keyword uid) @todos)]
    (swap! todos assoc (keyword uid) (shared/filter-out-todo uid-todos id)) id))
