(ns client.todo.todos
  (:require [client.todo.todo :refer [generate-todo]]))

(defn generate-todos [todos-data] (map generate-todo todos-data))
