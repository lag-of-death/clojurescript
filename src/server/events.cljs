(ns server.events
  (:require
    [shared.domain :refer [change-todo-status]]
    [server.comms :refer [connected-uids chsk-send!]]
    [server.domain :refer [todos mark-as-done del-todo gen-next-todo]]))

(defmulti event-msg-handler
  "Multimethod to handle Sente `event-msg`s"
  :id)

(defmethod event-msg-handler :default
           [{:keys [event ?reply-fn]}]
           (when ?reply-fn
                 (?reply-fn {:umatched-event-as-echoed-from-from-server event})))


(defmethod event-msg-handler :todos/get-all
           [{:keys [ring-req ?reply-fn]}]
           (let [body    (:body ring-req)
                 session (aget body "session")
                 uid     (aget session "uid")]
             (js/console.log "all-todos" (clj->js @todos))
             (when ?reply-fn
                   (?reply-fn {:uid uid :connected-uids (:ws @connected-uids) :todos @todos}))))


(defmethod event-msg-handler :todos/mark-as-done
           [{:keys [?data]}]
           (let [id      (aget (clj->js ?data) "id")
                 er      (mark-as-done (clj->js ?data))]
             (js/console.log (clj->js @todos))
             (doseq [uid (:any @connected-uids)]
               (chsk-send! uid
                           [:todos/marked-as-done
                            {:body er}]))))

(defmethod event-msg-handler :todos/mark-as-deleted
           [{:keys [?data ?reply-fn]}]
           (let [id      (aget (clj->js ?data) "id")]
             (when ?reply-fn
                   (?reply-fn {:connected-uids (:ws @connected-uids) :body (del-todo todos id)}))))


(defmethod event-msg-handler :todos/add
           [{:keys [?data ?reply-fn]}]
           (let [todo-name      (aget (clj->js ?data) "todo-name")]
             (when ?reply-fn
                   (?reply-fn
                    {:connected-uids (:ws @connected-uids)
                     :body           (->> todo-name
                                          (gen-next-todo todos))}))))
