(ns client.todo.button
  (:require
    [taoensso.sente :as sente]
    [client.comms :refer [chsk-send!]]
    [cljs.core.async :refer [put!]]
    [client.domain :refer [del-todo-channel]]))


(defn handle-del-btn-clicked-with-channel [channel id]
  (chsk-send!
   [:todos/mark-as-deleted {:id id}]
   8000
   (fn [reply]
     (if (sente/cb-success? reply)
       (put! channel reply)
       (js/console.error reply)))))

(def handle-del-btn-clicked
  (partial handle-del-btn-clicked-with-channel del-todo-channel))

(defn del-button [on-click todo-id]
  [:button.button {:on-click #(on-click todo-id)} "x"])

(defn button [todo-id] (del-button handle-del-btn-clicked todo-id))
