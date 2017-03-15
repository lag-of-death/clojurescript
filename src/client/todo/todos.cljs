(ns client.todo.todos
  (:require [client.todo.todo :refer [todo]]))

(defn todos [on-delete state deref-state]
  (let [on-del (partial on-delete state)]
    (-> (partial todo on-del state)
        (map deref-state))))