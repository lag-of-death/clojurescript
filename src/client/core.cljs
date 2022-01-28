(ns client.core
  (:require
    [client.events :refer [start-router!]]
    [client.state_changes :refer [create-store]]
    [client.todo.core :as todo]
    [client.domain :refer [state]]
    [reagent.core :as reagent]))

(def form (js/document.getElementById "form"))

(.addEventListener
  (js/document.getElementById "login") "click"

  (fn [e]
    (.preventDefault e)

    (if (.checkValidity form)
      (.then

        (js/fetch
         "/login"
         (clj->js
          {:method  "POST"
           :headers (clj->js {:content-type "application/json"})
           :body    (js/JSON.stringify
                     (clj->js
                      {:room-name (.-value (js/document.getElementById "room-name"))
                       :password  (.-value (js/document.getElementById "password"))}))}))

        (fn [x]
          (.then (.text x)
                 (fn [text]
                   (if (= text "no auth")
                     (js/alert "no auth")
                     (let [sente-router (start-router!)]
                       (.remove form)
                       (reagent/render [todo/app (create-store state) sente-router]
                                       (.getElementById js/document "app"))))))))

      (js/alert "please provide room-name and pass"))))
