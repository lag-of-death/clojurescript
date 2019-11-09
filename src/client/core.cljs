(ns client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [taoensso.sente :as sente
     :refer             (cb-success?)]
    [client.state_changes :refer [create-store]]
    [client.todo.core :as todo]
    [client.state :refer [state]]
    [cljs.core.async :refer [put!]]
    [client.channels
     :refer
     [all-todos-channel]]
    [reagent.core :as reagent]))


(reagent/render [todo/app (create-store state)] (.getElementById js/document "app"))

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk"
                                  {:type :auto})]
  (def chsk chsk)
  (def ch-chsk ch-recv)
  (def chsk-send! send-fn)
  (def chsk-state state))


(http/get "http://localhost:4000/login")

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

