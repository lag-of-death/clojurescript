(ns client.todo.todos
  (:require [client.todo.todo :refer [generate-todo]]))

(defn generate-todos [send-fn todos-data]
  (map (fn [todo-data] (generate-todo send-fn todo-data)) todos-data))
