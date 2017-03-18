(ns client.todo.button
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [cljs.core.async :refer [put! <!]]
    [client.channels :refer [del-todo-channel]]))

(defn on-del-btn-clicked [todo-id]
  (go
    (let
      [res (<! (http/delete (str "http://localhost:4000/todos/" todo-id)))]
      (put! del-todo-channel res))))

(defn button
  ([todo-id] [:button {:on-click #(on-del-btn-clicked todo-id)} "x"])
  ([on-click child-node] [:button {:on-click on-click} child-node]))
