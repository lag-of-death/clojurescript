(ns server.core
  (:require
    [server.helpers :refer [del-todo add-todo mark-todo]]
    [taoensso.encore :as encore
     :refer              ()]
    [taoensso.timbre
     :as           timbre
     :refer-macros (tracef debugf infof warnf errorf)]
    [taoensso.sente :as sente]
    [taoensso.sente.server-adapters.express :as sente-express]
    [cljs.nodejs :as nodejs]))

(def http (nodejs/require "http"))
(def express (nodejs/require "express"))
(def express-ws (nodejs/require "express-ws"))
(def ws (nodejs/require "ws"))
(def body-parser (nodejs/require "body-parser"))
(def session (nodejs/require "express-session"))

(let [packer :edn
      {:keys [ch-recv
              send-fn
              ajax-post-fn
              ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente-express/make-express-channel-socket-server!
       {:packer        packer
        :csrf-token-fn nil
        :user-id-fn    (fn [ring-req] (aget (:body ring-req) "session" "uid"))})]
  (def ajax-post ajax-post-fn)
  (def ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk ch-recv)
  (def chsk-send! send-fn)
  (def connected-uids connected-uids))


(defn express-login-handler
  [req res]
  (let [req-session (aget req "session")
        body        (aget req "body")
        user-id     (aget body "user-id")]
    (debugf "Login request: %s" user-id)
    (aset req-session "uid" (.random js/Math))
    (.send res "Success")))

(def todos
  (atom
   [{:name "Learn ClojureScript" :is-done false :id 0}
    {:name "Write a great app in ClojureScript" :is-done false :id 1}]))

(defn get-id [req]
  (-> (.-params req)
      (js->clj)
      (get "id")
      (js/parseFloat)))

(defn get-random-id [] (.random js/Math))

(defn gen-next-todo [state todo-name]
  (let [new-todo {:name todo-name :is-done false :id (get-random-id)}]
    (add-todo state new-todo) new-todo))

(defn add-sente-routes [express-app]
  (doto express-app
        (.ws "/chsk"
             (fn [ws req next]
               (ajax-get-or-ws-handshake req nil nil
                                         {:websocket? true
                                          :websocket  ws})))

        (.get "/chsk" ajax-get-or-ws-handshake)
        (.post "/chsk" ajax-post)))


(defmulti event-msg-handler
  "Multimethod to handle Sente `event-msg`s"
  :id)

(defmethod event-msg-handler :default
           [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
           (let [session (:session ring-req)
                 uid     (:uid session)]
             (debugf "Unhandled event: %s" event)
             (when ?reply-fn
                   (?reply-fn {:umatched-event-as-echoed-from-from-server event}))))


(defmethod event-msg-handler :todos/get-all
           [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
           (let [body    (:body ring-req)
                 session (aget body "session")
                 uid     (aget session "uid")]
             (when ?reply-fn
                   (?reply-fn {:hello (:ws @connected-uids) :todos @todos}))))

(defmethod event-msg-handler :todos/mark-as-done
           [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
           (let [body    (:body ring-req)
                 id      (aget (clj->js ?data) "id")]
             (when ?reply-fn
                   (?reply-fn {:hello (:ws @connected-uids) :body (mark-as-done id)}))))

(defmethod event-msg-handler :todos/mark-as-deleted
           [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
           (let [body    (:body ring-req)
                 id      (aget (clj->js ?data) "id")]
             (when ?reply-fn
                   (?reply-fn {:hello (:ws @connected-uids) :body (del-todo todos id)}))))


(defmethod event-msg-handler :todos/add
           [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
           (let [body           (:body ring-req)
                 todo-name      (aget (clj->js ?data) "todo-name")]
             (when ?reply-fn
                   (?reply-fn
                    {:hello (:ws @connected-uids)
                     :body  (->> todo-name
                                 (gen-next-todo todos))}))))


(defonce router_ (atom nil))

(defn stop-router! [] (when-let [stop-f @router_] (stop-f)))
(defn start-router! []
  (stop-router!)
  (reset! router_
          (sente/start-server-chsk-router!
           ch-chsk event-msg-handler)))

(defn mark-as-done [id]
  (->> id
       (#(filter (fn [todo] (= (:id todo) %)) @todos))
       (first)
       (mark-todo todos)))

(defn -main [& args]
  (let [app  (express)
        _    (express-ws app)
        port (.-PORT (.-env cljs.nodejs/process))]

    (.use app
          (body-parser))

    (.use app
          (session
           (clj->js {:saveUninitialized true :secret "abc 123", :name "express-session"})))

    (.get app "/login" express-login-handler)

    (.use app
          (.static express "resources/public"))

    (add-sente-routes app)

    (start-router!)

    (.use app
          (fn [req res next]
            (js/console.warn "Unhandled request: %s" (.-originalUrl req))
            (next)))

    (.listen app (if port port 4000))))


(set! *main-cli-fn* -main)
