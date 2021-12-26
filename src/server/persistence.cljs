(ns server.persistence
  (:require
    [server.helpers :refer [to-keywords map-todos]]
    [cljs.nodejs :as nodejs]
    [server.domain :refer [rooms passwords todos]]))

(def pg (nodejs/require "pg"))
(def uri (nodejs/require "uri-js"))

(def url-from-env (.-DATABASE_URL (.-env cljs.nodejs/process)))

(def db-spec (.parse uri url-from-env))
(def user-and-pass (.split (aget db-spec "userinfo") ":"))

(def client
  (pg.Client.
    (clj->js
     {:user     (get user-and-pass 0)
      :host     (aget db-spec "host")
      :ssl      true
      :port     (aget db-spec "port")
      :database (aget (.split (aget db-spec "path") "/") 1)
      :password (get user-and-pass 1)})))

(def c (.connect client))

(->
 c
 (.then
   (fn []
     (->
      (.query client "select * from state")
      (.then
        (fn [data]
          (let [data                 (aget (.-rows data) "0")
                rooms-table-data     (aget data "rooms")
                todos-table-data     (aget data "todos")
                passwords-table-data (aget data "passwords")

                r                    (js->clj rooms-table-data)
                t                    (js->clj todos-table-data)
                p                    (js->clj passwords-table-data)]

            (js/console.log "state %s %s %s" r t p)

            (reset! todos (map-todos t))
            (reset! rooms (set (map keyword r)))
            (reset! passwords (to-keywords p))))))))
 (.catch (fn [err] (js/console.log "catch!" err))))

(defn watch [db-client atom table-name]
  (add-watch atom (keyword table-name)
             (fn [_ _ old-state new-state]
               (when (not= old-state new-state)
                 (do
                   (js/console.log (str table-name " new state: %s") new-state)
                   (->
                    (.query db-client (str "update state set " table-name " = $1::json where idx=1")
                            (clj->js [(js/JSON.stringify (clj->js new-state))]))
                    (.then
                      (fn [x] (js/console.log "OK:" x)))
                    (.catch
                      (fn [x] (js/console.log "ERR:" x)))))))))

(watch client passwords "passwords")
(watch client rooms "rooms")
(watch client todos "todos")
