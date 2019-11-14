(ns client.events
  (:require
    [taoensso.sente :as sente]
    [cljs.core.async :refer [put!]]
    [client.domain
     :refer
     [all-todos-channel add-todo-channel done-todo-channel del-todo-channel]]))

(defmulti event-msg-handler (fn [send-fn opts] (:id opts)))


(defmethod event-msg-handler :default
           [chsk-send! {:keys [event]}]
           (js/console.log "evt" event))

(defn callback [reply] (put! all-todos-channel (:todos reply)))

(defmethod event-msg-handler :chsk/state
           [chsk-send!]
           (chsk-send!
            [:todos/get-all]
            8000
            (fn [reply]
              (if (sente/cb-success? reply)
                (callback reply)
                (js/console.error reply)))))

(defmethod event-msg-handler :chsk/recv
           [chsk-send! {:keys [?data]}]
           (let [data       (aget (clj->js ?data) "1")
                 body       (aget data "body")
                 event-name (aget (clj->js ?data) "0")]
             (case event-name
                   "deleted"        (put! del-todo-channel {:body body})
                   "marked-as-done" (put! done-todo-channel {:body body})
                   "added"          (put! add-todo-channel body))))

(defonce router_ (atom nil))

(defn stop-router! [] (when-let [stop-f @router_] (stop-f)))
(defn start-router! []
  (let [{:keys [chsk ch-recv send-fn state]}
        (sente/make-channel-socket! "/chsk"
                                    {:type :auto})]

    (stop-router!)
    (reset! router_
            (sente/start-client-chsk-router!
             ch-recv (partial event-msg-handler send-fn)))

    send-fn))
