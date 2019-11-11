(ns client.todo.button
  (:require
    [client.comms :refer [chsk-send!]]))


(defn handle-del-btn-clicked-with-channel [id]
  (chsk-send!
   [:todos/mark-as-deleted {:id id}]
   8000))


(defn del-button [on-click todo-id]
  [:button.button {:on-click #(on-click todo-id)} "x"])

(defn button [todo-id]
  (del-button handle-del-btn-clicked-with-channel todo-id))
