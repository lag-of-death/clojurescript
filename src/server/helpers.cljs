(ns server.helpers
  (:require
    [shared.core :as shared]
    [server.state :refer [todos]]))

(defn mark-todo [todos todo-to-mark]
  (swap! todos (fn [old-todos] (shared/change-todo-status old-todos todo-to-mark)))
  todo-to-mark)

(defn add-todo [todos todo]
  (swap! todos (fn [old-todos] (cons todo old-todos))) todo)

(defn del-todo [todos id]
  (swap! todos
         (fn [old-todos] (shared/filter-out-todo old-todos id)))
  id)

(defn get-random-id [] (.random js/Math))

(defn gen-next-todo [state todo-name]
  (let [new-todo {:name todo-name :is-done false :id (get-random-id)}]
    (add-todo state new-todo) new-todo))

(defn mark-as-done [id]
  (->> id
       (#(filter (fn [todo] (= (:id todo) %)) @todos))
       (first)
       (mark-todo todos)))
