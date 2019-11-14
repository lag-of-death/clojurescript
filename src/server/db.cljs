(ns server.db
  (:require
    [cljs.nodejs :as nodejs]
    [server.domain :refer [rooms passwords todos]]))

(def pg (nodejs/require "pg"))
(def uri (nodejs/require "uri-js"))

(def URL
  "postgres://rmpympltjjjlbl:b8247097aac15a4cb690f06df43299caf98d92b0027f88cc701cf78dd6a3d48f@ec2-54-247-92-167.eu-west-1.compute.amazonaws.com:5432/d6ead8ak9o2geh")

(def url-from-env (.-DATABASE_URL (.-env cljs.nodejs/process)))

(def db-spec (.parse uri (or url-from-env URL)))
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

(defn to-keywords [my-map]
  (into {}
        (for [[k v] my-map]
          {(keyword k) v})))

(defn map-todos [my-map]
  (into {}
        (for [[k v] my-map]
          {(keyword k) (map (fn [x] (to-keywords x)) v)})))

(->
 c
 (.then
   (fn []
     (->
      (.query client "select * from state")
      (.then
        (fn [data]
          (let [data         (aget (.-rows data) "0")
                rooms-db     (aget data "rooms")
                todos-db     (aget data "todos")
                passwords-db (aget data "passwords")

                r            (js->clj rooms-db)
                t            (js->clj todos-db)
                p            (js->clj passwords-db)]

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
                            (clj->js [(JSON.stringify (clj->js new-state))]))
                    (.then
                      (fn [x] (js/console.log "OK:" x)))
                    (.catch
                      (fn [x] (js/console.log "ERR:" x)))))))))

(watch client passwords "passwords")
(watch client rooms "rooms")
(watch client todos "todos")
