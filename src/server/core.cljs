(ns server.core
  (:require
    [server.helpers :refer [get-random-id]]
    [taoensso.sente :as sente]
    [server.comms :refer [ajax-get-or-ws-handshake ajax-post ch-chsk]]
    [server.events :refer [event-msg-handler]]
    [cljs.nodejs :as nodejs]))

(def http (nodejs/require "http"))
(def express (nodejs/require "express"))
(def express-ws (nodejs/require "express-ws"))
(def ws (nodejs/require "ws"))
(def body-parser (nodejs/require "body-parser"))
(def session (nodejs/require "express-session"))

(defn express-login-handler
  [req res]
  (let [req-session (aget req "session")
        body        (aget req "body")
        user-id     (aget body "user-id")]
    (js/console.log "Login request: %s" user-id)
    (aset req-session "uid" (get-random-id))
    (.send res "Success")))

(defn add-sente-routes [express-app]
  (doto express-app
        (.ws "/chsk"
             (fn [ws req]
               (ajax-get-or-ws-handshake req nil nil
                                         {:websocket? true
                                          :websocket  ws})))

        (.get "/chsk" ajax-get-or-ws-handshake)
        (.post "/chsk" ajax-post)))

(defonce router_ (atom nil))

(defn stop-router! [] (when-let [stop-f @router_] (stop-f)))
(defn start-router! []
  (stop-router!)
  (reset! router_
          (sente/start-server-chsk-router!
           ch-chsk event-msg-handler)))

(defn -main []
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
          (fn [req _res next]
            (js/console.warn "Unhandled request: %s" (.-originalUrl req))
            (next)))

    (.listen app (or port 4000))))


(set! *main-cli-fn* -main)
