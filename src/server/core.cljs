(ns server.core
  (:require
    [taoensso.sente :as sente]
    [server.domain :refer [passes rooms]]
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
  (let [req-session         (aget req "session")
        body                (aget req "body")

        pass                (aget body "password")
        identifier          (aget body "room-name")

        room-id             (str identifier ":" pass)]

    (if (= nil ((keyword identifier) @rooms))
      (do
        (swap! rooms conj (keyword identifier))
        (swap! passes assoc-in [(keyword pass)] pass)

        (aset req-session "uid" room-id)

        (.send res "room created"))
      (if (= pass ((keyword pass) @passes))
        (do
          (aset req-session "uid" room-id)
          (.send res "Success"))

        (.send res "no auth")))))

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
          (.json express))


    (.use app
          (session
           (clj->js {:saveUninitialized true :secret "abc 123", :name "express-session"})))

    (.post app "/login" express-login-handler)

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
