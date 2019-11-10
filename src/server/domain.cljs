(ns server.domain
  (:require
    [server.helpers :refer [get-random-id]]
    [shared.domain :as shared]))

(def todos
  (atom
   [{:name "Learn ClojureScript" :is-done false :id 0}
    {:name "Write a great app in ClojureScript" :is-done false :id 1}]))

(defn add-todo [todos todo]
  (swap! todos (fn [old-todos] (cons todo old-todos))) todo)


(defn gen-next-todo [state todo-name]
  (let [new-todo {:name todo-name :is-done false :id (get-random-id)}]
    (add-todo state new-todo) new-todo))

(defn mark-todo [todos todo-to-mark]
  (swap! todos (fn [old-todos] (shared/change-todo-status old-todos todo-to-mark)))
  todo-to-mark)

(defn mark-as-done [id]
  (->> id
       (#(filter (fn [todo] (= (:id todo) %)) @todos))
       (first)
       (mark-todo todos)))


(defn del-todo [todos id]
  (swap! todos
         (fn [old-todos] (shared/filter-out-todo old-todos id)))
  id)
