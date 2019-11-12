(ns client.core
  (:require
    [client.events :refer [start-router!]]
    [client.comms :refer [create-channel]]
    [client.state_changes :refer [create-store]]
    [client.todo.core :as todo]
    [client.domain :refer [state]]
    [reagent.core :as reagent]))

(.addEventListener (js/document.getElementById "login") "click"
                   (fn [e]
                     (.preventDefault e)

                     (if (.checkValidity (js/document.getElementById "form"))
                       (->>
                        (.-value (js/document.getElementById "room-name"))
                        (str "/login/")
                        (js/fetch)

                        (#(.then %1
                           (do
                             (create-channel)
                             (start-router!)
                             (reagent/render [todo/app (create-store state)] (.getElementById js/document "app"))))))

                       (js/alert "not valid"))))
