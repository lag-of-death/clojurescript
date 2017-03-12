(ns client.todo.todos
  (:require [client.todo.todo :refer [todo]]))

(defn todos [state on-delete]
  (let [on-del (partial on-delete state)]
    (-> (partial todo on-del)
        (map @state))))