(ns client.todo.todo
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [client.channels :refer [done-todo-channel]]
    [cljs.core.async :refer [<! put!]]
    [client.todo.button :refer [button]]))

(defn handle-click-with-channel [channel todo]
  (go
    (let [res (<! (http/post (str "http://localhost:4000/todos/" (:id todo))))]
      (put! channel res))))

(def handle-click (partial handle-click-with-channel done-todo-channel))

(def generate-todo-with-on-click
  (fn [on-click-handler todo-data]
    ^{:key (:id todo-data)} [:li
                             {:class    "todo"
                              :on-click #(on-click-handler todo-data)}
                             [:span
                              {:class
                               (if (:is-done todo-data)
                                 "todo__done-item"
                                 "todo__new-item")}
                              (:name todo-data)]
                             [button (:id todo-data)]]))

(def generate-todo (partial generate-todo-with-on-click handle-click))
