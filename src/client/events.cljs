(ns client.events
  (:require
    [taoensso.sente :as sente]
    [cljs.core.async :refer [put!]]
    [client.domain
     :refer
     [all-todos-channel add-todo-channel done-todo-channel del-todo-channel]]))

(defmulti event-msg-handler (fn [_ opts] (:id opts)))


(defmethod event-msg-handler :default
           [_ {:keys [event]}]
           (js/console.log "evt" event))

(defn callback [reply] (put! all-todos-channel reply))

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
           [_ {:keys [?data]}]
           (let [data       (aget (clj->js ?data) "1")
                 event-name (aget (clj->js ?data) "0")]
             (case event-name
                   "deleted"        (put! del-todo-channel data)
                   "marked-as-done" (put! done-todo-channel data)
                   "added"          (put! add-todo-channel data))))

(defonce router_ (atom nil))

(defn stop-router! [] (when-let [stop-f @router_] (stop-f)))
(defn start-router! []
  (let [{:keys [ch-recv send-fn]}
        (sente/make-channel-socket! "/chsk"
                                    {:type :auto})]

    (stop-router!)
    (reset! router_
            (sente/start-client-chsk-router!
             ch-recv (partial event-msg-handler send-fn)))

    send-fn))
