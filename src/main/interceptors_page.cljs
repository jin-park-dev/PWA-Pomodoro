(ns interceptors-page
  (:require [reagent.core :as reagent]
            [clojure.string :as str]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [re-frame.db :as db]))

(defn interceptors-container []
  [:<>
   [:h1 "Interceptors Container"]
   ])