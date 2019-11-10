(ns client.todo.todo
  (:require
    [client.comms :refer [chsk-send!]]
    [taoensso.sente :as sente]
    [client.domain :refer [done-todo-channel]]
    [cljs.core.async :refer [put!]]
    [client.todo.button :refer [button]]))

(defn handle-click-with-channel [channel todo]
  (chsk-send!
   [:todos/mark-as-done {:id (:id todo)}]
   8000
   (fn [reply]
     (if (sente/cb-success? reply)
       (put! channel reply)
       (js/console.error reply)))))

(def handle-click (partial handle-click-with-channel done-todo-channel))

(def generate-todo-with-on-click
  (fn [on-click-handler todo-data]
    ^{:key (:id todo-data)} [:li.todo
                             {}
                             [:span
                              {:on-click #(on-click-handler todo-data)
                               :class
                               (if (:is-done todo-data)
                                 "todo__done-item"
                                 "todo__new-item")}
                              (:name todo-data)]
                             [button (:id todo-data)]]))

(def generate-todo (partial generate-todo-with-on-click handle-click))
