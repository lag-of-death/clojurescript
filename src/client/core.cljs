(ns client.core
  (:require
    [client.events :refer [start-router!]]
    [client.state_changes :refer [create-store]]
    [client.todo.core :as todo]
    [client.domain :refer [state]]
    [reagent.core :as reagent]))

(.addEventListener (js/document.getElementById "login") "click"
                   (fn [e]
                     (.preventDefault e)

                     (if (.checkValidity (js/document.getElementById "form"))
                       (->>
                        (js/fetch "/login"
                                  (clj->js
                                   {:method  "POST"
                                    :headers (clj->js {:content-type "application/json"})
                                    :body    (js/JSON.stringify
                                              (clj->js
                                               {:room-name (.-value (js/document.getElementById "room-name"))
                                                :password  (.-value (js/document.getElementById "password"))}))}))

                        (#(.then %1
                           (fn [x]
                             (.then (.text x)
                                    (fn [text]
                                      (if (= text "no auth")
                                        (js/alert "no auth")
                                        (do
                                          (let [sente-router (start-router!)]
                                            (reagent/render [todo/app (create-store state) sente-router]
                                                            (.getElementById js/document "app")))))))))))

                       (js/alert "please provide room-name and pass"))))
