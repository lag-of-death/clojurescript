(ns server.helpers
  (:require
    [shared.core :as shared]))

(defn mark-todo [todos todo-to-mark]
  (swap! todos (fn [old-todos] (shared/change-todo-status old-todos todo-to-mark)))
  todo-to-mark)

(defn add-todo [todos todo]
  (swap! todos (fn [old-todos] (cons todo old-todos))) todo)

(defn del-todo [todos id]
  (swap! todos
         (fn [old-todos] (shared/filter-out-todo old-todos id)))
  id)