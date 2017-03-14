(ns client.todo.todo
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [shared.core :refer [mark-todo-as-done]]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]
    [client.todo.button :refer [button]]))

(defn on-click [todo todos]
  (go (let [res (<! (http/post (str "http://localhost:4000/todos/" (:id todo))))]))
  (mark-todo-as-done todos todo))

(def todo
  (fn [on-delete todos value]
    (let [button (button (partial on-delete (:id value)))]
      ^{:key (:id value)} [:li {:style {:text-decoration (if (:is-done value) "line-through" "none")} :on-click #(on-click value todos)} (:name value) button])))
