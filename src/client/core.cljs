(ns client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [client.todo.core :as todo]
    [shared.core :refer [del-todo]]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<! chan put!]]
    [reagent.core :as reagent]))


(def CHANNEL (chan))

(defonce state (reagent/atom []))

(defn on-del-btn-clicked [todo-id]
  (go
    (let
      [res (<! (http/delete (str "http://localhost:4000/todos/" todo-id)))]
      (del-todo state (:body res)))))

(reagent/render [todo/app state on-del-btn-clicked] (.getElementById js/document "app"))

(http/get "http://localhost:4000/todos" {:channel CHANNEL})

(go (let [res (<! CHANNEL)] (swap! state (fn [] (:body res)))))
