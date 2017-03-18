(ns client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [client.state.changes :refer [react-on-changes]]
    [client.state :refer [filter-todos-by input-atom todos]]
    [client.todo.core :as todo]
    [cljs-http.client :as http]
    [reagent.core :as reagent]))

(react-on-changes)

(reagent/render [todo/app] (.getElementById js/document "app"))

