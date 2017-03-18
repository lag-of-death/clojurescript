(ns client.todo.todo
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [client.channels :refer [done-todo-channel]]
    [cljs.core.async :refer [<! put!]]
    [client.todo.button :refer [button]]))

(defn on-click [todo]
  (go (let [res (<! (http/post (str "http://localhost:4000/todos/" (:id todo))))]
        (put! done-todo-channel res))))

(def generate-todo
  (fn [todo-data]
    ^{:key (:id todo-data)} [:li {:style    {:text-decoration (if (:is-done todo-data) "line-through" "none")}
                                  :on-click #(on-click todo-data)}
                             (:name todo-data)
                             [button (:id todo-data)]]))
