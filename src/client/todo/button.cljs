(ns client.todo.button
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [cljs.core.async :refer [put! <!]]
    [client.channels :refer [del-todo-channel]]))

(defn handle-del-btn-clicked-with-channel [channel todo-id]
  (go
    (let
      [res (<! (http/delete (str "http://localhost:4000/todos/" todo-id)))]
      (put! channel res))))

(def handle-del-btn-clicked (partial handle-del-btn-clicked-with-channel del-todo-channel))

(defn del-button [on-click todo-id] [:button {:on-click #(on-click todo-id)} "x"])

(defn button
  ([todo-id] (del-button handle-del-btn-clicked todo-id))
  ([on-click child-node] [:button {:on-click on-click} child-node]))
