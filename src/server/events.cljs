(ns server.events
  (:require
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
             (when ?reply-fn
                   (?reply-fn {:uid uid :connected-uids (:ws @connected-uids) :todos @todos}))))


(defmethod event-msg-handler :todos/mark-as-done
           [{:keys [?data ring-req]}]
           (let [body        (mark-as-done (aget (clj->js ?data) "id"))

                 req-body    (:body ring-req)
                 session     (aget req-body "session")
                 uid         (aget session "uid")]

             (chsk-send! uid
                         [:todos/marked-as-done {:body (:id body)}])))

(defmethod event-msg-handler :todos/mark-as-deleted
           [{:keys [?data]}]
           (let [id                   (aget (clj->js ?data) "id")
                 deleted-todo-id      (del-todo todos id)]
             (doseq [uid (:any @connected-uids)]
               (chsk-send! uid
                           [:todos/deleted {:body deleted-todo-id}]))))


(defmethod event-msg-handler :todos/add
           [{:keys [?data]}]

           (let [todo-name      (aget (clj->js ?data) "todo-name")
                 next-todo      (->> todo-name
                                     (gen-next-todo todos))]

             (doseq [uid (:any @connected-uids)]
               (chsk-send! uid
                           [:todos/added {:body next-todo}]))))
