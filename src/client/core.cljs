(ns client.core
  (:require
    [client.events :refer [start-router!]]
    [cljs-http.client :as http]
    [client.state_changes :refer [create-store]]
    [client.todo.core :as todo]
    [client.domain :refer [state]]
    [reagent.core :as reagent]))


(start-router!)

(reagent/render [todo/app (create-store state)] (.getElementById js/document "app"))

(http/get "/login")
