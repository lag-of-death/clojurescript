(ns client.core
  (:require
    [client.events :refer [start-router!]]
    [client.comms :refer [create-channel]]
    [client.state_changes :refer [create-store]]
    [client.todo.core :as todo]
    [client.domain :refer [state]]
    [reagent.core :as reagent]))


(.addEventListener (js/document.getElementById "login") "change"
                   (fn [e]
                     (-> (js/fetch (str "/login/" (-> e .-target .-value)))
                         (.then
                           (create-channel)
                           (start-router!)

                           (reagent/render [todo/app (create-store state)] (.getElementById js/document "app"))))))
