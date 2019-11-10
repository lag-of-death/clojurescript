(ns server.events
  (:require
    [server.state :refer [todos]]
    [server.comms :refer [connected-uids]]
    [server.helpers :refer [mark-as-done del-todo gen-next-todo]]))


(defmulti event-msg-handler
  "Multimethod to handle Sente `event-msg`s"
  :id)

(defmethod event-msg-handler :default
           [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
           (let [session (:session ring-req)
                 uid     (:uid session)]
             (when ?reply-fn
                   (?reply-fn {:umatched-event-as-echoed-from-from-server event}))))


(defmethod event-msg-handler :todos/get-all
           [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
           (let [body    (:body ring-req)
                 session (aget body "session")
                 uid     (aget session "uid")]
             (when ?reply-fn
                   (?reply-fn {:connected-uids (:ws @connected-uids) :todos @todos}))))

(defmethod event-msg-handler :todos/mark-as-done
           [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
           (let [body    (:body ring-req)
                 id      (aget (clj->js ?data) "id")]
             (when ?reply-fn
                   (?reply-fn {:connected-uids (:ws @connected-uids) :body (mark-as-done id)}))))

(defmethod event-msg-handler :todos/mark-as-deleted
           [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
           (let [body    (:body ring-req)
                 id      (aget (clj->js ?data) "id")]
             (when ?reply-fn
                   (?reply-fn {:connected-uids (:ws @connected-uids) :body (del-todo todos id)}))))


(defmethod event-msg-handler :todos/add
           [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
           (let [body           (:body ring-req)
                 todo-name      (aget (clj->js ?data) "todo-name")]
             (when ?reply-fn
                   (?reply-fn
                    {:connected-uids (:ws @connected-uids)
                     :body           (->> todo-name
                                          (gen-next-todo todos))}))))
