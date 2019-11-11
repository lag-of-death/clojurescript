(ns client.events
  (:require [taoensso.sente :as sente]
            [cljs.core.async :refer [put!]]
            [client.domain :refer [done-todo-channel]]
            [client.comms :refer [ch-chsk]]))

(defmulti event-msg-handler
  "Multimethod to handle Sente `event-msg`s"
  :id)


(defmethod event-msg-handler :default
           []
           (js/console.log ":default"))

(defmethod event-msg-handler :chsk/recv
           [{:keys [?data]}]
           (let [body (aget (aget (clj->js ?data) "1") "body")]
             (put! done-todo-channel {:body body})))

(defonce router_ (atom nil))

(defn stop-router! [] (when-let [stop-f @router_] (stop-f)))
(defn start-router! []
  (stop-router!)
  (reset! router_
          (sente/start-client-chsk-router!
           ch-chsk event-msg-handler)))
