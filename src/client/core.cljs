(ns client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [taoensso.sente :as sente
     :refer             (cb-success?)]
    [client.state_changes :refer [create-store]]
    [client.todo.core :as todo]
    [client.domain :refer [state all-todos-channel]]
    [cljs.core.async :refer [put!]]
    [client.comms :refer [chsk-send!]]
    [reagent.core :as reagent]))


(reagent/render [todo/app (create-store state)] (.getElementById js/document "app"))

(http/get "/login")

(defn callback [reply] (put! all-todos-channel (:todos reply)))

(js/setTimeout
 (fn []
   (chsk-send!
    [:todos/get-all]
    8000
    (fn [reply]
      (if (sente/cb-success? reply)
        (callback reply)
        (js/console.error reply)))))
 500)

