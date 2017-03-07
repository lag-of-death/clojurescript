(ns client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [client.todo.core :as todo]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<! chan put!]]
    [reagent.core :as reagent]))

(defonce state (reagent/atom []))

(def CHANNEL (chan))

(http/get "http://localhost:4000/todos" {:channel CHANNEL})

(go (let [res (<! CHANNEL)] (swap! state (fn [] (:body res)))))

(reagent/render [todo/app state] (.getElementById js/document "app"))